package com.cjp.service;

import com.cjp.domain.Category;
import com.cjp.domain.Order;
import com.cjp.domain.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface AdminService {
    public List<Category> findAllCategory();

    public void saveProduct(Product product) throws SQLException;

    public List<Order> findAllOrders();

    public List<Map<String, Object>> findOrderInfoByOid(String oid);
}
