package com.meli.application.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.meli.application.dto.SatelliteInfoDto;
import com.meli.application.dto.TargetDto;
import com.meli.application.dto.TopSecretPostRequestDto;
import com.meli.application.service.TopSecretService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/${api.context-path}/${api.version}")
public class TopSecretController {

    private final TopSecretService topSecretService;

    public TopSecretController(TopSecretService topSecretService) {
        this.topSecretService = topSecretService;
    }

    @Operation(summary = "Obtener posición y mensaje", description = "Devuelve la ubicación de la nave y el mensaje reconstruido.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {
                  "position": { "x": -100.0, "y": 75.5 },
                  "message": "este es un mensaje secreto"
                }
                """))),
        @ApiResponse(responseCode = "404", description = "Información insuficiente", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {
                  "status": 404,
                  "error": "Not Found",
                  "message": "No hay suficiente información para determinar la posición y el mensaje"
                }
                """)))
    })
    @PostMapping("/topsecret")
    public Mono<ResponseEntity<TargetDto>> getTarget(@Valid @RequestBody TopSecretPostRequestDto requestBody) {
        return topSecretService.getTarget(requestBody.getSatellites().toArray(new SatelliteInfoDto[0]))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar información de un satélite", description = "Recibe la información de un satélite individual.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Información actualizada con éxito"),
        @ApiResponse(responseCode = "404", description = "Satélite no encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {
                  "status": 404,
                  "error": "Not Found",
                  "message": "Satélite no encontrado"
                }
                """)))
    })
    @PostMapping("/topsecret_split/{satellite_name}")
    public Mono<ResponseEntity<Object>> updateSatellite(
            @Parameter(description = "Nombre del satélite", required = true) @PathVariable String satellite_name,
            @Valid @RequestBody SatelliteInfoDto satellite) {
        return topSecretService.updateSatellite(satellite)
                .then(Mono.just(ResponseEntity.ok().build()))
                .onErrorReturn(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener posición y mensaje desde satélites actualizados", description = "Devuelve la posición y el mensaje si se han recibido datos suficientes.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {
                  "position": { "x": -100.0, "y": 75.5 },
                  "message": "este es un mensaje secreto"
                }
                """))),
        @ApiResponse(responseCode = "404", description = "Información insuficiente", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {
                  "status": 404,
                  "error": "Not Found",
                  "message": "No hay suficiente información para determinar la posición y el mensaje"
                }
                """)))
    })
    @GetMapping("/topsecret_split")
    public Mono<ResponseEntity<TargetDto>> getTarget() {
        return topSecretService.getTarget()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
