package com.meli.provider.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import com.meli.core.entity.Point;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "coll_satellite")
public class SatelliteModel {
    @Field("id")
    private String satelliteId;
    @Indexed(unique = true)
    private String name;
    private Point location;
    private Double distance;
    private String[] message;
    @CreatedDate
    @Field(name = "createdAt")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Field(name = "updatedAt")
    private LocalDateTime updatedAt;
}