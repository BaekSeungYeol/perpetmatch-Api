package com.perpetmatch.api.dto.Board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardApplyUsers {

    List<ApplyUsers> users = new ArrayList<>();

}
