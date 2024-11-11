package com.meli.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.meli.application.service.ParamService;
import com.meli.core.LocationUseCase;
import com.meli.core.entity.Point;
import com.meli.core.entity.Satellite;

@Service
public class LocationUseCaseImpl implements LocationUseCase {

    private final ParamService paramService;

    public LocationUseCaseImpl(ParamService paramService) {
        this.paramService = paramService;
    }

    @Override
    public Point getLocation(Satellite[] satellites) {
        if (satellites == null || satellites.length == 0) {
            return Point.builder().x(0.0).y(0.0).build();
        }

        List<Satellite> satelliteLocationsParam = new ArrayList<>(paramService.mapParamList("SATELLITE_LOCATIONS", Satellite.class));
        for (Satellite satellite : satellites) {
            for (Satellite satellitePosition : new ArrayList<>(satelliteLocationsParam)) {
                if (satellite.getName().equals(satellitePosition.getName())) {
                    satellite.setLocation(satellitePosition.getLocation());
                    satelliteLocationsParam.remove(satellitePosition);
                    break;
                }
            }
        }

        // Lógica de trilateración para 'n' satélites utilizando el método de mínimos
        // cuadrados
        double sumX = 0;
        double sumY = 0;
        double sumWeights = 0;

        for (Satellite satellite : satellites) {
            double x = satellite.getLocation().getX();
            double y = satellite.getLocation().getY();
            double distance = satellite.getDistance();

            if (distance > 0) {
                double weight = 1.0 / (distance * distance);
                sumX += x * weight;
                sumY += y * weight;
                sumWeights += weight;
            }
        }

        double estimatedX = sumX / sumWeights;
        double estimatedY = sumY / sumWeights;

        return Point.builder().x(estimatedX).y(estimatedY).build();
    }

}
