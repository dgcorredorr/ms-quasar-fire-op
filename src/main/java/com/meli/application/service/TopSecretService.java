package com.meli.application.service;

import com.meli.application.dto.SatelliteInfoDto;
import com.meli.application.dto.TargetDto;
import com.meli.application.dto.UpdateSatelliteInfoDto;

import reactor.core.publisher.Mono;

public interface TopSecretService {

    /**
     * Obtiene el objetivo a partir de un array de satélites.
     *
     * @param satellites Array de satélites.
     * @return El objetivo.
     */
    Mono<TargetDto> getTarget(SatelliteInfoDto[] satellites);

    /**
     * Guarda la información de un satélite.
     *
     * @param satellite El satélite a guardar.
     * @param satelliteName El nombre del satélite.
     */
    Mono<Void> updateSatellite(UpdateSatelliteInfoDto satellite, String satelliteName);

    /**
     * Obtiene el objetivo una vez haya información suficiente.
     *
     * @return El objetivo.
     */
    Mono<TargetDto> getTarget();
}
