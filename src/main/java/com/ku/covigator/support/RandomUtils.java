package com.ku.covigator.support;

import java.security.SecureRandom;

public class RandomUtils {

    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomMixStr(int length) {

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%&";
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }

    /**
     * return 1 ~ range
     */
    public static int generateRandomNumber(int range) {
        return (random.nextInt(range) + 1);
    }
}
