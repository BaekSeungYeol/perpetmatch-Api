package com.perpetmatch.apiDto.Member;

import lombok.*;

@EqualsAndHashCode(of = "id")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDto {

    private Long id;

    private String name;

    private String email;
}
