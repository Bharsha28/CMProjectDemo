package com.CardMaster.mapper.cias;

import com.CardMaster.Enum.cias.CardStatus;
import com.CardMaster.dto.cias.CardRequestDto;
import com.CardMaster.dto.cias.CardResponseDto;
import com.CardMaster.model.cias.Card;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardMapper {

    // Entity -> Response DTO
    public CardResponseDto toDTO(Card card) {
        CardResponseDto dto = new CardResponseDto();
        dto.setCardId(card.getCardId());
        dto.setApplicationId(card.getApplication().getApplicationId());
        dto.setCustomerId(card.getCustomer().getCustomerId());
        dto.setProductId(card.getProduct().getProductId());
        dto.setMaskedCardNumber(card.getMaskedCardNumber());
        dto.setExpiryDate(card.getExpiryDate());
        dto.setStatus(card.getStatus().name());
        return dto;
    }
}
