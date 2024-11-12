package com.meli.core.impl;

import java.util.Arrays;
import org.springframework.stereotype.Service;

import com.meli.application.service.MessageService;
import com.meli.common.exception.ServiceException;
import com.meli.common.utils.tasks.Task;
import com.meli.common.utils.tasks.Task.Origin;
import com.meli.core.AidMessageUseCase;

@Service
public class AidMessageUseCaseImpl implements AidMessageUseCase {

    private final MessageService messageService;

    private static final Task task = new Task("DECODE_MESSAGE", "Decodificación del mensaje");

    public AidMessageUseCaseImpl(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public String getMessage(String[][] messages) {
        int minLength = findMinLength(messages);
        String[][] normalizedMessages = normalizeMessages(messages, minLength);
        return decodeMessage(normalizedMessages, minLength);
    }

    private int findMinLength(String[][] messages) {
        return Arrays.stream(messages)
                     .mapToInt(arr -> arr.length)
                     .min()
                     .orElse(0);
    }

    private String[][] normalizeMessages(String[][] messages, int minLength) {
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

    private String decodeMessage(String[][] messages, int minLength) {
        String[] result = new String[minLength];
        for (int i = 0; i < minLength; i++) {
            for (String[] message : messages) {
                if (i < message.length && !message[i].isEmpty()) {
                    result[i] = message[i];
                    break;
                }
            }
        }
        String decodedMessage = String.join(" ", result).trim();
        decodedMessage = decodedMessage.replaceAll("null", "").trim();
        
        if (decodedMessage.contains("  ") || decodedMessage.contains("null") || decodedMessage.isEmpty()) {
            task.setOrigin(Origin.builder().originClass("AidMessageUseCaseImpl").originMethod("decodeMessage").build());
            throw new ServiceException(messageService.mapMessage("MESSAGE_INSUFFICIENT_INFORMATION"), null, task, null, null);
        }
        
        return decodedMessage;
    }
}