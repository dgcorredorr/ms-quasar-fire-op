package com.meli.core.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Satellite {
    private String name;
    private Point location;
    private Double distance;
    private String[] message;
}

