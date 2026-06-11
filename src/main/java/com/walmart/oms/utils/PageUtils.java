package com.walmart.oms.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.io.Serializable;
import java.util.List;

/**
 * 分页工具类
 */
public class PageUtils implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 当前页码
     */
    private long pageNum;
    
    /**
     * 每页数量
     */
    private long pageSize;
    
    /**
     * 总记录数
     */
    private long total;
    
    /**
     * 总页数
     */
    private long pages;
    
    /**
     * 数据列表
     */
    private List<?> list;
    
    public PageUtils() {
    }
    
    public PageUtils(IPage<?> page) {
        this.pageNum = page.getCurrent();
        this.pageSize = page.getSize();
        this.total = page.getTotal();
        this.pages = page.getPages();
        this.list = page.getRecords();
    }
    
    public long getPageNum() {
        return pageNum;
    }
    
    public void setPageNum(long pageNum) {
        this.pageNum = pageNum;
    }
    
    public long getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }
    
    public long getTotal() {
        return total;
    }
    
    public void setTotal(long total) {
        this.total = total;
    }
    
    public long getPages() {
        return pages;
    }
    
    public void setPages(long pages) {
        this.pages = pages;
    }
    
    public List<?> getList() {
        return list;
    }
    
    public void setList(List<?> list) {
        this.list = list;
    }

    /**
     * 兼容前端常用字段名 records（与 list 同义）
     */
    public List<?> getRecords() {
        return list;
    }

    public void setRecords(List<?> records) {
        this.list = records;
    }
}
