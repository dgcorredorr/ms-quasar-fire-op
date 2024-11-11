package com.meli.core.impl;

import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.meli.core.AidMessageUseCase;

@Service
public class AidMessageUseCaseImpl implements AidMessageUseCase {

    @Override
    public String getMessage(String[][] messages) {
        int minLength = findMinLength(messages);
        String[][] normalizedMessages = normalizeMessages(messages, minLength);
        return decodeMessage(normalizedMessages, minLength);
    }

    private static int findMinLength(String[][] messages) {
        return Arrays.stream(messages)
                     .mapToInt(arr -> arr.length)
                     .min()
                     .orElse(0);
    }

    private static String[][] normalizeMessages(String[][] messages, int minLength) {
        String[][] normalizedMessages = new String[messages.length][minLength];
        for (int i = 0; i < messages.length; i++) {
            if (messages[i].length > minLength) {
                normalizedMessages[i] = Arrays.copyOfRange(messages[i], messages[i].length - minLength, messages[i].length);
            } else {
                normalizedMessages[i] = messages[i];
            }
        }
        return normalizedMessages;
    }

    private static String decodeMessage(String[][] messages, int minLength) {
        String[] result = new String[minLength];
        for (int i = 0; i < minLength; i++) {
            for (String[] message : messages) {
                if (i < message.length && !message[i].isEmpty()) {
                    result[i] = message[i];
                    break;
                }
            }
        }
        return String.join(" ", result).trim();
    }
}
