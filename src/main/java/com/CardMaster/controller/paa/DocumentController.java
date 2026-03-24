package com.CardMaster.controller.paa;

import com.CardMaster.dto.paa.DocumentDto;
import com.CardMaster.dto.paa.ResponseStructure;
import com.CardMaster.service.paa.DocumentService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private static final Logger log = LogManager.getLogger(DocumentController.class);
    private final DocumentService service;

    // --- Upload Document with File ---
    @PostMapping("/upload")
    public ResponseEntity<ResponseStructure<DocumentDto>> uploadFile(
            @RequestParam("applicationId") Long applicationId,
            @RequestParam("documentType") String documentType,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @RequestHeader("Authorization") String token) {

        log.info("Uploading actual file for application {}", applicationId);
        DocumentDto saved = service.uploadDocumentWithFile(applicationId, documentType, file, token);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseStructure<>("Document file uploaded successfully", saved));
    }

    // --- Upload Document ---
    @PostMapping
    public ResponseEntity<ResponseStructure<DocumentDto>> upload(
            @RequestBody DocumentDto dto,
            @RequestHeader("Authorization") String token) {

        log.info("Uploading document for application {}", dto.getApplicationId());
        DocumentDto saved = service.uploadDocument(dto, token);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseStructure<>("Document uploaded successfully", saved));
    }

    // --- Get Document by ID ---
    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<DocumentDto>> getById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        log.info("Fetching document {}", id);
        DocumentDto doc = service.getDocument(id, token);
        return ResponseEntity.ok(new ResponseStructure<>("Document retrieved successfully", doc));
    }

    // --- Get Documents by Application ---
    @GetMapping("/application/{applicationId}")
    public ResponseEntity<ResponseStructure<List<DocumentDto>>> getByApplication(
            @PathVariable Long applicationId,
            @RequestHeader("Authorization") String token) {

        log.info("Fetching documents for application {}", applicationId);
        List<DocumentDto> docs = service.getDocumentsByApplication(applicationId, token);
        return ResponseEntity.ok(new ResponseStructure<>("Documents retrieved successfully", docs));
    }

    // --- Update Document Status ---
    @PutMapping("/{id}/status")
    public ResponseEntity<ResponseStructure<DocumentDto>> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestHeader("Authorization") String token) {

        log.info("Updating document {} status to {}", id, status);
        DocumentDto updated = service.updateDocumentStatus(id, status, token);
        return ResponseEntity.ok(new ResponseStructure<>("Document status updated successfully", updated));
    }

    // --- Delete Document ---
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseStructure<Void>> deleteDocument(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        log.info("Deleting document {}", id);
        service.deleteDocument(id, token);
        return ResponseEntity.ok(new ResponseStructure<>("Document deleted successfully", null));
    }

    // --- Get All Documents ---
    @GetMapping
    public ResponseEntity<ResponseStructure<List<DocumentDto>>> getAllDocuments(
            @RequestHeader("Authorization") String token) {

        log.info("Fetching all documents");
        List<DocumentDto> docs = service.getAllDocuments(token);
        return ResponseEntity.ok(new ResponseStructure<>("All documents retrieved successfully", docs));
    }
}
