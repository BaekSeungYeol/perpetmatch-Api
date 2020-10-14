package com.perpetmatch.api.dto.Order;

import com.perpetmatch.Domain.Item.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {


    private Long id;
    private String title;
    private int price;
    private int stockQuantity;
    private int sale;
    private String image;
    private String company;

    public ItemDto(Item i) {
        this.id = i.getId();
        this.image = i.getImage();
        this.title = i.getTitle();
        this.company = i.getCompany();
        this.price = i.getPrice();
        this.stockQuantity = i.getStockQuantity();
        this.sale = i.getSale();
    }
}
