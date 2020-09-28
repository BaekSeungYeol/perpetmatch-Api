package com.perpetmatch.Order;

import com.perpetmatch.Domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
