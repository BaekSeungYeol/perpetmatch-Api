package com.perpetmatch.AdoptBoard.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AdoptBoardUpdateEvent {

    private final Board AdoptBoard;

    private final String message;

}
