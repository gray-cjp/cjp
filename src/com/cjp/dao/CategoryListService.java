package com.cjp.dao;

import com.cjp.domain.PageBean;
import com.cjp.domain.Product;

import java.sql.SQLException;
import java.util.List;

public class CategoryListService {
    public PageBean findProductListByCid(String categoryCid,int currentPage,int currentCount) {
        ProductDao dao =new ProductDao();
        PageBean<Product> pageBean =new PageBean<Product>();
        pageBean.setCurrentPage(currentPage);
        pageBean.setCurrentCount(currentCount);
        int totalCount = 0;
        try {
            totalCount = dao.totalCount(categoryCid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        pageBean.setTotalCount(totalCount);
        int totalPage = (int) Math.ceil((1.0*totalCount)/currentCount);
        pageBean.setTotalPage(totalPage);
        int index = (currentPage-1)*currentCount;
        List<Product> productList =null;
        try {
            productList = dao.findProductList(categoryCid,index,currentCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        pageBean.setList(productList);
        return pageBean;
    }
}
