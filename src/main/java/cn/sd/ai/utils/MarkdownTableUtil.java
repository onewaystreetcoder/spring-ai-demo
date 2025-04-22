package cn.sd.ai.utils;

import java.util.*;
import java.util.stream.Collectors;

public class MarkdownTableUtil {

    /**
     * 将 List<Map<String, Object>> 数据转换为 Markdown 表格格式
     * @param maps 数据源
     * @return Markdown 格式的表格字符串，如果输入为空则返回空字符串
     */
    public static String convertToMarkdown(List<Map<String, Object>> maps) {
        if (maps == null || maps.isEmpty()) return "";

        // 使用 LinkedHashSet 维护表头的插入顺序
        LinkedHashSet<String> headers = new LinkedHashSet<>();
        for (Map<String, Object> map : maps) {
            headers.addAll(map.keySet());
        }

        // 处理空数据的情况
        if (headers.isEmpty()) return "";

        // 构建表头和分隔线
        StringBuilder sb = new StringBuilder();
        String headerLine = headers.stream()
                .map(MarkdownTableUtil::escapeCell)
                .collect(Collectors.joining("|", "|", "|\n"));
        String separatorLine = headers.stream()
                .map(h -> ":-:")
                .collect(Collectors.joining("|", "|", "|\n"));

        sb.append(headerLine).append(separatorLine);

        // 构建数据行
        for (Map<String, Object> row : maps) {
            String dataLine = headers.stream()
                    .map(header -> row.getOrDefault(header, ""))
                    .map(value -> escapeCell(String.valueOf(value)))
                    .collect(Collectors.joining("|", "|", "|\n"));
            sb.append(dataLine);
        }

        return sb.toString();
    }

    /**
     * 转义 Markdown 表格中的特殊字符
     * @param content 单元格内容
     * @return 转义后的安全内容
     */
    private static String escapeCell(String content) {
        return content.replace("|", "&#124;")  // 转义竖线
                .replace("\n", " ")     // 替换换行为空格
                .replace("\r", "")      // 移除回车
                .trim();                // 去除首尾空格
    }

    // 示例用法
    public static void main(String[] args) {
        List<Map<String, Object>> data = new ArrayList<>();

        Map<String, Object> row1 = new LinkedHashMap<>();
        row1.put("Name", "Alice");
        row1.put("Age", 30);
        row1.put("City", "New\nYork");
        data.add(row1);

        Map<String, Object> row2 = new LinkedHashMap<>();
        row2.put("Name", "Bob");
        row2.put("Age", 25);
        row2.put("City", "San | Francisco");
        data.add(row2);

        System.out.println(convertToMarkdown(data));
    }
}
