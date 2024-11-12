package com.meli.provider;

import com.meli.core.entity.Satellite;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Proveedor de satélites utilizado para interactuar con el almacenamiento de satélites.
 */
public interface SatelliteProvider {

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
     * Mapea un satélite por su nombre.
     *
     * @param name El nombre del satélite a mapear.
     * @return El satélite mapeado o null si no se encuentra.
     */
    Satellite mapSatellite(String name);
}
