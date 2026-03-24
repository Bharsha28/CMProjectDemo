package com.CardMaster.dao.paa;

import com.CardMaster.model.paa.CardApplication;
import com.CardMaster.model.paa.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardApplicationRepository extends JpaRepository<CardApplication, Long> {
    List<CardApplication> findByCustomerCustomerId(Long customerId);
}
