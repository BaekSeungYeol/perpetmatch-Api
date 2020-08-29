package com.perpetmatch.api.dto.Board;

import com.perpetmatch.Domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardApplyUsers {

    List<String> users = new ArrayList<>();


}
