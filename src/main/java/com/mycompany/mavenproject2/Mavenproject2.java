/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.mavenproject2;

import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByCharacterSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.List;
import jdk.jfr.Description;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.DocumentParser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author amjad
 */
public class Mavenproject2 {

    public static void main1(OpenAiChatModel model, String[] args) {
        StackTraceElement element = Thread.currentThread().getStackTrace()[1];
        System.out.printf("%n--------------->Class:%s Method:%s%n",element.getClassName(), element.getMethodName());

        String answer = model.chat("qwhat is today?'");
        System.out.println("OpenAiChatModel:" + answer); // Hello World

        interface Assistant {

            String chat(String userMessage);
        }
        Assistant assistant = AiServices.create(Assistant.class, model);

        answer = assistant.chat("Hello");
        System.out.println("Assistant:" + answer); // Hello World

    }

    public static void main2(OpenAiChatModel model, String[] args) {
        StackTraceElement element = Thread.currentThread().getStackTrace()[1];
        System.out.printf("%n--------------->Class:%s Method:%s%n",element.getClassName(), element.getMethodName());

        interface Friend {

            @SystemMessage("You are a good friend of mine. Answer using slang.")
            String chat(String userMessage);
        }

        Friend friend = AiServices.create(Friend.class, model);

        String answer = friend.chat("Hello"); // Hey! What's up?
        System.out.println("Friend:" + answer); // Hello World

        friend = AiServices.builder(Friend.class)
                .chatModel(model)
                .systemMessageProvider(chatMemoryId -> "You are a good friend of mine. Answer using slang.")
                .build();

        answer = friend.chat("Hello"); // Hey! What's up?
        System.out.println("Friend:" + answer); // Hello World
    }

    public static void main2_2(OpenAiChatModel model, String[] args) {
        StackTraceElement element = Thread.currentThread().getStackTrace()[1];
        System.out.printf("%n--------------->Class:%s Method:%s%n",element.getClassName(), element.getMethodName());

        interface Friend {

            @UserMessage("You are a good friend of mine. Answer using slang. {{message}}")
            String chat(String userMessage);
        }

        Friend friend = AiServices.create(Friend.class, model);

        String answer = friend.chat("Hello"); // Hey! What's up?
        System.out.println("Friend:" + answer); // Hello World

        friend = AiServices.builder(Friend.class)
                .chatModel(model)
                .systemMessageProvider(chatMemoryId -> "You are a good friend of mine. Answer using slang.")
                .build();

        answer = friend.chat("Hello"); // Hey! What's up?
        System.out.println("Friend:" + answer); // Hello World
    }

    public static void main2_3(OpenAiChatModel model, String[] args) {
        StackTraceElement element = Thread.currentThread().getStackTrace()[1];
        System.out.printf("%n--------------->Class:%s Method:%s%n",element.getClassName(), element.getMethodName());

        interface Friend {

            @UserMessage("You are a good friend of mine. Answer using slang. {{it}}")
            String chat(String userMessage);
        }
        try {
            Friend friend = AiServices.create(Friend.class, model);

            String answer = friend.chat("Hello"); // Hey! What's up?
            System.out.println("Friend:" + answer); // Hello World

            friend = AiServices.builder(Friend.class)
                    .chatModel(model)
                    .systemMessageProvider(chatMemoryId -> "You are a good friend of mine. Answer using slang.")
                    .build();

            answer = friend.chat("Hello"); // Hey! What's up?
            System.out.println("Friend:" + answer); // Hello World
        } catch (Exception exc) {
            exc.printStackTrace();
        }

    }

    public static void main3(OpenAiChatModel model, String[] args) {
        StackTraceElement element = Thread.currentThread().getStackTrace()[1];
        System.out.printf("%n--------------->Class:%s Method:%s%n",element.getClassName(), element.getMethodName());

        @Description("an address") // you can add an optional description to help an LLM have a better understanding
        class Address {

            String street;
            String streetNumber;
            String city;
        }

        class Person {

            @Description("first name of a person") // you can add an optional description to help an LLM have a better understanding
            String firstName;
            String lastName;
            String birthDate;
            Address address;
        }

        interface PersonExtractor {

            @UserMessage("Extract information about a person from {{it}}")
            Person extractPersonFrom(String text);
        }

        try {
            PersonExtractor personExtractor = AiServices.create(PersonExtractor.class, model);

            String text = """
            In 1968, amidst the fading echoes of Independence Day,
            a child named John arrived under the calm evening sky.
            This newborn, bearing the surname Doe, marked the start of a new journey.
            He was welcomed into the world at 345 Whispering Pines Avenue
            a quaint street nestled in the heart of Springfield
            an abode that echoed with the gentle hum of suburban dreams and aspirations.
            """;

            Person person = personExtractor.extractPersonFrom(text);

            System.out.println(person);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

// Custom document loader with enhanced metadata
    public static List<Document> loadDocumentsWithFullMetadata(String directoryPath, PathMatcher pathMatcher) throws IOException {
        List<Document> documents = new ArrayList<>();

        Files.walk(Paths.get(directoryPath))
                .filter(path -> pathMatcher.matches(path))
                .forEach(path -> {
                    try
                    {
                        // Load document content
                        Document doc = FileSystemDocumentLoader.loadDocument(path.toString(),
                                new ApachePdfBoxDocumentParser());

                        // Get file system metadata
                        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);

                        // Get PDF metadata using PDFBox directly
                        Map<String, Object> enhancedMetadata = new HashMap<>(doc.metadata().toMap());
                        File pdfFile = new File(path.toString());
                        
                        try (PDDocument pdfDoc = PDDocument.load(pdfFile))
                        {
                            PDDocumentInformation info = pdfDoc.getDocumentInformation();

                            // Add PDF metadata - only add if not null/empty
                            addIfNotNull(enhancedMetadata, "title", info.getTitle());
                            addIfNotNull(enhancedMetadata, "author", info.getAuthor());
                            addIfNotNull(enhancedMetadata, "subject", info.getSubject());
                            addIfNotNull(enhancedMetadata, "keywords", info.getKeywords());
                            addIfNotNull(enhancedMetadata, "creator", info.getCreator());
                            addIfNotNull(enhancedMetadata, "producer", info.getProducer());

                            // Convert dates to supported types
                            if (info.getCreationDate() != null) 
                            {
                                Calendar creationDate = info.getCreationDate();
                                enhancedMetadata.put("pdf_creation_date_timestamp", creationDate.getTimeInMillis());
                                enhancedMetadata.put("pdf_creation_date", creationDate.getTime().toString());
                            }

                            if (info.getModificationDate() != null)
                            {
                                Calendar modificationDate = info.getModificationDate();
                                enhancedMetadata.put("pdf_modification_date_timestamp", modificationDate.getTimeInMillis());
                                enhancedMetadata.put("pdf_modification_date", modificationDate.getTime().toString());
                            }

                            // Add file system metadata
                            enhancedMetadata.put("file_size", attrs.size());
                            enhancedMetadata.put("file_creation_time", attrs.creationTime().toString());
                            enhancedMetadata.put("file_last_modified", attrs.lastModifiedTime().toString());
                            enhancedMetadata.put("file_last_accessed", attrs.lastAccessTime().toString());
                            enhancedMetadata.put("page_count", pdfDoc.getNumberOfPages());
                            enhancedMetadata.put("file_name", pdfFile.getName());

                        }

                        // Create new document with enhanced metadata
                        Document enhancedDoc = Document.from(doc.text(), Metadata.from(enhancedMetadata));
                        documents.add(enhancedDoc);

                    } 
                    catch (IOException e) 
                    {
                        System.err.println("Error processing file: " + path + " - " + e.getMessage());
                    }
                });

        return documents;
    }

    private static void addIfNotNull(Map<String, Object> map, String key, String value) {
        if (value != null && !value.trim().isEmpty()) {
            map.put(key, value);
        }
    }

    public static List<Document> loadDocumentsWithFullMetadataV2(String directoryPath, PathMatcher pathMatcher) throws IOException {
        List<Document> documents = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Files.walk(Paths.get(directoryPath))
                .filter(path -> pathMatcher.matches(path))
                .forEach(path -> {
                    try {
                        Document doc = FileSystemDocumentLoader.loadDocument(path.toString(),
                                new ApachePdfBoxDocumentParser());

                        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                        Map<String, Object> enhancedMetadata = new HashMap<>(doc.metadata().toMap());

                        try (PDDocument pdfDoc = PDDocument.load(new File(path.toString()))) {
                            PDDocumentInformation info = pdfDoc.getDocumentInformation();

                            // Add PDF metadata
                            addIfNotNull(enhancedMetadata, "title", info.getTitle());
                            addIfNotNull(enhancedMetadata, "author", info.getAuthor());
                            addIfNotNull(enhancedMetadata, "subject", info.getSubject());
                            addIfNotNull(enhancedMetadata, "keywords", info.getKeywords());
                            addIfNotNull(enhancedMetadata, "creator", info.getCreator());
                            addIfNotNull(enhancedMetadata, "producer", info.getProducer());

                            // Handle dates properly
                            if (info.getCreationDate() != null) {
                                Calendar creationCal = info.getCreationDate();
                                Instant instant = creationCal.toInstant();

                                // Store as timestamp (long) and formatted string
                                enhancedMetadata.put("pdf_creation_timestamp", instant.toEpochMilli());
                                enhancedMetadata.put("pdf_creation_date",
                                        instant.atZone(ZoneId.systemDefault()).format(formatter));
                            }

                            if (info.getModificationDate() != null) {
                                Calendar modCal = info.getModificationDate();
                                Instant instant = modCal.toInstant();

                                enhancedMetadata.put("pdf_modification_timestamp", instant.toEpochMilli());
                                enhancedMetadata.put("pdf_modification_date",
                                        instant.atZone(ZoneId.systemDefault()).format(formatter));
                            }

                            // File system metadata
                            enhancedMetadata.put("file_size", attrs.size());
                            enhancedMetadata.put("file_creation_time", attrs.creationTime().toString());
                            enhancedMetadata.put("file_last_modified", attrs.lastModifiedTime().toString());
                            enhancedMetadata.put("page_count", pdfDoc.getNumberOfPages());
                        }

                        Document enhancedDoc = Document.from(doc.text(), Metadata.from(enhancedMetadata));
                        documents.add(enhancedDoc);

                    } catch (IOException e) {
                        System.err.println("Error processing file: " + path + " - " + e.getMessage());
                    }
                });

        return documents;
    }

    public class EnhancedPdfDocumentParser implements DocumentParser {

        private final ApachePdfBoxDocumentParser baseParser = new ApachePdfBoxDocumentParser();

        @Override
        public Document parse(InputStream inputStream) {
            // This won't have file path info, so use the method above instead
            // or implement with file path parameter
            throw new UnsupportedOperationException("Use parseWithPath method instead");
        }

        public Document parseWithPath(Path filePath) throws IOException {
            // Parse content using base parser
            Document baseDoc = baseParser.parse(Files.newInputStream(filePath));

            // Get enhanced metadata
            Map<String, Object> metadata = extractFullMetadata(filePath);

            return Document.from(baseDoc.text(), Metadata.from(metadata));
        }

        private Map<String, Object> extractFullMetadata(Path filePath) throws IOException {
            Map<String, Object> metadata = new HashMap<>();

            // File system metadata
            BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
            metadata.put("file_name", filePath.getFileName().toString());
            metadata.put("file_path", filePath.toString());
            metadata.put("file_size", attrs.size());
            metadata.put("file_creation_time", attrs.creationTime().toString());
            metadata.put("file_last_modified", attrs.lastModifiedTime().toString());

            // PDF-specific metadata
            try (PDDocument pdfDoc = PDDocument.load(filePath.toFile())) {
                PDDocumentInformation info = pdfDoc.getDocumentInformation();

                addIfNotNull(metadata, "title", info.getTitle());
                addIfNotNull(metadata, "author", info.getAuthor());
                addIfNotNull(metadata, "subject", info.getSubject());
                addIfNotNull(metadata, "keywords", info.getKeywords());
                addIfNotNull(metadata, "creator", info.getCreator());
                addIfNotNull(metadata, "producer", info.getProducer());

                if (info.getCreationDate() != null) {
                    metadata.put("pdf_creation_date", info.getCreationDate().getTime());
                }
                if (info.getModificationDate() != null) {
                    metadata.put("pdf_modification_date", info.getModificationDate().getTime());
                }

                metadata.put("page_count", pdfDoc.getNumberOfPages());
            }

            return metadata;
        }

        private void addIfNotNull(Map<String, Object> map, String key, String value) {
            if (value != null && !value.trim().isEmpty()) {
                map.put(key, value);
            }
        }
    }

    public static void printMetadata(Metadata metadata, String title, String... excludeKeys) {
        
        Set<String> excludeSet = Set.of(excludeKeys);
        
        System.out.printf("=== %s ===%n", title);

        TreeMap<String, Object> sortedMap = new TreeMap<>(metadata.toMap());

        // Find the longest key for alignment
        int maxKeyLength = sortedMap.keySet().stream()
                .mapToInt(String::length)
                .max()
                .orElse(0);

        for (Map.Entry<String, Object> entry : sortedMap.entrySet()) {
            
            if (!excludeSet.contains(entry.getKey())) {            
                System.out.printf("%-" + maxKeyLength + "s : %s%n", entry.getKey(), entry.getValue());
            }
        }
        System.out.println("================");
    }

    public static void main4(OpenAiChatModel model, String[] args) {
        StackTraceElement element = Thread.currentThread().getStackTrace()[1];
        System.out.printf("%n--------------->Class:%s Method:%s%n",element.getClassName(), element.getMethodName());

        try {
            String path = Mavenproject2.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            System.out.println("Running from: " + path);
            File directoryPath = new File("src/main/resources/documents");
            List<Document> documents;
            PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:**.txt");

            // Load documents (auto-detect parser based on file type)
            documents = FileSystemDocumentLoader.loadDocuments(directoryPath.getAbsolutePath(),
                    pathMatcher, new TextDocumentParser());
            for (Document doc : documents) {
                    printMetadata(doc.metadata(), "TEXT", "file_path","absolute_directory_path");
            }

            pathMatcher = FileSystems.getDefault().getPathMatcher("glob:**.pdf");
            // Force Text parser (like TextLoader in Python)
            documents = FileSystemDocumentLoader.loadDocumentsRecursively(directoryPath.getAbsolutePath(),
                    pathMatcher, new ApachePdfBoxDocumentParser());

            for (Document doc : documents) {
                printMetadata(doc.metadata(), "PDF", "file_path","absolute_directory_path");
            }

            documents = loadDocumentsWithFullMetadata(directoryPath.getAbsolutePath(), 
                    FileSystems.getDefault().getPathMatcher("glob:**.pdf"));

            for (Document doc : documents) {
                printMetadata(doc.metadata(), "PDF2", "file_path","absolute_directory_path");
            }

            documents = loadDocumentsWithFullMetadataV2(directoryPath.getAbsolutePath(), 
                    FileSystems.getDefault().getPathMatcher("glob:**.pdf"));

            for (Document doc : documents) {
                printMetadata(doc.metadata(), "PDF3", "file_path","absolute_directory_path");
            }

            Document doc0 = documents.get(0);

            DocumentByCharacterSplitter splitter = new DocumentByCharacterSplitter(200, 20);
            List<TextSegment> chunks = splitter.split(doc0);
            System.out.printf("📄 Created %d chunks%n", chunks.size());
            if (!chunks.isEmpty()) {
                System.out.printf("First chunk:%n%s%n---------%n",
                        chunks.get(0).text().substring(0, Math.min(chunks.get(0).text().length(), 100)));
            }

            //InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
            //EmbeddingStoreIngestor.ingest(documents, embeddingStore);
        } catch (Exception exc) {
            exc.printStackTrace();
        }

    }

    public static void main5(OpenAiChatModel model, String[] args) {
        StackTraceElement element = Thread.currentThread().getStackTrace()[1];
        System.out.printf("%n--------------->Class:%s Method:%s%n",element.getClassName(), element.getMethodName());

        try {

            File directoryPath = new File("src/main/resources/documents");
            File document = new File("src/main/resources/documents/pdf/attention.pdf");

            List<Document> docs = TikaAdvancedPdfLoader.loadWithTika(document.getAbsolutePath());
            for (Document doc : docs) {
                printMetadata(doc.metadata(), "main5-PDF1", "file_path","absolute_directory_path");
            }
// Load entire directory
            docs = TikaAdvancedPdfLoader.loadDirectoryWithTika(directoryPath.getAbsolutePath(),".pdf");
// Print metadata
            for (Document doc : docs) {
                printMetadata(doc.metadata(), "main5-PDF2", "file_path","absolute_directory_path");
            }

        } catch (Exception exc) {
            exc.printStackTrace();
        }

    }

    public static void main6(OpenAiChatModel model, String[] args) {
        StackTraceElement element = Thread.currentThread().getStackTrace()[1];
        System.out.printf("%n--------------->Class:%s Method:%s%n",element.getClassName(), element.getMethodName());

        try 
        {

            File directoryPath = new File("src/main/resources/documents");

// Method 2: Enhanced PDFBox (page-by-page like PyMuPDF)
            List<Document> enhancedDocs = EnhancedPdfBoxLoader.loadWithEnhancedFeatures(directoryPath.getAbsolutePath(), 
                    FileSystems.getDefault().getPathMatcher("glob:**.pdf"));
// Print results
            for (Document doc : enhancedDocs) {
                
                printMetadata(doc.metadata(), "main6-PDF1", "file_path","absolute_directory_path");
                
                // Check string value
                String hasImages = doc.metadata().getString("has_images");
                if ("true".equals(hasImages))
                {
                    System.out.println("Images found: " + doc.metadata().getString("image_objects"));
                }
            }

// Method 3: Your existing enhanced metadata approach
            List<Document> metadataDocs = loadDocumentsWithFullMetadata(directoryPath.getAbsolutePath(), 
                    FileSystems.getDefault().getPathMatcher("glob:**.pdf"));

            // Print results
            for (Document doc : metadataDocs)
            {

                printMetadata(doc.metadata(), "main6-PDF2", "file_path","absolute_directory_path");
                                
                // Check string value
                String hasImages = doc.metadata().getString("has_images");
                if ("true".equals(hasImages)) {
                    System.out.println("Images found: " + doc.metadata().getString("image_objects"));
                }
            }

        } 
        catch (Exception exc) 
        {
            exc.printStackTrace();
        }

    }

    public static void main(String[] args) {
        StackTraceElement element = Thread.currentThread().getStackTrace()[1];
        System.out.printf("%n--------------->Class:%s Method:%s%n",element.getClassName(), element.getMethodName());

        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .apiKey("demo")
                .modelName("gpt-4o-mini")
                .build();
        //main1(model, args);
        //main2(model, args);
        //main3(model, args);
        //main4(model, args);
        //main5(model, args);
        main6(model, args);

    }
}
