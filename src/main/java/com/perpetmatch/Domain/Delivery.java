package com.perpetmatch.Domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.perpetmatch.api.dto.Order.AddressDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@EqualsAndHashCode(of = "id")
public class Delivery {

    @Id
    @GeneratedValue
    @Column(name ="delivery_id")
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "delivery")
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status; //READY, COMP


    public void setId(Long id) {
        this.id = id;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
    public void setAddress(AddressDto ad) {
        Address address = new Address(ad.getTo(), ad.getZipcode(), ad.getCity(),ad.getStreet(),ad.getMemo());
        this.address = address;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }
}
