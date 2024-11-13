package com.meli.core.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.meli.application.service.MessageService;
import com.meli.common.exception.ServiceException;

public class AidMessageUseCaseImplTest {

    @InjectMocks
    AidMessageUseCaseImpl aidMessageUseCaseImpl;

    @Mock
    MessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMessage() {
        // Arrange
        AidMessageUseCaseImpl aidMessageUseCaseImpl = new AidMessageUseCaseImpl(messageService);
        String[][] messages = new String[][] {
            {"", "este", "es", "un", "mensaje"},
            {"este", "", "un", "mensaje"},
            {"", "", "es", "", "mensaje"}
        };
        String expected = "este es un mensaje";
        
        // Act
        String result = aidMessageUseCaseImpl.getMessage(messages);
        
        // Assert
        assertEquals(expected, result);
    }
    
    @Test
    void testGetMessageWithComplexDelay() {
        // Arrange
        AidMessageUseCaseImpl aidMessageUseCaseImpl = new AidMessageUseCaseImpl(messageService);
        String[][] messages = new String[][] {{"", "", "Hola", "este", "", "un", "mensaje", "", "prueba"},
                                              {"", "", "", "", "este", "es", "un", "", "de", ""},
                                              {"", "", "", "", "", "", "", "", "", "es", "un", "mensaje", "", ""}};
        String expected = "Hola este es un mensaje de prueba";
        
        // // Act
        String result = aidMessageUseCaseImpl.getMessage(messages);
        
        // // Assert
        assertEquals(expected, result);
    }

    @Test
    void testGetMessageWithUndecodableMessage() {
        // Arrange
        AidMessageUseCaseImpl aidMessageUseCaseImpl = new AidMessageUseCaseImpl(messageService);
        String[][] messages = new String[][] {
            {"", "", "Hola", "este", "", "un", "mensaje", "", "prueba"},
            {"", "", "", "", "este", "es", "un", "", "", ""},
            {"", "", "", "", "", "", "", "", "", "es", "un", "mensaje", "", ""}
        };
        String exceptionMessage = "No se pudo decodificar el mensaje.";
        when(messageService.mapMessage("MESSAGE_INSUFFICIENT_INFORMATION")).thenReturn(exceptionMessage);
        
        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            aidMessageUseCaseImpl.getMessage(messages);
        });
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    void testGetMessageWithEmptyMessages() {
        // Arrange
        AidMessageUseCaseImpl aidMessageUseCaseImpl = new AidMessageUseCaseImpl(messageService);
        String[][] messages = new String[][] {
            {"", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", ""}
        };
        String exceptionMessage = "No se pudo decodificar el mensaje.";
        when(messageService.mapMessage("MESSAGE_INSUFFICIENT_INFORMATION")).thenReturn(exceptionMessage);
        
        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            aidMessageUseCaseImpl.getMessage(messages);
        });
        assertEquals(exceptionMessage, exception.getMessage());
    }
    
}
