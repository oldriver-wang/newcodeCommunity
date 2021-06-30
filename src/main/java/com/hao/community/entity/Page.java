package com.hao.community.entity;
/*
    封装分页的相关的信息
 */
public class Page {
    // 当前页码
    public int current = 1;
    // 显示上限
    public int limit = 10;
    // 数据总数(用于计算总页数)
    public int rows;
    // 查询路径(用于复用分页链接)
    public String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1)
            this.current = current;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    // 获取当前页的起始行
    public int getOffset() {
        // current*limit - limit
        return (current-1)*limit;
    }

    // 获取总页数
    public int getTotal() {
        // rows / limit [+1]
        if(rows % limit == 0) {
            return rows / limit;
        }
        else {
            return rows / limit + 1;
        }
    }
    // 算一下起始页和截至页是多少
    // 获取起始页码
    public int getFrom() {
        return current - 2 < 1 ? 1 : current - 2;
    }
    //获取截止页码
    public int getTo() {
        int total = getTotal();
        return current + 2 > total ? total : current + 2;
    }
}
