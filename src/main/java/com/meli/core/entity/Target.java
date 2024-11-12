package com.meli.core.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Target {
    private Point location;
    private String message;
}
