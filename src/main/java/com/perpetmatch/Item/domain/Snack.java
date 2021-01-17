package com.perpetmatch.Item.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Snack")
@Getter
@Setter
public class Snack extends Item{

}
