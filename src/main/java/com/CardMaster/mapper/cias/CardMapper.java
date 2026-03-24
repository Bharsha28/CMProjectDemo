package com.CardMaster.mapper.cias;

import com.CardMaster.dto.cias.CardResponseDto;
import com.CardMaster.model.cias.Card;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardMapper {

    // Entity -> Response DTO
    public CardResponseDto toDTO(Card card) {
        if (card == null) return null;
        CardResponseDto dto = new CardResponseDto();
        dto.setCardId(card.getCardId());
        
        if (card.getApplication() != null) {
            dto.setApplicationId(card.getApplication().getApplicationId());
        }
        
        if (card.getCustomer() != null) {
            dto.setCustomerId(card.getCustomer().getCustomerId());
            dto.setCustomerName(card.getCustomer().getName());
            dto.setCustomerEmail(card.getCustomer().getContactInfo() != null ? card.getCustomer().getContactInfo().getEmail() : "");
        }
        
        if (card.getProduct() != null) {
            dto.setProductId(card.getProduct().getProductId());
        }
        
        dto.setMaskedCardNumber(card.getMaskedCardNumber());
        dto.setExpiryDate(card.getExpiryDate());
        dto.setStatus(card.getStatus() != null ? card.getStatus().name() : "");
        return dto;
    }
}
