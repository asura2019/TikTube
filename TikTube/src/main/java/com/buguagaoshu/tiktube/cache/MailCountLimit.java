package com.buguagaoshu.tiktube.cache;

import com.sun.source.tree.ReturnTree;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-05
 */
@Component
public class MailCountLimit {
    private final ConcurrentMap<String, Long> mailCountMap = new ConcurrentHashMap<String, Long>();

    public void setMailCount(String ip, long count) {
        mailCountMap.compute(ip, (k, v) -> v == null ? count : v + count);
    }

    public Long getCount(String ip) {
       return mailCountMap.getOrDefault(ip, 0L);
    }

    public void clear() {
        this.mailCountMap.clear();
    }
}
