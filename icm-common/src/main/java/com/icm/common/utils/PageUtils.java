package com.icm.common.utils;

import com.github.pagehelper.PageHelper;
import com.icm.common.core.page.PageDomain;
import com.icm.common.core.page.TableSupport;
import com.icm.common.utils.sql.SqlUtil;

/**
 * 分页工具类
 * 
 * @author ruoyi
 */
public class PageUtils extends PageHelper
{
    /**
     * 设置请求分页数据
     *
     * 面试踩坑点：PageHelper.startPage(pageNum, pageSize, orderBy) 这个三参数重载，
     * 拿到的 orderBy 字符串只会被简单拼接到最终 SQL 末尾（" order by " + orderBy），
     * 它不会去解析/替换 Mapper.xml 里原本就写死的 ORDER BY。如果某条 SQL 本身已经带了
     * ORDER BY（典型场景：查 ClickHouse 时为了保证 MergeTree 引擎返回顺序手写死 ORDER BY），
     * 前端一旦点了可排序表头（带上 orderByColumn/isAsc 参数），这里就会拼出两个 ORDER BY，
     * 语法直接报错。真实案例见 UserBehaviorTrackMapper.xml（selectUserBehaviorTrackList）
     * 和 UserBehaviorTrackServiceImpl#demoPageHelperOrderByBug 的复现代码。
     */
    public static void startPage()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
        Boolean reasonable = pageDomain.getReasonable();
        PageHelper.startPage(pageNum, pageSize, orderBy).setReasonable(reasonable);
    }

    /**
     * 清理分页的线程变量
     */
    public static void clearPage()
    {
        PageHelper.clearPage();
    }
}
