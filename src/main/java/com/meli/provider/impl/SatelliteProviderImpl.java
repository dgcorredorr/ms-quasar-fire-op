package com.meli.provider.impl;

import com.meli.common.configuration.GeneralConfig;
import com.meli.core.entity.Satellite;
import com.meli.provider.SatelliteProvider;
import com.meli.provider.mapper.SatelliteMapper;
import com.meli.provider.repository.SatelliteRepository;

import co.elastic.apm.api.CaptureSpan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SatelliteProviderImpl implements SatelliteProvider {

    @Autowired
    private SatelliteRepository satelliteRepository;

    @Autowired
    private SatelliteMapper satelliteMapper;

    private final List<Satellite> satelliteCache = new CopyOnWriteArrayList<>();

    @PostConstruct
    @CaptureSpan("initSatelliteCache")
    public void init() {
        satelliteRepository.findAll()
            .collectList()
            .flatMapMany(Flux::fromIterable)
            .flatMap(satelliteMapper::toEntity)
            .collectList()
            .doOnNext(satellites -> {
                satelliteCache.clear();
                satelliteCache.addAll(satellites);
            })
            .subscribe();
    }

    @Override
    @CaptureSpan("loadSatellites")
    public Mono<Void> loadSatellites() {
        return Mono.fromRunnable(this::init);
    }

    @Override
    @CaptureSpan("getSatellites")
    public Flux<Satellite> getSatellites() {
        return Flux.fromIterable(satelliteCache);
    }

    @Override
    @CaptureSpan("getSatellite")
    public Mono<Satellite> getSatellite(String name) {
        return Mono.justOrEmpty(satelliteCache.stream()
                .filter(satellite -> satellite.getName().equals(name))
                .findFirst());
    }

    @Override
    @CaptureSpan("mapSatellite")
    public Satellite mapSatellite(String name) {
        return satelliteCache.stream()
                .filter(satellite -> satellite.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    @CaptureSpan("updateSatellite")
    public Mono<Satellite> updateSatellite(Satellite satellite) {
        return satelliteRepository.findByName(satellite.getName())
                .flatMap(existingSatellite -> {
                    existingSatellite.setMessage(satellite.getMessage());
                    existingSatellite.setDistance(satellite.getDistance());
                    existingSatellite.setUpdatedBy(GeneralConfig.getAppId());
                    return satelliteRepository.save(existingSatellite);
                })
                .flatMap(satelliteMapper::toEntity)
                .doOnNext(updatedSatellite -> {
                    satelliteCache.removeIf(s -> s.getName().equals(updatedSatellite.getName()));
                    satelliteCache.add(updatedSatellite);
                });
    }

    @Override
    @CaptureSpan("updateSatellitesBatch")
    public Flux<Satellite> updateSatellitesBatch(List<Satellite> satellites) {
        return Flux.fromIterable(satellites)
                .flatMap(satellite -> satelliteRepository.findByName(satellite.getName())
                        .flatMap(existingSatellite -> {
                            existingSatellite.setMessage(satellite.getMessage());
                            existingSatellite.setDistance(satellite.getDistance());
                            existingSatellite.setUpdatedBy(GeneralConfig.getAppId());
                            return satelliteRepository.save(existingSatellite);
                        })
                        .flatMap(satelliteMapper::toEntity))
                        .doOnNext(updatedSatellite -> {
                            satelliteCache.removeIf(s -> s.getName().equals(updatedSatellite.getName()));
                            satelliteCache.add(updatedSatellite);
                        });
    }
}