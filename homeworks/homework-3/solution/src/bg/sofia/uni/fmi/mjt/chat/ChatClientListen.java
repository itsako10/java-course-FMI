package bg.sofia.uni.fmi.mjt.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;

public class ChatClientListen extends Thread {
    private SocketChannel socketChannel;

    public ChatClientListen(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(Channels.newReader(socketChannel, "UTF-8"))) {
            while (true) {
                if (!socketChannel.isOpen()) {
                    System.out.println("The client channel is closed. There is no need to listen anymore.");
                    return;
                }
                String reply = reader.readLine(); // read the response from the server
                System.out.println("\n" + reply);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}