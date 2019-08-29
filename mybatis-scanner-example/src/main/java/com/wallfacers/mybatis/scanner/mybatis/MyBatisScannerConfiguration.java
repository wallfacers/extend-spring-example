package com.wallfacers.mybatis.scanner.mybatis;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * MapperScannerConfigurer填充外部配置，不基于PropertyResourceConfigurer的解决方式
 *
 * @author wallfacers
 */
@Configuration
@PropertySource("classpath:mybatis/mybatis.properties")
public class MyBatisScannerConfiguration extends MapperScannerConfigurer {

    @Override
    public void afterPropertiesSet() throws Exception {
        super.setProcessPropertyPlaceHolders(true);
        super.setSqlSessionFactoryBeanName("sqlSessionFactoryBean");
        super.setBasePackage("${base-package}");
    }
}
