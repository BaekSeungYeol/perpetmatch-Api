package com.perpetmatch.Domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
@EqualsAndHashCode(of = "id")
public class PetAge {

    @Id @GeneratedValue
    private Long id;

    private String petRange;


    @Override
    public String toString() {
        return petRange;
    }
}
