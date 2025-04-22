package cn.sd.ai.controller;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    private static final boolean ANONYMOUS_ACCESS = false;
    @GetMapping({ "/login", "/"})
    public String loginIndex() {
        if (ANONYMOUS_ACCESS || StpUtil.isLogin()) {
            return "home";
        }
        return "login";
    }

    @GetMapping({"home"})
    public String home() {
        if (ANONYMOUS_ACCESS || StpUtil.isLogin()) {
            return "home";
        }
        return "login";
    }

    @GetMapping("/main")
    public String indexHome() {
        if (ANONYMOUS_ACCESS || StpUtil.isLogin()) {
            return "main";
        }
        return "login";
    }

    @GetMapping("/chat")
    public String chat() {
        if (ANONYMOUS_ACCESS || StpUtil.isLogin()) {
            return "chat";
        }
        return "login";
    }

    @GetMapping("/chat_tools")
    public String chatTools() {
        if (ANONYMOUS_ACCESS || StpUtil.isLogin()) {
            return "chat_tools";
        }
        return "login";
    }

    @GetMapping("/embedding")
    public String embedding() {
        if (ANONYMOUS_ACCESS || StpUtil.isLogin()) {
            return "embedding";
        }
        return "login";
    }

}
