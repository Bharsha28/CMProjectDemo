package com.CardMaster.dao.cau;

import com.CardMaster.model.cau.UnderwritingDecision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UnderwritingDecisionRepository extends JpaRepository<UnderwritingDecision, Long> {

    Optional<UnderwritingDecision>
    findTopByApplication_ApplicationIdOrderByDecisionDateDesc(Long applicationId);
}
