package com.perpetmatch.Domain;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@EqualsAndHashCode(of="id")
public class Zone {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String city;

    public Zone(String city) {
        this.city = city;
    }
}
