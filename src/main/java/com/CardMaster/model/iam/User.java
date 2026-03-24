package com.CardMaster.model.iam;

import com.CardMaster.Enum.iam.UserEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.CardMaster.model.cau.UnderwritingDecision;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank
    @Column(length = 100, nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserEnum role; // CUSTOMER, OFFICER, UNDERWRITER, RISK, ADMIN

    @Email
    @Column(length = 100, unique = true, nullable = false)
    private String email;

    @NotBlank
    @Column(length = 10, unique = true, nullable = false)
    private String phone;


    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Column(length =100, nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuditLog> auditLogs;

    @OneToMany(mappedBy = "underwriter")
    private List<UnderwritingDecision> underwritingDecisions;
}
