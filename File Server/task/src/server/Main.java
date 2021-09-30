package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {

        try {

            String address = "127.0.0.1";
            int port = 23456;

            ServerSocket server = new ServerSocket(port, 50, InetAddress.getByName(address));

            System.out.println("Server started!");

            Socket socket = server.accept();

            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output  = new DataOutputStream(socket.getOutputStream());

            System.out.println("Received: " + input.readUTF());
            output.writeUTF("All files were sent!");
            System.out.println("Sent: All files were sent!");


        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }
}