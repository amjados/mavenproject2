/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject2;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class AdvancedPdfProcessor {
    
    public static List<Document> loadWithAdvancedFeatures(String filePath) throws IOException {
        List<Document> documents = new ArrayList<>();
        
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            // Extract text with page-by-page processing
            PDFTextStripper stripper = new PDFTextStripper();
            
            for (int page = 1; page <= document.getNumberOfPages(); page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                String pageText = stripper.getText(document);
                
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("source", filePath);
                metadata.put("page_number", page);
                metadata.put("total_pages", document.getNumberOfPages());
                
                // Add page-specific metadata
                PDPage pdPage = document.getPage(page - 1);
                metadata.put("page_width", pdPage.getMediaBox().getWidth());
                metadata.put("page_height", pdPage.getMediaBox().getHeight());
                
                // Extract images from page (similar to PyMuPDF's image extraction)
                List<String> imageNames = extractImageNames(pdPage);
                if (!imageNames.isEmpty()) {
                    metadata.put("images", String.join(",", imageNames));
                    metadata.put("image_count", imageNames.size());
                }
                
                documents.add(Document.from(pageText, Metadata.from(metadata)));
            }
        }
        
        return documents;
    }
    
    private static List<String> extractImageNames(PDPage page) {
        // Implementation to extract image information
        List<String> imageNames = new ArrayList<>();
        try {
            if (page.getResources() != null && page.getResources().getXObjectNames() != null) {
                for (COSName name : page.getResources().getXObjectNames()) {
                    if (page.getResources().getXObject(name) instanceof PDImageXObject) {
                        imageNames.add(name.getName());
                    }
                }
            }
        } catch (IOException e) {
            // Handle error
        }
        return imageNames;
    }
}
