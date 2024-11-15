package com.meli.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * Estructura utilizada por el servicio para devolver la información del objetivo.
 *
 * <p>Esta clase representa el formato de respuesta utilizado para devolver la información
 * del objetivo, incluyendo la posición y el mensaje decodificado.</p>
 *
 * @see Serializable
 */
@Data
@Schema(description = "Estructura de respuesta para devolver la información del objetivo.")
public class TargetDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Posición del objetivo.", example = "{x: -100.0, y: 75.5}")
    @NotNull(message = "La posición no puede ser nula.")
    @Valid
    private PositionDto position;

    @Schema(description = "Mensaje decodificado del objetivo.", example = "Este es un mensaje secreto.")
    @NotNull(message = "El mensaje no puede ser nulo.")
    private String message;
}
