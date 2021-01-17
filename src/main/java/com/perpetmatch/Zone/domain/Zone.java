package com.perpetmatch.Zone.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Zone {

    @Id @GeneratedValue
    private Long id;

    private String province;

    @Override
    public String toString() {
        return province;
    }
}
