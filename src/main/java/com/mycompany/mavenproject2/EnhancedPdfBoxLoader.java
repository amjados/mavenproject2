/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject2;
import java.io.IOException;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;

import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class EnhancedPdfBoxLoader {
    
    public static List<Document> loadWithEnhancedFeatures(String directoryPath, PathMatcher pathMatcher) throws IOException {
        List<Document> documents = new ArrayList<>();        
        
        Files.walk(Paths.get(directoryPath))
            .filter(path -> pathMatcher.matches(path))
            .forEach(path -> {
                try {
                    documents.addAll(processAdvancedPdf(path.toString()));
                } catch (IOException e) {
                    System.err.println("Error processing: " + path + " - " + e.getMessage());
                }
            });
        
        return documents;
    }
    
    private static List<Document> processAdvancedPdf(String filePath) throws IOException {
        List<Document> documents = new ArrayList<>();
        
        File pdFile = new File(filePath);
        try (PDDocument document = PDDocument.load(pdFile)) {
            PDDocumentInformation info = document.getDocumentInformation();
            
            // Process each page separately (like PyMuPDF)
            for (int pageNum = 0; pageNum < document.getNumberOfPages(); pageNum++) {
                PDPage page = document.getPage(pageNum);
                
                // Extract text with position information
                PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                stripper.setSortByPosition(true);
                
                // Define regions for different parts of the page
                Rectangle2D region = new Rectangle2D.Float(0, 0, 
                    page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
                stripper.addRegion("page", region);
                stripper.extractRegions(page);
                
                String pageText = stripper.getTextForRegion("page");
                
                // Enhanced metadata
                Map<String, Object> metadata = createEnhancedMetadata(filePath, info, pageNum, page, document);
                
                // Detect potential table structures in text
                String enhancedContent = detectAndMarkStructures(pageText);
                
                documents.add(Document.from(enhancedContent, Metadata.from(metadata)));
            }
        }
        
        return documents;
    }
    
private static Map<String, Object> createEnhancedMetadata(String filePath, 
                                                        PDDocumentInformation info,
                                                        int pageNum, 
                                                        PDPage page, 
                                                        PDDocument document) throws IOException {
    Map<String, Object> metadata = new HashMap<>();
    
    // ... other metadata ...
    File pdFile = new File(filePath);
    
    // Image detection - store as string
    List<String> imageInfo = detectImages(page);
    if (!imageInfo.isEmpty()) {
        metadata.put("has_images", "true");  // Store as String
        metadata.put("image_count", imageInfo.size());
        metadata.put("image_objects", String.join(",", imageInfo));
    } else {
        metadata.put("has_images", "false"); // Store as String
        metadata.put("image_count", 0);
    }
    metadata.put("file_name", pdFile.getName());
    metadata.put("page_number", String.valueOf(pageNum));
    
    return metadata;
}

    private static List<String> detectImages(PDPage page) {
        List<String> imageInfo = new ArrayList<>();
        
        if (page.getResources() != null && page.getResources().getXObjectNames() != null) {
            for (COSName name : page.getResources().getXObjectNames()) {
                try {
                    if (page.getResources().getXObject(name) instanceof PDImageXObject) {
                        PDImageXObject image = (PDImageXObject) page.getResources().getXObject(name);
                        imageInfo.add(String.format("%s(%dx%d)", 
                                name.getName(), image.getWidth(), image.getHeight()));
                    }
                } catch (IOException e) {
                    // Skip problematic images
                }
            }
        } // Handle error
        
        return imageInfo;
    }
    
    private static String detectAndMarkStructures(String text) {
        StringBuilder enhanced = new StringBuilder();
        String[] lines = text.split("\n");
        
        for (String line : lines) {
            // Simple table detection (lines with multiple tabs or spaces)
            if (line.matches(".*\\s{3,}.*\\s{3,}.*") || line.contains("\t")) {
                enhanced.append("[TABLE_ROW] ").append(line).append("\n");
            }
            // Header detection (short lines, often centered)
            else if (line.length() < 50 && line.trim().length() > 0 && 
                     line.matches(".*[A-Z]{2,}.*")) {
                enhanced.append("[HEADER] ").append(line).append("\n");
            }
            // List detection
            else if (line.matches("^\\s*[•\\-\\*]\\s+.*") || 
                     line.matches("^\\s*\\d+\\.\\s+.*")) {
                enhanced.append("[LIST_ITEM] ").append(line).append("\n");
            }
            else {
                enhanced.append(line).append("\n");
            }
        }
        
        return enhanced.toString();
    }
    
    private static void addIfNotNull(Map<String, Object> map, String key, String value) {
        if (value != null && !value.trim().isEmpty()) {
            map.put(key, value);
        }
    }
}