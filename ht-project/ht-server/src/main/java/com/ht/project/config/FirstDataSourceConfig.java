package com.ht.project.config;

import com.alibaba.druid.pool.DruidDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;


@Configuration
@tk.mybatis.spring.annotation.MapperScan(basePackages = FirstDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "firstSqlSessionFactory")
public class FirstDataSourceConfig {

    static final String PACKAGE = "com.ht.project.mapper.first";
    static final String MAPPER_LOCATION = "classpath:mapper/first/*.xml";

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driverClassName}")
    private String driverClass;

    @Value("${spring.datasource.type}")
    private String type;

    @Bean(name = "firstDataSource")
    @Primary
    public DataSource firstDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setDbType(type);
        dataSource.setPassword(password);
        return dataSource;
    }
    @Bean(name = "firstDataSourceProxy")
    public DataSourceProxy firstDataSourceProxy(@Qualifier("firstDataSource") DataSource dataSource) {
        return new DataSourceProxy(dataSource);
    }

    @Bean(name = "firstTransactionManager")
    @Primary
    public DataSourceTransactionManager firstTransactionManager() {
        return new DataSourceTransactionManager(firstDataSource());
    }

    @Bean(name = "firstSqlSessionFactory")
    @Primary
    public SqlSessionFactory firstSqlSessionFactory(@Qualifier("firstDataSourceProxy") DataSourceProxy firstDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(firstDataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(FirstDataSourceConfig.MAPPER_LOCATION));
        sessionFactory.setTypeHandlersPackage("com.ht.project.typehandler");
        return sessionFactory.getObject();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
