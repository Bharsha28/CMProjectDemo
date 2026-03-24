package com.CardMaster.dao.paa;

import com.CardMaster.model.paa.CardApplication;
import com.CardMaster.model.paa.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardApplicationRepository extends JpaRepository<CardApplication, Long> {
    List<CardApplication> findByCustomerCustomerId(Long customerId);
    List<CardApplication> findByCustomerContactInfoEmail(String email);
    List<CardApplication> findByCustomer(com.CardMaster.model.paa.Customer customer);

    @Query("SELECT a FROM CardApplication a WHERE a.customer.contactInfo.email = :email")
    List<CardApplication> findByEmailDirect(@Param("email") String email);

    @Query(value = "SELECT ca.* FROM card_application ca " +
                   "JOIN customers c ON ca.customer_id = c.customer_id " +
                   "WHERE c.email = :email", nativeQuery = true)
    List<CardApplication> findByEmailNative(@Param("email") String email);
}
