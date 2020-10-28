package com.perpetmatch.Order;

import com.perpetmatch.Delivery.DeliveryRepository;
import com.perpetmatch.Domain.*;
import com.perpetmatch.Domain.Item.Item;
import com.perpetmatch.Item.ItemRepository;
import com.perpetmatch.Member.UserRepository;
import com.perpetmatch.Member.UserService;
import com.perpetmatch.OrderItem.OrderItemRepository;
import com.perpetmatch.api.dto.Order.AddressDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final DeliveryRepository deliveryRepository;

    public Order createOrder(Long id, AddressDto addressDto) {

        User user = userRepository.findByIdWithBags(id);
        Set<OrderItem> items = user.getBag();
        ArrayList<OrderItem> orderItems = new ArrayList<>(items);
        Delivery delivery = new Delivery();
        delivery.setAddress(addressDto);

       deliveryRepository.save(delivery);

        Order order = makeOrder(user, orderItems, delivery);

        calculate(user);

        return orderRepository.findByIdWithUser(order.getId());
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

    private void calculate(User user) {
        int totalSum = 0;
        Set<OrderItem> bags = user.getBag();
        for (OrderItem bag : bags) {
            totalSum += bag.getTotalPrice();
            user.getBag().remove(bag);
        }
        user.setCredit(Math.max(0,user.getCredit()-totalSum));
        // TODO 0보다 작을때
    }
}
