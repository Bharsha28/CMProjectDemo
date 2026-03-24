
package com.CardMaster.dao.cau;

import com.CardMaster.model.cau.CreditScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CreditScoreRepository extends JpaRepository<CreditScore, Long> {
    Optional<CreditScore> findTopByApplication_ApplicationIdOrderByGeneratedDateDesc(Long applicationId);
}