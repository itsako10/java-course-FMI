package bg.sofia.uni.fmi.mjt.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

public class ChatServer implements AutoCloseable {
    private static final String SERVER_HOST = "localhost";
    public static final int SERVER_PORT = 8080;
    private static final int BUFFER_SIZE = 1024;
    private Selector selector;
    private ByteBuffer buffer;
    private ServerSocketChannel serverSocketChannel;
    private Map<String, SocketChannel> users = new HashMap<>();

    public ChatServer() throws IOException {
        selector = Selector.open();
        buffer = ByteBuffer.allocate(BUFFER_SIZE);
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
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

        for (String i : splittedCommand) {
            if (!i.equals("")) {
                notEmptySplittedCommands.add(i);
            }
        }

        final int minArgumentsForSendMethod = 3;

        if (notEmptySplittedCommands.size() == 1) {
            if ("disconnect".equalsIgnoreCase(notEmptySplittedCommands.get(0))) {
                String toBeDeleted = " ";
                for (Map.Entry<String, SocketChannel> i : this.users.entrySet()) {
                    if (i.getValue().equals(socketChannel)) {
                        toBeDeleted = i.getKey();
                        break;
                    }
                }
                this.users.remove(toBeDeleted);
                socketChannel.close();
                System.out.println("Disconnected user");
            } else if ("list-users".equalsIgnoreCase(notEmptySplittedCommands.get(0))) {
                StringBuilder allUsersBuilder = new StringBuilder();
                for (String i : users.keySet()) {
                    allUsersBuilder.append(i).append(", ");
                }

                allUsersBuilder.delete(allUsersBuilder.length() - 2, allUsersBuilder.length() - 1);

                buffer.clear();
                buffer.put((allUsersBuilder.toString() + System.lineSeparator()).getBytes());
                buffer.flip();
                socketChannel.write(buffer);

            } else {
                System.out.println("Incorrect command!");
            }
        } else if (notEmptySplittedCommands.size() == 2) {
            if ("nick".equalsIgnoreCase(notEmptySplittedCommands.get(0))) {
                String username = notEmptySplittedCommands.get(1);
                users.put(username, socketChannel);
            } else if ("send-all".equalsIgnoreCase(notEmptySplittedCommands.get(0))) {
                String message = notEmptySplittedCommands.get(1);
                for (Map.Entry<String, SocketChannel> i : this.users.entrySet()) {
                    executeCommand("send " + i.getKey() + " " + message, socketChannel);
                }
            } else {
                System.out.println("Incorrect command!");
            }
        } else if (notEmptySplittedCommands.size() >= 2) {
            if ("send-all".equalsIgnoreCase(notEmptySplittedCommands.get(0))) {
                StringBuilder message = new StringBuilder();
                for (int i = 1; i < notEmptySplittedCommands.size(); ++i) {
                    message.append(notEmptySplittedCommands.get(i)).append(" ");
                }

                for (Map.Entry<String, SocketChannel> i : this.users.entrySet()) {
                    executeCommand("send " + i.getKey() + " " + message, socketChannel);
                }
            } else if (notEmptySplittedCommands.size() >= minArgumentsForSendMethod) {
                if ("send".equalsIgnoreCase(notEmptySplittedCommands.get(0))) {
                    String toUsername = notEmptySplittedCommands.get(1);
                    StringBuilder message = new StringBuilder();
                    for (int i = 2; i < notEmptySplittedCommands.size(); ++i) {
                        message.append(notEmptySplittedCommands.get(i)).append(" ");
                    }

                    SocketChannel toUser = this.users.get(toUsername);

                    if (toUser == null) {
                        buffer.clear();
                        buffer.put(("User [" + toUsername + "] seems to be offline" +
                                System.lineSeparator()).getBytes());
                        buffer.flip();
                        socketChannel.write(buffer);
                        return;
                    }

                    String fromUser = " ";
                    for (Map.Entry<String, SocketChannel> i : this.users.entrySet()) {
                        if (i.getValue().equals(socketChannel)) {
                            fromUser = i.getKey();
                            break;
                        }
                    }

                    buffer.clear();
                    buffer.put((fromUser + ": " + message + System.lineSeparator()).getBytes());
                    buffer.flip();
                    toUser.write(buffer);
                }
            } else {
                System.out.println("Incorrect command!");
            }
        }
    }

    @Override
    public void close() throws Exception {
        serverSocketChannel.close();
        selector.close();
    }

    public static void main(String[] args) throws IOException {
        try (ChatServer server = new ChatServer()) {
            server.start();
        } catch (Exception e) {
            System.out.println("An error has occured");
            e.printStackTrace();
        }
    }
}