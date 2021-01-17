package com.perpetmatch.Order.application;

import com.perpetmatch.Item.domain.Item;
import com.perpetmatch.Item.domain.ItemRepository;
import com.perpetmatch.Order.domain.OrderRepository;
import com.perpetmatch.User.domain.UserRepository;
import com.perpetmatch.Delivery.domain.Delivery;
import com.perpetmatch.Delivery.domain.DeliveryRepository;
import com.perpetmatch.User.domain.User;
import com.perpetmatch.Order.domain.Order;
import com.perpetmatch.Order.domain.OrderItem;
import com.perpetmatch.Order.domain.OrderStatus;
import com.perpetmatch.api.dto.Order.AddressDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final DeliveryRepository deliveryRepository;

    public Order makeAndPayOrders(Long id, AddressDto addressDto) {

        User user = userRepository.findByIdWithBags(id);

        ArrayList<OrderItem> orderItems = makeOrderItems(user,id);
        Delivery delivery = makeDelivery(addressDto);

        Order order = makeOrder(user, orderItems, delivery);

        user.calculate();
        return orderRepository.findByIdWithUser(order.getId());
    }

    public Delivery makeDelivery(AddressDto addressDto) {
        Delivery delivery = new Delivery();
        delivery.setAddress(addressDto);
        return deliveryRepository.save(delivery);
    }

    private Order makeOrder(User user, ArrayList<OrderItem> orderItems, Delivery delivery) {
        Order order = new Order();
        order.setUser(user);
        for(OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
            removeStock(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);
        savedOrder.setDelivery(delivery);
        return savedOrder;
    }

    private void removeStock(OrderItem orderItem) {
        Item item = orderItem.getItem();
        item.setStockQuantity(Math.max(0,item.getStockQuantity() - orderItem.getCount()));
        itemRepository.save(item);
        // item.removeStock(orderItem.getCount());
    }
    public ArrayList<OrderItem> makeOrderItems(User user, Long id) {
        return new ArrayList<OrderItem>(user.getBag());

    }

}
