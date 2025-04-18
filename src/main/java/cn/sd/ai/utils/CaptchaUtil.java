package cn.sd.ai.utils;

import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.extra.servlet.JakartaServletUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;

public class CaptchaUtil {
    public static void createCaptchaCode(HttpServletRequest request, HttpServletResponse response){
        CircleCaptcha circleCaptcha = cn.hutool.captcha.CaptchaUtil.createCircleCaptcha(130, 48, 4, 4);
        System.out.println(circleCaptcha.getCode());
        request.getSession().setAttribute("captcha", circleCaptcha.getCode());
        JakartaServletUtil.write(response, new ByteArrayInputStream(circleCaptcha.getImageBytes()));
    }

    public static boolean checkCaptchaCode(HttpServletRequest request, String captchaCode){
        String sessionCaptchaCode = (String) request.getSession().getAttribute("captcha");
        request.getSession().removeAttribute("captcha");
        return sessionCaptchaCode.equalsIgnoreCase(captchaCode);
    }
}
