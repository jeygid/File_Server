package server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {

        try {

            String address = "127.0.0.1";
            int port = 23459;

            ServerSocket server = new ServerSocket(port, 50, InetAddress.getByName(address));

            Socket socket = server.accept();
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            boolean exit = false;
            while (!exit) {

                String command = input.readUTF();

                if (command.equals("EXIT")) {
                    exit = true;
                    continue;
                }


                if (command.matches("GET [\\S]+")) {

                    String fileName = command.replace("GET ", "");

                    File file = new File(fileName);

                    if (file.exists()) {
                        String content = new String(Files.readAllBytes(Paths.get(fileName)));
                        output.writeUTF("200 " + content);
                    } else {
                        output.writeUTF("404");
                    }

                    continue;
                }

                if (command.matches("PUT [\\S]+ [\\S ]+")) {

                    String[] commandArray = command.split(" ");
                    String fileName = commandArray[1];
                    String text = command.replaceAll("PUT [\\S]+ ", "");

                    File file = new File(fileName);

                    if (file.exists()) {
                        output.writeUTF("403");
                    } else {
                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.write(text);
                        fileWriter.flush();
                        output.writeUTF("200");
                    }

                    continue;
                }

                if (command.matches("DELETE [\\S]+")) {

                    String fileName = command.replace("DELETE ", "");

                    File file = new File(fileName);

                    if (!file.exists()) {

                        output.writeUTF("404");
                    } else {
                        Files.delete(Paths.get(fileName));
                        output.writeUTF("200");
                    }

                    continue;
                }

            }

            socket.close();
            server.close();

        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }
}