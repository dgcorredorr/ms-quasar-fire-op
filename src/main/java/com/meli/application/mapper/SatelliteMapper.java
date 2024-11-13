package com.meli.application.mapper;

import java.util.List;

import com.meli.application.dto.SatelliteInfoDto;
import com.meli.application.dto.UpdateSatelliteInfoDto;
import com.meli.core.entity.Satellite;

public class SatelliteMapper {

    public static Satellite toEntity(UpdateSatelliteInfoDto dto) {
        return Satellite.builder()
                .distance(dto.getDistance())
                .message(dto.getMessage().toArray(new String[0]))
                .build();
    }

    public static SatelliteInfoDto toDto(Satellite entity) {
        return SatelliteInfoDto.builder()
                .name(entity.getName())
                .distance(entity.getDistance())
                .message(List.of(entity.getMessage()))
                .build();
    }
}
