package cn.sd.ai.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.sd.ai.utils.MarkdownTableUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DbInvokeService {

    public static final Logger logger = LoggerFactory.getLogger(DbInvokeService.class);

    @Resource
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> executeSql(String sql) {
        StopWatch stopWatch = new StopWatch();
        Map<String, Object> result = new HashMap<>();
        result.put("sql", sql);
        result.put("code", "200");
        result.put("invokeTime", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
        result.put("message", "success");
        stopWatch.start();
        List<Map<String, Object>> maps = new ArrayList<>();
        if (StrUtil.isBlank(sql)) {
            result.put("message", "sql can't null");
            result.put("code", "500");
            return result;
        }
        try {
            maps = jdbcTemplate.queryForList(sql);
            result.put("costTime", stopWatch.getTotalTimeMillis() + "ms");
            result.put("data", maps);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("costTime", stopWatch.getTotalTimeMillis() + "ms");
            result.put("message", "error");
            result.put("code", "500");
            result.put("errorMssage", e.getMessage());
        }
        return result;
    }

    @Tool(description = "Execute SQL to return query results", returnDirect = true)
    public String getSqlResult(@NotNull String sql) {
        logger.info("sql:{}", sql);
        Map<String, Object> sqlResult = executeSql(sql);
        if (!"500".equals(sqlResult.get("code"))) {
            String markdown = MarkdownTableUtil.convertToMarkdown((List<Map<String, Object>>) sqlResult.get("data"));
//            logger.info("markdown:\n{}", markdown);
            String markdownSql = "```sql\n" + sql + "\n``` \n";
            return markdownSql + markdown;
        }
        String s = JSONUtil.toJsonStr(sqlResult);
        return s;

    }


}
