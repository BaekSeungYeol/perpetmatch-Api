package com.perpetmatch.modules.Order;

import com.perpetmatch.Domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {


    @Query("select distinct o from Order o left join fetch o.user where o.id = :id")
    Order findByIdWithUser(@Param("id") Long id);
}
