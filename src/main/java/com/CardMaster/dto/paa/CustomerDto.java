package com.CardMaster.dto.paa;


import com.CardMaster.model.paa.ContactInfo_Customer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomerDto {

    private Long customerId;
    @NotBlank
    private String name;

    @NotNull
    private String dob; // Represented as String (e.g., "1990-05-10") for JSON compatibility

    private ContactInfo_Customer contactInfo;

    @NotNull
    private Double income;

    @NotBlank
    private String employmentType; // Enum mapped as String

    @NotBlank
    private String status; // Enum mapped as String
}



