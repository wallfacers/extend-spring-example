### 分析MyBatis-Spring中的MapperScannerConfigurer读取不到外部化配置的原因

#### 一、MapperScannerConfigurer的作用

主要作用是在Spring与MyBatis整合时，作为一个桥接的作用，对包路径的扫描，扫描包下的接口(标注了注解@Component的接口，或其派生注解)，读取完成后会被转换成为MapperFactoryBean类型的BeanDefinition。

其实MapperScans(或MapperScan)也能达到同样的目的



#### 二、简单的MapperScannerConfigurer案例

##### 1、配置SqlSessionFactoryBean和MapperScannerConfigurer

一般情况下我们在MyBatis与Spring整合时可能会有如下配置，也即是配置SqlSessionFacotryBean和MapperScannerConfigurer。SqlSessionFacotryBean用于对SqlSessionFacotry的配置和创建（映射文件的添加，别名设置等），MapperScannerConfigurer用于对Dao接口扫描

```java
/**
 * 配置SqlSessionFactory和MapperScannerConfigurer
 *
 * @author wallfacers
 */
@Configuration
public class MybatisConfiguration {

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
                    resourceLoader.getResources("classpath:com/wallfacers/mybatis/scanner/repsitory/*.xml");
            sqlSessionFactoryBean.setMapperLocations(resources);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return sqlSessionFactoryBean;
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {

        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();

        // SqlSessionFactoryBean中的sqlSessionFactory是在InitializingBean#afterPropertiesSet初始化
        // 在getBean("sqlSessionFactoryBean") 其实得到的是SqlSessionFactory因为其为FactoryBean，可查看getObject方法
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryBean");
        // 扫描的包路径，用于创建MapperFactoryBean类型的BeanDefinition
        mapperScannerConfigurer.setBasePackage("com.wallfacers.mybatis.scanner.repsitory");

        return mapperScannerConfigurer;
    }

}
```

> 提示：数据源相关的配置就不贴出来了，可以查看DataSourceConfiguration文件



如果上面的配置类改为如下结构，也即是通过@Value占位符的方式注入backPackage，从代码中可以看出在创建MapperScannerConfigurer阶段backPackage根本没有被注入

````java
/**
 * 配置SqlSessionFactory和MapperScannerConfigurer
 *
 * @author wallfacers
 */
@Configuration
@PropertySource("classpath:mybatis/mybatis.properties")
public class MybatisConfiguration {

    @Value("${base-package}")
    private String backPackage;

    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) {
        .....
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        // null
        System.out.println(this.backPackage);
        .....
    }

}
````

> mybatis.properties配置文件中的内容base-package=com.wallfacers.mybatis.scanner.repsitory



#### 三、分析创建MapperScannerConfigurer阶段时外部配置没有被注入原因

##### 1、@Value的标注的属性被注入阶段

默认情况下，@Value标注的属性值被注入的阶段是在populateBean阶段（Bean创建过程中会注入属性），使用的解析类是一个匿名的StringValueResolver，在AbstractApplicationContext的finishBeanFactoryInitialization阶段被添加到ConfigurableListableBeanFactory中。

> 这里根本不是使用PropertySourcesPlaceholderConfigurer作为占位符解析的，除非在上下文配置文件中显示的配置了`<contenx:property-placeholder/>` 才会创建PropertySourcesPlaceholderConfigurer类型的Bean，这个Bean是BeanFactoryPostProcessor类型的Bean，其postProcessBeanFactory阶段也会将一个匿名的StringValueResolver添加到ConfigurationListtableBeanFactory



##### 2、MapperScannerConfigurer被创建时期

MapperScannerConfigurer是一个BeanDefinitionRegistryPostProcessor实现类，其postProcessBeanDefinitionRegistry在AbstractApplicationContext的invokeBeanFactoryPostProcessors阶段。

> postProcessBeanDefinitionRegistry的执行时机要比postProcessBeanFactory早一点



##### 3、外部配置没有被注入的原因

在AbstractApplicationContext#refresh方法中，invokeBeanFactoryPostProcessors的执行要比finishBeanFactoryInitialization早很多，finishBeanFactoryInitialization方法的执行已经接近于上下文刷新的尾声了，这个阶段是对剩余的Bean进行创建，创建过程中在populateBean阶段发现属性是字符串，就尝试解析占位符的内容进行注入。

不管使用什么方式创建的StringValueResolver解析器，其被创建的时机都晚于MapperScannerConfigurer创建，从这可以看出占位符解析的工具都没有被创建，那么创建的过程中就别想着属性被注入了。



#### 四、如何解决MapperScannerConfigurer不能注入外部配置

##### 1、基于PropertyResourceConfigurer的解决方式

````xml
<?xml version="1.0" encoding="UTF-8" ?>
<beans ....>

    <!-- 创建数据源 -->
    <bean id="dataSource" class="com.zaxxer.hikari.HikariDataSource">
        <property name="jdbcUrl" value="${datasource.url}"/>
        <property name="username" value="${datasource.username}"/>
        <property name="password" value="${datasource.password}"/>
        <property name="driverClassName" value="${datasource.driver}"/>
    </bean>

    <bean id="sqlSessionFactoryBean" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource" />
        <property name="typeAliasesPackage" value="com.wallfacers.common.api.domain" />
        <property name="mapperLocations" value="classpath*:com/wallfacers/mybatis/scanner/repository/*.xml" />
    </bean>

    <!-- 创建 PropertySourcesPlaceholderConfigurer -->
    <bean id="propertySourcesPlaceholderConfigurer" class="org.springframework.context.support.PropertySourcesPlaceholderConfigurer">
        <property name="locations">
            <array>
                <value>classpath:mybatis/mybatis.properties</value>
                <value>classpath:mybatis/jdbc.properties</value>
            </array>
        </property>
    </bean>

    <!-- 创建MapperScannerConfigurer -->
    <bean id="mapperScannerConfigurer" class="org.mybatis.spring.mapper.MapperScannerConfigurer" >
        <property name="basePackage" value="${base-package}"/>
        <property name="processPropertyPlaceHolders" value="true" />
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactoryBean"/>
    </bean>

</beans>
````

> 如果使用PropertyResourceConfigurer方式来解决该问题，那么MapperScannerConfigurer必须使用XML方式去定义这个Bean，这是因为PropertyResourceConfigurer对属性的注入是针对于AbstractBeanDefinition的propertyValues属性，如果通过@Component方式去创建的话，那么propertyValues必然是null。那么根本达不到属性注入的目的



##### 2、不基于PropertyResourceConfigurer的解决方式

````java
/**
 * MapperScannerConfigurer注入外部配置，不基于PropertyResourceConfigurer的解决方式
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
````

> 这种方式必须使用mybatis-spring:2.0.2版本



