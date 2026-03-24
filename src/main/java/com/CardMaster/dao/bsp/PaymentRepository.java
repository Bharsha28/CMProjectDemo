package com.CardMaster.dao.bsp;


import com.CardMaster.model.bsp.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByAccount_AccountIdOrderByPaymentDateDesc(Long accountId);
}
