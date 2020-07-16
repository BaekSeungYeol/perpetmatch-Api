package com.perpetmatch.Domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of="id")
public class Pet {

    @Id
    @GeneratedValue
    private Long id;

    private String kind;

    public Pet(String kind) {
        this.kind = kind;
    }
}
