package com.ht.project.config;

import com.alibaba.druid.pool.DruidDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
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

    @Bean(name = "firstDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.first")
    public DataSource firstDataSource() {

        return new DruidDataSource();
    }
    @Bean(name = "firstDataSourceProxy")
    public DataSourceProxy firstDataSourceProxy(@Qualifier("firstDataSource") DataSource dataSource) {
        return new DataSourceProxy(dataSource);
    }

    @Bean(name = "firstTransactionManager")
    @Primary
    public DataSourceTransactionManager firstTransactionManager(@Qualifier("firstDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
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


}
