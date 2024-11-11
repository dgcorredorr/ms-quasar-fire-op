package com.meli.core.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.meli.application.service.MessageService;
import com.meli.application.service.ParamService;
import com.meli.common.exception.ServiceException;
import com.meli.core.entity.Point;
import com.meli.core.entity.Satellite;

class LocationUseCaseImplTest {

    @Mock
    private ParamService paramService;

    @Mock
    private MessageService messageService;

    private LocationUseCaseImpl locationUseCaseImpl;

    private static final double delta = 0.5;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        locationUseCaseImpl = new LocationUseCaseImpl(paramService, messageService);
    }

    @Test
    void testGetLocation() {
        // Arrange
        Satellite satellite1 = Satellite.builder().name("kenobi").distance(100.0).location(null).build();
        Satellite satellite2 = Satellite.builder().name("skywalker").distance(115.5).location(null).build();
        Satellite satellite3 = Satellite.builder().name("sato").distance(142.7).location(null).build();
        Satellite[] satellites = { satellite1, satellite2, satellite3 };

        List<Satellite> satelliteLocationsParam = List.of(
            Satellite.builder().name("kenobi").location(Point.builder().x(-500).y(-200).build()).build(),
            Satellite.builder().name("skywalker").location(Point.builder().x(100).y(-100).build()).build(),
            Satellite.builder().name("sato").location(Point.builder().x(500).y(100).build()).build()
        );

        when(paramService.mapParamList("SATELLITE_LOCATIONS", Satellite.class)).thenReturn(satelliteLocationsParam);

        // Act
        Point result = locationUseCaseImpl.getLocation(satellites);

        // Assert
        assertEquals(-487.5, result.getX(), delta);
        assertEquals(1557.0, result.getY(), delta);
    }

    @Test
    void testGetLocationWithNoDistances() {
        // Arrange
        Satellite satellite1 = Satellite.builder().name("sat1").build();
        Satellite satellite2 = Satellite.builder().name("sat2").build();
        Satellite satellite3 = Satellite.builder().name("sat3").build();
        Satellite[] satellites = { satellite1, satellite2, satellite3 };

        List<Satellite> satelliteLocationsParam = List.of(
            Satellite.builder().name("sat1").location(Point.builder().x(0).y(0).build()).build(),
            Satellite.builder().name("sat2").location(Point.builder().x(100).y(0).build()).build(),
            Satellite.builder().name("sat3").location(Point.builder().x(50).y(100).build()).build()
        );

        String exceptionMessage = "Información insuficiente.";

        when(paramService.mapParamList("SATELLITE_LOCATIONS", Satellite.class)).thenReturn(satelliteLocationsParam);
        when(messageService.mapMessage("INSUFFICIENT_INFORMATION")).thenReturn(exceptionMessage);

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> locationUseCaseImpl.getLocation(satellites));
        assertEquals(exceptionMessage, exception.getMessage());        
    }

    @Test
    void testGetLocationWithSingleSatellite() {
        // Arrange
        Satellite satellite1 = Satellite.builder().name("sat1").distance(100.0).build();
        Satellite[] satellites = { satellite1 };

        List<Satellite> satelliteLocationsParam = List.of(
            Satellite.builder().name("sat1").location(Point.builder().x(0).y(0).build()).build()
        );

        String exceptionMessage = "Se requieren al menos tres satélites para calcular la ubicación.";

        when(paramService.mapParamList("SATELLITE_LOCATIONS", Satellite.class)).thenReturn(satelliteLocationsParam);
        when(messageService.mapMessage("INSUFFICIENT_SATELLITES")).thenReturn(exceptionMessage);

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> locationUseCaseImpl.getLocation(satellites));
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    void testGetLocationWithIdenticalSatellites() {
        // Arrange
        Satellite satellite1 = Satellite.builder().name("sat1").distance(100.0).build();
        Satellite satellite2 = Satellite.builder().name("sat1").distance(100.0).build();
        Satellite satellite3 = Satellite.builder().name("sat1").distance(100.0).build();
        Satellite[] satellites = { satellite1, satellite2, satellite3 };

        List<Satellite> satelliteLocationsParam = List.of(
            Satellite.builder().name("sat1").location(Point.builder().x(0).y(0).build()).build(),
            Satellite.builder().name("sat1").location(Point.builder().x(0).y(0).build()).build(),
            Satellite.builder().name("sat1").location(Point.builder().x(0).y(0).build()).build()
        );

        String exceptionMessage = "Los nombres de los satélites no pueden ser idénticos.";

        when(paramService.mapParamList("SATELLITE_LOCATIONS", Satellite.class)).thenReturn(satelliteLocationsParam);
        when(messageService.mapMessage("DUPLICATE_SATELLITE_NAMES")).thenReturn(exceptionMessage);

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> locationUseCaseImpl.getLocation(satellites));
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    void testGetLocationWithIdenticalSatelliteLocations() {
        // Arrange
        Satellite satellite1 = Satellite.builder().name("sat1").distance(100.0).build();
        Satellite satellite2 = Satellite.builder().name("sat2").distance(100.0).build();
        Satellite satellite3 = Satellite.builder().name("sat3").distance(100.0).build();
        Satellite[] satellites = { satellite1, satellite2, satellite3 };

        List<Satellite> satelliteLocationsParam = List.of(
            Satellite.builder().name("sat1").location(Point.builder().x(0).y(0).build()).build(),
            Satellite.builder().name("sat2").location(Point.builder().x(0).y(0).build()).build(),
            Satellite.builder().name("sat3").location(Point.builder().x(0).y(0).build()).build()
        );

        String exceptionMessage = "Singularidad detectada, las ubicaciones de los satélites no pueden ser idénticas.";

        when(paramService.mapParamList("SATELLITE_LOCATIONS", Satellite.class)).thenReturn(satelliteLocationsParam);
        when(messageService.mapMessage("IDENTICAL_SATELLITE_POSITIONS")).thenReturn(exceptionMessage);

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> locationUseCaseImpl.getLocation(satellites));
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    void testGetLocationWithNoSatellites() {
        // Arrange
        Satellite[] satellites = {};

        List<Satellite> satelliteLocationsParam = List.of();

        String exceptionMessage = "Se requieren al menos tres satélites para calcular la ubicación.";

        when(paramService.mapParamList("SATELLITE_LOCATIONS", Satellite.class)).thenReturn(satelliteLocationsParam);
        when(messageService.mapMessage("INSUFFICIENT_SATELLITES")).thenReturn(exceptionMessage);

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> locationUseCaseImpl.getLocation(satellites));
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    void testGetLocationWithNullSatellites() {
        // Arrange
        Satellite[] satellites = null;

        List<Satellite> satelliteLocationsParam = List.of();
        
        String exceptionMessage = "Se requieren al menos tres satélites para calcular la ubicación.";

        when(paramService.mapParamList("SATELLITE_LOCATIONS", Satellite.class)).thenReturn(satelliteLocationsParam);
        when(messageService.mapMessage("INSUFFICIENT_SATELLITES")).thenReturn(exceptionMessage);

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> locationUseCaseImpl.getLocation(satellites));
        assertEquals(exceptionMessage, exception.getMessage());
    }
}
