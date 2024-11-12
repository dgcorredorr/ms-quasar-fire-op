package com.meli.core;

import com.meli.core.entity.Satellite;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Caso de uso para la gestión de satélites.
 */
public interface SatelliteUseCase {

    /**
     * Carga los satélites disponibles de manera reactiva.
     *
     * @return Un Mono que indica la finalización de la carga.
     */
    Mono<Void> loadSatellites();

    /**
     * Obtiene todos los satélites disponibles de manera reactiva.
     *
     * @return Un Flux que contiene la lista de satélites.
     */
    Flux<Satellite> getSatellites();

    /**
     * Obtiene un satélite por su nombre de manera reactiva.
     *
     * @param name El nombre del satélite a buscar.
     * @return Un Mono que contiene el satélite encontrado o vacío si no existe.
     */
    Mono<Satellite> getSatellite(String name);

    /**
     * Guarda un satélite de manera reactiva.
     *
     * @param satellite El satélite a guardar.
     * @return Un Mono que contiene el satélite guardado.
     */
    Mono<Satellite> updateSatellite(Satellite satellite);
}
