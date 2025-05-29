package com.buguagaoshu.tiktube.utils;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-26
 */
public class MarkdownUtils {
    
    /**
     * 解析Markdown文本并提取其中的所有链接
     * 
     * @param markdown Markdown格式的文本
     * @return 文本中包含的链接列表
     */
    public static List<String> extractLinks(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // 创建解析器
        Parser parser = Parser.builder().build();
        // 解析Markdown文本
        Node document = parser.parse(markdown);
        
        // 创建链接收集器
        LinkCollector linkCollector = new LinkCollector();
        // 遍历文档节点收集链接
        document.accept(linkCollector);
        
        return linkCollector.getLinks();
    }
    
    /**
     * 链接收集器，用于遍历Markdown文档节点并收集链接
     */
    private static class LinkCollector extends AbstractVisitor {
        private final List<String> links = new ArrayList<>();
        
        @Override
        public void visit(Link link) {
            // 收集链接URL
            links.add(link.getDestination());
            // 继续访问子节点
            visitChildren(link);
        }
        
        @Override
        public void visit(Image image) {
            // 也可以收集图片链接
            links.add(image.getDestination());
            // 继续访问子节点
            visitChildren(image);
        }
        
        /**
         * 获取收集到的链接列表
         * 
         * @return 链接列表
         */
        public List<String> getLinks() {
            return links;
        }
    }
}
