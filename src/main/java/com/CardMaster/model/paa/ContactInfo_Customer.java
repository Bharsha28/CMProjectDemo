package com.CardMaster.model.paa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactInfo_Customer {

        @NotBlank
        private String address;

        @Email
        @NotBlank
        @Column(unique = true)
        private String email;

        @Size(max = 10)
        @Column(unique = true, nullable = false)
        private String phone;
}

