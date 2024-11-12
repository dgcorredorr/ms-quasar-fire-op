package com.meli.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Estructura utilizada por el servicio para recibir la información de múltiples satélites.
 *
 * <p>Esta clase representa el formato de solicitud utilizado para recibir la información
 * de múltiples satélites. Contiene una lista de satélites con sus respectivas distancias
 * y mensajes.</p>
 *
 * @see Serializable
 */
@Data
@Schema(description = "Estructura de petición para recibir la información de múltiples satélites.")
public class TopSecretPostRequestDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Lista de satélites con su información.")
    @NotNull(message = "La lista de satélites no puede ser nula.")
    @NotEmpty(message = "La lista de satélites no puede estar vacía.")
    @Valid
    private List<SatelliteInfoDto> satellites;
}
