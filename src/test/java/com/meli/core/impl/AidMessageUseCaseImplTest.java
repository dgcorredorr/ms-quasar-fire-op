package com.meli.core.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.meli.common.exception.ServiceException;

public class AidMessageUseCaseImplTest {

    @Test
    void testGetMessage() {
        // Arrange
        AidMessageUseCaseImpl aidMessageUseCaseImpl = new AidMessageUseCaseImpl();
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
        AidMessageUseCaseImpl aidMessageUseCaseImpl = new AidMessageUseCaseImpl();
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
        AidMessageUseCaseImpl aidMessageUseCaseImpl = new AidMessageUseCaseImpl();
        String[][] messages = new String[][] {
            {"", "", "Hola", "este", "", "un", "mensaje", "", "prueba"},
            {"", "", "", "", "este", "es", "un", "", "", ""},
            {"", "", "", "", "", "", "", "", "", "es", "un", "mensaje", "", ""}
        };
        
        // Act & Assert
        assertThrows(ServiceException.class, () -> {
            aidMessageUseCaseImpl.getMessage(messages);
        });
    }

    @Test
    void testGetMessageWithEmptyMessages() {
        // Arrange
        AidMessageUseCaseImpl aidMessageUseCaseImpl = new AidMessageUseCaseImpl();
        String[][] messages = new String[][] {
            {"", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", ""}
        };
        
        // Act & Assert
        assertThrows(ServiceException.class, () -> {
            aidMessageUseCaseImpl.getMessage(messages);
        });
    }
    
}
