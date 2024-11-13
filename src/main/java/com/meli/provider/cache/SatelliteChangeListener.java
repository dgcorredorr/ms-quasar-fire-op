package com.meli.provider.cache;

import org.bson.BsonDocument;
import org.bson.Document;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;

import com.meli.common.configuration.GeneralConfig;
import com.meli.common.utils.enums.LogLevel;
import com.meli.common.utils.log.ServiceLogger;
import com.meli.common.utils.tasks.Task;
import com.meli.common.utils.tasks.Task.Origin;
import com.meli.provider.SatelliteProvider;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;
import com.mongodb.client.model.changestream.UpdateDescription;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class SatelliteChangeListener {

    private final ServiceLogger<SatelliteChangeListener> logger = new ServiceLogger<>(SatelliteChangeListener.class);
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final SatelliteProvider satelliteProvider;

    private static final Task task = new Task("SATELLITE_CACHE_UPDATED", "Satellite cache updated");

    public SatelliteChangeListener(ReactiveMongoTemplate reactiveMongoTemplate, SatelliteProvider satelliteProvider) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.satelliteProvider = satelliteProvider;
    }

    @PostConstruct
    public void init() {
        task.setOrigin(Origin.builder().originClass("SatelliteChangeListener").originMethod("init").build());

        // Observa los cambios en la colección de forma reactiva
        Flux<ChangeStreamDocument<Document>> changeStream = reactiveMongoTemplate
                .getCollection("coll_satellite")
                .flatMapMany(collection -> collection.watch(Document.class));

        // Procesa cada cambio y recarga los parámetros en `satelliteUseCase`
        changeStream
                .filter(change -> !isUpdatedBySelf(change))
                .filter(this::isRelevantChange)
                .flatMap(change -> satelliteProvider.loadSatellites()
                        .doOnSuccess(aVoid -> logger.log(
                                "Satellite cache updated",
                                task,
                                LogLevel.INFO,
                                change,
                                null)) // Log después de actualizar
                        .onErrorResume(e -> { // Maneja errores y continúa escuchando cambios
                            logger.log(
                                    "Error al cargar satélites en caché",
                                    task,
                                    LogLevel.ERROR,
                                    e,
                                    null);
                            return Mono.empty();
                        }))
                .subscribe();
    }

    // Filtra solo los cambios relevantes (INSERT, UPDATE, REPLACE, DELETE)
    private boolean isRelevantChange(ChangeStreamDocument<Document> changeStreamDocument) {
        OperationType operationType = changeStreamDocument.getOperationType();
        return operationType == OperationType.INSERT ||
                operationType == OperationType.UPDATE ||
                operationType == OperationType.REPLACE ||
                operationType == OperationType.DELETE;
    }

    private boolean isUpdatedBySelf(ChangeStreamDocument<Document> change) {
        UpdateDescription updateDescription = change.getUpdateDescription();
        if (updateDescription != null) {
            BsonDocument updatedFields = updateDescription.getUpdatedFields();
            return updatedFields.containsKey("updatedBy") && 
                   updatedFields.getString("updatedBy").toString().equals(GeneralConfig.getAppId());
        }
        return change.getFullDocument().get("updatedBy").equals(GeneralConfig.getAppId());
    }
}
