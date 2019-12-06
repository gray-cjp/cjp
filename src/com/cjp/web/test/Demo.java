package com.cjp.web.test;

import com.cjp.domain.Customer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class Demo {
    public void demo(){
        Configuration configuration = new Configuration().configure();
        SessionFactory factory = configuration.buildSessionFactory();
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();
        Customer customer = new Customer();
        customer.setCust_name("aa");
        session.save(customer);
        transaction.commit();
        session.close();
        factory.close();

    }
}
