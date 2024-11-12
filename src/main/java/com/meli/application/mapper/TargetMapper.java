package com.meli.application.mapper;

import com.meli.application.dto.TargetDto;
import com.meli.core.entity.Target;

public class TargetMapper {

    public static TargetDto toDto(Target target) {
        if (target == null) {
            return null;
        }
        TargetDto targetDto = new TargetDto();
        targetDto.setPosition(PositionMapper.toDto(target.getPosition()));
        targetDto.setMessage(target.getMessage());
        return targetDto;
    }

    public static Target toEntity(TargetDto targetDto) {
        if (targetDto == null) {
            return null;
        }
        return Target.builder()
                .position(PositionMapper.toEntity(targetDto.getPosition()))
                .message(targetDto.getMessage())
                .build();
    }
}
