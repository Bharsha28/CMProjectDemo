package com.CardMaster.service.cpl;

import com.CardMaster.Enum.cpl.CardCategory;
import com.CardMaster.Enum.cpl.ProductStatus;
import com.CardMaster.dao.cpl.CardProductRepository;
import com.CardMaster.dto.cpl.CardProductRequestDto;
import com.CardMaster.dto.cpl.CardProductResponseDto;
import com.CardMaster.mapper.cpl.CardProductMapper;
import com.CardMaster.model.cpl.CardProduct;
import com.CardMaster.service.cpl.CardProductService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CardProductServiceTest {

    @Mock
    private CardProductRepository repository;

    @Mock
    private CardProductMapper mapper;

    @InjectMocks
    private CardProductService service;

    @Captor
    private ArgumentCaptor<CardProduct> productCaptor;

    private CardProductRequestDto request;
    private CardProduct entity;
    private CardProduct saved;
    private CardProductResponseDto response;

    @BeforeEach
    void setUp() {
        request = new CardProductRequestDto();
        request.setName("Platinum Plus");
        request.setCategory(CardCategory.Platinum);  // enum, not "Platinum"
        request.setInterestRate(2.49);               // Double, not BigDecimal
        request.setAnnualFee(4999.0);                // Double, not BigDecimal
        request.setStatus(ProductStatus.ACTIVE);     // enum, not "ACTIVE"

        entity = CardProduct.builder()
                .name(request.getName())
                .category(request.getCategory())
                .interestRate(request.getInterestRate())
                .annualFee(request.getAnnualFee())
                .status(request.getStatus())
                .build();

        saved = CardProduct.builder()
                .productId(101L)
                .name(request.getName())
                .category(request.getCategory())
                .interestRate(request.getInterestRate())
                .annualFee(request.getAnnualFee())
                .status(request.getStatus())
                .build();

        response = CardProductResponseDto.builder()
                .productId(saved.getProductId())
                .name(saved.getName())
                .category(saved.getCategory())
                .interestRate(saved.getInterestRate())
                .annualFee(saved.getAnnualFee())
                .status(saved.getStatus())
                .build();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {

        @Test
        @DisplayName("maps request -> entity, saves, and maps saved -> response")
        void create_success() {
            when(mapper.toEntity(request)).thenReturn(entity);
            when(repository.save(entity)).thenReturn(saved);
            when(mapper.toResponse(saved)).thenReturn(response);

            CardProductResponseDto result = service.create(request);

            assertThat(result).isNotNull();
            assertThat(result.getProductId()).isEqualTo(101L);
            assertThat(result.getName()).isEqualTo("Platinum Plus");
            assertThat(result.getCategory()).isEqualTo(CardCategory.Platinum);
            assertThat(result.getInterestRate()).isEqualTo(2.49);
            assertThat(result.getAnnualFee()).isEqualTo(4999.0);
            assertThat(result.getStatus()).isEqualTo(ProductStatus.ACTIVE);

            verify(mapper).toEntity(request);
            verify(repository).save(productCaptor.capture());
            verify(mapper).toResponse(saved);
            verifyNoMoreInteractions(mapper, repository);

            CardProduct captured = productCaptor.getValue();
            assertThat(captured.getName()).isEqualTo(request.getName());
            assertThat(captured.getCategory()).isEqualTo(request.getCategory());
            assertThat(captured.getInterestRate()).isEqualTo(request.getInterestRate());
            assertThat(captured.getAnnualFee()).isEqualTo(request.getAnnualFee());
            assertThat(captured.getStatus()).isEqualTo(request.getStatus());
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {

        @Test
        @DisplayName("returns mapped DTOs for all entities")
        void findAll_success() {
            CardProduct e1 = saved;

            CardProduct e2 = CardProduct.builder()
                    .productId(202L)
                    .name("Gold Everyday")
                    .category(CardCategory.Gold)
                    .interestRate(3.25)
                    .annualFee(1999.0)
                    .status(ProductStatus.ACTIVE)
                    .build();

            CardProductResponseDto r1 = response;

            CardProductResponseDto r2 = CardProductResponseDto.builder()
                    .productId(202L)
                    .name("Gold Everyday")
                    .category(CardCategory.Gold)
                    .interestRate(3.25)
                    .annualFee(1999.0)
                    .status(ProductStatus.ACTIVE)
                    .build();

            when(repository.findAll()).thenReturn(List.of(e1, e2));
            when(mapper.toResponse(e1)).thenReturn(r1);
            when(mapper.toResponse(e2)).thenReturn(r2);

            List<CardProductResponseDto> result = service.findAll();

            assertThat(result).containsExactly(r1, r2);

            verify(repository).findAll();
            verify(mapper).toResponse(e1);
            verify(mapper).toResponse(e2);
            verifyNoMoreInteractions(mapper, repository);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {

        @Test
        @DisplayName("returns mapped DTO when entity is found")
        void findById_success() {
            Long id = 101L;
            when(repository.findById(id)).thenReturn(Optional.of(saved));
            when(mapper.toResponse(saved)).thenReturn(response);

            CardProductResponseDto result = service.findById(id);

            assertThat(result).isNotNull();
            assertThat(result.getProductId()).isEqualTo(101L);

            verify(repository).findById(id);
            verify(mapper).toResponse(saved);
            verifyNoMoreInteractions(mapper, repository);
        }

        @Test
        @DisplayName("throws RuntimeException('Product not found') when entity is missing")
        void findById_notFound() {
            Long id = 999L;
            when(repository.findById(id)).thenReturn(Optional.empty());

            RuntimeException ex = assertThrows(RuntimeException.class, () -> service.findById(id));
            assertThat(ex.getMessage()).isEqualTo("Product not found");

            verify(repository).findById(id);
            verifyNoInteractions(mapper);
            verifyNoMoreInteractions(repository);
        }
    }
}