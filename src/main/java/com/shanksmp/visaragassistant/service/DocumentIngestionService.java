package com.shanksmp.visaragassistant.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentIngestionService {

    @Autowired
    private VectorStore vectorStore;

    public int ingestPdf(String filePath, String sourceName) {
        // Step 1: Read the PDF, extracting text page by page
        PagePdfDocumentReader reader = new PagePdfDocumentReader(new FileSystemResource(filePath));
        List<Document> pages = reader.get();

        // Step 2: Split each page into smaller chunks (default ~800 tokens per chunk, with overlap)
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> chunks = splitter.apply(pages);

        // Step 3: Tag each chunk with its source filename, so we can cite it later
        chunks.forEach(chunk -> chunk.getMetadata().put("source", sourceName));

        // Step 4: Embed + store each chunk in pgvector (Spring AI handles the embedding call internally)
        vectorStore.add(chunks);

        return chunks.size();
    }
}