package cn.sd.ai.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.sd.ai.common.AjaxResult;
import cn.sd.ai.entity.SysUser;
import cn.sd.ai.utils.CaptchaUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class LoginController {
    @RequestMapping("/getVerify")
    public void getCaptchaCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CaptchaUtil.createCaptchaCode(request, response);
    }

    @PostMapping(value = "/login")
    @ResponseBody
    public AjaxResult login(@RequestBody @Valid SysUser vo, HttpServletRequest request) {
        //判断验证码
        if (!CaptchaUtil.checkCaptchaCode(request, vo.getCaptcha())) {
            return AjaxResult.error("验证码错误！");
        }
        StpUtil.login("10010");
        return AjaxResult.success();
//        return userService.login(vo);
    }

    @GetMapping("/home-info")
    @ResponseBody
    public AjaxResult getHomeInfo() {
        //通过access_token拿userId
        String userId = StpUtil.getLoginIdAsString();
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("deptName", "数字化部");
        userInfo.put("username", "张三");
        map.put("userInfo", userInfo);

        map.put("menus", ListUtil.empty());

        return AjaxResult.success(map);
    }
}
