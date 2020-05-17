package bg.sofia.uni.fmi.mjt.splitwise.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;

public class SplitWiseClientListen extends Thread {
    private SocketChannel socketChannel;

    public SplitWiseClientListen(SocketChannel socketChannel) {
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
                String reply = reader.readLine();
                System.out.println(reply);
            }
        } catch (IOException e) {
            System.out.println("There is a problem with the connection!");
        }
    }
}
