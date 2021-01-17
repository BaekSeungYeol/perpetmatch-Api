package com.perpetmatch.Item.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Feed")
@Getter
@Setter
public class Feed extends Item{

    public Feed() {
    }
}
