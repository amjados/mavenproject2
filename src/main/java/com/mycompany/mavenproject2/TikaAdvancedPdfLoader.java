/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject2;



import org.apache.tika.Tika;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class TikaAdvancedPdfLoader {
    
    public static List<Document> loadDirectoryWithTika(String directoryPath, String pathEndsWith) throws Exception {
        List<Document> allDocuments = new ArrayList<>();
        
        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            paths.filter(Files::isRegularFile)
                 .filter(path -> path.toString().toLowerCase().endsWith(pathEndsWith))
                 .forEach(path -> {
                     try {
                         allDocuments.addAll(loadWithTika(path.toString()));
                     } catch (Exception e) {
                         System.err.println("Error processing: " + path + " - " + e.getMessage());
                     }
                 });
        }
        
        return allDocuments;
    }
    
    public static List<Document> loadWithTika(String filePath) throws Exception {
        List<Document> documents = new ArrayList<>();
        
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist: " + filePath);
        }
        
        Tika tika = new Tika();
        
        // Extract content
        String content = tika.parseToString(file);
        
        // Extract metadata
        org.apache.tika.metadata.Metadata tikaMetadata = new org.apache.tika.metadata.Metadata();
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler(-1);
        ParseContext parseContext = new ParseContext();
        
        try (FileInputStream inputStream = new FileInputStream(file)) {
            parser.parse(inputStream, handler, tikaMetadata, parseContext);
        }
        
        // Convert metadata
        Map<String, Object> langchainMetadata = convertTikaMetadata(tikaMetadata, filePath);
        
        // Create document
        documents.add(Document.from(content, Metadata.from(langchainMetadata)));
        return documents;
    }
    
    private static Map<String, Object> convertTikaMetadata(org.apache.tika.metadata.Metadata tikaMetadata, String filePath) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", filePath);
        
        // Extract specific fields safely
        for (String name : tikaMetadata.names()) {
            String value = tikaMetadata.get(name);
            if (value != null && !value.trim().isEmpty()) {
                String cleanKey = name.toLowerCase()
                    .replace(":", "_")
                    .replace("-", "_")
                    .replace(" ", "_");
                metadata.put(cleanKey, value);
            }
        }
        
        return metadata;
    }
}