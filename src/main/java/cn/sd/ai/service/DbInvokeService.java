package cn.sd.ai.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.StopWatch;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DbInvokeService {

    public static final Logger logger = LoggerFactory.getLogger(DbInvokeService.class);
    @Resource
    private DataSource dataSource;

    @Resource
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> getSqlResult(String sql) {
        StopWatch stopWatch = new StopWatch();
        Map<String, Object> result = new HashMap<>();
        result.put("sql", sql);
        result.put("code", "200");
        result.put("invokeTime", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
        result.put("message", "success");
        stopWatch.start();
        List<Map<String, Object>> maps = new ArrayList<>();
        try {
//            jdbcTemplate = new JdbcTemplate(dataSource);
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

    @Tool(description = "Execute SQL to return query results")
    public String getSqlResultTool(String sql) {

        Map<String, Object> sqlResult = getSqlResult(sql);
        String s = JSONUtil.toJsonStr(sqlResult);
        return s;

    }


}
