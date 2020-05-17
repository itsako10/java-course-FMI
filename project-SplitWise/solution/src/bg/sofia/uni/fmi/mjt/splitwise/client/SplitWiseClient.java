package bg.sofia.uni.fmi.mjt.splitwise.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SplitWiseClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {

        try (SocketChannel socketChannel = SocketChannel.open();
             PrintWriter writer = new PrintWriter(Channels.newWriter(socketChannel, "UTF-8"), true);
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            System.out.println("Connected to the server.");

            SplitWiseClientListen reader = new SplitWiseClientListen(socketChannel);
            reader.setDaemon(true);
            reader.start();

            while (true) {
                System.out.println("Enter command: ");
                String command = scanner.nextLine();

                String[] splittedCommand = command.strip().split(" ");
                List<String> notEmptySplittedCommands = new ArrayList<>();

                for (String i : splittedCommand) {
                    if (!i.isBlank()) {
                        notEmptySplittedCommands.add(i);
                    }
                }

                final int firstArgument = 0;

                if (notEmptySplittedCommands.size() == 1) {
                    if ("logout".equalsIgnoreCase(notEmptySplittedCommands.get(firstArgument))) {
                        System.out.println("Logged out");
                        writer.println(command);
                        break;
                    } else if ("get-status".equalsIgnoreCase(notEmptySplittedCommands.get(firstArgument))) {
                        writer.println(command);
                    } else if ("get-all-history".equalsIgnoreCase(notEmptySplittedCommands.get(firstArgument))) {
                        writer.println(command);
                    } else {
                        System.out.println("Incorrect command!");
                    }
                } else if (notEmptySplittedCommands.size() == 2) {
                    if ("add-friend".equalsIgnoreCase(notEmptySplittedCommands.get(firstArgument))) {
                        writer.println(command);
                    } else {
                        System.out.println("Incorrect command!");
                    }
                } else if (notEmptySplittedCommands.size() == 3) {
                    if ("payed".equalsIgnoreCase(notEmptySplittedCommands.get(firstArgument))) {
                        writer.println(command);
                    } else if ("login".equalsIgnoreCase(notEmptySplittedCommands.get(firstArgument))) {
                        writer.println(command);
                    } else if ("register".equalsIgnoreCase(notEmptySplittedCommands.get(firstArgument))) {
                        writer.println(command);
                    } else {
                        System.out.println("Incorrect command!");
                    }
                } else if (notEmptySplittedCommands.size() >= 4) {
                    if ("create-group".equalsIgnoreCase(notEmptySplittedCommands.get(firstArgument))) {
                        writer.println(command);
                    } else if ("split".equalsIgnoreCase(notEmptySplittedCommands.get(firstArgument))) {
                        writer.println(command);
                    } else if ("split-group".equalsIgnoreCase(notEmptySplittedCommands.get(firstArgument))) {
                        writer.println(command);
                    } else if ("payed".equalsIgnoreCase(notEmptySplittedCommands.get(firstArgument))) {
                        writer.println(command);
                    } else {
                        System.out.println("Incorrect command!");
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Not connected to the server!");
        }
    }
}