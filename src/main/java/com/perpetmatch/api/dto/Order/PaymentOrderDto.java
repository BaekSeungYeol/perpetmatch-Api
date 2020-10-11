package com.perpetmatch.api.dto.Order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.perpetmatch.Domain.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.FetchType.LAZY;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderDto {

    private Long id;
    private String nickname;
    private int totalSum;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime orderDate;
    private int credit;

    public PaymentOrderDto(Order order) {
        this.id = order.getId();
        this.nickname = order.getUser().getNickname();
        this.totalSum = order.getTotalPrice();
        this.orderDate = order.getOrderDate();
        this.credit = order.getUser().getCredit();
    }
}
