package com.clothrent.config;

import com.clothrent.config.intercepors.AdminLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfigurer implements WebMvcConfigurer {

    @Autowired
    private AdminLoginInterceptor adminLoginInterceptor;


    @Value("${com.jane.file.baseFilePath}")
    private String baseFilePath;


    //跨域配置
    @Bean
    public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").
                        allowedOrigins("*"). //允许跨域的域名，可以用*表示允许任何域名使用
                        allowedMethods("*"). //允许任何方法（post、get等）
                        allowedHeaders("*"). //允许任何请求头
                        allowCredentials(true). //带上cookie信息
                        exposedHeaders(HttpHeaders.SET_COOKIE).maxAge(3600L);
                //maxAge(3600)表明在3600秒内，不需要再发送预检验请求，可以缓存该结果
            }
        };
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/swagger-ui/").setViewName("forward:/swagger-ui/index.html");
    }

    // 这个方法是用来配置静态资源的，比如html，js，css，等等
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/file/**").addResourceLocations("file:"+baseFilePath);
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
                .resourceChain(false);
        registry.addResourceHandler("/layuiadmin/**").addResourceLocations("classpath:/static/layuiadmin/");
//        针对swagger2.x
//        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    // 这个方法用来注册拦截器，我们自己写好的拦截器需要通过这里添加注册才能生效
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //后台管理系统拦截器
        registry.addInterceptor(adminLoginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                "/admin/login"    //后台管理系统登录页面不拦截
                ,"/login"    //后台管理系统登录页面不拦截
                ,"/user/checkLogin"    //用户登录
                ,"/teacher/checkLogin"    //用户登录
                , "/admin/logOut" //退出
                , "/admin/checkLogin" //登录检测
                , "/static/**" //静态资源
                , "/swagger-ui/**" //接口文档
                , "/swagger-resources/**" //接口文档
                , "/api/**" //接口文档
                , "/v3/**" //接口文档
                , "/common/**" //通用
                , "/static/**" //通用
                , "/app/**" //客户端请求
                , "/home/**" //客户端请求
                , "/" //客户端请求
                , "/wx/**" //小程序端请求
                , "/file/**" //文件请求
                , "/ueditor/**" //文富文本编辑器的请求
                , "/images/**" //图片请求
        );
    }

}
