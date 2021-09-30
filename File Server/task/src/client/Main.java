package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Main {
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
                System.out.println("Enter action (1 - get a file, 2 - create a file, 3 - delete a file):");
                String command = scanner.nextLine();

                switch (command) {

                    case "1":

                        System.out.println("Enter filename:");
                        String getFileName = scanner.nextLine();

                        output.writeUTF("GET " + getFileName);

                        System.out.println("The request was sent.");
                        String getFileResponse = input.readUTF();

                        if (getFileResponse.equals("404")) {
                            System.out.println("The response says that the file was not found!");
                        } else if (getFileResponse.startsWith("200 ")) {
                            System.out.println("The content of the file is: "
                                    + getFileResponse.replace("200 ", ""));
                        }


                        break;

                    case "2":

                        System.out.println("Enter filename:");
                        String createFileName = scanner.nextLine();

                        System.out.println("Enter file content:");
                        String createFileContent = scanner.nextLine();

                        output.writeUTF("PUT " + createFileName + " " + createFileContent);

                        String createFileResponse = input.readUTF();

                        if (createFileResponse.equals("200")) {
                            System.out.println("The response says that file was created!");
                        } else if (createFileResponse.equals("403")) {
                            System.out.println("The response says that creating the file was forbidden!");
                        }

                        break;

                    case "3":

                        System.out.println("Enter filename:");
                        String deleteFileName = scanner.nextLine();

                        output.writeUTF("DELETE " + deleteFileName);

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

            }

            socket.close();

        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }
}
