package com.cjp.service;

import com.cjp.dao.ProductDao;
import com.cjp.domain.Order;
import com.cjp.domain.Product;
import com.cjp.utils.DataSourceUtils;

import java.sql.SQLException;
import java.util.List;

public class ProductService {
    ProductDao dao =new ProductDao();
    public List<Product> findHotProductList() {
        List<Product> HotProductList =null;
        try {
            HotProductList = dao.findHotProductList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return HotProductList;
    }

    public List<Product> findNewProductList() {
        List<Product>NewProductList = null;
        try {
            NewProductList = dao.findNewProductList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return NewProductList;
    }

    public Product findProductInfo(String pid) {
        Product productListInfo=null;
        try {
            productListInfo = dao.findProductInfo(pid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productListInfo;
    }
    //将订单项和订单数据存到数据库中，该操作需要开启事务
    public void submitOrder(Order order) {
        try {
            DataSourceUtils.startTransaction();
            dao.submitOrder(order);
            dao.submitOrderItem(order);

        } catch (SQLException e) {
            try {
                DataSourceUtils.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        finally {
            try {
                DataSourceUtils.commitAndRelease();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void upDateOrders(Order order) {
        try {
            dao.upDateOrders(order);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void upDateOrdersState(String r6_order) {
        try {
            dao.upDateOrdersState(r6_order);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Object> searchWord(String word) {
        List<Object> productName =null;
        try {
            productName = dao.searchWord(word);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productName;
    }
}
