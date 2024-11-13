package com.meli.core.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.meli.application.service.MessageService;
import com.meli.common.exception.ServiceException;
import com.meli.core.entity.Point;
import com.meli.core.entity.Satellite;

class LocationUseCaseImplTest {

    @Mock
    private MessageService messageService;

    private LocationUseCaseImpl locationUseCaseImpl;

    private static final double delta = 0.5;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        locationUseCaseImpl = new LocationUseCaseImpl(messageService);
    }

    @Test
    void testGetLocation() {
        // Arrange
        Satellite satellite1 = Satellite.builder().name("kenobi").distance(100.0).location(Point.builder().x(-500).y(-200).build()).build();
        Satellite satellite2 = Satellite.builder().name("skywalker").distance(115.5).location(Point.builder().x(100).y(-100).build()).build();
        Satellite satellite3 = Satellite.builder().name("sato").distance(142.7).location(Point.builder().x(500).y(100).build()).build();
        Satellite[] satellites = { satellite1, satellite2, satellite3 };

        // Act
        Point result = locationUseCaseImpl.getLocation(satellites);

        // Assert
        assertEquals(-487.3, result.getX(), delta);
        assertEquals(1557.0, result.getY(), delta);
    }

    

    @Test
    void testGetLocationWithIdenticalSatelliteLocations() {
        // Arrange
        Satellite satellite1 = Satellite.builder().name("sat1").distance(100.0).location(Point.builder().x(0).y(0).build()).build();
        Satellite satellite2 = Satellite.builder().name("sat2").distance(100.0).location(Point.builder().x(0).y(0).build()).build();
        Satellite satellite3 = Satellite.builder().name("sat3").distance(100.0).location(Point.builder().x(0).y(0).build()).build();
        Satellite[] satellites = { satellite1, satellite2, satellite3 };

        String exceptionMessage = "Singularidad detectada, las ubicaciones de los satélites no pueden ser idénticas.";

        when(messageService.mapMessage("IDENTICAL_SATELLITE_POSITIONS")).thenReturn(exceptionMessage);

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> locationUseCaseImpl.getLocation(satellites));
        assertEquals(exceptionMessage, exception.getMessage());
    }
}
