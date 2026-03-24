package com.CardMaster.service.paa;

import com.CardMaster.controller.paa.DocumentController;
import com.CardMaster.dao.paa.CardApplicationRepository;
import com.CardMaster.dao.paa.DocumentRepository;
import com.CardMaster.dto.paa.DocumentDto;
import com.CardMaster.dto.paa.ResponseStructure;
import com.CardMaster.exceptions.paa.ApplicationNotFoundException;
import com.CardMaster.exceptions.paa.DocumentNotFoundException;
import com.CardMaster.model.paa.CardApplication;
import com.CardMaster.model.paa.Document;
import com.CardMaster.security.iam.JwtUtil;
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

public class DocumentCombinedTest {

    @Mock private DocumentRepository repo;
    @Mock private CardApplicationRepository appRepo;
    @Mock private JwtUtil jwtUtil;
    @InjectMocks private DocumentService service;

    @Mock private DocumentService mockService;
    @InjectMocks private DocumentController controller;

    private DocumentDto dto;
    private Document doc;
    private CardApplication app;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        app = new CardApplication();
        app.setApplicationId(1L);
        app.setStatus(CardApplication.CardApplicationStatus.Submitted);

        dto = new DocumentDto();
        dto.setDocumentId(10L);
        dto.setApplicationId(1L);
        dto.setStatus("Submitted");
        dto.setDocumentType("IdentityProof"); // must match enum

        doc = new Document();
        doc.setDocumentId(10L);
        doc.setApplication(app);
        doc.setStatus(Document.DocumentStatus.Submitted);
        doc.setDocumentType(Document.DocumentType.IdentityProof); // valid enum
    }

    // -------- Service Tests --------
    @Test
    void testUploadDocument_Success() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("user");
        when(appRepo.findById(1L)).thenReturn(Optional.of(app));
        when(repo.save(any(Document.class))).thenReturn(doc);

        DocumentDto result = service.uploadDocument(dto, "Bearer token");
        assertEquals(10L, result.getDocumentId());
        assertEquals("IdentityProof", result.getDocumentType());
    }

    @Test
    void testUploadDocument_ApplicationNotFound() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("user");
        when(appRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ApplicationNotFoundException.class,
                () -> service.uploadDocument(dto, "Bearer token"));
    }

    @Test
    void testGetDocument_Success() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("user");
        when(repo.findById(10L)).thenReturn(Optional.of(doc));

        DocumentDto result = service.getDocument(10L, "Bearer token");
        assertEquals("IdentityProof", result.getDocumentType());
    }

    @Test
    void testGetDocument_NotFound() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("user");
        when(repo.findById(10L)).thenReturn(Optional.empty());

        assertThrows(DocumentNotFoundException.class,
                () -> service.getDocument(10L, "Bearer token"));
    }

    @Test
    void testGetDocumentsByApplication() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("user");
        when(repo.findByApplicationApplicationId(1L)).thenReturn(List.of(doc));

        List<DocumentDto> result = service.getDocumentsByApplication(1L, "Bearer token");
        assertEquals(1, result.size());
        assertEquals("IdentityProof", result.get(0).getDocumentType());
    }

    @Test
    void testUpdateDocumentStatus_Success() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("user");
        when(repo.findById(10L)).thenReturn(Optional.of(doc));
        when(repo.save(any(Document.class))).thenReturn(doc);

        DocumentDto result = service.updateDocumentStatus(10L, "Rejected", "Bearer token");
        assertEquals("Rejected", result.getStatus());
    }

    @Test
    void testUpdateDocumentStatus_InvalidStatus() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("user");
        when(repo.findById(10L)).thenReturn(Optional.of(doc));

        assertThrows(DocumentNotFoundException.class,
                () -> service.updateDocumentStatus(10L, "INVALID", "Bearer token"));
    }

    @Test
    void testDeleteDocument_Success() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("user");
        when(repo.existsById(10L)).thenReturn(true);

        service.deleteDocument(10L, "Bearer token");
        verify(repo, times(1)).deleteById(10L);
    }

    @Test
    void testDeleteDocument_NotFound() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("user");
        when(repo.existsById(10L)).thenReturn(false);

        assertThrows(DocumentNotFoundException.class,
                () -> service.deleteDocument(10L, "Bearer token"));
    }

    @Test
    void testGetAllDocuments() {
        when(jwtUtil.extractUsername(anyString())).thenReturn("user");
        when(repo.findAll()).thenReturn(List.of(doc));

        List<DocumentDto> result = service.getAllDocuments("Bearer token");
        assertEquals(1, result.size());
        assertEquals("IdentityProof", result.get(0).getDocumentType());
    }

    // -------- Controller Tests --------
    @Test
    void testControllerUploadDocument() {
        when(mockService.uploadDocument(any(DocumentDto.class), anyString())).thenReturn(dto);

        ResponseEntity<ResponseStructure<DocumentDto>> response =
                controller.upload(dto, "Bearer token");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Document uploaded successfully", response.getBody().getMessage());
    }

    @Test
    void testControllerGetById() {
        when(mockService.getDocument(10L, "Bearer token")).thenReturn(dto);

        ResponseEntity<ResponseStructure<DocumentDto>> response =
                controller.getById(10L, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10L, response.getBody().getData().getDocumentId());
    }

    @Test
    void testControllerGetByApplication() {
        when(mockService.getDocumentsByApplication(1L, "Bearer token")).thenReturn(List.of(dto));

        ResponseEntity<ResponseStructure<List<DocumentDto>>> response =
                controller.getByApplication(1L, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getData().size());
    }

    @Test
    void testControllerUpdateStatus() {
        dto.setStatus("Approved");
        when(mockService.updateDocumentStatus(10L, "Approved", "Bearer token")).thenReturn(dto);

        ResponseEntity<ResponseStructure<DocumentDto>> response =
                controller.updateStatus(10L, "Approved", "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Approved", response.getBody().getData().getStatus());
    }

    @Test
    void testControllerDeleteDocument() {
        doNothing().when(mockService).deleteDocument(10L, "Bearer token");

        ResponseEntity<ResponseStructure<Void>> response =
                controller.deleteDocument(10L, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Document deleted successfully", response.getBody().getMessage());
    }

    @Test
    void testControllerGetAllDocuments() {
        when(mockService.getAllDocuments("Bearer token")).thenReturn(List.of(dto));

        ResponseEntity<ResponseStructure<List<DocumentDto>>> response =
                controller.getAllDocuments("Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getData().size());
    }

    // -------- Repository Tests (Mockito-based) --------
    @Test
    void testRepositorySaveAndFind() {
        DocumentRepository mockRepo = mock(DocumentRepository.class);

        when(mockRepo.save(any(Document.class))).thenReturn(doc);
        when(mockRepo.findById(10L)).thenReturn(Optional.of(doc));
        when(mockRepo.findByApplicationApplicationId(1L)).thenReturn(List.of(doc));

        Document saved = mockRepo.save(doc);
        Optional<Document> found = mockRepo.findById(10L);
        List<Document> byApp = mockRepo.findByApplicationApplicationId(1L);

        assertEquals(10L, saved.getDocumentId());
        assertTrue(found.isPresent());
        assertEquals(1, byApp.size());
    }
    @Test
    void testFindByApplicationApplicationId_NoResults() {
        DocumentRepository mockRepo = mock(DocumentRepository.class);

        when(mockRepo.findByApplicationApplicationId(99L)).thenReturn(List.of());

        List<Document> result = mockRepo.findByApplicationApplicationId(99L);
        assertTrue(result.isEmpty());
    }

    @Test
    void testExistsById() {
        DocumentRepository mockRepo = mock(DocumentRepository.class);

        when(mockRepo.existsById(10L)).thenReturn(true);
        when(mockRepo.existsById(99L)).thenReturn(false);

        assertTrue(mockRepo.existsById(10L));
        assertFalse(mockRepo.existsById(99L));
    }

    @Test
    void testDeleteById() {
        DocumentRepository mockRepo = mock(DocumentRepository.class);

        doNothing().when(mockRepo).deleteById(10L);

        mockRepo.deleteById(10L);
        verify(mockRepo, times(1)).deleteById(10L);
    }

    @Test
    void testFindAll() {
        DocumentRepository mockRepo = mock(DocumentRepository.class);

        CardApplication app = new CardApplication();
        app.setApplicationId(1L);

        Document doc1 = new Document();
        doc1.setDocumentId(1L);
        doc1.setApplication(app);
        doc1.setStatus(Document.DocumentStatus.Submitted);
        doc1.setDocumentType(Document.DocumentType.IdentityProof);

        Document doc2 = new Document();
        doc2.setDocumentId(2L);
        doc2.setApplication(app);
        doc2.setStatus(Document.DocumentStatus.Verified);
        doc2.setDocumentType(Document.DocumentType.AddressProof);

        when(mockRepo.findAll()).thenReturn(List.of(doc1, doc2));

        List<Document> result = mockRepo.findAll();
        assertEquals(2, result.size());
        assertEquals(Document.DocumentType.IdentityProof, result.get(0).getDocumentType());
        assertEquals(Document.DocumentType.AddressProof, result.get(1).getDocumentType());
    }

}
