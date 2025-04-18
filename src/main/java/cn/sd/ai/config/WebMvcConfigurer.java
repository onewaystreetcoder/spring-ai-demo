package cn.sd.ai.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.io.File;

@Configuration
public class WebMvcConfigurer extends WebMvcConfigurationSupport {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations(
                "classpath:/static/");
        registry.addResourceHandler("/webjars/**").addResourceLocations(
                "classpath:/META-INF/resources/webjars/");
    }

    //添加拦截器
    //web是从管理后台来的接口，不走app那一套校验逻辑
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new SaInterceptor(handler ->
//                SaRouter.match("/**", r -> StpUtil.checkLogin()))
//                .isAnnotation(true))
//                .excludePathPatterns("/doc.html")
//                .excludePathPatterns("/swagger-resources/**")
//                .excludePathPatterns("/webjars/**")
//                .excludePathPatterns("/error")
//                .excludePathPatterns("/static/**")
//                .excludePathPatterns("/files/**")
//                .excludePathPatterns("/login")
//                .excludePathPatterns("/index/login")
//                .excludePathPatterns("/sys/user/login")
//                .excludePathPatterns("/sys/getVerify")
//
//                .excludePathPatterns("/app/api/**")
//                .addPathPatterns("/**");
//    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/login", "/captcha",
                        // 静态资源
                        "/api/**", "/css/**", "/js/**", "/img/**", "/lib/**",
                        "/pageVue/**", "/util/**", "/config.js");
    }

}
