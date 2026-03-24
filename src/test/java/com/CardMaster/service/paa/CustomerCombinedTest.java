package com.CardMaster.service.paa;

import com.CardMaster.controller.paa.CustomerController;
import com.CardMaster.dao.paa.CustomerRepository;
import com.CardMaster.dto.paa.CustomerDto;
import com.CardMaster.dto.paa.ResponseStructure;
import com.CardMaster.exceptions.paa.CustomerNotFoundException;
import com.CardMaster.mapper.paa.EntityMapper;
import com.CardMaster.model.paa.ContactInfo_Customer;
import com.CardMaster.model.paa.Customer;
import com.CardMaster.security.iam.JwtUtil;
import com.CardMaster.service.paa.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomerCombinedTest {

    // --- Service layer mocks ---
    @Mock private CustomerRepository customerRepository;
    @Mock private JwtUtil jwtUtil;
    @InjectMocks private CustomerService customerService;

    // --- Controller layer mocks ---
    @Mock private CustomerService service;
    @InjectMocks private CustomerController controller;

    private CustomerDto dto;
    private Customer entity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        dto = new CustomerDto();
        dto.setCustomerId(1L);
        dto.setName("Alice");
        dto.setDob("1990-01-01");
        dto.setIncome(50000.0);
        dto.setEmploymentType("Salaried");
        dto.setStatus("Active");
        dto.setContactInfo(new ContactInfo_Customer("Addr", "alice@example.com", "1234567890"));

        entity = EntityMapper.toCustomerEntity(dto);
        entity.setCustomerId(1L);
    }

    // ---------------- SERVICE TESTS ----------------
    @Test
    void testCreateCustomer_Success() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("user");
        when(customerRepository.save(any(Customer.class))).thenReturn(entity);

        CustomerDto result = customerService.createCustomer(dto, "Bearer token");
        assertEquals("Alice", result.getName());
        assertEquals(1L, result.getCustomerId());
    }

    @Test
    void testGetCustomer_Success() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("user");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(entity));

        CustomerDto result = customerService.getCustomer(1L, "Bearer token");
        assertEquals("Alice", result.getName());
    }

    @Test
    void testGetCustomer_NotFound() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("user");
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class,
                () -> customerService.getCustomer(1L, "Bearer token"));
    }

    @Test
    void testUpdateCustomer_Success() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("user");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(customerRepository.save(any(Customer.class))).thenReturn(entity);

        dto.setName("Updated");
        CustomerDto result = customerService.updateCustomer(1L, dto, "Bearer token");
        assertEquals("Updated", result.getName());
    }

    @Test
    void testDeleteCustomer_Success() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("user");
        when(customerRepository.existsById(1L)).thenReturn(true);

        customerService.deleteCustomer(1L, "Bearer token");
        verify(customerRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteCustomer_NotFound() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("user");
        when(customerRepository.existsById(1L)).thenReturn(false);

        assertThrows(CustomerNotFoundException.class,
                () -> customerService.deleteCustomer(1L, "Bearer token"));
    }

    @Test
    void testGetAllCustomers_Success() {
        Customer c1 = new Customer();
        c1.setCustomerId(1L);
        c1.setName("Alice");
        c1.setDob(LocalDate.of(1990, 1, 1)); // required
        c1.setEmploymentType(Customer.EmploymentType.Salaried); // required if mapper uses it
        c1.setStatus(Customer.CustomerStatus.Active);           // required if mapper uses it
        c1.setContactInfo(new ContactInfo_Customer("Addr1", "alice@example.com", "1111111111"));

        Customer c2 = new Customer();
        c2.setCustomerId(2L);
        c2.setName("Bob");
        c2.setDob(LocalDate.of(1985, 5, 20)); // required
        c2.setEmploymentType(Customer.EmploymentType.Student);
        c2.setStatus(Customer.CustomerStatus.Inactive);
        c2.setContactInfo(new ContactInfo_Customer("Addr2", "bob@example.com", "2222222222"));

        when(jwtUtil.extractUsername(anyString())).thenReturn("user");
        when(customerRepository.findAll()).thenReturn(List.of(c1, c2));

        List<CustomerDto> result = customerService.getAllCustomers("Bearer token");

        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getName());
        assertEquals("Bob", result.get(1).getName());
        assertEquals("1990-01-01", result.get(0).getDob());
        assertEquals("1985-05-20", result.get(1).getDob());
    }


    // ---------------- CONTROLLER TESTS ----------------
    @Test
    void testControllerCreateCustomer() {
        when(service.createCustomer(any(CustomerDto.class), anyString())).thenReturn(dto);

        ResponseEntity<ResponseStructure<CustomerDto>> response =
                controller.createCustomer(dto, "Bearer token");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Customer created successfully", response.getBody().getMessage());
    }

    @Test
    void testControllerGetCustomer() {
        when(service.getCustomer(1L, "Bearer token")).thenReturn(dto);

        ResponseEntity<ResponseStructure<CustomerDto>> response =
                controller.getCustomer(1L, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Alice", response.getBody().getData().getName());
    }

    @Test
    void testControllerUpdateCustomer() {
        dto.setName("Updated");
        when(service.updateCustomer(eq(1L), any(CustomerDto.class), anyString())).thenReturn(dto);

        ResponseEntity<ResponseStructure<CustomerDto>> response =
                controller.updateCustomer(1L, dto, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated", response.getBody().getData().getName());
    }

    @Test
    void testControllerDeleteCustomer() {
        doNothing().when(service).deleteCustomer(1L, "Bearer token");

        ResponseEntity<ResponseStructure<Void>> response =
                controller.deleteCustomer(1L, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Customer deleted successfully", response.getBody().getMessage());
    }

    @Test
    void testControllerGetAllCustomers() {
        when(service.getAllCustomers("Bearer token")).thenReturn(List.of(dto));

        ResponseEntity<ResponseStructure<List<CustomerDto>>> response =
                controller.getAllCustomers("Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getData().size());
    }

    // ---------------- REPOSITORY TESTS (Mockito-based) ----------------
    @Test
    void testRepositorySaveAndFind() {
        when(customerRepository.save(any(Customer.class))).thenReturn(entity);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(entity));

        Customer saved = customerRepository.save(entity);
        Optional<Customer> found = customerRepository.findById(1L);

        assertTrue(found.isPresent());
        assertEquals("Alice", found.get().getName());
        assertEquals(saved.getCustomerId(), found.get().getCustomerId());
    }
}
