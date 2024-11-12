package com.meli.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.meli.application.service.MessageService;
import com.meli.common.exception.ServiceException;
import com.meli.common.utils.tasks.Task;
import com.meli.common.utils.tasks.Task.Origin;
import com.meli.core.LocationUseCase;
import com.meli.core.entity.Point;
import com.meli.core.entity.Satellite;
import com.meli.provider.SatelliteProvider;

@Service
public class LocationUseCaseImpl implements LocationUseCase {

    private static final Task task = new Task("GET_LOCATION", "Cálculo de la ubicación");

    private final SatelliteProvider satelliteProvider;

    private final MessageService messageService;

    public LocationUseCaseImpl(SatelliteProvider satelliteProvider, MessageService messageService) {
        this.satelliteProvider = satelliteProvider;
        this.messageService = messageService;
    }

    @Override
    public Point getLocation(Satellite[] satellites) {

        task.setOrigin(Origin.builder().originClass("LocationUseCaseImpl").originMethod("getLocation").build());

        if (satellites == null || satellites.length < 3) {
            throw new ServiceException(messageService.mapMessage("INSUFFICIENT_SATELLITES"), null, task, null, satellites);
        }

        for (int i = 0; i < satellites.length; i++) {
            for (int j = i + 1; j < satellites.length; j++) {
                if (satellites[i].getName().equals(satellites[j].getName())) {
                    throw new ServiceException(messageService.mapMessage("DUPLICATE_SATELLITE_NAMES"), null, task, null, satellites);
                }
            }
        }

        List<Satellite> satelliteLocationsParam = satelliteProvider.getSatellites().collectList().block();
        for (Satellite satellite : satellites) {
            for (Satellite satellitePosition : new ArrayList<>(satelliteLocationsParam)) {
                if (satellite.getName().equals(satellitePosition.getName())) {
                    satellite.setLocation(satellitePosition.getLocation());
                    satelliteLocationsParam.remove(satellitePosition);
                    break;
                }
            }
            if (satellite.getLocation() == null || satellite.getDistance() == null) {
                throw new ServiceException(messageService.mapMessage("INSUFFICIENT_INFORMATION"), null, task, null, satellites);
            }
        }

        double[] position = this.calculatePosition(List.of(satellites));
        return Point.builder().x(position[0]).y(position[1]).build();
    }

    private double[] calculatePosition(List<Satellite> satellites) {
        int n = satellites.size();
        double[][] A = new double[n - 1][2];
        double[] B = new double[n - 1];

        Satellite reference = satellites.get(0);
        double xRef = reference.getLocation().getX();
        double yRef = reference.getLocation().getY();
        double dRef = reference.getDistance();

        for (int i = 1; i < n; i++) {
            Satellite satellite = satellites.get(i);
            double x = satellite.getLocation().getX();
            double y = satellite.getLocation().getY();
            double d = satellite.getDistance();

            A[i - 1][0] = 2 * (x - xRef);
            A[i - 1][1] = 2 * (y - yRef);
            B[i - 1] = Math.pow(dRef, 2) - Math.pow(d, 2) - Math.pow(xRef, 2) + Math.pow(x, 2) - Math.pow(yRef, 2)
                    + Math.pow(y, 2);
        }

        return solveLinearSystem(A, B);
    }

    private double[] solveLinearSystem(double[][] A, double[] B) {
        int rows = A.length;
        int cols = A[0].length;
        double[] solution = new double[cols];
        double[][] AtA = new double[cols][cols];
        double[] AtB = new double[cols];

        // Calculamos AtA y AtB
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                for (int k = 0; k < cols; k++) {
                    AtA[j][k] += A[i][j] * A[i][k];
                }
                AtB[j] += A[i][j] * B[i];
            }
        }

        // Verificar si el determinante de AtA es cero
        if (isSingular(AtA)) {
            throw new ServiceException(messageService.mapMessage("IDENTICAL_SATELLITE_POSITIONS"), null, task, null,
                    null);
        }

        // Resolución del sistema AtA * solution = AtB por eliminación de Gauss
        for (int i = 0; i < cols; i++) {
            for (int j = i + 1; j < cols; j++) {
                double factor = AtA[j][i] / AtA[i][i];
                for (int k = i; k < cols; k++) {
                    AtA[j][k] -= factor * AtA[i][k];
                }
                AtB[j] -= factor * AtB[i];
            }
        }

        for (int i = cols - 1; i >= 0; i--) {
            solution[i] = AtB[i] / AtA[i][i];
            for (int j = i - 1; j >= 0; j--) {
                AtB[j] -= AtA[j][i] * solution[i];
            }
        }

        return solution;
    }

    private boolean isSingular(double[][] matrix) {
        double determinant = matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        return determinant == 0;
    }
}
