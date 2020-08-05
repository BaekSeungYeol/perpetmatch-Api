package com.perpetmatch.api.dto;

import lombok.*;

@EqualsAndHashCode(of = "id")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AllMembersResponse<T> {

    private T users;
}
