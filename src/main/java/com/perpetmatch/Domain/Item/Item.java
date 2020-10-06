package com.perpetmatch.Domain.Item;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public abstract class Item {

    @Id
    @GeneratedValue
    @Column
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0) {
            //TODO throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}

