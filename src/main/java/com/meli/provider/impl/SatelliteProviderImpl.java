package com.meli.provider.impl;

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
        this.satelliteCache.clear();
        satelliteRepository.findAll()
                .flatMap(satelliteMapper::toEntity)
                .doOnNext(satelliteCache::add)
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
}