package com.CardMaster.mapper.cias;

import com.CardMaster.dto.cias.CardAccountResponseDto;
import com.CardMaster.model.cias.CardAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardAccountMapper {

    // Entity -> Response DTO
    public CardAccountResponseDto toDTO(CardAccount account) {
        CardAccountResponseDto dto = new CardAccountResponseDto();
        dto.setAccountId(account.getAccountId());
        dto.setCardId(account.getCard().getCardId()); // only reference cardId
        dto.setApplicationId(account.getCard().getApplication().getApplicationId());
        dto.setCreditLimit(account.getCreditLimit());
        dto.setAvailableLimit(account.getAvailableLimit());
        dto.setOpenDate(account.getOpenDate());
        dto.setStatus(account.getStatus().name());
        return dto;
    }
}
