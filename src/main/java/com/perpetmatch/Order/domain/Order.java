package com.perpetmatch.Order.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.perpetmatch.User.domain.User;
import com.perpetmatch.Delivery.domain.Delivery;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "orders")
@Getter @Setter
@EqualsAndHashCode(of = "id")
public class Order {

    @Id @GeneratedValue
    @Column
    private Long id;

    //private Long userId;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<OrderItem> orderItems = new HashSet<>();


    @OneToOne(fetch = LAZY)
    private Delivery delivery;


    @ManyToOne(fetch = LAZY)
    private User user;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 [ORDER, CANCEL]

    //==연관관계 메서드==//
    public void setUser(User user) {
        this.user = user;
        user.getOrders().add(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }


    //== 생성 메서드 ==//
    public static Order createOrder(User user,Delivery delivery, ArrayList<OrderItem> orderItems) {
        Order order = new Order();
        order.setUser(user);
        order.setDelivery(delivery);
        for(OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }
    /**
     * 주문 취소
     */
    public void cancel() {
        this.setStatus(OrderStatus.CANCEL);
        for(OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }
    public int getTotalPrice() {
        int totalPrice = orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
        return totalPrice;
    }

}
