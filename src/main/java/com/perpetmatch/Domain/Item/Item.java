package com.perpetmatch.Domain.Item;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.perpetmatch.modules.Item.ItemType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    private String title;
    private int price;
    private int stockQuantity;
    private int sale;

    private LocalDateTime publishedDateTime;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String boardImageHead;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String boardImageMain;
    private String company;

    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    @ElementCollection
    private Set<String> options = new HashSet<>();


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

