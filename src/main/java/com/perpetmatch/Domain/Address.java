package com.perpetmatch.Domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {

    private String to;
    private String zipcode;
    private String city;
    private String street;
    private String memo;


    protected  Address() {
    }

    public Address(String to, String zipcode,String city, String street, String memo) {
        this.to = to;
        this.zipcode = zipcode;
        this.city = city;
        this.street = street;
        this.memo = memo;
    }
}
