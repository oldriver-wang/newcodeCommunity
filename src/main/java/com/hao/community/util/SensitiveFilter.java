package com.hao.community.util;


import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    // 敏感词过滤器

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    private static final String REPLACEMENT = "***";

    // 初始化树 根节点
    private TrieNode rootNode = new TrieNode();
    //  在服务启动就初始化了
    // 当容器实例化这个Bean以后，再调用构造器之后，就会自动调用
    @PostConstruct
    public void init() {
        try(
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                ){
                String keyword;
                while ((keyword = reader.readLine()) != null) {
                    this.addKeyword(keyword);
                }
        }catch (IOException e){
            logger.error("加载敏感词失败:", e.getMessage());
        }
    }

    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode==null) {
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }
            // 指针指向子节点 进入下一次循环
            tempNode = subNode;
            // 设置结束的标识
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * 返回过滤后的文本

     */
    public String filter(String text) {


        StringBuilder stringBuilder = new StringBuilder();
        if (StringUtils.isBlank(text)) {
            return null;
        }
        // 指针1
        TrieNode tempNode = rootNode;
        // 指针2
        int begin = 0;
        // 指针3
        int position = 0;

        while (position < text.length()) {
            char c = text.charAt(position);
            // 跳过符号
            if (isSymbol(c)) {
                // 若指针1处于根节点, 将次符号计入结果，让指针2向下走一步
                if (tempNode == rootNode) {
                    stringBuilder.append(c);
                    begin ++;
                }
                position ++;
                continue;
            }
            // 不是符号
            // 检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                // 以begin为开头的字符串不是敏感词
                stringBuilder.append(text.charAt(begin));
                position = ++begin;
                // 重新指向根节点
                tempNode = rootNode;
            }else if(tempNode.isKeywordEnd()) {
                // 发现敏感词， 将begin-position 字符串替换掉
                stringBuilder.append(REPLACEMENT);
                begin = ++ position;
                tempNode = rootNode;
            }else {
                // 继续检查下一个字符
                position ++;
            }

        }
        // 将最后一批字符记录入结果
        stringBuilder.append(text.substring(begin));

        return stringBuilder.toString();
    }

    // 判断是否为符号
    private boolean isSymbol(Character c) {
        // (c < 0x2E80 || c > 0x9FFF)  东亚文字范围
        return CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }


    // 前缀数
    private class TrieNode {
        // 关键词结束标识
        private boolean isKeywordEnd = false;

        // 子节点  key 是下级字符  value 是下级节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        // 添加子节点
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }
}
