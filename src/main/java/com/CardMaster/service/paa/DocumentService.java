package com.CardMaster.service.paa;

import com.CardMaster.dao.paa.DocumentRepository;
import com.CardMaster.dao.paa.CardApplicationRepository;
import com.CardMaster.dto.paa.DocumentDto;
import com.CardMaster.exceptions.paa.DocumentNotFoundException;
import com.CardMaster.exceptions.paa.ApplicationNotFoundException;
import com.CardMaster.mapper.paa.EntityMapper;
import com.CardMaster.model.paa.Document;
import com.CardMaster.model.paa.CardApplication;
import com.CardMaster.security.iam.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentService {

    private final DocumentRepository repo;
    private final CardApplicationRepository appRepo;
    private final FileStorageService fileStorageService;
    private final JwtUtil jwtUtil;

    public DocumentDto uploadDocumentWithFile(Long applicationId, String documentType, org.springframework.web.multipart.MultipartFile file, String token) {
        jwtUtil.extractUsername(token.substring(7));

        CardApplication app = appRepo.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found with id: " + applicationId));

        String filename = fileStorageService.save(file);

        Document doc = repo.findByApplicationApplicationId(applicationId).stream()
                .filter(d -> d.getDocumentType() == Document.DocumentType.valueOf(documentType))
                .findFirst()
                .orElse(new Document());

        doc.setApplication(app);
        doc.setDocumentType(Document.DocumentType.valueOf(documentType));
        doc.setFileURI(filename);
        doc.setUploadedDate(java.time.LocalDate.now());
        doc.setStatus(Document.DocumentStatus.Submitted);

        Document saved = repo.save(doc);
        return EntityMapper.toDocumentDto(saved);
    }
    public DocumentDto uploadDocument(DocumentDto dto, String token) {
        jwtUtil.extractUsername(token.substring(7));

        CardApplication app = appRepo.findById(dto.getApplicationId())
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found with id: " + dto.getApplicationId()));

        Document doc = EntityMapper.toDocumentEntity(dto, app);
        Document saved = repo.save(doc);
        return EntityMapper.toDocumentDto(saved);
    }

    // --- Get Document by ID ---
    public DocumentDto getDocument(Long id, String token) {
        jwtUtil.extractUsername(token.substring(7));

        Document doc = repo.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with id: " + id));
        return EntityMapper.toDocumentDto(doc);
    }

    // --- Get Documents by Application ---
    public List<DocumentDto> getDocumentsByApplication(Long appId, String token) {
        jwtUtil.extractUsername(token.substring(7));

        List<Document> docs = repo.findByApplicationApplicationId(appId);
        return docs.stream().map(EntityMapper::toDocumentDto).toList();
    }

    // --- Update Document Status ---
    public DocumentDto updateDocumentStatus(Long id, String status, String token) {
        jwtUtil.extractUsername(token.substring(7));

        Document doc = repo.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with id: " + id));

        try {
            doc.setStatus(Document.DocumentStatus.valueOf(status));
        } catch (IllegalArgumentException e) {
            throw new DocumentNotFoundException("Invalid status value: " + status);
        }

        Document updated = repo.save(doc);


        if (updated.getStatus() == Document.DocumentStatus.Rejected) {
            CardApplication app = updated.getApplication();
            if (app.getStatus() != CardApplication.CardApplicationStatus.Approved
                    && app.getStatus() != CardApplication.CardApplicationStatus.Rejected) {
                app.setStatus(CardApplication.CardApplicationStatus.UnderReview);
                appRepo.save(app);
            }
        }

        return EntityMapper.toDocumentDto(updated);
    }

    // --- Delete Document ---
    public void deleteDocument(Long id, String token) {
        jwtUtil.extractUsername(token.substring(7));

        if (!repo.existsById(id)) {
            throw new DocumentNotFoundException("Document not found with id: " + id);
        }
        repo.deleteById(id);
    }

    // --- Get All Documents ---
    public List<DocumentDto> getAllDocuments(String token) {
        jwtUtil.extractUsername(token.substring(7));
        return repo.findAll().stream().map(EntityMapper::toDocumentDto).toList();
    }
}