package server;

import client.Generator;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Main implements Serializable {

    private static final long serialVersionUID = 1L;

    //change if needed
    public static Path path = Path.of(new File("").getAbsolutePath() +
            "/File Server/task/src/server/data/");

    public static Map<String, String> filesByIdMap = new HashMap<>();

    public static void save() {

        try {

            FileOutputStream outputStream = new FileOutputStream("files.db");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(filesByIdMap);
            objectOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void load() {

        File db = new File("files.db");

        if (db.exists()) {

            try {

                FileInputStream fileInputStream = new FileInputStream("files.db");
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                filesByIdMap = (HashMap<String, String>) objectInputStream.readObject();

                objectInputStream.close();
                fileInputStream.close();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {

        load();

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


                if (command.matches("GET (BY_ID|BY_NAME) [\\S]+")) {

                    String fileName = "";

                    if (command.startsWith("GET BY_ID")) {
                        fileName = filesByIdMap.get(command.replace("GET BY_ID ", ""));
                    }

                    if (command.startsWith("GET BY_NAME")) {
                        fileName = command.replace("GET BY_NAME ", "");
                    }


                    File file = new File(path + "/" + fileName);


                    if (file.exists()) {

                        byte[] message = Files.readAllBytes(file.toPath());
                        output.writeUTF("200");
                        output.writeInt(message.length);
                        output.write(message);


                    } else {
                        output.writeUTF("404");
                    }
                }


                if (command.matches("PUT [\\S]+")) {

                    String fileName = command.replace("PUT ", "");

                    File file = new File(path + "/" + fileName);

                    if (file.exists()) {

                        output.writeUTF("403");

                    } else {

                        int length = input.readInt();
                        byte[] message = new byte[length];
                        input.readFully(message, 0, message.length);
                        Files.write(Path.of(file.getAbsolutePath()), message);

                        String fileId = Generator.generateId();
                        filesByIdMap.put(fileId, file.getName());
                        save();

                        output.writeUTF("200");
                        output.writeUTF(fileId);

                    }
                }


                if (command.matches("DELETE (BY_ID|BY_NAME) [\\S]+")) {

                    String fileName = "";

                    if (command.startsWith("DELETE BY_ID")) {
                        fileName = filesByIdMap.get(command.replace("DELETE BY_ID ", ""));
                    }

                    if (command.startsWith("DELETE BY_NAME")) {
                        fileName = command.replace("DELETE BY_NAME ", "");

                    }


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