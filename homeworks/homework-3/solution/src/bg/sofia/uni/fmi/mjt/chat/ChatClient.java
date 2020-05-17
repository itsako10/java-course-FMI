package bg.sofia.uni.fmi.mjt.chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//I want to tell that I have used these sources of information and code to help me do my homework:
//https://github.com/fmi/java-course/tree/master/11-network/snippets/echoclientserver/nio
//https://gist.github.com/ialidzhikov/04df81ae7da7fbefc561bf4608028152
//https://github.com/fmi/java-course/tree/mjt-2018-2019/09-network/lab/solution

public class ChatClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {

        try (SocketChannel socketChannel = SocketChannel.open();
             PrintWriter writer = new PrintWriter(Channels.newWriter(socketChannel, "UTF-8"), true);
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            System.out.println("Connected to the server.");

            ChatClientListen reader = new ChatClientListen(socketChannel);
            reader.setDaemon(true);
            reader.start();

            while (true) {
                System.out.print("Enter command: ");
                String command = scanner.nextLine();

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
                        System.out.println("Disconnected");
                        writer.println(command);
                        break;
                    } else if ("list-users".equalsIgnoreCase(notEmptySplittedCommands.get(0))) {
                        writer.println(command);
                    } else {
                        System.out.println("Incorrect command!");
                    }
                } else if (notEmptySplittedCommands.size() == 2) {
                    if ("nick".equalsIgnoreCase(notEmptySplittedCommands.get(0))) {
                        writer.println(command);
                    } else if ("send-all".equalsIgnoreCase(notEmptySplittedCommands.get(0))) {
                        writer.println(command);
                    } else {
                        System.out.println("Incorrect command!");
                    }
                } else if (notEmptySplittedCommands.size() >= 2) {
                    if ("send-all".equalsIgnoreCase(notEmptySplittedCommands.get(0))) {
                        writer.println(command);
                    } else if (notEmptySplittedCommands.size() >= minArgumentsForSendMethod) {
                        if ("send".equalsIgnoreCase(notEmptySplittedCommands.get(0))) {
                            writer.println(command);
                        } else {
                            System.out.println("Incorrect command!");
                        }
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}