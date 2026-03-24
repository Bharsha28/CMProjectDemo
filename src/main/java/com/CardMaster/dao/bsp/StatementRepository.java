package com.CardMaster.dao.bsp;

import com.CardMaster.Enum.bsp.StatementStatus;
import com.CardMaster.model.bsp.Statement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StatementRepository extends JpaRepository<Statement, Long> {
    List<Statement> findByAccount_AccountIdOrderByGeneratedDateDesc(Long accountId);

    Optional<Statement> findFirstByAccount_AccountIdAndStatusOrderByGeneratedDateDesc(
            Long accountId,
            StatementStatus status
    );
}
