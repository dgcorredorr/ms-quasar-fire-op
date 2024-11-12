package com.meli.application.mapper;

import com.meli.application.dto.PositionDto;
import com.meli.core.entity.Point;

public class PositionMapper {

    public static PositionDto toDto(Point point) {
        if (point == null) {
            return null;
        }
        PositionDto positionDto = new PositionDto();
        positionDto.setX(point.getX());
        positionDto.setY(point.getY());
        return positionDto;
    }

    public static Point toEntity(PositionDto positionDto) {
        if (positionDto == null) {
            return null;
        }
        return Point.builder()
                .x(positionDto.getX())
                .y(positionDto.getY())
                .build();
    }
}
