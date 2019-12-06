package com.cjp.dao;

import com.cjp.domain.Order;
import com.cjp.domain.OrderItem;
import com.cjp.domain.Product;
import com.cjp.utils.DataSourceUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProductDao {
    public List<Product> findHotProductList() throws SQLException {
        QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from product where is_hot=? limit ?,?";
        List<Product> HotproductList = qr.query(sql,new BeanListHandler<Product>(Product.class),1,0,9);
        return HotproductList;
    }

    public List<Product> findNewProductList() throws SQLException {
        QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from product order by pdate limit ?,?";
        List<Product> NewproductList = qr.query(sql,new BeanListHandler<Product>(Product.class),0,9);
        return NewproductList;
    }

    public int totalCount(String categoryCid) throws SQLException {
        QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select count(*) from product where cid=?";
        Long row = (Long) qr.query(sql,new ScalarHandler(),categoryCid);
        return row.intValue();
    }

    public List<Product> findProductList(String categoryCid, int index, int currentCount) throws SQLException {
        QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from product where cid =? limit ?,?";
        List<Product> productList = qr.query(sql,new BeanListHandler<Product>(Product.class),categoryCid,index,currentCount);
        return productList;
    }

    public Product findProductInfo(String pid) throws SQLException {
        QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from product where pid=?";
        Product productListInfo =qr.query(sql,new BeanHandler<Product>(Product.class),pid);
        return productListInfo;
    }
//订单数据存储
    public void submitOrder(Order order) throws SQLException {
        QueryRunner qr = new QueryRunner();
        Connection con = DataSourceUtils.getConnection();
        String sql ="insert into orders values (?,?,?,?,?,?,?,?)";
        qr.update(con,sql,order.getOid(),order.getOrdertiem(),order.getTotal(),order.getState(),
                order.getAddress(),order.getName(),order.getTelepone(),order.getUser().getUid());
    }
//订单项数据存储
    public void submitOrderItem(Order order) throws SQLException {
        QueryRunner qr = new QueryRunner();
        Connection con = DataSourceUtils.getConnection();
        String sql = "insert into orderitem values (?,?,?,?,?)";
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem : orderItems){
            qr.update(con,sql,orderItem.getItemid(),orderItem.getCount(),orderItem.getSubtotal(),orderItem.getProduct().getPid(),
                    orderItem.getOrder().getOid());
        }
    }

    public void upDateOrders(Order order) throws SQLException {
        QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
        String sql ="update orders set address=?,name=?,telephone=? where oid=?";
        qr.update(sql,order.getAddress(),order.getName(),order.getTelepone(),order.getOid());
    }

    public void upDateOrdersState(String r6_order) throws SQLException {
        QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
        String sql ="update orders set state=? where oid=?";
        qr.update(sql,1,r6_order);
    }

    public List<Object> searchWord(String word) throws SQLException {
        QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
        String sql ="select * from product where pname like ? limit ?,?";
        List<Object> productName =qr.query(sql,new ColumnListHandler("pname"),"%"+word+"%",0,7);
        return productName;
    }
}
