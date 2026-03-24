package com.CardMaster.dao.paa;

import com.CardMaster.model.paa.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByApplicationApplicationId(Long applicationId);
}
