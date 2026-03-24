package com.CardMaster.controller.service.cau;

import com.CardMaster.Enum.cau.UnderwritingDecisionType;
import com.CardMaster.Enum.iam.UserEnum;
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
import com.CardMaster.security.iam.JwtUtil;

import com.CardMaster.service.cau.UnderwritingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
class UnderwritingServiceTest {

    @Mock private CreditScoreRepository creditScoreRepository;
    @Mock private UnderwritingDecisionRepository decisionRepository;
    @Mock private CardApplicationRepository applicationRepository;
    @Mock private UserRepository userRepository;
    @Mock private JwtUtil jwtUtil;
    @Mock private UnderwritingMapper mapper;

    @InjectMocks
    private UnderwritingService service;

    private CardApplication app;
    private User underwriter;

    @BeforeEach
    void setUp() {
        // minimal CardApplication
        app = new CardApplication();
        app.setApplicationId(1L);
        app.setRequestedLimit(50_000.0);
        app.setStatus(CardApplication.CardApplicationStatus.Submitted);

        // minimal User with UNDERWRITER role
        underwriter = new User();
        underwriter.setUserId(10L);
        underwriter.setRole(UserEnum.UNDERWRITER);
    }

    // ---------------------------------------------------------------------
    // generateScore (success + invalid bureau)
    // ---------------------------------------------------------------------
    @Test
    void generateScore_success_returnsDto() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));

        CreditScoreGenerateRequest req = new CreditScoreGenerateRequest();
        req.setBureauScore(720);

        CreditScore saved = new CreditScore();
        saved.setScoreId(101L);
        saved.setApplication(app);
        saved.setBureauScore(720);
        saved.setInternalScore(72);
        saved.setGeneratedDate(LocalDateTime.now());

        when(creditScoreRepository.save(any(CreditScore.class))).thenReturn(saved);

        CreditScoreResponse dto = new CreditScoreResponse();
        dto.setScoreId(101L);
        dto.setApplicationId(1L);
        dto.setBureauScore(720);
        dto.setInternalScore(72);
        dto.setGeneratedDate(saved.getGeneratedDate().toString());
        when(mapper.toCreditScoreResponse(saved)).thenReturn(dto);

        CreditScoreResponse out = service.generateScore(1L, req);

        assertThat(out.getScoreId()).isEqualTo(101L);
        assertThat(out.getInternalScore()).isEqualTo(72);
        verify(mapper).toCreditScoreResponse(saved);
    }

    @Test
    void generateScore_invalidBureau_throws() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));

        CreditScoreGenerateRequest req = new CreditScoreGenerateRequest();
        req.setBureauScore(0); // invalid

        assertThatThrownBy(() -> service.generateScore(1L, req))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Bureau score must be positive");

        verify(creditScoreRepository, never()).save(any());
    }

    // ---------------------------------------------------------------------
    // getLatestScore (success + not found)
    // ---------------------------------------------------------------------
    @Test
    void getLatestScore_found_returnsDto() {
        CreditScore latest = new CreditScore();
        latest.setScoreId(200L);
        latest.setApplication(app);
        latest.setBureauScore(700);
        latest.setInternalScore(70);
        latest.setGeneratedDate(LocalDateTime.now());

        when(creditScoreRepository.findTopByApplication_ApplicationIdOrderByGeneratedDateDesc(1L))
                .thenReturn(Optional.of(latest));

        CreditScoreResponse dto = new CreditScoreResponse();
        dto.setScoreId(200L);
        dto.setApplicationId(1L);
        dto.setBureauScore(700);
        dto.setInternalScore(70);
        dto.setGeneratedDate(latest.getGeneratedDate().toString());
        when(mapper.toCreditScoreResponse(latest)).thenReturn(dto);

        CreditScoreResponse out = service.getLatestScore(1L);

        assertThat(out.getScoreId()).isEqualTo(200L);
        verify(mapper).toCreditScoreResponse(latest);
    }

    @Test
    void getLatestScore_notFound_throws() {
        when(creditScoreRepository.findTopByApplication_ApplicationIdOrderByGeneratedDateDesc(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getLatestScore(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Latest CreditScore for application");
    }

    // ---------------------------------------------------------------------
    // createDecision (MANUAL ONLY) – success cases
    // ---------------------------------------------------------------------
    @Test
    void createDecision_manual_process() {
        String token = "tok";
        String header = "Bearer tok";

        // JWT claims
        when(jwtUtil.validateToken("tok")).thenReturn(true);
        when(jwtUtil.extractUserId("tok")).thenReturn(10L);
        when(jwtUtil.extractRole("tok")).thenReturn("UNDERWRITER");

        // DB lookups
        when(userRepository.findById(10L)).thenReturn(Optional.of(underwriter));
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));

        // Persist decision
        UnderwritingDecision saved = new UnderwritingDecision();
        saved.setDecisionId(101L);
        saved.setApplication(app);
        saved.setUnderwriter(underwriter);
        saved.setDecision(UnderwritingDecisionType.APPROVE);
        saved.setApprovedLimit(50_000.0);
        saved.setDecisionDate(LocalDateTime.now());

        UnderwritingDecisionResponse dto = new UnderwritingDecisionResponse();
        dto.setDecisionId(101L);
        dto.setApplicationId(1L);
        dto.setUnderwriterId(10L);
        dto.setDecision(UnderwritingDecisionType.APPROVE);
        dto.setApprovedLimit(50_000.0);
        dto.setDecisionDate(saved.getDecisionDate().toString());

        when(decisionRepository.save(any(UnderwritingDecision.class))).thenReturn(saved);
        when(mapper.toUnderwritingDecisionResponse(saved)).thenReturn(dto);

        // Manual request
        UnderwritingDecisionRequest req = new UnderwritingDecisionRequest();
        req.setDecision(UnderwritingDecisionType.APPROVE);
        req.setApprovedLimit(50_000.0);
        req.setRemarks("Processed manually");

        UnderwritingDecisionResponse result = service.createDecision(1L, req, header);

        assertThat(result.getDecisionId()).isEqualTo(101L);
        verify(applicationRepository).save(any(CardApplication.class)); // status updated
    }

    @Test
    void createDecision_manual_conditional() {
        String token = "tok";
        String header = "Bearer tok";

        when(jwtUtil.validateToken("tok")).thenReturn(true);
        when(jwtUtil.extractUserId("tok")).thenReturn(10L);
        when(jwtUtil.extractRole("tok")).thenReturn("UNDERWRITER");

        when(userRepository.findById(10L)).thenReturn(Optional.of(underwriter));
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));

        UnderwritingDecision saved = new UnderwritingDecision();
        saved.setDecisionId(202L);
        saved.setApplication(app);
        saved.setUnderwriter(underwriter);
        saved.setDecision(UnderwritingDecisionType.CONDITIONAL);
        saved.setApprovedLimit(25_000.0);
        saved.setDecisionDate(LocalDateTime.now());

        UnderwritingDecisionResponse dto = new UnderwritingDecisionResponse();
        dto.setDecisionId(202L);
        dto.setApplicationId(1L);
        dto.setUnderwriterId(10L);
        dto.setDecision(UnderwritingDecisionType.CONDITIONAL);
        dto.setApprovedLimit(25_000.0);
        dto.setDecisionDate(saved.getDecisionDate().toString());

        when(decisionRepository.save(any(UnderwritingDecision.class))).thenReturn(saved);
        when(mapper.toUnderwritingDecisionResponse(saved)).thenReturn(dto);

        UnderwritingDecisionRequest req = new UnderwritingDecisionRequest();
        req.setDecision(UnderwritingDecisionType.CONDITIONAL);
        req.setApprovedLimit(25_000.0);

        UnderwritingDecisionResponse out = service.createDecision(1L, req, header);

        assertThat(out.getDecisionId()).isEqualTo(202L);
        verify(applicationRepository).save(any(CardApplication.class));
    }

    @Test
    void createDecision_manual_reject() {
        String token = "tok";
        String header = "Bearer tok";

        when(jwtUtil.validateToken("tok")).thenReturn(true);
        when(jwtUtil.extractUserId("tok")).thenReturn(10L);
        when(jwtUtil.extractRole("tok")).thenReturn("UNDERWRITER");

        when(userRepository.findById(10L)).thenReturn(Optional.of(underwriter));
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));

        UnderwritingDecision saved = new UnderwritingDecision();
        saved.setDecisionId(303L);
        saved.setApplication(app);
        saved.setUnderwriter(underwriter);
        saved.setDecision(UnderwritingDecisionType.REJECT);
        saved.setApprovedLimit(0.0);
        saved.setDecisionDate(LocalDateTime.now());

        UnderwritingDecisionResponse dto = new UnderwritingDecisionResponse();
        dto.setDecisionId(303L);
        dto.setApplicationId(1L);
        dto.setUnderwriterId(10L);
        dto.setDecision(UnderwritingDecisionType.REJECT);
        dto.setApprovedLimit(0.0);
        dto.setDecisionDate(saved.getDecisionDate().toString());

        when(decisionRepository.save(any(UnderwritingDecision.class))).thenReturn(saved);
        when(mapper.toUnderwritingDecisionResponse(saved)).thenReturn(dto);

        UnderwritingDecisionRequest req = new UnderwritingDecisionRequest();
        req.setDecision(UnderwritingDecisionType.REJECT);
        req.setApprovedLimit(0.0);

        UnderwritingDecisionResponse out = service.createDecision(1L, req, header);

        assertThat(out.getDecisionId()).isEqualTo(303L);
        verify(applicationRepository).save(any(CardApplication.class));
    }

    // ---------------------------------------------------------------------
    // createDecision – guard & validation failures
    // ---------------------------------------------------------------------
    @Test
    void createDecision_missingHeader_throwsUnauthorized() {
        UnderwritingDecisionRequest req = new UnderwritingDecisionRequest();
        req.setDecision(UnderwritingDecisionType.APPROVE);
        req.setApprovedLimit(10_000.0);

        assertThatThrownBy(() -> service.createDecision(1L, req, null))
                .isInstanceOf(UnauthorizedActionException.class)
                .hasMessageContaining("Missing or invalid Authorization header");
    }

    @Test
    void createDecision_invalidToken_throwsUnauthorized() {
        String header = "Bearer bad";
        when(jwtUtil.validateToken("bad")).thenReturn(false);

        UnderwritingDecisionRequest req = new UnderwritingDecisionRequest();
        req.setDecision(UnderwritingDecisionType.APPROVE);
        req.setApprovedLimit(10_000.0);

        assertThatThrownBy(() -> service.createDecision(1L, req, header))
                .isInstanceOf(UnauthorizedActionException.class)
                .hasMessageContaining("Invalid or expired token");
    }

    @Test
    void createDecision_wrongRole_throwsUnauthorized() {
        String token = "tok";
        String header = "Bearer tok";

        // IMPORTANT: Your service checks role CLAIM before DB lookup
        when(jwtUtil.validateToken("tok")).thenReturn(true);
        when(jwtUtil.extractUserId("tok")).thenReturn(10L);
        when(jwtUtil.extractRole("tok")).thenReturn("CUSTOMER"); // wrong role in claims

        UnderwritingDecisionRequest req = new UnderwritingDecisionRequest();
        req.setDecision(UnderwritingDecisionType.APPROVE);
        req.setApprovedLimit(10_000.0);

        assertThatThrownBy(() -> service.createDecision(1L, req, header))
                .isInstanceOf(UnauthorizedActionException.class)
                .hasMessageContaining("Only UNDERWRITER role");
    }

    @Test
    void createDecision_applicationNotFound_throwsEntityNotFound() {
        String token = "tok";
        String header = "Bearer tok";

        when(jwtUtil.validateToken("tok")).thenReturn(true);
        when(jwtUtil.extractUserId("tok")).thenReturn(10L);
        when(jwtUtil.extractRole("tok")).thenReturn("UNDERWRITER");

        when(userRepository.findById(10L)).thenReturn(Optional.of(underwriter));
        when(applicationRepository.findById(1L)).thenReturn(Optional.empty());

        UnderwritingDecisionRequest req = new UnderwritingDecisionRequest();
        req.setDecision(UnderwritingDecisionType.APPROVE);
        req.setApprovedLimit(10_000.0);

        assertThatThrownBy(() -> service.createDecision(1L, req, header))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("CardApplication");
    }

    @Test
    void createDecision_manualApprove_missingLimit_throwsValidation() {
        String token = "tok";
        String header = "Bearer tok";

        when(jwtUtil.validateToken("tok")).thenReturn(true);
        when(jwtUtil.extractUserId("tok")).thenReturn(10L);
        when(jwtUtil.extractRole("tok")).thenReturn("UNDERWRITER");

        when(userRepository.findById(10L)).thenReturn(Optional.of(underwriter));
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));

        UnderwritingDecisionRequest req = new UnderwritingDecisionRequest();
        req.setDecision(UnderwritingDecisionType.APPROVE);
        req.setApprovedLimit(null); // invalid for approve/conditional

        assertThatThrownBy(() -> service.createDecision(1L, req, header))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Approved limit must be positive");

        verify(decisionRepository, never()).save(any());
    }

    // ---------------------------------------------------------------------
    // getLatestDecision (success + not found)
    // ---------------------------------------------------------------------
    @Test
    void getLatestDecision_found_returnsDto() {
        UnderwritingDecision entity = new UnderwritingDecision();
        entity.setDecisionId(900L);
        entity.setApplication(app);
        entity.setUnderwriter(underwriter);
        entity.setDecision(UnderwritingDecisionType.APPROVE);
        entity.setApprovedLimit(50_000.0);
        entity.setDecisionDate(LocalDateTime.now());

        when(decisionRepository.findTopByApplication_ApplicationIdOrderByDecisionDateDesc(1L))
                .thenReturn(Optional.of(entity));

        UnderwritingDecisionResponse dto = new UnderwritingDecisionResponse();
        dto.setDecisionId(900L);
        dto.setApplicationId(1L);
        dto.setUnderwriterId(10L);
        dto.setDecision(UnderwritingDecisionType.APPROVE);
        dto.setApprovedLimit(50_000.0);
        dto.setDecisionDate(entity.getDecisionDate().toString());
        when(mapper.toUnderwritingDecisionResponse(entity)).thenReturn(dto);

        UnderwritingDecisionResponse out = service.getLatestDecision(1L);

        assertThat(out.getDecisionId()).isEqualTo(900L);
        verify(mapper).toUnderwritingDecisionResponse(entity);
    }

    @Test
    void getLatestDecision_notFound_throws() {
        when(decisionRepository.findTopByApplication_ApplicationIdOrderByDecisionDateDesc(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getLatestDecision(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Latest UnderwritingDecision");
    }
}