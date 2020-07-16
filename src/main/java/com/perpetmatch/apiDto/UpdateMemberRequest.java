package com.perpetmatch.apiDto;

import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@EqualsAndHashCode(of = "id")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemberRequest {

    private Long id;

    private String name;

    private String password;

    private String email;
}
