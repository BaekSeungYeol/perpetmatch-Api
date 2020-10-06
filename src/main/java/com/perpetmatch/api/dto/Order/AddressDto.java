package com.perpetmatch.api.dto.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

    private String to;
    private String zipcode;
    private String city;
    private String street;
    private String memo;
}
