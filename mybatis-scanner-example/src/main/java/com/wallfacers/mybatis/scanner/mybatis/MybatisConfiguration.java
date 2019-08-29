package com.wallfacers.mybatis.scanner.mybatis;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * 配置SqlSessionFactory和MapperScannerConfigurer
 *
 * @author wallfacers
 */
@Configuration
// @PropertySource("classpath:mybatis/mybatis.properties")
public class MybatisConfiguration {

    /*@Value("${base-package}")
    private String backPackage;*/

    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();

        sqlSessionFactoryBean.setDataSource(dataSource);
        // 别名包路径
        sqlSessionFactoryBean.setTypeAliasesPackage("com.wallfacers.common.api.domain");

        PathMatchingResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver();
        try {
            // 获取映射文件
            Resource[] resources =
                    resourceLoader.getResources("classpath:com/wallfacers/mybatis/scanner/repository/*.xml");
            sqlSessionFactoryBean.setMapperLocations(resources);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return sqlSessionFactoryBean;
    }

    /*@Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {

        // null
        System.out.println(this.backPackage);

        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();

        // SqlSessionFactoryBean中的sqlSessionFactory是在InitializingBean#afterPropertiesSet初始化
        // 在getBean("sqlSessionFactoryBean") 其实得到的是SqlSessionFactory因为其为FactoryBean，可查看getObject方法
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("O");
        // 扫描的包路径，用于创建MapperFactoryBean类型的BeanDefinition
        mapperScannerConfigurer.setBasePackage("com.wallfacers.mybatis.scanner.repsitory");

        return mapperScannerConfigurer;
    }*/
}
