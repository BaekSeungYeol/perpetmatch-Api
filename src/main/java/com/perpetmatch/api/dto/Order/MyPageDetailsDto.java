package com.perpetmatch.api.dto.Order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.perpetmatch.Domain.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyPageDetailsDto  {

    private Long id;
    private String image;
    private String title;
    private int price;
    private String company;
    private int count;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime orderDate;

    public MyPageDetailsDto(BagDetailsDto o,LocalDateTime dateTime) {
        this.id = o.getId();
        this.image = o.getImage();
        this.title = o.getTitle();
        this.company = o.getCompany();
        this.price = o.getPrice();
        this.count = o.getCount();
        this.orderDate = dateTime;
    }
}
