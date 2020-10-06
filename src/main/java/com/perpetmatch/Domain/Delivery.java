package com.perpetmatch.Domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.perpetmatch.api.dto.Order.AddressDto;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Delivery {

    @Id @GeneratedValue
    private Long id;

    private String dear;

    private String zipcode;

    private String city;

    private String street;

    private String memo;


//    @Enumerated(EnumType.STRING)
//    private DeliveryStatus status; //READY, COMP

    public void setAddress(AddressDto addressDto) {
        this.dear = addressDto.getDear();
        this.zipcode = addressDto.getZipcode();
        this.city = addressDto.getCity();
        this.street = addressDto.getStreet();
        this.memo = addressDto.getMemo();
    }
}
