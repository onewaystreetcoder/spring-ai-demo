package cn.sd.ai.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbeddingService {
    private final OllamaEmbeddingModel embeddingModel;
    private final VectorStore vectorStore;

    public EmbeddingService(OllamaEmbeddingModel embeddingModel, VectorStore vectorStore) {
        this.embeddingModel = embeddingModel;
        this.vectorStore = vectorStore;
    }

    public void addDocument(Document document) {
        vectorStore.add(List.of(document));
    }
}
