package com.perpetmatch.Order;

import com.perpetmatch.Domain.Item.Item;
import com.perpetmatch.Domain.Order;
import com.perpetmatch.Domain.OrderItem;
import com.perpetmatch.Domain.User;
import com.perpetmatch.Item.ItemRepository;
import com.perpetmatch.Member.UserRepository;
import com.perpetmatch.api.dto.Order.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    public void create(Long currentMemberId, Long id, int count) {

        User user = userRepository.findById(currentMemberId).get();
        Item item = itemRepository.findById(id).get();

        OrderItem orderItem = OrderItem.createOrderItem(item,item.getPrice(),count);

        Order order = Order.createOrder(user, orderItem);


    }


}
