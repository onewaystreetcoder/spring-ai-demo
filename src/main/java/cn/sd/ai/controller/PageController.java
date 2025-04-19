package cn.sd.ai.controller;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping({ "/login", "/"})
    public String loginIndex() {
        if (StpUtil.isLogin()) {
            return "home";
        }
        return "login";
    }

    @GetMapping({"home"})
    public String home() {
        if (StpUtil.isLogin()) {
            return "home";
        }
        return "login";
    }

    @GetMapping("/main")
    public String indexHome() {
        if (StpUtil.isLogin()) {
            return "main";
        }
        return "login";
    }

    @GetMapping("/chat")
    public String chat() {
        if (StpUtil.isLogin()) {
            return "chat";
        }
        return "login";
    }

    @GetMapping("/chat_tools")
    public String chatTools() {
        if (StpUtil.isLogin()) {
            return "chat_tools";
        }
        return "login";
    }

    @GetMapping("/embedding")
    public String embedding() {
        if (StpUtil.isLogin()) {
            return "embedding";
        }
        return "login";
    }

}
