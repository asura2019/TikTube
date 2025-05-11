package com.buguagaoshu.tiktube.config;

import com.buguagaoshu.tiktube.cache.CategoryCache;
import com.buguagaoshu.tiktube.cache.WebSettingCache;
import com.buguagaoshu.tiktube.entity.OSSConfigEntity;
import com.buguagaoshu.tiktube.repository.impl.FileRepositoryInOSS;
import com.buguagaoshu.tiktube.service.CategoryService;
import com.buguagaoshu.tiktube.service.MailService;
import com.buguagaoshu.tiktube.service.WebConfigService;
import com.buguagaoshu.tiktube.service.WebSettingService;
import com.buguagaoshu.tiktube.service.impl.OssConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;


import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-05 17:51
 */
@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {


    private final LoginInterceptor loginInterceptor;

    private final AdminInterceptor adminInterceptor;

    private final VipInterceptor vipInterceptor;

    /**
     * 默认文件保存位置 0 为 本地
     * 其它数字依照数据库中 OSS 配置文件判断
     * */
    public static int FILE_SAVE_LOCATION = 0;


    @Autowired
    public WebConfig(LoginInterceptor loginInterceptor,
                     AdminInterceptor adminInterceptor,
                     VipInterceptor vipInterceptor) {
        this.loginInterceptor = loginInterceptor;
        this.adminInterceptor = adminInterceptor;
        this.vipInterceptor = vipInterceptor;
    }

    @Bean
    public CommandLineRunner dataLoader(WebSettingCache webSettingCache,
                                        WebConfigService webConfigService,
                                        CategoryService categoryService,
                                        CategoryCache categoryCache,
                                        MailService mailService) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                log.info("开始缓存分类信息");
                categoryCache.setCategoryEntities(categoryService.listWithTree());
                categoryCache.setCategoryEntityMap(categoryService.categoryEntityMap());
                categoryCache.setCategoryMapWithChildren(categoryService.categoryEntityMapWithChildren(categoryCache.getCategoryEntities()));
                log.info("分类信息缓存完成！");
                log.info("开始获取最新的 Web 设置");
                WebConfigData webConfigData = webConfigService.initConfig();
                webSettingCache.update(webConfigData);
                log.info("设置获取完成！");
                // 如果开启了邮箱服务，初始化邮件信息
                if (webConfigData.getOpenEmail().equals(1)) {
                    mailService.initMainConfig();
                }
            }
        };
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

//        registry.addInterceptor(loginInterceptor)
//                .addPathPatterns("/api/article/video",
//                        "/api/user/update/**",
//                        "/api/login/log/list")
//                .excludePathPatterns("/api/login", "/api/register", "/api/verifyImage");
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                    "/api/version",
                    "/api/login",
                    "/api/login/totp",
                    "/api/register",
                    "/api/user/info/**",
                    "/api/user/list/search",
                    "/api/verifyImage",
                    "/api/verify/send",
                    "/api/user/forgot/password",
                    "/api/web/info",
                    "/api/web/notice",
                    "/api/web/notice/**",
                    "/api/article/**", 
                    "/api/category/**",
                    "/api/danmaku/**",
                    "/api/upload/file/**",
                    "/api/upload/**/oss/**",
                    "/api/comment/**"
                );

        registry.addInterceptor(adminInterceptor)
                .addPathPatterns(
                        "/api/admin/**"
                );


//        registry.addInterceptor(vipInterceptor)
//                .addPathPatterns(
//                        // 发布视频接口
//                        "/api/article/video",
//                        // 更新视频接口
//                        "/api/article/video/update",
//                        // 删除视频接口
//                        "/api/studio/article/delete",
//                        // 用户状态更新接口
//                        "/api/upload/video",
//                        "/api/upload/photo"
//                );
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer(){
        return factory -> {
            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/index.html");
            factory.addErrorPages(error404Page);
        };
    }
}
