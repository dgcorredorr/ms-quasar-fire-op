package com.fstech.application.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MockDto {

    private int userId;
    private int id;
    private String title;
    private boolean completed;

    public MockDto(int userId, int id, String title, boolean completed) {
        this.userId = userId;
        this.id = id;
        this.title = title;
        this.completed = completed;
    }
}
