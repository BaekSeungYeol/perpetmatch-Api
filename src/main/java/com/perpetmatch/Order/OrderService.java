package com.perpetmatch.Order;

import com.perpetmatch.Domain.*;
import com.perpetmatch.Domain.Item.Item;
import com.perpetmatch.Item.ItemRepository;
import com.perpetmatch.Member.UserRepository;
import com.perpetmatch.Member.UserService;
import com.perpetmatch.OrderItem.OrderItemRepository;
import com.perpetmatch.api.dto.Order.AddressDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final OrderItemRepository orderItemRepository;

    public void createOrder(Long id, AddressDto addressDto) {

        User user = userRepository.findByIdWithBags(id);
        Set<OrderItem> items = user.getBag();
        ArrayList<OrderItem> orderItems = new ArrayList<>(items);
        Delivery delivery = new Delivery();
        delivery.setAddress(addressDto);

        Order order = makeOrder(user, orderItems, delivery);

        calculate(user);

        orderRepository.save(order);
    }

    private Order makeOrder(User user, ArrayList<OrderItem> orderItems, Delivery delivery) {
        Order order = new Order();
        order.setUser(user);
        order.setDelivery(delivery);
        for(OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
            removeStock(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    private void removeStock(OrderItem orderItem) {
        Item item = itemRepository.findById(orderItem.getId()).get();
        item.removeStock(orderItem.getCount());
    }

    private void calculate(User user) {
        int totalSum = userService.getTotalSum(user.getId());
        //user.calculateCredit(totalSum);
    }
}
