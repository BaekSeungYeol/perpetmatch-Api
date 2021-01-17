package com.perpetmatch.Item.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Goods")
@Getter
@Setter
public class Goods extends Item{


}
