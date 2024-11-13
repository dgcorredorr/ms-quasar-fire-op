package com.meli.application.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;

import com.meli.application.dto.GenericResponseDto;
import com.meli.application.dto.SatelliteInfoDto;
import com.meli.application.dto.TargetDto;
import com.meli.application.dto.TopSecretPostRequestDto;
import com.meli.application.dto.UpdateSatelliteInfoDto;
import com.meli.application.service.MessageService;
import com.meli.application.service.TopSecretService;
import com.meli.common.utils.enums.MessageMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/${api.context-path}/${api.version}")
public class TopSecretController {

  private final TopSecretService topSecretService;
  private final MessageService messageService;

  public TopSecretController(TopSecretService topSecretService, MessageService messageService) {
    this.topSecretService = topSecretService;
    this.messageService = messageService;
  }

  @Operation(summary = "Obtener posición y mensaje", description = "Devuelve la ubicación de la nave y el mensaje reconstruido.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TargetDto.class), examples = @ExampleObject(value = """
          {
            "position": {
                "x": -487.3,
                "y": 1557.0
            },
            "message": "este es un mensaje secreto"
          }
          """))),
      @ApiResponse(responseCode = "404", description = "Información insuficiente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponseDto.class), examples = @ExampleObject(value = """
          {
            "success": false,
            "origin": "/api/V1/topsecret",
            "message": "No hay suficiente información para determinar la posición y el mensaje",
            "timestamp": "2024-11-13T00:30:22.9504215",
            "requestId": "[3a112f93-52] "
          }
          """))),
      @ApiResponse(responseCode = "400", description = "Solicitud incorrecta, validación de campos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponseDto.class), examples = @ExampleObject(value = """
          {
            "success": false,
            "origin": "/api/V1/topsecret",
            "message": "Validation failed",
            "timestamp": "2024-11-13T00:35:51.0895544",
            "requestId": "[78bd35bf-54] ",
            "validationErrors": {
                "satellites[1].name": "El nombre no puede estar vacío., El nombre no puede ser nulo."
            }
          }
          """)))
  })
  @PostMapping("/topsecret")
  public Mono<ResponseEntity<TargetDto>> getTarget(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados del ejemplo", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "satellites": [
                {
                    "name": "kenobi",
                    "distance": 100.0,
                    "message": ["este", "", "", "mensaje", ""]
                },
                {
                    "name": "skywalker",
                    "distance": 115.5,
                    "message": ["", "es", "", "", "secreto"]
                },
                {
                    "name": "sato",
                    "distance": 142.7,
                    "message": ["este", "", "un", "", ""]
                }
            ]
          }
          """))) @Valid @RequestBody TopSecretPostRequestDto requestBody) {
    return topSecretService.getTarget(requestBody.getSatellites().toArray(new SatelliteInfoDto[0]))
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @Operation(summary = "Actualizar información de un satélite", description = "Recibe la información de un satélite individual.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Información actualizada con éxito", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponseDto.class), examples = @ExampleObject(value = """
          {
            "success": true,
            "origin": "/api/V1/topsecret_split/skywalker",
            "message": "Proceso ejecutado correctamente.",
            "timestamp": "2024-11-12T23:05:15.8093997",
            "requestId": "bfe99e61-34"
          }
          """))),
      @ApiResponse(responseCode = "404", description = "Satélite no encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponseDto.class), examples = @ExampleObject(value = """
          {
            "success": false,
            "origin": "/api/V1/topsecret_split/skywalkers",
            "message": "Error al actualizar la información del satélite.",
            "timestamp": "2024-11-13T00:26:27.6554886",
            "requestId": "[60077023-42] "
          }
          """)))
  })
  @PostMapping("/topsecret_split/{satellite_name}")
  public Mono<ResponseEntity<GenericResponseDto>> updateSatellite(
      @Parameter(description = "Nombre del satélite", required = true) @PathVariable(required = true, name = "satellite_name") String satelliteName,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados del ejemplo", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
          {
            "distance": 90,
            "message": ["", "", "es", "", "", "secreto"]
          }
          """))) @Valid @RequestBody UpdateSatelliteInfoDto satellite,
      ServerWebExchange exchange) {
    return topSecretService.updateSatellite(satellite, satelliteName)
        .then(Mono.defer(() -> {
          String mappedMessage = messageService.mapMessage(MessageMapping.DEFAULT_SUCCESS.toString());
          GenericResponseDto responseBuilder = GenericResponseDto.builder()
              .message(mappedMessage)
              .origin(exchange.getRequest().getPath().value())
              .success(true)
              .timestamp(LocalDateTime.now())
              .requestId(exchange.getRequest().getId())
              .build();
          return Mono.just(ResponseEntity.ok(responseBuilder));
        }));
  }

  @Operation(summary = "Obtener posición y mensaje desde satélites actualizados", description = "Devuelve la posición y el mensaje si se han recibido datos suficientes.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TargetDto.class), examples = @ExampleObject(value = """
          {
            "position": {
                "x": -487.3,
                "y": 1557.0
            },
            "message": "este es un mensaje secreto"
          }
          """))),
      @ApiResponse(responseCode = "404", description = "Información insuficiente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponseDto.class), examples = @ExampleObject(value = """
          {
            "success": false,
            "origin": "/api/V1/topsecret",
            "message": "No hay suficiente información para determinar la posición y el mensaje",
            "timestamp": "2024-11-13T00:30:22.9504215",
            "requestId": "[3a112f93-52] "
          }
          """))),
      @ApiResponse(responseCode = "400", description = "Solicitud incorrecta, validación de campos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponseDto.class), examples = @ExampleObject(value = """
          {
            "success": false,
            "origin": "/api/V1/topsecret_split/skywalker",
            "message": "Validation failed",
            "timestamp": "2024-11-13T00:32:25.1768275",
            "requestId": "[54b63d5e-53] ",
            "validationErrors": {
              "message": "El mensaje no puede ser nulo., El mensaje no puede estar vacío."
            }
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
