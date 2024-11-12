package com.meli.core.impl;

import com.meli.core.SatelliteUseCase;
import com.meli.core.entity.Satellite;
import com.meli.provider.SatelliteProvider;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Service;

@Service
public class SatelliteUseCaseImpl implements SatelliteUseCase {

    private final SatelliteProvider satelliteProvider;

    public SatelliteUseCaseImpl(SatelliteProvider satelliteProvider) {
        this.satelliteProvider = satelliteProvider;
    }

    @Override
    public Mono<Void> loadSatellites() {
        return satelliteProvider.loadSatellites();
    }

    @Override
    public Flux<Satellite> getSatellites() {
        return satelliteProvider.getSatellites();
    }

    @Override
    public Mono<Satellite> getSatellite(String name) {
        return satelliteProvider.getSatellite(name);
    }

    @Override
    public Mono<Satellite> updateSatellite(Satellite satellite) {
        return satelliteProvider.updateSatellite(satellite);
    }
}
