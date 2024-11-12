package com.meli.provider.mapper;

import org.springframework.stereotype.Component;
import com.meli.core.entity.Satellite;
import com.meli.provider.model.SatelliteModel;
import reactor.core.publisher.Mono;

/**
 * El Mapper concreto utilizado para convertir objetos de tipo {@link Satellite} a {@link SatelliteModel} y viceversa.
 * Este Mapper se utiliza para facilitar la conversión entre las entidades y los modelos utilizados en la capa
 * de proveedores de la aplicación.
 *
 * @see Mapper
 * @see Satellite
 * @see SatelliteModel
 */
@Component
public class SatelliteMapper implements ReactiveMapper<Satellite, SatelliteModel> {

    /**
     * Convierte un objeto de tipo {@link SatelliteModel} en una entidad de tipo {@link Satellite}.
     *
     * @param model El objeto de tipo {@link SatelliteModel} que se va a convertir en entidad.
     * @return La entidad de tipo {@link Satellite} resultante.
     */
    @Override
    public Mono<Satellite> toEntity(SatelliteModel model) {
        return Mono.justOrEmpty(model)
                   .map(m -> Satellite.builder()
                                      .name(m.getName())
                                      .location(m.getLocation())
                                      .distance(m.getDistance())
                                      .message(m.getMessage())
                                      .build());
    }

    /**
     * Convierte una entidad de tipo {@link Satellite} en un objeto de tipo {@link SatelliteModel}.
     *
     * @param entity La entidad de tipo {@link Satellite} que se va a convertir en modelo.
     * @return El objeto de tipo {@link SatelliteModel} resultante.
     */
    @Override
    public Mono<SatelliteModel> toModel(Satellite entity) {
        return Mono.justOrEmpty(entity)
                   .map(e -> SatelliteModel.builder()
                                           .name(e.getName())
                                           .location(e.getLocation())
                                           .distance(e.getDistance())
                                           .message(e.getMessage())
                                           .build());
    }
}
