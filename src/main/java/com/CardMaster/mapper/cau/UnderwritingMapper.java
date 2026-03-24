package com.CardMaster.mapper.cau;

import com.CardMaster.dto.cau.CreditScoreGenerateRequest;
import com.CardMaster.dto.cau.CreditScoreResponse;
import com.CardMaster.dto.cau.UnderwritingDecisionResponse;
import com.CardMaster.model.cau.CreditScore;
import com.CardMaster.model.cau.UnderwritingDecision;
import com.CardMaster.model.paa.CardApplication;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UnderwritingMapper {


    public CreditScore fromCreditScoreRequest(CreditScoreGenerateRequest req, CardApplication app) {
        CreditScore cs = new CreditScore();
        cs.setApplication(app);
        cs.setBureauScore(req.getBureauScore());
        cs.setInternalScore(req.getBureauScore() / 10);
        cs.setGeneratedDate(LocalDateTime.now());
        return cs;
    }

    public CreditScoreResponse toCreditScoreResponse(CreditScore e) {
        CreditScoreResponse d = new CreditScoreResponse();
        d.setScoreId(e.getScoreId());
        d.setApplicationId(e.getApplication().getApplicationId());
        d.setBureauScore(e.getBureauScore());
        d.setInternalScore(e.getInternalScore());
        d.setGeneratedDate(e.getGeneratedDate().toString());
        return d;
    }

    public UnderwritingDecisionResponse toUnderwritingDecisionResponse(UnderwritingDecision e) {
        UnderwritingDecisionResponse d = new UnderwritingDecisionResponse();
        d.setDecisionId(e.getDecisionId());
        d.setApplicationId(e.getApplication().getApplicationId());
        d.setUnderwriterId(e.getUnderwriter().getUserId());
        d.setDecision(e.getDecision());
        d.setApprovedLimit(e.getApprovedLimit());
        d.setRemarks(e.getRemarks());
        d.setDecisionDate(e.getDecisionDate().toString());
        return d;
    }
}
