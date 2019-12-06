package com.cjp.service.impl;

import com.cjp.dao.AdminDao;
import com.cjp.domain.Category;
import com.cjp.domain.Order;
import com.cjp.domain.Product;
import com.cjp.service.AdminService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AmindServiceImpl implements AdminService {
    @Override
    public List<Category> findAllCategory() {
        return null;
    }

    @Override
    public void saveProduct(Product product) throws SQLException {
        AdminDao dao = new AdminDao();
        dao.saveProduct(product);
    }

    @Override
    public List<Order> findAllOrders() {
        return null;
    }

    @Override
    public List<Map<String, Object>> findOrderInfoByOid(String oid) {
        return null;
    }
}
