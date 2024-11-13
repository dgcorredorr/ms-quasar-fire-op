package com.meli.provider.impl;

import com.meli.common.configuration.GeneralConfig;
import com.meli.core.entity.Satellite;
import com.meli.provider.SatelliteProvider;
import com.meli.provider.mapper.SatelliteMapper;
import com.meli.provider.repository.SatelliteRepository;

import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Span;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SatelliteProviderImpl implements SatelliteProvider {

    private SatelliteRepository satelliteRepository;

    private SatelliteMapper satelliteMapper;

    private final List<Satellite> satelliteCache = new CopyOnWriteArrayList<>();

    public SatelliteProviderImpl(SatelliteRepository satelliteRepository, SatelliteMapper satelliteMapper) {
        this.satelliteRepository = satelliteRepository;
        this.satelliteMapper = satelliteMapper;
    }

    @PostConstruct
    public void init() {
        Span span = ElasticApm.currentSpan().startSpan("db", "mongodb", "query");
        span.setName("MongoDB Find All Satellites");
            satelliteRepository.findAll()
                    .collectList()
                    .flatMapMany(Flux::fromIterable)
                    .flatMap(satelliteMapper::toEntity)
                    .collectList()
                    .doOnNext(satellites -> {
                        satelliteCache.clear();
                        satelliteCache.addAll(satellites);
                    })
                    .doOnError(span::captureException)
                    .doFinally(signalType -> span.end())
                    .subscribe();
    }

    @Override
    public Mono<Void> loadSatellites() {
        return Mono.fromRunnable(this::init);
    }

    @Override
    public Flux<Satellite> getSatellites() {
        return Flux.fromIterable(satelliteCache);
    }

    @Override
    public Mono<Satellite> getSatellite(String name) {
        return Mono.justOrEmpty(satelliteCache.stream()
                .filter(satellite -> satellite.getName().equals(name))
                .findFirst());
    }

    @Override
    public Satellite mapSatellite(String name) {
        return satelliteCache.stream()
                .filter(satellite -> satellite.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Mono<Satellite> updateSatellite(Satellite satellite) {
        Span span = ElasticApm.currentSpan().startSpan("db", "mongodb", "save");
        span.setName("MongoDB Update Satellite");
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
                })
                .doOnError(span::captureException)
                .doFinally(signalType -> span.end());
    }

    @Override
    public Flux<Satellite> updateSatellitesBatch(List<Satellite> satellites) {
        Span span = ElasticApm.currentSpan().startSpan("db", "mongodb", "save");
        span.setName("MongoDB Update Satellites Batch");
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
                })
                .doOnError(span::captureException)
                .doFinally(signalType -> span.end());
    }
}