/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject2;

//import dev.langchain4j.data.document.Document;
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import technology.tabula.RectangularTextContainer;
//import technology.tabula.Table;
//import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;
//import technology.tabula.extractors.BasicExtractionAlgorithm;

// Add Tabula dependency for table extraction
// <dependency>
//     <groupId>technology.tabula</groupId>
//     <artifactId>tabula-java</artifactId>
//     <version>1.0.5</version>
// </dependency>

public class PdfTableExtractor {
    
//    public static List<Document> extractWithTables(String filePath) throws IOException {
//        List<Document> documents = new ArrayList<>();
//        
//        try (PDDocument document = PDDocument.load(new File(filePath))) {
//            SpreadsheetExtractionAlgorithm extractor = new SpreadsheetExtractionAlgorithm();
//            
//            for (int i = 0; i < document.getNumberOfPages(); i++) {
//                Page page = new ObjectExtractor(document).extract(i + 1);
//                List<Table> tables = extractor.extract(page);
//                
//                Map<String, Object> metadata = new HashMap<>();
//                metadata.put("source", filePath);
//                metadata.put("page_number", i + 1);
//                metadata.put("table_count", tables.size());
//                
//                StringBuilder pageContent = new StringBuilder();
//                
//                // Extract regular text
//                PDFTextStripper stripper = new PDFTextStripper();
//                stripper.setStartPage(i + 1);
//                stripper.setEndPage(i + 1);
//                pageContent.append(stripper.getText(document));
//                
//                // Add table data
//                if (!tables.isEmpty()) {
//                    pageContent.append("\n\n=== TABLES ===\n");
//                    for (int tableIdx = 0; tableIdx < tables.size(); tableIdx++) {
//                        Table table = tables.get(tableIdx);
//                        pageContent.append("Table ").append(tableIdx + 1).append(":\n");
//                        for (List<RectangularTextContainer> row : table.getRows()) {
//                            pageContent.append(row.stream()
//                                .map(RectangularTextContainer::getText)
//                                .collect(Collectors.joining("\t")))
//                                .append("\n");
//                        }
//                    }
//                }
//                
//                documents.add(Document.from(pageContent.toString(), Metadata.from(metadata)));
//            }
//        }
//        
//        return documents;
//    }
}
