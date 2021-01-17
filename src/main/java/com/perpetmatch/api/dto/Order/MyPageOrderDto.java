package com.perpetmatch.api.dto.Order;

import com.perpetmatch.Order.domain.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyPageOrderDto {

    private Set<MyPageDetailsDto> orders;

    public MyPageOrderDto(Order order) {
        LocalDateTime orderDate = order.getOrderDate();
        Set<BagDetailsDto> collect = order.getOrderItems().stream().map(BagDetailsDto::new).collect(Collectors.toSet());
        this.orders = collect.stream().map(o -> new MyPageDetailsDto(o,orderDate)).collect(Collectors.toSet());
    }

}
