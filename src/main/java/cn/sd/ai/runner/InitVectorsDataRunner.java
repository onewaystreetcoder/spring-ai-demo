package cn.sd.ai.runner;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.sd.ai.entity.EmbeddingDocument;
import cn.sd.ai.service.EmbeddingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;

@Component
public class InitVectorsDataRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitVectorsDataRunner.class);
    @Value("${initVectorsData:false}")
    private String init = "";

    private final EmbeddingService embeddingService;
    public InitVectorsDataRunner(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }
    @Override
    public void run(String... args) throws Exception {
        if (!"true".equals(init)) {
            return;
        }
        try {
            URL resource = ResourceUtil.getResource("test_table.jsonl");
            if (null != resource) {
                List<String> strings = FileUtil.readLines(resource.getPath(), "utf-8");
                for (String string : strings) {
                    JSONObject entries = JSONUtil.parseObj(string);
                    EmbeddingDocument embeddingDocument = new EmbeddingDocument();
                    embeddingDocument.setQuestion(entries.getStr("input"));
                    embeddingDocument.setSchemas(entries.getStr("instruction"));
                    embeddingService.addDocument(embeddingDocument);
                }
            }

        } catch (Exception e) {
            logger.error("初始化数据失败", e);
        }
    }
}
