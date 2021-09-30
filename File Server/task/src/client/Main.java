package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {

        try {

            String address = "127.0.0.1";
            int port = 23456;

            Socket socket = new Socket(InetAddress.getByName(address), port);

            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            System.out.println("Client started!");
            System.out.println("Sent: Give me everything you have!");
            output.writeUTF("Give me everything you have!");
            System.out.println("Received: " + input.readUTF());


        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }
}
