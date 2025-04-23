package com.buguagaoshu.tiktube.utils;

import com.buguagaoshu.tiktube.config.MyConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2019-08-13 13:29
 */
@Component
@Slf4j
public class IpUtil {
    private final MyConfigProperties myConfigProperties;

    private Searcher searcher;


    public IpUtil(MyConfigProperties myConfigProperties) {
        this.myConfigProperties = myConfigProperties;
        // 1、从 dbPath 加载整个 xdb 到内存。
        byte[] cBuff = new byte[0];
        try {
            cBuff = Searcher.loadContentFromFile(myConfigProperties.getIpDbPath());
        } catch (Exception e) {
            log.error("不能正常加载IP地址数据库配置 {}: {}\n", myConfigProperties.getIpDbPath(), e);
        }
        try {
            this.searcher = Searcher.newWithBuffer(cBuff);
        } catch (Exception e) {
            log.error("不能创建IP查找缓存，IP地址查找功能失效: {}\n", e);
        }
    }

    public String getIpAddr(HttpServletRequest request) {
        if (this.myConfigProperties.getIsTheProxyConfigured()) {
            return getProxyIpAddr(request);
        }
        return request.getRemoteAddr();
    }

    public String getCity(String ip) {
        try {
            return searcher.search(ip);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取用户登陆IP
     * */
    public String getProxyIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1")) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) {
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress="";
        }
        // ipAddress = this.getRequest().getRemoteAddr();
        return ipAddress;
    }

    public String getUa(HttpServletRequest request) {
        String ua = request.getHeader("user-agent");
        if (!StringUtils.hasLength(ua)) {
            return "未知设备";
        }
        // 避免 UA 过长插入失败
        if(ua.length() > 255) {
            return ua.substring(0, 254);
        }
        return ua;
    }
}
