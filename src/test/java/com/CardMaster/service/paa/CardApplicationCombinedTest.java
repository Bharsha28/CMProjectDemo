package com.CardMaster.service.paa;

import com.CardMaster.controller.paa.CardApplicationController;
import com.CardMaster.dao.cpl.CardProductRepository;
import com.CardMaster.dao.paa.CardApplicationRepository;
import com.CardMaster.dao.paa.CustomerRepository;
import com.CardMaster.dao.paa.DocumentRepository;
import com.CardMaster.dto.paa.CardApplicationDto;
import com.CardMaster.dto.paa.ResponseStructure;
import com.CardMaster.exceptions.cpl.NotFoundException;
import com.CardMaster.exceptions.paa.ApplicationNotFoundException;
import com.CardMaster.exceptions.paa.CustomerNotFoundException;
import com.CardMaster.model.cpl.CardProduct;
import com.CardMaster.model.paa.CardApplication;
import com.CardMaster.model.paa.Customer;
import com.CardMaster.security.iam.JwtUtil;
import com.CardMaster.service.paa.CardApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardApplicationCombinedTest {

    // --- Service layer mocks ---
    @Mock private CardApplicationRepository applicationRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private CardProductRepository productRepository;
    @Mock private DocumentRepository documentRepository;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks private CardApplicationService service;

    // --- Controller layer mocks ---
    @Mock private CardApplicationService applicationService; // mock for controller
    @InjectMocks private CardApplicationController controller;

    private Customer customer;
    private CardProduct product;
    private CardApplication app;
    private CardApplicationDto dto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = new Customer();
        customer.setCustomerId(1L);

        product = new CardProduct();
        product.setProductId(2L);

        app = new CardApplication();
        app.setApplicationId(5L);
        app.setCustomer(customer);
        app.setProduct(product);
        app.setStatus(CardApplication.CardApplicationStatus.Submitted);

        dto = new CardApplicationDto();
        dto.setApplicationId(5L);
        dto.setCustomerId(1L);
        dto.setProductId(2L);
        dto.setStatus("Submitted");
    }

    // --- Service Tests ---
    @Test
    void testCreateApplication_CustomerNotFound() {
        CardApplicationDto input = new CardApplicationDto();
        input.setCustomerId(99L);

        when(jwtUtil.extractUsername(anyString())).thenReturn("user");
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class,
                () -> service.create(input, "Bearer token"));
    }

    @Test
    void testCreateApplication_ProductNotFound() {
        CardApplicationDto input = new CardApplicationDto();
        input.setCustomerId(1L);
        input.setProductId(99L);

        when(jwtUtil.extractUsername(anyString())).thenReturn("user");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(applicationRepository.findByCustomerCustomerId(1L)).thenReturn(List.of());
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.create(input, "Bearer token"));
    }

    @Test
    void testFindById_Success() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("user");
        when(applicationRepository.findById(5L)).thenReturn(Optional.of(app));

        CardApplicationDto result = service.findById(5L, "Bearer token");
        assertEquals(5L, result.getApplicationId());
    }

    @Test
    void testFindById_NotFound() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("user");
        when(applicationRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ApplicationNotFoundException.class,
                () -> service.findById(5L, "Bearer token"));
    }

    @Test
    void testUpdateApplicationStatus_Success() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("user");
        when(applicationRepository.findById(5L)).thenReturn(Optional.of(app));
        when(applicationRepository.save(any(CardApplication.class))).thenReturn(app);

        CardApplicationDto result = service.updateApplicationStatus(5L, "UNDERREVIEW", "Bearer token");
        assertEquals("UnderReview", result.getStatus());
    }


    @Test
    void testUpdateApplicationStatus_NotFound() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("user");
        when(applicationRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ApplicationNotFoundException.class,
                () -> service.updateApplicationStatus(5L, "Approved", "Bearer token"));
    }

    // --- Controller Tests ---
    @Test
    void testControllerCreate() {
        when(applicationService.create(any(CardApplicationDto.class), anyString())).thenReturn(dto);

        ResponseEntity<ResponseStructure<CardApplicationDto>> response =
                controller.create(dto, "Bearer token");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Application created successfully", response.getBody().getMessage());
    }

    @Test
    void testControllerGetById() {
        when(applicationService.findById(5L, "Bearer token")).thenReturn(dto);

        ResponseEntity<ResponseStructure<CardApplicationDto>> response =
                controller.getById(5L, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Application retrieved successfully", response.getBody().getMessage());
    }

    @Test
    void testControllerGetAllApplications() {
        when(applicationService.getAllApplications("Bearer token")).thenReturn(List.of(dto));

        ResponseEntity<ResponseStructure<List<CardApplicationDto>>> response =
                controller.getAllApplications("Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getData().size());
    }

    @Test
    void testControllerGetByCustomer_Success() {
        when(applicationService.getApplicationsByCustomer(1L, "Bearer token")).thenReturn(List.of(dto));

        ResponseEntity<ResponseStructure<List<CardApplicationDto>>> response =
                controller.getByCustomer(1L, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getData().size());
    }

    @Test
    void testControllerGetByCustomer_NoApplications() {
        when(applicationService.getApplicationsByCustomer(1L, "Bearer token")).thenReturn(List.of());

        ResponseEntity<ResponseStructure<List<CardApplicationDto>>> response =
                controller.getByCustomer(1L, "Bearer token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Customer doesn't have any applications", response.getBody().getMessage());
    }

    @Test
    void testControllerGetStatus() {
        when(applicationService.findById(5L, "Bearer token")).thenReturn(dto);

        ResponseEntity<ResponseStructure<Object>> response =
                controller.getStatus(5L, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        var map = (java.util.Map<?, ?>) response.getBody().getData();
        assertEquals(5L, map.get("applicationId"));
        assertEquals("Submitted", map.get("status"));
    }

    @Test
    void testControllerUpdateStatus() {
        dto.setStatus("Approved");
        when(applicationService.updateApplicationStatus(5L, "Approved", "Bearer token")).thenReturn(dto);

        ResponseEntity<ResponseStructure<CardApplicationDto>> response =
                controller.updateStatus(5L, "Approved", "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Approved", response.getBody().getData().getStatus());
    }
}
