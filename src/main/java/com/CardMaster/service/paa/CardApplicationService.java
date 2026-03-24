
package com.CardMaster.service.paa;

import com.CardMaster.Enum.cau.UnderwritingDecisionType;
import com.CardMaster.dao.cpl.CardProductRepository;
import com.CardMaster.dao.paa.CardApplicationRepository;
import com.CardMaster.dao.paa.CustomerRepository;
import com.CardMaster.dao.paa.DocumentRepository;
import com.CardMaster.dto.cau.UnderwritingDecisionRequest;
import com.CardMaster.dto.cau.UnderwritingDecisionResponse;
import com.CardMaster.dto.paa.CardApplicationDto;
import com.CardMaster.exceptions.paa.ApplicationNotFoundException;
import com.CardMaster.exceptions.paa.CustomerNotFoundException;
import com.CardMaster.exceptions.cpl.NotFoundException;
import com.CardMaster.exceptions.paa.DuplicateApplicationException;
import com.CardMaster.mapper.paa.EntityMapper;
import com.CardMaster.model.cpl.CardProduct;
import com.CardMaster.model.paa.CardApplication;
import com.CardMaster.model.paa.Customer;
import com.CardMaster.model.paa.Document;
import com.CardMaster.security.iam.JwtUtil;
import com.CardMaster.service.cau.UnderwritingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional
public class
CardApplicationService {

    private final CardApplicationRepository applicationRepository;
    private final CustomerRepository customerRepository;
    private final CardProductRepository productRepo;
    private final DocumentRepository documentRepository;
    private final UnderwritingService underwritingService;
    private final JwtUtil jwtUtil;


    // --- Create Application ---
    public CardApplicationDto create(CardApplicationDto dto, String token) {
        jwtUtil.extractUsername(token.substring(7)); // validate token

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + dto.getCustomerId()));


        // For testing purposes, we allow multiple applications (up to 10)
        List<CardApplication> existingApps = applicationRepository.findByCustomerCustomerId(dto.getCustomerId());
        if (existingApps.size() > 10) {
            throw new DuplicateApplicationException("Customer has too many applications (limit 10 for demo).");
        }

        CardProduct product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        CardApplication application = EntityMapper.toCardApplicationEntity(dto, customer, product);
        application.setStatus(CardApplication.CardApplicationStatus.Submitted);

        CardApplication saved = applicationRepository.save(application);
        return EntityMapper.toCardApplicationDto(saved);
    }

    // --- Get Application by ID ---
    public CardApplicationDto findById(Long id, String token) {
        jwtUtil.extractUsername(token.substring(7)); // validate token

        CardApplication app = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found with id: " + id));
        return EntityMapper.toCardApplicationDto(app);
    }

    // --- Get All Applications ---
    public List<CardApplicationDto> getAllApplications(String token) {
        jwtUtil.extractUsername(token.substring(7)); // validate token

        List<CardApplication> apps = applicationRepository.findAll();
        List<CardApplicationDto> dtos = new ArrayList<>();
        for (CardApplication app : apps) {
            dtos.add(EntityMapper.toCardApplicationDto(app));
        }
        return dtos;
    }

    // --- Get Applications by Customer ---
    public List<CardApplicationDto> getApplicationsByCustomer(Long customerId, String token) {
        jwtUtil.extractUsername(token.substring(7)); // validate token

        List<CardApplication> apps = applicationRepository.findByCustomerCustomerId(customerId);
        List<CardApplicationDto> dtos = new ArrayList<>();
        for (CardApplication app : apps) {
            dtos.add(EntityMapper.toCardApplicationDto(app));
        }
        return dtos;
    }


    public CardApplicationDto updateApplicationStatus(Long appId, String status, String token) {
        jwtUtil.extractUsername(token.substring(7)); // validate token

        CardApplication app = applicationRepository.findById(appId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found with id: " + appId));

        if (app.getStatus() == CardApplication.CardApplicationStatus.Approved
                || app.getStatus() == CardApplication.CardApplicationStatus.Rejected) {
            throw new IllegalStateException("Final application status can only be set by underwriting");
        }

        String normalized = status.toUpperCase();
        if (!"UNDERREVIEW".equals(normalized)) {
            throw new IllegalArgumentException("Only UNDERREVIEW can be set via this endpoint");
        }

        app.setStatus(CardApplication.CardApplicationStatus.UnderReview);
        applicationRepository.save(app);

        return EntityMapper.toCardApplicationDto(app);
    }
}
