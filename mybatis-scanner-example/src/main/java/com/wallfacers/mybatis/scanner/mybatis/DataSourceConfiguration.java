package com.wallfacers.mybatis.scanner.mybatis;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

/**
 * 数据源配置
 *
 * @author wallfacers
 */
@Configuration
@PropertySource("classpath:mybatis/jdbc.properties")
public class DataSourceConfiguration {

    @Value("${datasource.url}")
    private String url;

    @Value("${datasource.username}")
    private String username;

    @Value("${datasource.password}")
    private String password;

    @Value("${datasource.driver}")
    private String driver;

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource  = new HikariDataSource();
        dataSource.setJdbcUrl(this.url);
        dataSource.setUsername(this.username);
        dataSource.setPassword(this.password);
        dataSource.setDriverClassName(this.driver);
        return dataSource;
    }

}
