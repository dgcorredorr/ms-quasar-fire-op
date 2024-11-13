package com.meli.application.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Estructura utilizada por el servicio para recibir la información de un satélite en la actualización.
 *
 * <p>Esta clase representa el formato de solicitud utilizado para actualizar la información
 * de un satélite. Contiene la distancia y el mensaje recibido por el satélite.</p>
 *
 * @see Serializable
 */
@Data
@Builder
@Schema(description = "Estructura de petición para actualizar la información de un satélite.")
public class UpdateSatelliteInfoDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Distancia al emisor tal cual se recibe en el satélite.")
    @NotNull(message = "La distancia no puede ser nula.")
    private Double distance;

    @Schema(description = "El mensaje tal cual es recibido en el satélite.")
    @NotNull(message = "El mensaje no puede ser nulo.")
    @NotEmpty(message = "El mensaje no puede estar vacío.")
    private transient List<String> message;
}