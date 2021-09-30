package server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static Path path;

    public static void main(String[] args) {

        path = Path.of(System.getProperty("user.dir"), "src", "server", "data");

        try {

            String address = "127.0.0.1";
            int port = 23499;

            boolean exit = false;
            while (!exit) {

                ServerSocket server = new ServerSocket(port, 50, InetAddress.getByName(address));
                Socket socket = server.accept();

                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());

                String command = input.readUTF();

                if (command.equals("EXIT")) {
                    exit = true;
                }


                if (command.matches("GET [\\S]+")) {

                    String fileName = command.replace("GET ", "");

                    File file = new File(path + "/" + fileName);

                    if (file.exists()) {
                        String content = new String(Files.readAllBytes(Paths.get(path + "/" + fileName)));

                        output.writeUTF("200 " + content);
                    } else {
                        output.writeUTF("404");
                    }
                }


                if (command.matches("PUT [\\S]+ [\\S ]+")) {

                    String[] commandArray = command.split(" ");
                    String fileName = commandArray[1];
                    String text = command.replaceAll("PUT [\\S]+ ", "");

                    File file = new File(path + "/" + fileName);

                    if (file.exists()) {
                        output.writeUTF("403");
                    } else {
                        FileWriter fileWriter = new FileWriter(path + "/" + fileName);
                        fileWriter.write(text);
                        fileWriter.flush();
                        fileWriter.close();
                        output.writeUTF("200");
                    }
                }


                if (command.matches("DELETE [\\S]+")) {

                    String fileName = command.replace("DELETE ", "");

                    File file = new File(path + "/" + fileName);

                    if (!file.exists()) {
                        output.writeUTF("404");
                    } else {
                        Files.delete(Paths.get(path + "/" + fileName));
                        output.writeUTF("200");
                    }
                }

                socket.close();
                server.close();

            }

        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }
}