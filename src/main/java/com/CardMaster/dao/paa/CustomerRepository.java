package com.CardMaster.dao.paa;

import com.CardMaster.model.paa.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByContactInfoEmail(String email);
}
