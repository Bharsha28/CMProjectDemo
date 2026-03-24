package com.CardMaster.dto.paa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDto {

    private Long documentId;

    @NotNull
    private Long applicationId;   // Reference to CardApplication

    @NotBlank
    private String documentType;  // Enum as String (IdentityProof, AddressProof, etc.)

    @NotBlank
    private String fileURI;

    private LocalDate uploadedDate; // Auto-generated in entity, read-only in API

    @NotBlank
    private String status;        // Enum as String (Submitted, NotSubmitted, Verified, Rejected)
}
