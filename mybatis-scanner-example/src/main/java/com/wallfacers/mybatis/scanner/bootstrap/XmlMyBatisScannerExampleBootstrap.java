package com.wallfacers.mybatis.scanner.bootstrap;

import com.wallfacers.mybatis.scanner.repository.CustomerDao;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 基于XML的方式去配置MapperScannerConfigurer
 *
 * @author wallfacers
 */
public class XmlMyBatisScannerExampleBootstrap {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/context.xml");
        CustomerDao customerDao = context.getBean("customerDao", CustomerDao.class);
        System.out.println(customerDao.getCustomerByCustomerId(1));
    }

}
