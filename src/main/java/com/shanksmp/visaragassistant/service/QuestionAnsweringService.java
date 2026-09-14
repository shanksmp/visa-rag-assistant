package com.shanksmp.visaragassistant.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionAnsweringService {

    @Autowired
    private VectorStore vectorStore;

    private final ChatClient chatClient;

    @Autowired
    public QuestionAnsweringService(@Qualifier("anthropicChatModel") ChatModel anthropicChatModel) {
        this.chatClient = ChatClient.builder(anthropicChatModel).build();
    }

    public String answer(String question) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(question)
                .topK(3)
                .build();
        List<Document> relevantChunks = vectorStore.similaritySearch(searchRequest);

        if (relevantChunks.isEmpty()) {
            return "I don't have any relevant information to answer that question.";
        }

        String context = relevantChunks.stream()
                .map(doc -> "Source: " + doc.getMetadata().get("source") + "\nContent: " + doc.getText())
                .collect(Collectors.joining("\n\n---\n\n"));

        String promptText = """
                You are a helpful assistant answering questions about F-1 student visa OPT/STEM OPT rules.
                Answer ONLY using the context below. If the context doesn't contain the answer, say so clearly.
                Always mention which source document(s) you used.

                Context:
                %s

                Question: %s
                """.formatted(context, question);

        return chatClient.prompt()
                .user(promptText)
                .call()
                .content();
    }
}