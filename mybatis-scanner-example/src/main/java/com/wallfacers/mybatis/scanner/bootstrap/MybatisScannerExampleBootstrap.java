package com.wallfacers.mybatis.scanner.bootstrap;

import com.wallfacers.mybatis.scanner.service.CustomerService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * MapperScannerConfigurer的简单案例
 *
 * @author wallfacers
 */
public class MybatisScannerExampleBootstrap {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.wallfacers.mybatis.scanner");

        CustomerService customerDao = context.getBean("defaultCustomerService", CustomerService.class);

        System.out.println(customerDao.getCustomerByCustomerId(1));
        context.close();
    }
}
