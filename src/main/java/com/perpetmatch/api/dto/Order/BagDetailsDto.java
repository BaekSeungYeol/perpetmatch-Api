package com.perpetmatch.api.dto.Order;

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
    private String name;
    private int price;
    private int count;

}
