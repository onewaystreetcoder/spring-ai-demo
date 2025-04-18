package cn.sd.ai.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.IdUtil;
import cn.sd.ai.common.AjaxResult;
import cn.sd.ai.entity.EmbeddingDocument;
import cn.sd.ai.service.EmbeddingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class EmbeddingController {
    public static final Logger logger = LoggerFactory.getLogger(EmbeddingController.class);
    private final EmbeddingService embeddedService;
    public EmbeddingController(EmbeddingService embeddedService) {
        this.embeddedService = embeddedService;
    }

    @PostMapping("/embedding-add")
    @SaCheckPermission("embedding:add")
    public AjaxResult add(EmbeddingDocument reqDoc) {
        Map<String, Object> result = new HashMap<>();
        try {
            String uuid = IdUtil.fastUUID();
            Document doc = new Document(reqDoc.getQuestion(), Map.of(
                    "docId", uuid,
                    "schemas", reqDoc.getSchemas()
            ));
            embeddedService.addDocument(doc);
            return AjaxResult.success();
        } catch (Exception e) {
            logger.error("add embedding error", e);
            return AjaxResult.error();
        }
    }
}
