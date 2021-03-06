package com.perpetmatch.Order.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.perpetmatch.Item.domain.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue
    @Column
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn
    private Item item;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn
    private Order order;


    private int orderPrice; // 주문 가격
    private int count; // 주문 수량

    //==생성 메서드==/
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

       // item.removeStock(count);
        return orderItem;
    }

    //==비즈니스 로직==//
    public void cancel() {
        getItem().addStock(count);
    }

    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}

