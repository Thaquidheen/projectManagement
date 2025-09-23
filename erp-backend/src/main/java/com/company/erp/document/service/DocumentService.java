// Enhanced DocumentService.java
package com.company.erp.document.service;

import com.company.erp.common.exception.FileUploadException;
import com.company.erp.common.service.AuditService;
import com.company.erp.document.entity.Document;
import com.company.erp.document.entity.DocumentCategory;
import com.company.erp.document.entity.DocumentMetadata;
import com.company.erp.document.entity.DocumentTag;
import com.company.erp.document.repository.DocumentRepository;
import com.company.erp.document.repository.DocumentMetadataRepository;
import com.company.erp.document.repository.DocumentTagRepository;
import com.company.erp.user.entity.User;
import com.company.erp.user.service.UserService;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Transactional
public class DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "application/pdf",
            "image/jpeg", "image/jpg", "image/png", "image/gif",
            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain", "text/csv"
    );

    private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/jpg", "image/png", "image/gif");

    @Value("${app.document.max-file-size:10485760}") // 10MB default
    private long maxFileSize;

    @Value("${app.document.compression-enabled:true}")
    private boolean compressionEnabled;

    @Value("${app.document.virus-scan-enabled:false}")
    private boolean virusScanEnabled;

    private final DocumentRepository documentRepository;
    private final DocumentMetadataRepository metadataRepository;
    private final DocumentTagRepository tagRepository;
    private final FileStorageService fileStorageService;
    private final UserService userService;
    private final AuditService auditService;
    private final Tika tika = new Tika();

    public DocumentService(DocumentRepository documentRepository,
                           DocumentMetadataRepository metadataRepository,
                           DocumentTagRepository tagRepository,
                           FileStorageService fileStorageService,
                           UserService userService,
                           AuditService auditService) {
        this.documentRepository = documentRepository;
        this.metadataRepository = metadataRepository;
        this.tagRepository = tagRepository;
        this.fileStorageService = fileStorageService;
        this.userService = userService;
        this.auditService = auditService;
    }

    public Document uploadDocument(MultipartFile file, Long projectId, Long uploadedById,
                                   DocumentCategory category, Set<String> tags) {
        validateFile(file);

        try {
            User uploader = userService.getUserById(uploadedById);

            // Create document entity
            Document document = new Document();
            document.setFileName(file.getOriginalFilename());
            document.setFileSize(file.getSize());
            document.setMimeType(detectMimeType(file));
            document.setProjectId(projectId);
            document.setUploadedBy(uploader);
            document.setCategory(category != null ? category : autoDetectCategory(file));
            document.setChecksum(calculateChecksum(file.getBytes()));

            // Process file content
            byte[] processedContent = processFileContent(file);
            document.setFileData(processedContent);
            document.setFileSize((long) processedContent.length);

            // Extract metadata
            DocumentMetadata metadata = extractMetadata(file, document);

            // Save document
            document = documentRepository.save(document);

            // Save metadata
            metadata.setDocument(document);
            metadataRepository.save(metadata);

            // Process and save tags
            if (tags != null && !tags.isEmpty()) {
                saveTags(document, tags);
            }

            // Auto-tag based on content
            autoTagDocument(document);

            // Extract text content for search (OCR for images)
            extractAndSaveTextContent(document, processedContent);

            // Audit log
            auditService.logAction(uploadedById, "DOCUMENT_UPLOADED", "DOCUMENT",
                    document.getId(), "Document uploaded: " + file.getOriginalFilename(),
                    null, document);

            logger.info("Document uploaded successfully: {} by user {}",
                    file.getOriginalFilename(), uploadedById);

            return document;

        } catch (Exception e) {
            logger.error("Failed to upload document {}: {}", file.getOriginalFilename(), e.getMessage(), e);
            throw new FileUploadException("Failed to upload document: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("File is empty");
        }

        if (file.getSize() > maxFileSize) {
            throw new FileUploadException("File size exceeds maximum allowed size of " +
                    (maxFileSize / 1024 / 1024) + "MB");
        }

        String mimeType = detectMimeType(file);
        if (!ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new FileUploadException("File type not allowed: " + mimeType);
        }

        if (virusScanEnabled && !isFileClean(file)) {
            throw new FileUploadException("File failed security scan");
        }
    }

    private String detectMimeType(MultipartFile file) {
        try {
            return tika.detect(file.getBytes(), file.getOriginalFilename());
        } catch (Exception e) {
            logger.warn("Failed to detect MIME type for {}, using content type: {}",