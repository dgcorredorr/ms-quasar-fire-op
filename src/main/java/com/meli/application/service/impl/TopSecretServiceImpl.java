package com.meli.application.service.impl;

import com.meli.application.dto.SatelliteInfoDto;
import com.meli.application.dto.TargetDto;
import com.meli.application.mapper.SatelliteMapper;
import com.meli.application.mapper.TargetMapper;
import com.meli.application.service.MessageService;
import com.meli.application.service.TopSecretService;
import com.meli.core.entity.Point;
import com.meli.core.entity.Satellite;
import com.meli.core.entity.Target;
import com.meli.core.SatelliteUseCase;
import com.meli.core.LocationUseCase;
import com.meli.core.AidMessageUseCase;
import com.meli.common.exception.ServiceException;
import com.meli.common.utils.tasks.Task;
import com.meli.common.utils.tasks.Task.Origin;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TopSecretServiceImpl implements TopSecretService {

    private final SatelliteUseCase satelliteUseCase;
    private final LocationUseCase locationUseCase;
    private final AidMessageUseCase aidMessageUseCase;
    private final Task saveSatelliteTask = new Task("SAVE_SATELLITE_TASK", "Guardar satélite");
    private final Task getTargetTask = new Task("GET_TARGET", "Obtener información del emisor");
    private final MessageService messageService;

    public TopSecretServiceImpl(SatelliteUseCase satelliteUseCase, LocationUseCase locationUseCase,
            AidMessageUseCase aidMessageUseCase, MessageService messageService) {
        this.satelliteUseCase = satelliteUseCase;
        this.locationUseCase = locationUseCase;
        this.aidMessageUseCase = aidMessageUseCase;
        this.messageService = messageService;
    }

    @Override
    public Mono<TargetDto> getTarget(SatelliteInfoDto[] satellites) {
        getTargetTask.setOrigin(Origin.builder().originClass("TopSecretServiceImpl").originMethod("getTarget").build());

        return satelliteUseCase.getSatellites().collectList()
                .flatMap(satelliteLocations -> {
                    validateSatelliteNames(satellites, satelliteLocations);
                    updateSatelliteLocations(satellites, satelliteLocations);
                    Satellite[] satellitesArray = satelliteLocations.toArray(new Satellite[0]);
                    validateSatellites(satellitesArray);
                    return processSatellites(satellitesArray);
                });
    }

    private void validateSatelliteNames(SatelliteInfoDto[] satellites, List<Satellite> satelliteLocations) {
        Set<String> satelliteNames = satelliteLocations.stream()
                .map(Satellite::getName)
                .collect(Collectors.toSet());

        for (SatelliteInfoDto satellite : satellites) {
            if (!satelliteNames.contains(satellite.getName())) {
                throw new ServiceException(
                        messageService.mapMessage("WRONG_SATELLITE_INFO").replace("SATELLITE_NAME",
                                satellite.getName()),
                        null,
                        getTargetTask,
                        null,
                        satellites);
            }
        }
    }

    private void updateSatelliteLocations(SatelliteInfoDto[] satellites, List<Satellite> satelliteLocations) {
        for (Satellite satellitePosition : satelliteLocations) {
            for (SatelliteInfoDto satellite : satellites) {
                if (satellite.getName().equals(satellitePosition.getName())) {
                    satellitePosition.setDistance(satellite.getDistance());
                    satellitePosition.setMessage(satellite.getMessage().toArray(new String[0]));
                    break;
                }
            }
            if (satellitePosition.getDistance() == null || satellitePosition.getMessage() == null) {
                throw new ServiceException(
                        messageService.mapMessage("MISSING_SATELLITE_INFO").replace("SATELLITE_NAME",
                                satellitePosition.getName()),
                        null,
                        getTargetTask,
                        null,
                        satellites);
            }
        }
    }

    private Mono<TargetDto> processSatellites(Satellite[] satellitesArray) {
        Point targetLocation = locationUseCase.getLocation(satellitesArray);
        String[][] messages = Arrays.stream(satellitesArray).map(Satellite::getMessage).toArray(String[][]::new);
        String message = aidMessageUseCase.getMessage(messages);

        Target target = Target.builder()
                .position(targetLocation)
                .message(message)
                .build();

        return Mono.just(TargetMapper.toDto(target));
    }

    private void validateSatellites(Satellite[] satellites) {
        if (satellites == null || satellites.length < 3) {
            throw new ServiceException(messageService.mapMessage("INSUFFICIENT_SATELLITES"), null, getTargetTask, null,
                    null);
        }

        for (int i = 0; i < satellites.length; i++) {
            for (int j = i + 1; j < satellites.length; j++) {
                if (satellites[i].getName().equalsIgnoreCase(satellites[j].getName())) {
                    throw new ServiceException(messageService.mapMessage("DUPLICATE_SATELLITE_NAMES"),
                            null, getTargetTask, null, satellites);
                }
            }
            if (satellites[i].getDistance() == null || satellites[i].getMessage() == null) {
                throw new ServiceException(messageService.mapMessage("INSUFFICIENT_INFORMATION"), null, getTargetTask,
                        null, null);
            }
        }
    }

    @Override
    public Mono<Void> updateSatellite(SatelliteInfoDto satellite) {
        saveSatelliteTask.setOrigin(Origin.builder().originClass("TopSecretServiceImpl")
                .originMethod("saveSatellite(Satellite satellite)").build());
        return satelliteUseCase.updateSatellite(SatelliteMapper.toEntity(satellite)).then();
    }

    @Override
    public Mono<TargetDto> getTarget() {
        getTargetTask.setOrigin(Origin.builder().originClass("TopSecretServiceImpl")
                .originMethod("getTarget()").build());
        return satelliteUseCase.getSatellites().collectList().flatMap(satellites -> {
            Satellite[] satellitesArray = satellites.toArray(new Satellite[0]);
            this.validateSatellites(satellitesArray);
            return processSatellites(satellitesArray);
        });
    }
}