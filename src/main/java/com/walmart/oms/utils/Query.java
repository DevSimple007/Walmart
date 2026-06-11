package com.walmart.oms.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Map;

/**
 * 查询参数工具类
 */
public class Query<T> {
    
    /**
     * 当前页码
     */
    private static final String PAGE = "page";
    private static final String PAGE_NUM = "pageNum";
    
    /**
     * 每页数量
     */
    private static final String LIMIT = "limit";
    private static final String PAGE_SIZE = "pageSize";
    
    /**
     * 默认页码
     */
    private static final int DEFAULT_PAGE = 1;
    
    /**
     * 默认每页数量
     */
    private static final int DEFAULT_LIMIT = 10;
    
    /**
     * 构建分页参数
     */
    public IPage<T> getPage(Map<String, Object> params) {
        int page = DEFAULT_PAGE;
        int limit = DEFAULT_LIMIT;
        
        if (params != null) {
            Object pageParam = params.get(PAGE) != null ? params.get(PAGE) : params.get(PAGE_NUM);
            if (pageParam != null) {
                try {
                    page = Integer.parseInt(pageParam.toString());
                } catch (NumberFormatException ignored) {
                }
            }
            Object limitParam = params.get(LIMIT) != null ? params.get(LIMIT) : params.get(PAGE_SIZE);
            if (limitParam != null) {
                try {
                    limit = Integer.parseInt(limitParam.toString());
                } catch (NumberFormatException ignored) {
                }
            }
        }
        
        return new Page<>(page, limit);
    }
}
