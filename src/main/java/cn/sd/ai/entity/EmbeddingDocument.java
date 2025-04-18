package cn.sd.ai.entity;

public class EmbeddingDocument {
    private String question;
    private String schemas;

    public EmbeddingDocument() {
    }

    public EmbeddingDocument(String question, String schemas) {
        this.question = question;
        this.schemas = schemas;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getSchemas() {
        return schemas;
    }

    public void setSchemas(String schemas) {
        this.schemas = schemas;
    }
}
