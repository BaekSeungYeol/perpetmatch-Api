package com.perpetmatch.api.dto;

import lombok.*;

@EqualsAndHashCode(of = "id")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class JoinMemberResponse {

    private Long id;
}
