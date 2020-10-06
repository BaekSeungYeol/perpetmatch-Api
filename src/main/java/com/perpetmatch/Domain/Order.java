package com.perpetmatch.Domain;

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
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Order {

    @Id @GeneratedValue
    @Column
    private Long id;

    @ManyToOne(fetch = LAZY)
    private User user;

//    (mappedBy = "order", cascade = CascadeType.ALL)
    @OneToMany
    private Set<OrderItem> orderItems = new HashSet<>();

    @OneToOne(fetch = LAZY)
    private Delivery delivery;

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
