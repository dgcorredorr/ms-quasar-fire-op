package com.meli.core.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.meli.application.service.MessageService;
import com.meli.common.exception.ServiceException;
import com.meli.common.utils.tasks.Task;
import com.meli.core.LocationUseCase;
import com.meli.core.entity.Point;
import com.meli.core.entity.Satellite;

@Service
public class LocationUseCaseImpl implements LocationUseCase {

    private static final Task task = new Task("GET_LOCATION", "Cálculo de la ubicación");

    private final MessageService messageService;

    public LocationUseCaseImpl(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public Point getLocation(Satellite[] satellites) {

        int n = satellites.length;
        double[][] A = new double[n - 1][2];
        double[] B = new double[n - 1];

        Satellite reference = satellites[0];
        double xRef = reference.getLocation().getX();
        double yRef = reference.getLocation().getY();
        double dRef = reference.getDistance();

        for (int i = 1; i < n; i++) {
            Satellite satellite = satellites[i];
            double x = satellite.getLocation().getX();
            double y = satellite.getLocation().getY();
            double d = satellite.getDistance();

            A[i - 1][0] = 2 * (x - xRef);
            A[i - 1][1] = 2 * (y - yRef);
            B[i - 1] = Math.pow(dRef, 2) - Math.pow(d, 2) - Math.pow(xRef, 2) + Math.pow(x, 2) - Math.pow(yRef, 2)
                    + Math.pow(y, 2);
        }

        double[] position = solveLinearSystem(A, B);
        return Point.builder().x(position[0]).y(position[1]).build();
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
            throw new ServiceException(
                    messageService.mapMessage("IDENTICAL_SATELLITE_POSITIONS"),
                    HttpStatus.NOT_FOUND,
                    null,
                    task,
                    null,
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
