package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {

    //change if needed
    public static Path path = Path.of(new File("").getAbsolutePath() +
            "/File Server/task/src/client/data/");

    public static void main(String[] args) {

        try {

            String address = "127.0.0.1";
            int port = 23499;

            Socket socket = new Socket(InetAddress.getByName(address), port);

            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            boolean exit = false;
            while (!exit) {

                Scanner scanner = new Scanner(System.in);
                System.out.println("Enter action (1 - get a file, 2 - save a file, 3 - delete a file):");
                String command = scanner.nextLine();

                switch (command) {

                    case "1":

                        String getChoice;

                        while (true) {

                            System.out.println("Do you want to get the file by name or by id (1 - name, 2 - id):");
                            getChoice = scanner.nextLine();

                            if (getChoice.matches("1|2")) break;

                        }

                        if (getChoice.equals("1")) {
                            System.out.println("Enter name:");
                            String name = scanner.nextLine();
                            output.writeUTF("GET BY_NAME " + name);
                        }

                        if (getChoice.equals("2")) {
                            System.out.println("Enter id:");
                            String id = scanner.nextLine();
                            output.writeUTF("GET BY_ID " + id);
                        }

                        System.out.println("The request was sent.");
                        String getFileIdResponse = input.readUTF();

                        if (getFileIdResponse.equals("404")) {
                            System.out.println("The response says that this file is not found!");
                            break;
                        }

                        if (getFileIdResponse.startsWith("200")) {

                            System.out.println("The file was downloaded! Specify a name for it:");
                            String getFileName = scanner.nextLine();

                            int length = input.readInt();
                            byte[] message = new byte[length];
                            input.readFully(message, 0, message.length);

                            Files.write(Path.of(path + "\\" + getFileName), message);

                            System.out.println("File saved on the hard drive!");

                        }

                        break;

                    case "2":

                        System.out.println("Enter name of the file:");
                        String saveName = scanner.nextLine();

                        System.out.println(path + "\\" + saveName);

                        if (!new File(path + "\\" + saveName).exists()) {
                            System.out.println("Sorry, the file doesn't exist!");
                            break;
                        }


                        System.out.println("Enter name of the file to be saved on server:");
                        String serverName = scanner.nextLine();

                        if (serverName.matches("[ ]+") || serverName.equals("")) {
                            serverName = Generator.generateRandomName();
                        }

                        output.writeUTF("PUT " + serverName);

                        byte[] message = Files.readAllBytes(new File(path + "\\" + saveName).toPath());
                        output.writeInt(message.length);
                        output.write(message);

                        String response = input.readUTF();
                        System.out.println("The request was sent.");

                        if (response.equals("200")) {
                            String serverId = input.readUTF();
                            System.out.println("Response says that file is saved! ID = " + serverId);
                        } else if (response.equals("403")) {
                            System.out.println("The response says that creating the file was forbidden!");
                        }

                        break;

                    case "3":

                        String deleteChoice;

                        while (true) {

                            System.out.println("Do you want to get the file by name or by id (1 - name, 2 - id):");
                            deleteChoice = scanner.nextLine();

                            if (deleteChoice.matches("1|2")) break;

                        }

                        if (deleteChoice.equals("1")) {
                            System.out.println("Enter name:");
                            String deleteFileName = scanner.nextLine();
                            output.writeUTF("DELETE BY_NAME " + deleteFileName);
                        }


                        if (deleteChoice.equals("2")) {
                            System.out.println("Enter id:");
                            String deleteFileId = scanner.nextLine();
                            output.writeUTF("DELETE BY_ID " + deleteFileId);
                        }

                        System.out.println("The request was sent.");
                        String deleteFileResponse = input.readUTF();

                        if (deleteFileResponse.equals("404")) {
                            System.out.println("The response says that the file was not found!");
                        } else if (deleteFileResponse.startsWith("200")) {
                            System.out.println("The response says that the file was successfully deleted!");
                        }


                        break;


                    case "exit":
                        output.writeUTF("EXIT");
                        System.out.println("The request was sent.");
                        exit = true;
                    default:
                        break;

                }

                socket.close();

            }


        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }
}
