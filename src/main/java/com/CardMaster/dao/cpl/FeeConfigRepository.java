package com.CardMaster.dao.cpl;

import com.CardMaster.model.cpl.FeeConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeeConfigRepository extends JpaRepository<FeeConfig, Long> {
    List<FeeConfig> findByProduct_ProductId(Long productId);
}