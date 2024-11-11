package com.meli.core.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Point {
    private double x;
    private double y;
}
