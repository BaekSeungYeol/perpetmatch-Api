package com.perpetmatch.api.dto.Order;

import com.perpetmatch.Item.domain.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String company;
    private String boardImageHead;
    private String boardImageMain;


    public ItemDto(Item i) {
        this.id = i.getId();
        this.boardImageHead = i.getBoardImageHead();
        this.boardImageMain = i.getBoardImageMain();
        this.title = i.getTitle();
        this.company = i.getCompany();
        this.price = i.getPrice();
        this.stockQuantity = i.getStockQuantity();
        this.sale = i.getSale();
    }
}
