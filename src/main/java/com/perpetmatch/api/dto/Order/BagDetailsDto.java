package com.perpetmatch.api.dto.Order;

import com.perpetmatch.Domain.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BagDetailsDto {

    private Long id;
    private String image;
    private String title;
    private int price;
    private String company;
    private int count;

    public BagDetailsDto(OrderItem o) {
        this.id = o.getItem().getId();
        this.image = o.getItem().getBoardImageHead();
        this.title = o.getItem().getTitle();
        this.company = o.getItem().getCompany();
        this.price = o.getOrderPrice();
        this.count = o.getCount();
    }
}
