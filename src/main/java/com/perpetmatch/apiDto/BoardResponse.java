package com.perpetmatch.apiDto;

import lombok.Data;

@Data
public class BoardResponse {
    private Long id;

    public BoardResponse(Long id) {
        this.id = id;
    }
}
