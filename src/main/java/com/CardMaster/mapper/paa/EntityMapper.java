package com.CardMaster.mapper.paa;

import com.CardMaster.dto.paa.CustomerDto;
import com.CardMaster.dto.paa.CardApplicationDto;
import com.CardMaster.dto.paa.DocumentDto;
import com.CardMaster.model.cpl.CardProduct;
import com.CardMaster.model.paa.Customer;
import com.CardMaster.model.paa.CardApplication;
import com.CardMaster.model.paa.Document;

public class EntityMapper {

    // --- Customer ---
    public static CustomerDto toCustomerDto(Customer customer) {
        CustomerDto dto = new CustomerDto();
        dto.setCustomerId(customer.getCustomerId());
        dto.setName(customer.getName());
        dto.setDob(customer.getDob().toString());
        dto.setContactInfo(customer.getContactInfo());
        dto.setIncome(customer.getIncome());
        dto.setEmploymentType(customer.getEmploymentType().name());
        dto.setStatus(customer.getStatus().name());
        return dto;
    }

    public static Customer toCustomerEntity(CustomerDto dto) {
        Customer customer = new Customer();
        customer.setCustomerId(dto.getCustomerId());
        customer.setName(dto.getName());
        customer.setDob(java.time.LocalDate.parse(dto.getDob()));
        customer.setContactInfo(dto.getContactInfo());
        customer.setIncome(dto.getIncome());
        customer.setEmploymentType(Customer.EmploymentType.valueOf(dto.getEmploymentType()));
        customer.setStatus(Customer.CustomerStatus.valueOf(dto.getStatus()));
        return customer;
    }

    // --- CardApplication ---
    public static CardApplicationDto toCardApplicationDto(CardApplication app) {
        CardApplicationDto dto = new CardApplicationDto();
        dto.setApplicationId(app.getApplicationId());
        dto.setCustomerId(app.getCustomer().getCustomerId());
        dto.setProductId(app.getProduct().getProductId());
        dto.setRequestedLimit(app.getRequestedLimit());
        dto.setApplicationDate(app.getApplicationDate());
        dto.setStatus(app.getStatus().name());
        return dto;
    }

    // Here we pass in the actual Customer and CardProduct entities
    public static CardApplication toCardApplicationEntity(CardApplicationDto dto, Customer customer, CardProduct product) {
        CardApplication app = new CardApplication();
        app.setApplicationId(dto.getApplicationId());
        app.setCustomer(customer);
        app.setProduct(product);
        app.setRequestedLimit(dto.getRequestedLimit());
        app.setApplicationDate(dto.getApplicationDate());
        app.setStatus(CardApplication.CardApplicationStatus.valueOf(dto.getStatus()));
        return app;
    }

    // --- Document ---
    public static DocumentDto toDocumentDto(Document doc) {
        DocumentDto dto = new DocumentDto();
        dto.setDocumentId(doc.getDocumentId());
        dto.setApplicationId(doc.getApplication().getApplicationId());
        dto.setDocumentType(doc.getDocumentType().name());
        dto.setFileURI(doc.getFileURI());
        dto.setUploadedDate(doc.getUploadedDate());
        dto.setStatus(doc.getStatus().name());
        return dto;
    }

    public static Document toDocumentEntity(DocumentDto dto, CardApplication app) {
        Document doc = new Document();
        doc.setDocumentId(dto.getDocumentId());
        doc.setApplication(app);
        doc.setDocumentType(Document.DocumentType.valueOf(dto.getDocumentType()));
        doc.setFileURI(dto.getFileURI());
        doc.setUploadedDate(dto.getUploadedDate());
        doc.setStatus(Document.DocumentStatus.valueOf(dto.getStatus()));
        return doc;
    }
}
