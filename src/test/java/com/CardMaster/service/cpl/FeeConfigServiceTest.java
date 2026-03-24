package com.CardMaster.service.cpl;


import com.CardMaster.Enum.cpl.FeeType;
import com.CardMaster.dao.cpl.CardProductRepository;
import com.CardMaster.dao.cpl.FeeConfigRepository;
import com.CardMaster.dto.cpl.FeeConfigRequestDto;
import com.CardMaster.dto.cpl.FeeConfigResponseDto;
import com.CardMaster.mapper.cpl.FeeConfigMapper;
import com.CardMaster.model.cpl.CardProduct;
import com.CardMaster.model.cpl.FeeConfig;
import com.CardMaster.service.cpl.FeeConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;     // Optional (AssertJ)
import static org.junit.jupiter.api.Assertions.assertThrows;   // JUnit
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeeConfigServiceTest {

    @Mock
    private FeeConfigRepository feeRepo;

    @Mock
    private CardProductRepository productRepo;

    @Mock
    private FeeConfigMapper mapper;

    @InjectMocks
    private FeeConfigService service;

    @Captor
    private ArgumentCaptor<FeeConfig> feeConfigCaptor;

    private FeeConfigRequestDto req;
    private CardProduct product;
    private FeeConfig entityBeforeSave;
    private FeeConfig savedEntity;
    private FeeConfigResponseDto responseDto;

    @BeforeEach
    void setUp() {
        // ----- Request -----
        req = new FeeConfigRequestDto();
        req.setProductId(10L);
        req.setFeeType(FeeType.ANNUAL);
        req.setAmount(1499.0);

        // ----- Product (minimal fields required for this test) -----
        product = CardProduct.builder()
                .productId(10L)
                .name("Gold Everyday")
                .build();

        // ----- Entity before persisting -----
        entityBeforeSave = FeeConfig.builder()
                .product(product)
                .feeType(FeeType.ANNUAL)
                .amount(1499.0)
                .build();

        // ----- Saved entity with ID -----
        savedEntity = FeeConfig.builder()
                .feeId(501L)
                .product(product)
                .feeType(FeeType.ANNUAL)
                .amount(1499.0)
                .build();

        // ----- Response DTO -----
        responseDto = FeeConfigResponseDto.builder()
                .feeId(501L)
                .productId(10L)
                .feeType(FeeType.ANNUAL)
                .amount(1499.0)
                .build();
    }

    @Nested
    @DisplayName("addFee()")
    class AddFeeTests {

        @Test
        @DisplayName("loads product, maps request→entity, saves, and maps saved→response")
        void addFee_success() {
            // Arrange
            when(productRepo.findById(10L)).thenReturn(Optional.of(product));
            when(mapper.toEntity(req, product)).thenReturn(entityBeforeSave);
            when(feeRepo.save(entityBeforeSave)).thenReturn(savedEntity);
            when(mapper.toResponse(savedEntity)).thenReturn(responseDto);

            // Act
            FeeConfigResponseDto result = service.addFee(req);

            // Assert
        }
    }
}