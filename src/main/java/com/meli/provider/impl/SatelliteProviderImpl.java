package com.meli.provider.impl;

import com.meli.application.service.MessageService;
import com.meli.common.configuration.GeneralConfig;
import com.meli.common.exception.ServiceException;
import com.meli.common.utils.tasks.Task;
import com.meli.common.utils.tasks.Task.Origin;
import com.meli.core.entity.Satellite;
import com.meli.provider.SatelliteProvider;
import com.meli.provider.mapper.SatelliteMapper;
import com.meli.provider.repository.SatelliteRepository;

import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Span;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SatelliteProviderImpl implements SatelliteProvider {

    private final SatelliteRepository satelliteRepository;
    private final MessageService messageService;

    private final SatelliteMapper satelliteMapper;

    private final List<Satellite> satelliteCache = new CopyOnWriteArrayList<>();

    private final Task saveSatelliteTask = new Task("SAVE_SATELLITE_TASK", "Guardar satélite");
    private final Task findAllSatellitesTask = new Task("FIND_ALL_SATELLITES", "Buscar todos los satélites");

    public SatelliteProviderImpl(SatelliteRepository satelliteRepository, SatelliteMapper satelliteMapper,
            MessageService messageService) {
        this.satelliteRepository = satelliteRepository;
        this.satelliteMapper = satelliteMapper;
        this.messageService = messageService;
    }

    @PostConstruct
    public void init() {
        findAllSatellitesTask.setOrigin(
                Origin.builder().originClass("SatelliteProviderImpl").originMethod("init").build());
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
                .doOnError(exception -> {
                    span.captureException(exception);
                    throw new ServiceException(
                            messageService.mapMessage("QUERY_SATELLITES_ERROR"),
                            HttpStatus.NOT_FOUND,
                            exception,
                            findAllSatellitesTask,
                            exception.getClass(),
                            null);
                })
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
        saveSatelliteTask.setOrigin(
                Origin.builder().originClass("SatelliteProviderImpl").originMethod("updateSatellite").build());
        Span span = ElasticApm.currentSpan().startSpan("db", "mongodb", "save");
        span.setName("MongoDB Update Satellite");
        return satelliteRepository.findByName(satellite.getName())
                .switchIfEmpty(Mono.error(new ServiceException(
                        messageService.mapMessage("SATELLITE_NOT_FOUND"),
                        HttpStatus.NOT_FOUND,
                        null,
                        saveSatelliteTask,
                        null,
                        satellite)))
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
                .doOnError(exception -> {
                    span.captureException(exception);
                    throw new ServiceException(
                            messageService.mapMessage("SAVE_SATELLITES_ERROR"),
                            HttpStatus.NOT_FOUND,
                            exception,
                            saveSatelliteTask,
                            exception.getClass(),
                            satellite);
                })
                .doFinally(signalType -> span.end());
    }

    @Override
    public Flux<Satellite> updateSatellitesBatch(List<Satellite> satellites) {
        saveSatelliteTask.setOrigin(
                Origin.builder().originClass("SatelliteProviderImpl").originMethod("updateSatellitesBatch").build());
        Span span = ElasticApm.currentSpan().startSpan("db", "mongodb", "save");
        span.setName("MongoDB Update Satellites Batch");
        return Flux.fromIterable(satellites)
                .flatMap(satellite -> satelliteRepository.findByName(satellite.getName())
                        .switchIfEmpty(Mono.error(new ServiceException(
                                messageService.mapMessage("SATELLITE_NOT_FOUND"),
                                HttpStatus.NOT_FOUND,
                                null,
                                saveSatelliteTask,
                                null,
                                satellite)))
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
                .doOnError(exception -> {
                    span.captureException(exception);
                    throw new ServiceException(
                            messageService.mapMessage("SAVE_SATELLITES_ERROR"),
                            HttpStatus.NOT_FOUND,
                            exception,
                            saveSatelliteTask,
                            exception.getClass(),
                            satellites);
                })
                .doFinally(signalType -> span.end());
    }
}