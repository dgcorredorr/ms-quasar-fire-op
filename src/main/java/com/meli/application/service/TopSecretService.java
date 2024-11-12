package com.meli.application.service;

import com.meli.application.dto.SatelliteInfoDto;
import com.meli.application.dto.TargetDto;

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
     */
    Mono<Void> updateSatellite(SatelliteInfoDto satellite);

    /**
     * Obtiene el objetivo una vez haya información suficiente.
     *
     * @return El objetivo.
     */
    Mono<TargetDto> getTarget();
}
