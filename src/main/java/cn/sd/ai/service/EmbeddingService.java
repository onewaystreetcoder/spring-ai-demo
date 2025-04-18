package cn.sd.ai.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.sd.ai.entity.EmbeddingDocument;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EmbeddingService {
    private final OllamaEmbeddingModel embeddingModel;
    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;

    public EmbeddingService(OllamaEmbeddingModel embeddingModel, VectorStore vectorStore, JdbcTemplate jdbcTemplate) {
        this.embeddingModel = embeddingModel;
        this.vectorStore = vectorStore;
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<EmbeddingDocument> search(EmbeddingDocument reqDoc) {
        String sql = "select * from schemas_embedding where 1 = 1 ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (StrUtil.isNotBlank(reqDoc.getQuestion())) {
            sql += "and question = :question";
            params.addValue("question", reqDoc.getQuestion());
        }
        if (StrUtil.isNotBlank(reqDoc.getSchemas())) {
            sql += "and table_schemas = :schemas";
            params.addValue("schemas", reqDoc.getSchemas());
        }
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<EmbeddingDocument> embeddingDocuments = namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            EmbeddingDocument ed = new EmbeddingDocument();
            ed.setId(rs.getString("id"));
            ed.setQuestion(rs.getString("question"));
            ed.setSchemas(rs.getString("table_schemas"));
            return ed;
        });
        return embeddingDocuments;
    }

    public void addDocument(EmbeddingDocument reqDoc) {
        String uuid = IdUtil.fastUUID();
        reqDoc.setId(uuid);
        String sql = "insert into schemas_embedding (id, question, table_schemas) values (:id, :question, :table_schemas)";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", uuid);
        params.addValue("question", reqDoc.getQuestion());
        params.addValue("table_schemas", reqDoc.getSchemas());
        int update = namedParameterJdbcTemplate.update(sql, params);
        if (update > 0) {
            Document doc = new Document(reqDoc.getQuestion(), Map.of(
                    "docId", uuid,
                    "schemas", reqDoc.getSchemas()
            ));
            vectorStore.add(List.of(doc));
        }
    }

    public void editDocument(EmbeddingDocument reqDoc) {
        String sql = "update schemas_embedding set question = :question, table_schemas = :table_schemas where id = :id";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", reqDoc.getId());
        params.addValue("question", reqDoc.getQuestion());
        params.addValue("table_schemas", reqDoc.getSchemas());
        int update = namedParameterJdbcTemplate.update(sql, params);
        if (update > 0) {
            Document doc = new Document(reqDoc.getQuestion(), Map.of(
                    "docId", reqDoc.getId(),
                    "schemas", reqDoc.getSchemas()
            ));
            vectorStore.delete(List.of(reqDoc.getId()));
            vectorStore.add(List.of(doc));
        }
    }

    public void deleteDocument(EmbeddingDocument reqDoc) {
        String sql = "delete from schemas_embedding where id = :id";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", reqDoc.getId());
        int update = namedParameterJdbcTemplate.update(sql, params);
        if (update > 0) {
            vectorStore.delete(List.of(reqDoc.getId()));
        }
    }

    public List<Document> searchDocument(String query) {
        SearchRequest request = SearchRequest.builder().query(query).topK(1).build();
        List<Document> documents = vectorStore.similaritySearch(request);
//        List<Document> documents = vectorStore.similaritySearch(query);
        return documents;
    }
}
