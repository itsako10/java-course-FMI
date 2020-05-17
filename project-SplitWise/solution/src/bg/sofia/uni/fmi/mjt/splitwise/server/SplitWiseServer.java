package bg.sofia.uni.fmi.mjt.splitwise.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class SplitWiseServer implements AutoCloseable {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    private static final int BUFFER_SIZE = 32768;
    private final int NUMBER_OF_DIGITS_AFTER_DECIMAL_POINT = 2;
    private Selector selector;
    private ByteBuffer buffer;
    private ServerSocketChannel serverSocketChannel;

    //bidimap
    private Map<SocketChannel, String> loggedInSocketChannelsUsers;
    private Map<String, SocketChannel> loggedInUsersSocketChannels;
    private Map<String, String> registeredUsers;
    private Map<String, Set<String>> usersFriends;
    private Map<FriendsCouple, Double> friendsDebts;
    private Map<String, List<FriendsGroup>> usersGroups;
    private Map<FriendsGroup, Map<FriendsCouple, Double>> groupsDebts;
    private Map<String, UserHistory> usersHistory;

    public SplitWiseServer(boolean isStartedForTheFirstTime) throws IOException {
        selector = Selector.open();
        buffer = ByteBuffer.allocate(BUFFER_SIZE);
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
        loggedInSocketChannelsUsers = new HashMap<>();
        loggedInUsersSocketChannels = new HashMap<>();
        registeredUsers = new HashMap<>();
        usersFriends = new HashMap<>();
        friendsDebts = new HashMap<>();
        usersGroups = new HashMap<>();
        groupsDebts = new HashMap<>();
        usersHistory = new HashMap<>();

        if (!isStartedForTheFirstTime) {
            loadDataFromFiles();
        }
    }

    private void loadDataFromFiles() {
        try (FileReader registeredUsersReader = new FileReader("registeredUsers.txt");
             FileReader usersFriendsReader = new FileReader("usersFriends.txt");
             FileReader friendsDebtsReader = new FileReader("friendsDebts.txt");
             FileReader usersGroupsReader = new FileReader("usersGroups.txt");
             FileReader groupsDebtsReader = new FileReader("groupsDebts.txt");
             FileReader usersHistoryReader = new FileReader("usersHistory.txt")) {
            loadRegisteredUsersFromJson(registeredUsersReader);
            loadUsersFriendsFromJson(usersFriendsReader);
            loadFriendsDebtsFromJason(friendsDebtsReader);
            loadUsersGroupsFromJson(usersGroupsReader);
            loadGroupsDebtsFromJson(groupsDebtsReader);
            loadUsersHistoryFromJson(usersHistoryReader);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Can't find the file", e);
        } catch (IOException e) {
            System.out.println("Problem with the input/output!");
        }

    }

    public void start() throws IOException {
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            int readyChannels = selector.select();
            if (readyChannels <= 0) {
                continue;
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isReadable()) {
                    this.read(key);
                } else if (key.isAcceptable()) {
                    this.accept(key);
                }

                keyIterator.remove();
            }

        }
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        try {
            buffer.clear();
            int r = socketChannel.read(buffer);
            if (r <= 0) {
                System.out.println("Nothing to read, will close channel!");
                socketChannel.close();
                return;
            }

            buffer.flip();
            String message = Charset.forName("UTF-8").decode(buffer).toString();

            executeCommand(message, socketChannel);
        } catch (IOException e) {
            System.out.println("Client disconnected " + socketChannel.getRemoteAddress().toString());
            socketChannel.close();
        }
    }

    private void executeCommand(String command, SocketChannel socketChannel) throws IOException {
        String[] splittedCommand = command.strip().split(" ");
        List<String> notEmptySplittedCommands = new ArrayList<>();

        for (String argument : splittedCommand) {
            if (!argument.equals("")) {
                notEmptySplittedCommands.add(argument);
            }
        }

        final int firstArgument = 0;
        final int secondArgument = 1;
        final int thirdArgument = 2;
        final int fourthArgument = 3;

        if (!isLoggedIn(socketChannel) && !"login".equalsIgnoreCase(notEmptySplittedCommands.get(firstArgument)) &&
                !"register".equalsIgnoreCase(notEmptySplittedCommands.get(firstArgument))) {
            sendMessageToUser("You are not logged in!", socketChannel);
            return;
        }

        if (notEmptySplittedCommands.size() == 1) {
            if ("logout".equalsIgnoreCase(notEmptySplittedCommands.get(firstArgument))) {
                String currentUsername = loggedInSocketChannelsUsers.get(socketChannel);
                loggedInSocketChannelsUsers.remove(socketChannel);
                loggedInUsersSocketChannels.remove(currentUsername);
                sendMessageToUser("Logged out.", socketChannel);
                socketChannel.close();
            } else if ("get-status".equalsIgnoreCase(notEmptySplittedCommands.get(firstArgument))) {
                getStatus(socketChannel);
            } else if ("get-all-history".equalsIgnoreCase(notEmptySplittedCommands.get(firstArgument))) {
                getUsersAllHistory(socketChannel);
            } else {
                sendMessageToUser("Incorrect command!", socketChannel);
            }
        } else if (notEmptySplittedCommands.size() == 2) {
            if ("add-friend".equalsIgnoreCase(notEmptySplittedCommands.get(firstArgument))) {
                String friendUsername = notEmptySplittedCommands.get(secondArgument);
                addFriend(friendUsername, socketChannel);
            } else {
                sendMessageToUser("Incorrect command!", socketChannel);
            }
        } else if (notEmptySplittedCommands.size() == 3) {
            if ("payed".equalsIgnoreCase(notEmptySplittedCommands.get(firstArgument))) {
                try {
                    String currentUsername = loggedInSocketChannelsUsers.get(socketChannel);
                    double amount = Double.parseDouble(notEmptySplittedCommands.get(secondArgument));
                    String friendUsername = notEmptySplittedCommands.get(thirdArgument);
                    payed(currentUsername, amount, friendUsername, socketChannel);
                } catch (NumberFormatException e) {
                    sendMessageToUser("Amount of money is not real number", socketChannel);
                }
            } else if ("login".equalsIgnoreCase(notEmptySplittedCommands.get(firstArgument))) {
                String username = notEmptySplittedCommands.get(secondArgument);
                String password = notEmptySplittedCommands.get(thirdArgument);
                logIn(username, password, socketChannel);
            } else if ("register".equalsIgnoreCase(notEmptySplittedCommands.get(firstArgument))) {
                String username = notEmptySplittedCommands.get(secondArgument);
                String password = notEmptySplittedCommands.get(thirdArgument);
                register(username, password, socketChannel);
            } else {
                sendMessageToUser("Incorrect command!", socketChannel);
            }
        } else if (notEmptySplittedCommands.size() >= 4) {
            if ("create-group".equalsIgnoreCase(notEmptySplittedCommands.get(firstArgument))) {
                String groupName = notEmptySplittedCommands.get(secondArgument);
                List<String> users = new ArrayList<>();

                for (int i = 2; i < notEmptySplittedCommands.size(); ++i) {
                    users.add(notEmptySplittedCommands.get(i));
                }
                createGroup(groupName, users, socketChannel);
            } else if ("split".equalsIgnoreCase(notEmptySplittedCommands.get(firstArgument))) {
                try {
                    String currentUsername = loggedInSocketChannelsUsers.get(socketChannel);
                    double amount = Double.parseDouble(notEmptySplittedCommands.get(secondArgument));
                    String friendUsername = notEmptySplittedCommands.get(thirdArgument);
                    StringBuilder reasonForPayment = new StringBuilder();
                    for (int i = 3; i < notEmptySplittedCommands.size(); ++i) {
                        reasonForPayment.append(notEmptySplittedCommands.get(i)).append(" ");
                    }
                    reasonForPayment.deleteCharAt(reasonForPayment.length() - 1);
                    split(currentUsername, friendUsername, amount, reasonForPayment.toString(), socketChannel);
                } catch (NumberFormatException e) {
                    sendMessageToUser("Amount of money is not real number", socketChannel);
                }
            } else if ("split-group".equalsIgnoreCase(notEmptySplittedCommands.get(firstArgument))) {
                try {
                    String currentUsername = loggedInSocketChannelsUsers.get(socketChannel);
                    double amount = Double.parseDouble(notEmptySplittedCommands.get(secondArgument));
                    String groupName = notEmptySplittedCommands.get(thirdArgument);
                    StringBuilder reasonForPayment = new StringBuilder();
                    for (int i = 3; i < notEmptySplittedCommands.size(); ++i) {
                        reasonForPayment.append(notEmptySplittedCommands.get(i)).append(" ");
                    }
                    reasonForPayment.deleteCharAt(reasonForPayment.length() - 1);
                    splitGroup(currentUsername, groupName, amount, reasonForPayment.toString(), socketChannel);
                } catch (NumberFormatException e) {
                    sendMessageToUser("Amount of money is not real number", socketChannel);
                }
            } else if ("payed".equalsIgnoreCase(notEmptySplittedCommands.get(firstArgument))) {
                try {
                    String currentUsername = loggedInSocketChannelsUsers.get(socketChannel);
                    double amount = Double.parseDouble(notEmptySplittedCommands.get(secondArgument));
                    String groupName = notEmptySplittedCommands.get(thirdArgument);
                    String friendUsername = notEmptySplittedCommands.get(fourthArgument);
                    payed(currentUsername, amount, groupName, friendUsername, socketChannel);
                } catch (NumberFormatException e) {
                    sendMessageToUser("Amount of money is not real number", socketChannel);
                }
            } else {
                sendMessageToUser("Incorrect command!", socketChannel);
            }
        }
    }

    public static void main(String[] args) {
        boolean isServersFirstStart = true;

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Is the server started for the first time? (yes/no): ");
            String isServerStartedForTheFirstTime = scanner.nextLine();

            boolean flag = true;
            while (flag) {
                if (isServerStartedForTheFirstTime.equalsIgnoreCase("yes")) {
                    isServersFirstStart = true;
                    flag = false;
                } else if (isServerStartedForTheFirstTime.equalsIgnoreCase("no")) {
                    isServersFirstStart = false;
                    flag = false;
                }
            }

        }

        try (SplitWiseServer server = new SplitWiseServer(isServersFirstStart)) {
            System.out.println("The server has started.");
            server.start();
        } catch (Exception e) {
            System.out.println("An error has occurred!");
        }
    }

    private void getStatus(SocketChannel socketChannel) throws IOException {
        sendMessageToUser(getFriendsStatus(socketChannel) + "\n\n" + getGroupsStatus(socketChannel), socketChannel);
    }

    private String getFriendsStatus(SocketChannel socketChannel) {
        String currentUsername = loggedInSocketChannelsUsers.get(socketChannel);
        StringBuilder message = new StringBuilder();
        message.append("Friends:");
        for (String friend : usersFriends.get(currentUsername)) {
            FriendsCouple friendsCouple = new FriendsCouple(currentUsername, friend);
            Double debt = friendsDebts.get(friendsCouple);

            if (debt.compareTo(0.0) < 0) {
                if (friendsCouple.getUsername1().equals(currentUsername)) {
                    message.append("\n* ").append(friend).append(": You owe ").append(-debt).append(" LV");
                } else {
                    message.append("\n* ").append(friend).append(": Owes you ").append(-debt).append(" LV");
                }
            } else if (debt.compareTo(0.0) > 0) {
                if (friendsCouple.getUsername1().equals(currentUsername)) {
                    message.append("\n* ").append(friend).append(": Owes you ").append(debt).append(" LV");
                } else {
                    message.append("\n* ").append(friend).append(": You owe ").append(debt).append(" LV");
                }
            } else {
                message.append("\n* ").append(friend).append(": You are clear.");
            }
        }

        return message.toString();
    }

    private String getGroupsStatus(SocketChannel socketChannel) {
        String currentUsername = loggedInSocketChannelsUsers.get(socketChannel);
        StringBuilder message = new StringBuilder();
        message.append("Groups:");
        for (FriendsGroup group : usersGroups.get(currentUsername)) {

            List<String> usernamesInTheGroup = group.getFriendsUsernames();
            message.append("\n* ").append(group.getGroupName());

            for (String friendInTheGroupUsername : usernamesInTheGroup) {
                if (!currentUsername.equals(friendInTheGroupUsername)) {
                    FriendsCouple friendsCouple = new FriendsCouple(currentUsername, friendInTheGroupUsername);
                    Double debt = groupsDebts.get(group).get(friendsCouple);

                    if (debt.compareTo(0.0) < 0) {
                        if (friendsCouple.getUsername1().equals(currentUsername)) {
                            message.append("\n - ").append(friendInTheGroupUsername).
                                    append(": You owe ").append(-debt).append(" LV");
                        } else {
                            message.append("\n - ").append(friendInTheGroupUsername).
                                    append(": Owes you ").append(-debt).append(" LV");
                        }
                    } else if (debt.compareTo(0.0) > 0) {
                        if (friendsCouple.getUsername1().equals(currentUsername)) {
                            message.append("\n - ").append(friendInTheGroupUsername).
                                    append(": Owes you ").append(debt).append(" LV");
                        } else {
                            message.append("\n - ").append(friendInTheGroupUsername).
                                    append(": You owe ").append(debt).append(" LV");
                        }
                    } else {
                        message.append("\n - ").append(friendInTheGroupUsername).append(": You are clear.");
                    }
                }
            }
        }

        return message.toString();
    }

    private void addFriend(String friendUsername, SocketChannel socketChannel) throws IOException {
        if (!isUsernameRegistered(friendUsername)) {
            sendMessageToUser("There is no registered user with username <" +
                    friendUsername + ">!", socketChannel);
            return;
        }

        String currentUsername = loggedInSocketChannelsUsers.get(socketChannel);

        if (currentUsername.equals(friendUsername)) {
            sendMessageToUser("You can not add yourself in your own friend list.", socketChannel);
            return;
        }

        boolean flag = usersFriends.get(currentUsername).add(friendUsername);
        if (!flag) {
            sendMessageToUser("The user <" + friendUsername +
                    "> is already in your friend list.", socketChannel);
        } else {
            usersFriends.get(friendUsername).add(currentUsername);
            friendsDebts.put(new FriendsCouple(currentUsername, friendUsername), 0.0);
            sendMessageToUser("User <" + friendUsername +
                    "> is successfully added in your friend list.", socketChannel);
            try (FileWriter usersFriendsWriter = new FileWriter("usersFriends.txt");
                 FileWriter friendsDebtsWriter = new FileWriter("friendsDebts.txt")) {
                saveUsersFriendsToJsonFile(usersFriendsWriter);
                saveFriendsDebtsToJsonFile(friendsDebtsWriter);
            }

            if (isLoggedIn(friendUsername)) {
                SocketChannel friendsSocketChannel = getUsernamesSocketChannel(friendUsername);
                sendMessageToUser("User <" + currentUsername +
                        "> added you as a friend - you are now friends.", friendsSocketChannel);
            }
        }
    }

    private void createGroup(String groupName, List<String> users, SocketChannel socketChannel) throws IOException {
        if (areThereUnregisteredUsers(users, socketChannel)) {
            return;
        }

        String currentUsername = loggedInSocketChannelsUsers.get(socketChannel);
        List<String> allUsersInTheGroup = new ArrayList<>(users);
        allUsersInTheGroup.add(currentUsername);

        if (areThereUsersThatAreNotFriends(allUsersInTheGroup, socketChannel)) {
            return;
        }

        FriendsGroup newGroup = new FriendsGroup(groupName, allUsersInTheGroup);

        if (groupsDebts.containsKey(newGroup)) {
            sendMessageToUser("This group already exists.", socketChannel);
            return;
        }

        for (String user : allUsersInTheGroup) {
            usersGroups.get(user).add(newGroup);
        }

        groupsDebts.put(newGroup, new HashMap<>());

        for (int i = 0; i < allUsersInTheGroup.size(); ++i) {
            for (int j = i + 1; j < allUsersInTheGroup.size(); ++j) {
                String username1 = allUsersInTheGroup.get(i);
                String username2 = allUsersInTheGroup.get(j);
                groupsDebts.get(newGroup).put(new FriendsCouple(username1, username2), 0.0);
            }
        }

        sendMessageToUser("The group has been successfully created.", socketChannel);
        try (FileWriter usersGroupsWriter = new FileWriter("usersGroups.txt");
             FileWriter groupsDebtsWriter = new FileWriter("groupsDebts.txt")) {
            saveUsersGroupsToJsonFile(usersGroupsWriter);
            saveGroupsDebtsToJsonFile(groupsDebtsWriter);
        }
    }

    private boolean areThereUnregisteredUsers(List<String> users, SocketChannel socketChannel) throws IOException {
        List<String> unregisteredUsers = new ArrayList<>();
        for (String user : users) {
            if (!isUsernameRegistered(user)) {
                unregisteredUsers.add(user);
            }
        }

        if (unregisteredUsers.isEmpty()) {
            return false;
        } else {
            sendMessageToUser("The group was not created. " +
                    "There are no registered users with these usernames <"
                    + unregisteredUsers + ">!", socketChannel);
            return true;
        }
    }

    private boolean areThereUsersThatAreNotFriends(
            List<String> usernames, SocketChannel socketChannel) throws IOException {

        List<String> areNotFriends = new ArrayList<>();
        for (int i = 0; i < usernames.size(); ++i) {
            for (int j = i + 1; j < usernames.size(); ++j) {
                if (!areFriends(usernames.get(i), usernames.get(j))) {
                    areNotFriends.add("(" + usernames.get(i) + ", " + usernames.get(j) + ")");
                }
            }
        }

        if (areNotFriends.isEmpty()) {
            return false;
        } else {
            sendMessageToUser("The group was not created. Users <"
                    + areNotFriends + "> are not friends!", socketChannel);
            return true;
        }
    }

    private void logIn(String username, String password, SocketChannel socketChannel) throws IOException {
        if (!registeredUsers.containsKey(username)) {
            sendMessageToUser("There is no registered user with this name!", socketChannel);
        } else if (!password.equals(registeredUsers.get(username))) {
            if (isLoggedIn(socketChannel)) {
                sendMessageToUser("You are already logged in.", socketChannel);
            } else {
                sendMessageToUser("Wrong password!", socketChannel);
            }
        } else {
            if (isLoggedIn(socketChannel)) {
                sendMessageToUser("You are already logged in.", socketChannel);
            } else {
                loggedInSocketChannelsUsers.put(socketChannel, username);
                loggedInUsersSocketChannels.put(username, socketChannel);
                sendMessageToUser("Successful login!", socketChannel);
                sendMessageToUser("*** Notifications ***\n" + getUsersUnsentHistory(username), socketChannel);
                usersHistory.get(username).markTheWholeHistoryAsSent();
                try (FileWriter usersHistoryWriter = new FileWriter("usersHistory.txt")) {
                    saveUsersHistoryToJsonFile(usersHistoryWriter);
                }
            }
        }
    }

    private void register(String username, String password, SocketChannel socketChannel) throws IOException {
        if (registeredUsers.containsKey(username)) {
            sendMessageToUser("The username already exists!", socketChannel);
        } else {
            registeredUsers.put(username, password);
            usersFriends.put(username, new HashSet<>());
            usersGroups.put(username, new ArrayList<>());
            usersHistory.put(username, new UserHistory());
            sendMessageToUser("Successful registration.", socketChannel);
            try (FileWriter registeredUsersWriter = new FileWriter("registeredUsers.txt")) {
                saveRegisteredUsersToJsonFile(registeredUsersWriter);
            }
            executeCommand("login " + username + " " + password, socketChannel);
        }
    }

    private void split(String currentUsername, String friendUsername, double amount,
                       String reasonForPayment, SocketChannel socketChannel) throws IOException {

        if (!areFriends(currentUsername, friendUsername)) {
            sendMessageToUser("User <" + friendUsername + "> is not in your friend list!", socketChannel);
            return;
        }

        FriendsCouple friendsCouple = new FriendsCouple(currentUsername, friendUsername);
        Double currentDebt = friendsDebts.get(friendsCouple);

        double halfAmount = amount / 2;

        amount = cutDouble(amount);
        halfAmount = cutDouble(halfAmount);

        String messageToCurrentUser;
        String messageToFriend;

        if (friendsCouple.getUsername1().equals(currentUsername)) {
            friendsDebts.put(friendsCouple, currentDebt + halfAmount);
            Double newDebt = friendsDebts.get(friendsCouple);

            if (newDebt.compareTo(0.0) < 0) {
                messageToCurrentUser = "You owe " + (-newDebt) +
                        " LV to <" + friendUsername + "> [" + reasonForPayment + "].";
                messageToFriend = "<" + currentUsername +
                        "> owes you " + (-newDebt) + " LV [" + reasonForPayment + "].";
            } else if (newDebt.compareTo(0.0) > 0) {
                messageToCurrentUser = "<" + friendUsername +
                        "> owes you " + newDebt + " LV [" + reasonForPayment + "].";
                messageToFriend = "You owe " + newDebt +
                        " LV to <" + currentUsername + "> [" + reasonForPayment + "].";
            } else {
                messageToCurrentUser = "You are clear with <" +
                        friendUsername + "> [" + reasonForPayment + "].";
                messageToFriend = "You are clear with <" +
                        currentUsername + "> [" + reasonForPayment + "].";
            }
        } else {
            friendsDebts.put(friendsCouple, currentDebt + (-halfAmount));
            Double newDebt = friendsDebts.get(friendsCouple);

            if (newDebt.compareTo(0.0) < 0) {
                messageToCurrentUser = "<" + friendUsername +
                        "> owes you " + (-newDebt) + " LV [" + reasonForPayment + "].";
                messageToFriend = "You owe " + (-newDebt) +
                        " LV to <" + currentUsername + "> [" + reasonForPayment + "].";
            } else if (newDebt.compareTo(0.0) > 0) {
                messageToCurrentUser = "You owe " + newDebt +
                        " LV to <" + friendUsername + "> [" + reasonForPayment + "].";
                messageToFriend = "<" + currentUsername +
                        "> owes you " + newDebt + " LV [" + reasonForPayment + "].";
            } else {
                messageToCurrentUser = "You are clear with <" +
                        friendUsername + "> [" + reasonForPayment + "].";
                messageToFriend = "You are clear with <" +
                        currentUsername + "> [" + reasonForPayment + "].";
            }
        }
        sendMessageToUser("Splitted " + amount + " LV between you and <" +
                friendUsername + ">. Reason for payment <" + reasonForPayment + ">.", socketChannel);
        try (FileWriter friendsDebtsWriter = new FileWriter("friendsDebts.txt")) {
            saveFriendsDebtsToJsonFile(friendsDebtsWriter);
        }
        sendMessageToUser("Current status: " + messageToCurrentUser, socketChannel);
        usersHistory.get(currentUsername).addToHistory(messageToCurrentUser, true);

        if (isLoggedIn(friendUsername)) {
            SocketChannel friendsSocketChannel = getUsernamesSocketChannel(friendUsername);
            sendMessageToUser(messageToFriend, friendsSocketChannel);
            usersHistory.get(friendUsername).addToHistory(messageToFriend, true);
            try (FileWriter usersHistoryWriter = new FileWriter("usersHistory.txt")) {
                saveUsersHistoryToJsonFile(usersHistoryWriter);
            }
        } else {
            usersHistory.get(friendUsername).addToHistory(messageToFriend, false);
            try (FileWriter usersHistoryWriter = new FileWriter("usersHistory.txt")) {
                saveUsersHistoryToJsonFile(usersHistoryWriter);
            }
        }
    }

    private void splitGroup(String currentUsername, String groupName, double amount,
                            String reasonForPayment, SocketChannel socketChannel) throws IOException {

        FriendsGroup friendsGroup = findGroupOfUser(currentUsername, groupName);

        if (friendsGroup == null) {
            sendMessageToUser("You do not have group with name <" + groupName + ">!", socketChannel);
            return;
        }

        double equalPartOfTheAmount = amount / friendsGroup.getFriendsUsernames().size();

        amount = cutDouble(amount);
        equalPartOfTheAmount = cutDouble(equalPartOfTheAmount);

        List<String> usernamesInTheGroup = friendsGroup.getFriendsUsernames();

        sendMessageToUser("Splitted " + amount + " LV between you and group <" +
                groupName + ">. Reason for payment <" + reasonForPayment + ">.", socketChannel);

        String messageToCurrentUser;
        String messageToFriend;

        for (String friendInTheGroupUsername : usernamesInTheGroup) {
            if (!currentUsername.equals(friendInTheGroupUsername)) {
                FriendsCouple friendsCouple = new FriendsCouple(currentUsername, friendInTheGroupUsername);
                Double currentDebt = groupsDebts.get(friendsGroup).get(friendsCouple);

                if (friendsCouple.getUsername1().equals(currentUsername)) {
                    groupsDebts.get(friendsGroup).put(friendsCouple, currentDebt + equalPartOfTheAmount);
                    Double newDebt = groupsDebts.get(friendsGroup).get(friendsCouple);

                    if (newDebt.compareTo(0.0) < 0) {
                        messageToCurrentUser = "You owe " + (-newDebt) + " LV to <" + friendInTheGroupUsername +
                                "> in the group <" + groupName + "> [" + reasonForPayment + "].";
                        messageToFriend = "<" + currentUsername + "> owes you " + (-newDebt) +
                                " LV in the group <" + groupName + "> [" + reasonForPayment + "].";
                    } else if (newDebt.compareTo(0.0) > 0) {
                        messageToCurrentUser = "<" + friendInTheGroupUsername + "> owes you " + newDebt +
                                " LV in the group <" + groupName + "> [" + reasonForPayment + "].";
                        messageToFriend = "You owe " + newDebt + " LV to <" + currentUsername +
                                "> in the group <" + groupName + "> [" + reasonForPayment + "].";
                    } else {
                        messageToCurrentUser = "You are clear with <" + friendInTheGroupUsername +
                                "> in the group <" + groupName + "> [" + reasonForPayment + "].";
                        messageToFriend = "You are clear with <" + currentUsername +
                                "> in the group <" + groupName + "> [" + reasonForPayment + "].";
                    }
                } else {
                    groupsDebts.get(friendsGroup).put(friendsCouple, currentDebt + (-equalPartOfTheAmount));
                    Double newDebt = groupsDebts.get(friendsGroup).get(friendsCouple);

                    if (newDebt.compareTo(0.0) < 0) {
                        messageToCurrentUser = "<" + friendInTheGroupUsername + "> owes you " + (-newDebt) +
                                " LV in the group <" + groupName + "> [" + reasonForPayment + "].";
                        messageToFriend = "You owe " + (-newDebt) + " LV to <" + currentUsername +
                                "> in the group <" + groupName + "> [" + reasonForPayment + "].";
                    } else if (newDebt.compareTo(0.0) > 0) {
                        messageToCurrentUser = "You owe " + newDebt + " LV to <" + friendInTheGroupUsername +
                                "> in the group <" + groupName + "> [" + reasonForPayment + "].";
                        messageToFriend = "<" + currentUsername + "> owes you " + newDebt +
                                " LV in the group <" + groupName + "> [" + reasonForPayment + "].";
                    } else {
                        messageToCurrentUser = "You are clear with <" + friendInTheGroupUsername +
                                "> in the group <" + groupName + "> [" + reasonForPayment + "].";
                        messageToFriend = "You are clear with <" + currentUsername +
                                "> in the group <" + groupName + "> [" + reasonForPayment + "].";
                    }
                }
                sendMessageToUser("Current status in the group: " + messageToCurrentUser, socketChannel);
                usersHistory.get(currentUsername).addToHistory(messageToCurrentUser, true);

                if (isLoggedIn(friendInTheGroupUsername)) {
                    SocketChannel friendsSocketChannel = getUsernamesSocketChannel(friendInTheGroupUsername);
                    sendMessageToUser(messageToFriend, friendsSocketChannel);
                    usersHistory.get(friendInTheGroupUsername).addToHistory(messageToFriend, true);
                } else {
                    usersHistory.get(friendInTheGroupUsername).addToHistory(messageToFriend, false);
                }
            }
        }
        try (FileWriter groupsDebtsWriter = new FileWriter("groupsDebts.txt");
             FileWriter usersHistoryWriter = new FileWriter("usersHistory.txt")) {
            saveGroupsDebtsToJsonFile(groupsDebtsWriter);
            saveUsersHistoryToJsonFile(usersHistoryWriter);
        }
    }

    private double cutDouble(double number) {
        BigDecimal bigDecimalAmount = new BigDecimal(number);
        number = bigDecimalAmount.setScale(
                NUMBER_OF_DIGITS_AFTER_DECIMAL_POINT, RoundingMode.HALF_UP).doubleValue();

        return number;
    }

    private FriendsGroup findGroupOfUser(String username, String groupName) {
        List<FriendsGroup> currentUserAllGroups = usersGroups.get(username);

        FriendsGroup friendsGroup = null;
        for (FriendsGroup group : currentUserAllGroups) {
            if (groupName.equals(group.getGroupName())) {
                friendsGroup = group;
                break;
            }
        }

        return friendsGroup;
    }

    private void payed(String currentUsername, double amount,
                       String friendUsername, SocketChannel socketChannel) throws IOException {

        if (!areFriends(currentUsername, friendUsername)) {
            sendMessageToUser("User <" + friendUsername + "> is not in your friend list!", socketChannel);
            return;
        }

        FriendsCouple friendsCouple = new FriendsCouple(currentUsername, friendUsername);
        Double currentDebt = friendsDebts.get(friendsCouple);

        amount = cutDouble(amount);

        String messageToCurrentUserAboutCurrentStatus;

        if (friendsCouple.getUsername1().equals(currentUsername)) {
            friendsDebts.put(friendsCouple, currentDebt - amount);
            Double newDebt = friendsDebts.get(friendsCouple);

            if (newDebt.compareTo(0.0) < 0) {
                messageToCurrentUserAboutCurrentStatus = "Current status: You owe " +
                        (-newDebt) + " LV to <" + friendUsername + ">.";
            } else if (newDebt.compareTo(0.0) > 0) {
                messageToCurrentUserAboutCurrentStatus = "Current status: <" +
                        friendUsername + "> owes you " + newDebt + " LV.";
            } else {
                messageToCurrentUserAboutCurrentStatus = "Current status: You are clear with <"
                        + friendUsername + ">.";
            }
        } else {
            friendsDebts.put(friendsCouple, currentDebt + amount);
            Double newDebt = friendsDebts.get(friendsCouple);

            if (newDebt.compareTo(0.0) < 0) {
                messageToCurrentUserAboutCurrentStatus = "Current status: <" +
                        friendUsername + "> owes you " + (-newDebt) + " LV.";
            } else if (newDebt.compareTo(0.0) > 0) {
                messageToCurrentUserAboutCurrentStatus = "Current status: You owe "
                        + newDebt + " LV to <" + friendUsername + ">.";
            } else {
                messageToCurrentUserAboutCurrentStatus = "Current status: You are clear with <"
                        + friendUsername + ">.";
            }
        }
        String messageToCurrentUser = "<" + friendUsername + "> payed you " + amount + " LV.";
        sendMessageToUser(messageToCurrentUser, socketChannel);
        try (FileWriter friendsDebtsWriter = new FileWriter("friendsDebts.txt")) {
            saveFriendsDebtsToJsonFile(friendsDebtsWriter);
        }
        usersHistory.get(currentUsername).addToHistory(messageToCurrentUser, true);
        sendMessageToUser(messageToCurrentUserAboutCurrentStatus, socketChannel);

        String messageToFriend = "<" + currentUsername + "> approved your payment " + amount + " LV.";

        if (isLoggedIn(friendUsername)) {
            SocketChannel friendsSocketChannel = getUsernamesSocketChannel(friendUsername);
            sendMessageToUser(messageToFriend, friendsSocketChannel);
            usersHistory.get(friendUsername).addToHistory(messageToFriend, true);
            try (FileWriter usersHistoryWriter = new FileWriter("usersHistory.txt")) {
                saveUsersHistoryToJsonFile(usersHistoryWriter);
            }
        } else {
            usersHistory.get(friendUsername).addToHistory(messageToFriend, false);
            try (FileWriter usersHistoryWriter = new FileWriter("usersHistory.txt")) {
                saveUsersHistoryToJsonFile(usersHistoryWriter);
            }
        }
    }

    private void payed(String currentUsername, double amount, String groupName,
                       String friendUsername, SocketChannel socketChannel) throws IOException {

        FriendsGroup friendsGroup = findGroupOfUser(currentUsername, groupName);

        if (friendsGroup == null) {
            sendMessageToUser("You do not have group with name <" + groupName + ">!", socketChannel);
            return;
        }

        FriendsCouple friendsCouple = new FriendsCouple(currentUsername, friendUsername);

        if (!groupsDebts.get(friendsGroup).containsKey(friendsCouple)) {
            sendMessageToUser("User <" + friendUsername +
                    "< is not in group <" + groupName + ">.", socketChannel);
            return;
        }

        Double currentDebt = groupsDebts.get(friendsGroup).get(friendsCouple);

        amount = cutDouble(amount);

        String messageToCurrentUserAboutCurrentStatus;

        if (friendsCouple.getUsername1().equals(currentUsername)) {
            groupsDebts.get(friendsGroup).put(friendsCouple, currentDebt - amount);
            Double newDebt = groupsDebts.get(friendsGroup).get(friendsCouple);

            if (newDebt.compareTo(0.0) < 0) {
                messageToCurrentUserAboutCurrentStatus = "Current status: You owe " + (-newDebt) +
                        " LV to <" + friendUsername + "> for the group <" + groupName + ">.";
            } else if (newDebt.compareTo(0.0) > 0) {
                messageToCurrentUserAboutCurrentStatus = "Current status: <" + friendUsername +
                        "> owes you " + newDebt + " LV for the group <" + groupName + ">.";
            } else {
                messageToCurrentUserAboutCurrentStatus = "Current status: You are clear with <"
                        + friendUsername + "> in the group <" + groupName + ">.";
            }
        } else {
            groupsDebts.get(friendsGroup).put(friendsCouple, currentDebt + amount);
            Double newDebt = groupsDebts.get(friendsGroup).get(friendsCouple);

            if (newDebt.compareTo(0.0) < 0) {
                messageToCurrentUserAboutCurrentStatus = "Current status: <" + friendUsername +
                        "> owes you " + (-newDebt) + " LV for the group <" + groupName + ">.";
            } else if (newDebt.compareTo(0.0) > 0) {
                messageToCurrentUserAboutCurrentStatus = "Current status: You owe " + newDebt +
                        " LV to <" + friendUsername + "> for the group <" + groupName + ">.";
            } else {
                messageToCurrentUserAboutCurrentStatus = "Current status: You are clear with <"
                        + friendUsername + "> in the group <" + groupName + ">.";
            }
        }

        String messageToCurrentUser = "<" + friendUsername + "> payed you " +
                amount + " LV for the group <" + groupName + ">.";
        try (FileWriter groupsDebtsWriter = new FileWriter("groupsDebts.txt")) {
            saveGroupsDebtsToJsonFile(groupsDebtsWriter);
        }
        sendMessageToUser(messageToCurrentUser, socketChannel);
        usersHistory.get(currentUsername).addToHistory(messageToCurrentUser, true);
        sendMessageToUser(messageToCurrentUserAboutCurrentStatus, socketChannel);

        String messageToFriend = "<" + currentUsername +
                "> approved your payment " + amount + " LV for the group <" + groupName + ">.";

        if (isLoggedIn(friendUsername)) {
            SocketChannel friendsSocketChannel = getUsernamesSocketChannel(friendUsername);
            sendMessageToUser(messageToFriend, friendsSocketChannel);
            usersHistory.get(friendUsername).addToHistory(messageToFriend, true);
            try (FileWriter usersHistoryWriter = new FileWriter("usersHistory.txt")) {
                saveUsersHistoryToJsonFile(usersHistoryWriter);
            }
        } else {
            usersHistory.get(friendUsername).addToHistory(messageToFriend, false);
            try (FileWriter usersHistory = new FileWriter("usersHistory.txt")) {
                saveUsersHistoryToJsonFile(usersHistory);
            }
        }
    }

    private void getUsersAllHistory(SocketChannel socketChannel) throws IOException {
        String username = loggedInSocketChannelsUsers.get(socketChannel);
        String allHistory = usersHistory.get(username).getAllHistory();
        sendMessageToUser(allHistory, socketChannel);
    }

    private String getUsersUnsentHistory(String username) {
        return usersHistory.get(username).getUnsentHistory();
    }

    private SocketChannel getUsernamesSocketChannel(String username) {
        return loggedInUsersSocketChannels.getOrDefault(username, null);
    }

    private boolean areFriends(String username1, String username2) {
        if ((!isUsernameRegistered(username1)) || (!isUsernameRegistered(username2))) {
            return false;
        } else return usersFriends.get(username1).contains(username2);
    }

    private void sendMessageToUser(String message, SocketChannel socketChannel) throws IOException {
        buffer.clear();
        buffer.put((message + System.lineSeparator()).getBytes());
        buffer.flip();
        socketChannel.write(buffer);
    }

    private boolean isLoggedIn(SocketChannel socketChannel) {
        return loggedInSocketChannelsUsers.containsKey(socketChannel);
    }

    private boolean isLoggedIn(String username) {
        return loggedInUsersSocketChannels.containsKey(username);
    }

    private boolean isUsernameRegistered(String username) {
        return registeredUsers.containsKey(username);
    }

    Map<String, String> getRegisteredUsers() {
        return Collections.unmodifiableMap(registeredUsers);
    }

    private String registeredUsersToJson(Map<String, String> registeredUsers) {
        Gson gson = new Gson();
        return gson.toJson(registeredUsers);
    }

    private void saveRegisteredUsersToJsonFile(Writer registeredUsersFile) throws IOException {
        registeredUsersFile.write(registeredUsersToJson(this.registeredUsers));
        registeredUsersFile.flush();
    }

    private void loadRegisteredUsersFromJson(Reader registeredUsersJson) {
        String json = readMysteryTextInString(registeredUsersJson);
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        this.registeredUsers = gson.fromJson(json, type);
    }

    private String usersFriendsToJson(Map<String, Set<String>> usersFriends) {
        Gson gson = new Gson();
        return gson.toJson(usersFriends);
    }

    private void saveUsersFriendsToJsonFile(Writer usersFriendsFile) throws IOException {
        usersFriendsFile.write(usersFriendsToJson(usersFriends));
        usersFriendsFile.flush();
    }

    private void loadUsersFriendsFromJson(Reader usersFriendsJson) {
        String json = readMysteryTextInString(usersFriendsJson);
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Set<String>>>() {
        }.getType();
        this.usersFriends = gson.fromJson(json, type);
    }

    private String friendsDebtsToJson(Map<FriendsCouple, Double> friendsDebts) {
        Gson gson = new Gson();
        return gson.toJson(friendsDebts);
    }

    private void saveFriendsDebtsToJsonFile(Writer friendsDebtsFile) throws IOException {
        friendsDebtsFile.write(friendsDebtsToJson(friendsDebts));
        friendsDebtsFile.flush();
    }

    private void loadFriendsDebtsFromJason(Reader friendsDebtsJson) {
        String json = readMysteryTextInString(friendsDebtsJson);
        Gson gson = new Gson();
        Type type = new TypeToken<Map<FriendsCouple, Double>>() {
        }.getType();
        this.friendsDebts = gson.fromJson(json, type);
    }

    private String usersGroupsToJson(Map<String, List<FriendsGroup>> usersGroups) {
        Gson gson = new Gson();
        return gson.toJson(usersGroups);
    }

    private void saveUsersGroupsToJsonFile(Writer usersGroupsFile) throws IOException {
        usersGroupsFile.write(usersGroupsToJson(usersGroups));
        usersGroupsFile.flush();
    }

    private void loadUsersGroupsFromJson(Reader usersGroupsJson) {
        String json = readMysteryTextInString(usersGroupsJson);
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, List<FriendsGroup>>>() {
        }.getType();
        this.usersGroups = gson.fromJson(json, type);
    }

    private String groupsDebtsToJson(Map<FriendsGroup, Map<FriendsCouple, Double>> groupsDebts) {
        Gson gson = new Gson();
        return gson.toJson(groupsDebts);
    }

    private void saveGroupsDebtsToJsonFile(Writer groupsDebtsFile) throws IOException {
        groupsDebtsFile.write(groupsDebtsToJson(groupsDebts));
        groupsDebtsFile.flush();
    }

    private void loadGroupsDebtsFromJson(Reader groupsDebtsJson) {
        String json = readMysteryTextInString(groupsDebtsJson);
        Gson gson = new Gson();
        Type type = new TypeToken<Map<FriendsGroup, Map<FriendsCouple, Double>>>() {
        }.getType();
        this.groupsDebts = gson.fromJson(json, type);
    }

    private String usersHistoryToJson(Map<String, UserHistory> usersHistory) {
        Gson gson = new Gson();
        return gson.toJson(usersHistory);
    }

    private void saveUsersHistoryToJsonFile(Writer usersHistoryFile) throws IOException {
        usersHistoryFile.write(usersHistoryToJson(usersHistory));
        usersHistoryFile.flush();
    }

    private void loadUsersHistoryFromJson(Reader usersHistoryJson) {
        String json = readMysteryTextInString(usersHistoryJson);
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, UserHistory>>() {
        }.getType();
        this.usersHistory = gson.fromJson(json, type);
    }

    private String readAllLinesWithStream(BufferedReader reader) {
        return reader.lines()
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String readMysteryTextInString(Reader mysteryText) {
        try (BufferedReader reader = new BufferedReader(mysteryText)) {
            return readAllLinesWithStream(reader);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Can't find the file", e);
        } catch (IOException e) {
            throw new RuntimeException("Problem with the input stream", e);
        }
    }

    @Override
    public void close() throws IOException {
        if (!(serverSocketChannel == null)) {
            serverSocketChannel.close();
        }

        if (!(selector == null)) {
            selector.close();
        }
    }
}