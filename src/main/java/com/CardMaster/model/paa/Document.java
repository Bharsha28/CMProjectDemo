package com.CardMaster.model.paa;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;

    @ManyToOne
    @JoinColumn(name = "application_id",nullable = false)
    private CardApplication application;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    @NotBlank

    private String fileURI;

    @CreationTimestamp
    private LocalDate uploadedDate;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status;

    // Enum for document type
    public enum DocumentType {
        IdentityProof,
        AddressProof,
        IncomeProof,
        EmploymentProof,
        Other
    }

    // Enum for document status
    public enum DocumentStatus {
        Submitted,
        NotSubmitted,
        Verified,
        Rejected
    }
}
