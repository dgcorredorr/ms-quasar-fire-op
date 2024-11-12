package com.meli.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * Estructura utilizada por el servicio para devolver la posición del objetivo.
 *
 * <p>Esta clase representa el formato de respuesta utilizado para devolver la posición
 * del objetivo, incluyendo las coordenadas X e Y.</p>
 *
 * @see Serializable
 */
@Data
@Schema(description = "Estructura de respuesta para devolver la posición del objetivo.")
public class PositionDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Coordenada X del objetivo.", example = "-100.0")
    @NotNull(message = "La coordenada X no puede ser nula.")
    private Double x;

    @Schema(description = "Coordenada Y del objetivo.", example = "75.5")
    @NotNull(message = "La coordenada Y no puede ser nula.")
    private Double y;

    public void setX(Double x) {
        this.x = formatDouble(x);
    }

    public void setY(Double y) {
        this.y = formatDouble(y);
    }

    private Double formatDouble(Double value) {
        DecimalFormat df = new DecimalFormat("#.#");
        return Double.valueOf(df.format(value));
    }
}
