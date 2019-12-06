package com.cjp.dao;

import com.cjp.domain.Category;
import com.cjp.utils.DataSourceUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.List;

public class CategoryDao {
    public List<Category> findCategoryList() throws SQLException {
        QueryRunner qr =new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from category";
        List<Category> categoryList = qr.query(sql,new BeanListHandler<Category>(Category.class));
        return categoryList;
    }
}
