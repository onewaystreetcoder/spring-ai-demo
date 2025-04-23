package cn.sd.ai.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.sd.ai.common.AjaxResult;
import cn.sd.ai.entity.EmbeddingDocument;
import cn.sd.ai.service.EmbeddingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EmbeddingController {
    public static final Logger logger = LoggerFactory.getLogger(EmbeddingController.class);
    private final EmbeddingService embeddedService;
    public EmbeddingController(EmbeddingService embeddedService) {
        this.embeddedService = embeddedService;
    }


    @PostMapping("/embedding")
    @SaCheckPermission("embedding:list")
    public AjaxResult embedding(@RequestBody EmbeddingDocument reqDoc) {
        try {
            List<EmbeddingDocument> documents =embeddedService.search(reqDoc);
            return AjaxResult.success(documents);
        } catch (Exception e) {
            logger.error("list embedding error", e);
            return AjaxResult.error();
        }
    }

    @PostMapping("/embedding-add")
    @SaCheckPermission("embedding:add")
    public AjaxResult add(@RequestBody EmbeddingDocument reqDoc) {
        try {

            embeddedService.addDocument(reqDoc);
            return AjaxResult.success();
        } catch (Exception e) {
            logger.error("add embedding error", e);
            return AjaxResult.error();
        }
    }

    @PostMapping("/embedding-edit")
    @SaCheckPermission("embedding:edit")
    public AjaxResult edit(@RequestBody EmbeddingDocument reqDoc) {
        try {
            embeddedService.editDocument(reqDoc);
            return AjaxResult.success();
        } catch (Exception e) {
            logger.error("edit embedding error", e);
            return AjaxResult.error();
        }
    }

    @PostMapping("/embedding-del")
    @SaCheckPermission("embedding:delete")
    public AjaxResult delete(@RequestBody EmbeddingDocument reqDoc) {
        try {
            embeddedService.deleteDocument(reqDoc);
            return AjaxResult.success();
        } catch (Exception e) {
            logger.error("delete embedding error", e);
            return AjaxResult.error();
        }
    }
}
