package com.buguagaoshu.tiktube.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring上下文工具类
 * 用于获取Spring上下文和Bean
 * @author Pu Zhiwei
 * @create 2023-05-14
 */
@Component
public class SpringContextUtils implements ApplicationContextAware {
    
    private static ApplicationContext applicationContext;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtils.applicationContext = applicationContext;
    }
    
    /**
     * 获取ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    
    /**
     * 通过name获取Bean
     */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }
    
    /**
     * 通过class获取Bean
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }
    
    /**
     * 通过name,以及Clazz返回指定的Bean
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }
    
    /**
     * 判断Bean是否存在
     */
    public static boolean containsBean(String name) {
        return getApplicationContext().containsBean(name);
    }
}