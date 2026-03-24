package com.CardMaster.dao.cias;

import com.CardMaster.model.cias.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    // Find all cards belonging to a specific customer
    List<Card> findByCustomerCustomerId(Long customerId);

    // Find all cards by product type
    List<Card> findByProductProductId(Long productId);

    // Find card by its masked number (unique)
    Card findByMaskedCardNumber(String maskedCardNumber);

    boolean existsByApplicationApplicationId(Long applicationId);

    Optional<Card> findByApplicationApplicationId(Long applicationId);

    List<Card> findByCustomerContactInfoEmail(String email);
}
