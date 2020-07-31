package com.perpetmatch.apiDto.Board;

import lombok.Data;

@Data
public class BoardResponse {
    private Long id;

    public BoardResponse(Long id) {
        this.id = id;
    }
}
