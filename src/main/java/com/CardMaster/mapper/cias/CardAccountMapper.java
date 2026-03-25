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
        if (account == null) return null;
        CardAccountResponseDto dto = new CardAccountResponseDto();
        dto.setAccountId(account.getAccountId());
        
        if (account.getCard() != null) {
            dto.setCardId(account.getCard().getCardId());
            if (account.getCard().getApplication() != null) {
                dto.setApplicationId(account.getCard().getApplication().getApplicationId());
                if (account.getCard().getApplication().getProduct() != null) {
                    dto.setCardProductName(account.getCard().getApplication().getProduct().getName());
                }
            }
            if (account.getCard().getCustomer() != null) {
                dto.setCustomerName(account.getCard().getCustomer().getName());
                dto.setCustomerEmail(account.getCard().getCustomer().getContactInfo() != null ? 
                        account.getCard().getCustomer().getContactInfo().getEmail() : "");
            }
            dto.setMaskedCardNumber(account.getCard().getMaskedCardNumber());
        }
        
        dto.setCreditLimit(account.getCreditLimit());
        dto.setAvailableLimit(account.getAvailableLimit());
        dto.setOpenDate(account.getOpenDate());
        dto.setStatus(account.getStatus() != null ? account.getStatus().name() : "");
        return dto;
    }
}
