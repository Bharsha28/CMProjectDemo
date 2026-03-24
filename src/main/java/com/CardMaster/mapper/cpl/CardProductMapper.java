package com.CardMaster.mapper.cpl;

import com.CardMaster.dto.cpl.CardProductRequestDto;
import com.CardMaster.dto.cpl.CardProductResponseDto;
import com.CardMaster.model.cpl.CardProduct;
import org.springframework.stereotype.Component;

@Component
public class CardProductMapper {

    public CardProduct toEntity(CardProductRequestDto req) {
        CardProduct e = new CardProduct();
        e.setName(req.getName());
        e.setCategory(req.getCategory());
        e.setInterestRate(req.getInterestRate());
        e.setAnnualFee(req.getAnnualFee());
        e.setStatus(req.getStatus());
        return e;
    }

    public CardProductResponseDto toResponse(CardProduct e) {
        return CardProductResponseDto.builder()
                .productId(e.getProductId())
                .name(e.getName())
                .category(e.getCategory())
                .interestRate(e.getInterestRate())
                .annualFee(e.getAnnualFee())
                .status(e.getStatus())
                .build();
    }
}