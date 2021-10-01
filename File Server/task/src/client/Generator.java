package client;

import java.util.Random;

public class Generator {

    public static String generateRandomName() {

        int wordLength = 15;

        Random random = new Random();

        StringBuilder stringBuilder = new StringBuilder(15);

        for(int i = 0; i < wordLength; i++) {
            char temp = (char) ('A' + random.nextInt('Z' - 'A'));
            stringBuilder.append(temp);
        }

        return stringBuilder.toString();
    }


    public static String generateId() {

        int wordLength = 8;

        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(8);

        for(int i = 0; i < wordLength; i++) {
            char temp = (char) ('1' + random.nextInt('9' - '1'));
            stringBuilder.append(temp);
        }

        return stringBuilder.toString();

    }
}
