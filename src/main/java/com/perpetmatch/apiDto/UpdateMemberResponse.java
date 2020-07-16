package com.perpetmatch.apiDto;

import lombok.*;

@EqualsAndHashCode(of = "id")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemberResponse {

    private Long id;
}

