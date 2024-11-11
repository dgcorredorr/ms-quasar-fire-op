package com.meli.core.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.meli.application.service.ParamService;
import com.meli.core.entity.Point;
import com.meli.core.entity.Satellite;

class LocationUseCaseImplTest {

    @Mock
    private ParamService paramService;

    private LocationUseCaseImpl locationUseCaseImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        locationUseCaseImpl = new LocationUseCaseImpl(paramService);
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
        assertEquals(-100.0, result.getX(), 0.1);
        assertEquals(75.5, result.getY(), 0.1);
    }

    @Test
    void testGetLocationWithDifferentDistances() {
        // Arrange
        Satellite satellite1 = Satellite.builder().name("sat1").distance(200.0).location(Point.builder().x(0).y(0).build()).build();
        Satellite satellite2 = Satellite.builder().name("sat2").distance(150.0).location(Point.builder().x(100).y(0).build()).build();
        Satellite satellite3 = Satellite.builder().name("sat3").distance(100.0).location(Point.builder().x(50).y(100).build()).build();
        Satellite[] satellites = { satellite1, satellite2, satellite3 };

        List<Satellite> satelliteLocationsParam = List.of(
            Satellite.builder().name("sat1").location(Point.builder().x(0).y(0).build()).build(),
            Satellite.builder().name("sat2").location(Point.builder().x(100).y(0).build()).build(),
            Satellite.builder().name("sat3").location(Point.builder().x(50).y(100).build()).build()
        );

        when(paramService.mapParamList("SATELLITE_LOCATIONS", Satellite.class)).thenReturn(satelliteLocationsParam);

        // Act
        Point result = locationUseCaseImpl.getLocation(satellites);

        // Assert
        assertEquals(50.0, result.getX(), 0.1);
        assertEquals(50.0, result.getY(), 0.1);
    }

    @Test
    void testGetLocationWithNoDistances() {
        // Arrange
        Satellite satellite1 = Satellite.builder().name("sat1").distance(0.0).location(Point.builder().x(0).y(0).build()).build();
        Satellite satellite2 = Satellite.builder().name("sat2").distance(0.0).location(Point.builder().x(100).y(0).build()).build();
        Satellite satellite3 = Satellite.builder().name("sat3").distance(0.0).location(Point.builder().x(50).y(100).build()).build();
        Satellite[] satellites = { satellite1, satellite2, satellite3 };

        List<Satellite> satelliteLocationsParam = List.of(
            Satellite.builder().name("sat1").location(Point.builder().x(0).y(0).build()).build(),
            Satellite.builder().name("sat2").location(Point.builder().x(100).y(0).build()).build(),
            Satellite.builder().name("sat3").location(Point.builder().x(50).y(100).build()).build()
        );

        when(paramService.mapParamList("SATELLITE_LOCATIONS", Satellite.class)).thenReturn(satelliteLocationsParam);

        // Act
        Point result = locationUseCaseImpl.getLocation(satellites);

        // Assert
        assertEquals(0.0, result.getX(), 0.1);
        assertEquals(0.0, result.getY(), 0.1);
    }

    @Test
    void testGetLocationWithSingleSatellite() {
        // Arrange
        Satellite satellite1 = Satellite.builder().name("sat1").distance(100.0).location(Point.builder().x(0).y(0).build()).build();
        Satellite[] satellites = { satellite1 };

        List<Satellite> satelliteLocationsParam = List.of(
            Satellite.builder().name("sat1").location(Point.builder().x(0).y(0).build()).build()
        );

        when(paramService.mapParamList("SATELLITE_LOCATIONS", Satellite.class)).thenReturn(satelliteLocationsParam);

        // Act
        Point result = locationUseCaseImpl.getLocation(satellites);

        // Assert
        assertEquals(0.0, result.getX(), 0.1);
        assertEquals(0.0, result.getY(), 0.1);
    }

    @Test
    void testGetLocationWithIdenticalSatellites() {
        // Arrange
        Satellite satellite1 = Satellite.builder().name("sat1").distance(100.0).location(Point.builder().x(0).y(0).build()).build();
        Satellite satellite2 = Satellite.builder().name("sat1").distance(100.0).location(Point.builder().x(0).y(0).build()).build();
        Satellite[] satellites = { satellite1, satellite2 };

        List<Satellite> satelliteLocationsParam = List.of(
            Satellite.builder().name("sat1").location(Point.builder().x(0).y(0).build()).build()
        );

        when(paramService.mapParamList("SATELLITE_LOCATIONS", Satellite.class)).thenReturn(satelliteLocationsParam);

        // Act
        Point result = locationUseCaseImpl.getLocation(satellites);

        // Assert
        assertEquals(0.0, result.getX(), 0.1);
        assertEquals(0.0, result.getY(), 0.1);
    }

    @Test
    void testGetLocationWithNoSatellites() {
        // Arrange
        Satellite[] satellites = {};

        List<Satellite> satelliteLocationsParam = List.of();

        when(paramService.mapParamList("SATELLITE_LOCATIONS", Satellite.class)).thenReturn(satelliteLocationsParam);

        // Act
        Point result = locationUseCaseImpl.getLocation(satellites);

        // Assert
        assertEquals(0.0, result.getX(), 0.1);
        assertEquals(0.0, result.getY(), 0.1);
    }

    @Test
    void testGetLocationWithNullSatellites() {
        // Arrange
        Satellite[] satellites = null;

        List<Satellite> satelliteLocationsParam = List.of();

        when(paramService.mapParamList("SATELLITE_LOCATIONS", Satellite.class)).thenReturn(satelliteLocationsParam);

        // Act
        Point result = locationUseCaseImpl.getLocation(satellites);

        // Assert
        assertEquals(0.0, result.getX(), 0.1);
        assertEquals(0.0, result.getY(), 0.1);
    }
}
