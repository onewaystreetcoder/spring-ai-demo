package cn.sd.ai.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class WeatherService {
	@Tool(description = "获取中国城市天气预报。 输入 城市名称 ")
	public String getChineseAlerts(String city) {
		List<String> list = new ArrayList<>();
		list.add("气温范围：15°C~25°C，早晚温差大，偶有雷雨\n 穿搭建议：白天：轻便长袖T恤/衬衫 + 牛仔裤/休闲裤\n 早晚：薄外套（如夹克、针织开衫）或薄风衣\n 其他：防晒霜、遮阳帽必备，随身携带折叠伞");
		list.add("气温范围：9°C~21°C，气候多变，昼夜温差显著，偶有雷雨\n 穿搭建议：基础搭配：长袖T恤/薄毛衣 + 直筒裤/阔腿裤\n 外套选择：西装外套、短风衣或绒面夹克\n 其他：建议叠穿方便增减，备雨伞和防晒霜");
		list.add("气温范围：5°C~25°C，以晴天为主，偶有阴雨\n 穿搭建议：晴天：短袖衬衫/长袖T恤 + 薄外套（如牛仔外套）\n 阴雨天：厚卫衣/毛衣 + 大衣，怕冷可搭配薄羽绒服\n 其他：运动鞋优先，雨天注意防风保暖");
		list.add("气温范围：18°C~25°C，紫外线强，昼夜温差\n 穿搭建议：短袖/薄长袖 + 防晒衣，搭配运动鞋/休闲鞋");
		list.add("气温范围：15°C~20°C（白天），夜间可降至5°C\n 穿搭建议：长袖T恤/薄卫衣 + 防风外套（如软壳衣），备雨具");
		return city + list.get(new Random().nextInt(list.size()));
	}

	public static void main(String[] args) {

	}

}