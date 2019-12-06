package com.cjp.dao;

import com.cjp.domain.Product;
import com.cjp.utils.DataSourceUtils;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.SQLException;

public class AdminDao {

    public void saveProduct(Product product) throws SQLException {
        QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "insert into product values (?,?,?,?,?,?,?,?,?,?)";
        qr.update(sql,product.getPid(),product.getPname(),product.getMarket_price(),
                product.getShop_price(),product.getPimage(),product.getPdate(),
                product.getIs_hot(),product.getPdesc(),product.getPflag(),product.getCategory().getCid());
    }
}
