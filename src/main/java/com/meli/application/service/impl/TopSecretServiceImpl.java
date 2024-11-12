package com.meli.application.service.impl;

import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.meli.application.dto.SatelliteInfoDto;
import com.meli.application.mapper.SatelliteMapper;
import com.meli.application.service.MessageService;
import com.meli.application.service.TopSecretService;
import com.meli.common.exception.ServiceException;
import com.meli.common.utils.tasks.Task;
import com.meli.common.utils.tasks.Task.Origin;
import com.meli.core.AidMessageUseCase;
import com.meli.core.LocationUseCase;
import com.meli.core.SatelliteUseCase;
import com.meli.core.entity.Point;
import com.meli.core.entity.Satellite;
import com.meli.core.entity.Target;

import reactor.core.publisher.Mono;

@Service
public class TopSecretServiceImpl implements TopSecretService {

    private final LocationUseCase locationUseCase;
    private final AidMessageUseCase aidMessageUseCase;
    private final SatelliteUseCase satelliteUseCase;
    private final MessageService messageService;

    private final Task saveSatelliteTask = new Task("SAVE_SATELLITE_TASK", "Guardar satélite");
    private final Task getTargetTask = new Task("GET_TARGET", "Obtención del objetivo");

    public TopSecretServiceImpl(LocationUseCase locationUseCase, AidMessageUseCase aidMessageUseCase,
            SatelliteUseCase satelliteUseCase, MessageService messageService) {
        this.locationUseCase = locationUseCase;
        this.aidMessageUseCase = aidMessageUseCase;
        this.satelliteUseCase = satelliteUseCase;
        this.messageService = messageService;
    }

    @Override
    public Mono<Target> getTarget(SatelliteInfoDto[] satellites) {

        getTargetTask.setOrigin(Origin.builder().originClass("TopSecretServiceImpl")
                .originMethod("getTarget(Satellite[] satellites)").build());

        return satelliteUseCase.getSatellites().collectList().flatMap(satelliteLocations -> {

            for (Satellite satellitePosition : satelliteLocations) {
                for (SatelliteInfoDto satellite : satellites) {
                    if (satellite.getName().equals(satellitePosition.getName())) {
                        satellitePosition.setDistance(satellite.getDistance());
                        satellitePosition.setMessage(satellite.getMessage().toArray(new String[0]));
                        break;
                    }
                }
            }
            
            Satellite[] satellitesArray = satelliteLocations.toArray(new Satellite[0]);

            this.validateSatellites(satellitesArray);

            for (Satellite satellite : satellitesArray) {
                this.updateSatellite(SatelliteMapper.toDto(satellite)).subscribe();
            }

            return processSatellites(satellitesArray);
        });
    }

    @Override
    public Mono<Void> updateSatellite(SatelliteInfoDto satellite) {
        saveSatelliteTask.setOrigin(Origin.builder().originClass("TopSecretServiceImpl")
                .originMethod("saveSatellite(Satellite satellite)").build());
        return satelliteUseCase.updateSatellite(SatelliteMapper.toEntity(satellite)).then();
    }

    @Override
    public Mono<Target> getTarget() {
        getTargetTask.setOrigin(Origin.builder().originClass("TopSecretServiceImpl")
                .originMethod("getTarget()").build());
        return satelliteUseCase.getSatellites().collectList().flatMap(satellites -> {
            Satellite[] satellitesArray = satellites.toArray(new Satellite[0]);
            this.validateSatellites(satellitesArray);
            return processSatellites(satellitesArray);
        });
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
            if (satellites[i].getLocation() == null || satellites[i].getDistance() == null) {
                throw new ServiceException(messageService.mapMessage("INSUFFICIENT_INFORMATION"), null,
                        getTargetTask, null, satellites);
            }
        }
    }

    private Mono<Target> processSatellites(Satellite[] satellites) {
        Point targetLocation = locationUseCase.getLocation(satellites);
        String[][] messages = Arrays.stream(satellites).map(Satellite::getMessage).toArray(String[][]::new);
        String message = aidMessageUseCase.getMessage(messages);
        return Mono.just(Target.builder().location(targetLocation).message(message).build());
    }
}
