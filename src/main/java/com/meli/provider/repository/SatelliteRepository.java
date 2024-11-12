package com.meli.provider.repository;

import com.meli.provider.model.SatelliteModel;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


/**
 * Repositorio para gestionar operaciones con SatelliteModel en una base de datos MongoDB de manera reactiva.
 */
@Repository
public interface SatelliteRepository extends ReactiveMongoRepository<SatelliteModel, String> {
    
    /**
     * Encuentra una instancia de SatelliteModel con el nombre dado.
     *
     * @param name el nombre del satélite a buscar
     * @return un Mono que emite la instancia encontrada de SatelliteModel, o vacío si no se encuentra
     */
    Mono<SatelliteModel> findByName(String name);
}
