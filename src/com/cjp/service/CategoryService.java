package com.cjp.service;

import com.cjp.dao.CategoryDao;
import com.cjp.domain.Category;

import java.sql.SQLException;
import java.util.List;

public class CategoryService {
    public List<Category> findCategoryList() {
        CategoryDao dao =new CategoryDao();
        List<Category> categoryList= null;
        try {
            categoryList =dao.findCategoryList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoryList;
    }
}
