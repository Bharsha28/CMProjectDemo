package com.CardMaster.service.cau;

import com.CardMaster.dao.cau.CreditScoreRepository;
import com.CardMaster.dao.cau.UnderwritingDecisionRepository;
import com.CardMaster.dao.iam.UserRepository;
import com.CardMaster.dao.paa.CardApplicationRepository;

import com.CardMaster.dto.cau.CreditScoreGenerateRequest;
import com.CardMaster.dto.cau.CreditScoreResponse;
import com.CardMaster.dto.cau.UnderwritingDecisionRequest;
import com.CardMaster.dto.cau.UnderwritingDecisionResponse;

import com.CardMaster.exceptions.cau.EntityNotFoundException;
import com.CardMaster.exceptions.cau.UnauthorizedActionException;
import com.CardMaster.exceptions.cau.ValidationException;

import com.CardMaster.mapper.cau.UnderwritingMapper;

import com.CardMaster.model.cau.CreditScore;
import com.CardMaster.model.cau.UnderwritingDecision;
import com.CardMaster.model.iam.User;
import com.CardMaster.model.paa.CardApplication;

import com.CardMaster.Enum.cau.UnderwritingDecisionType;
import com.CardMaster.Enum.iam.UserEnum;
import com.CardMaster.security.iam.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UnderwritingService {

    private final CreditScoreRepository creditScoreRepository;
    private final UnderwritingDecisionRepository decisionRepository;
    private final CardApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final UnderwritingMapper mapper;


    public CreditScoreResponse generateScore(Long appId, CreditScoreGenerateRequest req) {
        CardApplication app = applicationRepository.findById(appId)
                .orElseThrow(() -> new EntityNotFoundException("CardApplication", appId));

        if (req.getBureauScore() == null || req.getBureauScore() <= 0) {
            throw new ValidationException("Bureau score must be positive");
        }

        CreditScore score = CreditScore.builder()
                .application(app)
                .bureauScore(req.getBureauScore())
                .internalScore(req.getBureauScore() / 10)
                .generatedDate(LocalDateTime.now())
                .build();

        CreditScore saved = creditScoreRepository.save(score);
        return mapper.toCreditScoreResponse(saved);
    }


    @Transactional(readOnly = true)
    public CreditScoreResponse getLatestScore(Long appId) {
        CreditScore latest = creditScoreRepository
                .findTopByApplication_ApplicationIdOrderByGeneratedDateDesc(appId)
                .orElseThrow(() -> new EntityNotFoundException("Latest CreditScore for application", appId));

        return mapper.toCreditScoreResponse(latest);
    }


    public UnderwritingDecisionResponse createDecision(Long appId,
                                                       UnderwritingDecisionRequest req,
                                                       String authorizationHeader) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedActionException("Missing or invalid Authorization header");
        }
        String token = authorizationHeader.substring(7);


        if (!jwtUtil.validateToken(token)) {
            throw new UnauthorizedActionException("Invalid or expired token");
        }

        Long underwriterId = jwtUtil.extractUserId(token);
        if (underwriterId == null) {
            throw new UnauthorizedActionException("Token is missing required userId claim");
        }

        String roleClaim = jwtUtil.extractRole(token);
        if (roleClaim == null || !roleClaim.equalsIgnoreCase("UNDERWRITER")) {
            throw new UnauthorizedActionException("Only UNDERWRITER role can create decisions");
        }


        User underwriter = userRepository.findById(underwriterId)
                .orElseThrow(() -> new EntityNotFoundException("User (underwriter) by id", underwriterId));

        if (underwriter.getRole() != UserEnum.UNDERWRITER) {
            throw new UnauthorizedActionException("Only UNDERWRITER role can create decisions");
        }


        CardApplication app = applicationRepository.findById(appId)
                .orElseThrow(() -> new EntityNotFoundException("CardApplication", appId));


        UnderwritingDecisionType finalDecision = req.getDecision();
        Double finalLimit = req.getApprovedLimit();

        if (finalDecision == null) {
            CreditScore latest = creditScoreRepository
                    .findTopByApplication_ApplicationIdOrderByGeneratedDateDesc(appId)
                    .orElseThrow(() -> new ValidationException("Generate Credit Score first"));

            int bureau = latest.getBureauScore();
            if (bureau >= 750) {
                finalDecision = UnderwritingDecisionType.APPROVE;
                finalLimit = app.getRequestedLimit();
            } else if (bureau < 600) {
                finalDecision = UnderwritingDecisionType.REJECT;
                finalLimit = 0.0;
            } else {
                finalDecision = UnderwritingDecisionType.CONDITIONAL;
                finalLimit = (app.getRequestedLimit() != null) ? app.getRequestedLimit() * 0.5 : 0.0;
            }
        } else {
            if (finalDecision == UnderwritingDecisionType.APPROVE ||
                    finalDecision == UnderwritingDecisionType.CONDITIONAL) {
                if (finalLimit == null || finalLimit <= 0) {
                    throw new ValidationException("Approved limit must be positive for APPROVE/CONDITIONAL");
                }
            }
            if (finalDecision == UnderwritingDecisionType.REJECT) {
                finalLimit = 0.0;
            }
        }


        UnderwritingDecision decision = UnderwritingDecision.builder()
                .application(app)
                .underwriter(underwriter)
                .decision(finalDecision)
                .approvedLimit(finalLimit)
                .remarks(req.getRemarks())
                .decisionDate(LocalDateTime.now())
                .build();

        UnderwritingDecision saved = decisionRepository.save(decision);


        switch (finalDecision) {
            case APPROVE     -> app.setStatus(CardApplication.CardApplicationStatus.Approved);
            case REJECT      -> app.setStatus(CardApplication.CardApplicationStatus.Rejected);
            case CONDITIONAL -> app.setStatus(CardApplication.CardApplicationStatus.UnderReview);
        }
        applicationRepository.save(app);


        return mapper.toUnderwritingDecisionResponse(saved);
    }


    @Transactional(readOnly = true)
    public UnderwritingDecisionResponse getLatestDecision(Long appId) {
        UnderwritingDecision latest = decisionRepository
                .findTopByApplication_ApplicationIdOrderByDecisionDateDesc(appId)
                .orElseThrow(() -> new EntityNotFoundException("Latest UnderwritingDecision", appId));

        return mapper.toUnderwritingDecisionResponse(latest);
    }
}
