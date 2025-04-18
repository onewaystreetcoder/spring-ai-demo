package cn.sd.ai.entity;

public class EmbeddingDocument {
    private static final long serialVersionUID = 1L;
    private String id;
    private String question;
    private String schemas;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
