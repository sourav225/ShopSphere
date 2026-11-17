package com.shopsphere.repo;

import com.shopsphere.entity.Order;
import com.shopsphere.entity.User;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    List<Order> findAllByOrderByCreatedAtDesc();
    long countByStatus(Order.Status status);

    @Query("select coalesce(sum(o.total), 0) from Order o where o.paymentStatus = :ps")
    BigDecimal revenue(@Param("ps") Order.PaymentStatus ps);
}
