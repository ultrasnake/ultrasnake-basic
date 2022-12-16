package cn.creedon.ns4j.http.filter;

import cn.creedon.ns4j.http.bean.Ns4jRequest;
import cn.creedon.ns4j.http.bean.Ns4jResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/***
 * *  _ ___ ___.__   __                  _________              __         ©
 * * |_   | |  |\  |_/  |_____________   /   _____/ ____ _____  |  | __ ____
 * *   |  | |  ||  |\   __\_  __ \__  \  \_____  \ /    \\__  \ |  |/ // __ \
 * *   |  |_|  /|  |_|  |  |  | \// __ \_/        \   |  \/ __ \|    <\  ___/
 * *   |______/ |____/__|  |__|  (____  /_______  /___|  (____  /__|_ \\___  >
 * *   UltraSnake - WDC               \/        \/     \/     \/     \/    \/
 * *
 * *   功能描述：Ns4jFilter过滤器
 * *
 * *   @DATE    2022/11/18
 * *   @AUTHOR  WD.C
 ***/
public class FilterChain {

    private static final List<Ns4jFilter> filterList = new ArrayList<>();
    private int currentIndex = 0;

    /**
     * 初始化过滤器
     */
    public FilterChain() {
        filterList.add(new Ns4jRequestFilter());
    }

    /**
     * 添加过滤器
     *
     * @param ns4jFilter
     */
    public void addFilter(Ns4jFilter ns4jFilter) {
        filterList.add(ns4jFilter);
        filterList.sort(Comparator.comparingInt(Ns4jFilter::getOrder));
    }

    /**
     * 添加过滤器
     *
     */
    public void addFilter(Class<?> clazz) {
        try {
            filterList.add((Ns4jFilter) clazz.newInstance());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        filterList.sort(Comparator.comparingInt(Ns4jFilter::getOrder));
    }

    /**
     * 执行过滤动作
     * 说明：
     * 1、保证每次请求以第一个过滤器执行
     * 2、获取下一个过滤器并执行过滤请求
     *
     * @param request
     * @param response
     */
    public boolean doFilter(Ns4jRequest request, Ns4jResponse response) {
        this.currentIndex = 0;
        return processFilter(request, response);
    }

    private boolean processFilter(Ns4jRequest request, Ns4jResponse response) {
        Ns4jFilter ns4jFilter = filterList.get(this.currentIndex);
        boolean filterResult = ns4jFilter.doFilter(request, response);
        if (filterResult && this.currentIndex < filterList.size() - 1) {
            this.currentIndex = this.currentIndex + 1;
            return processFilter(request, response);
        } else {
            return filterResult && this.currentIndex == filterList.size() - 1;
        }
    }

}
