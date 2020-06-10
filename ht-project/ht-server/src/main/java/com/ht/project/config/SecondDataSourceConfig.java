package com.ht.project.config;

import com.alibaba.druid.pool.DruidDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;


@Configuration
@tk.mybatis.spring.annotation.MapperScan(basePackages = SecondDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "secondSqlSessionFactory")
public class SecondDataSourceConfig {

    static final String PACKAGE = "com.ht.project.mapper.second";
    static final String MAPPER_LOCATION = "classpath:mapper/second/*.xml";


    @Bean(name = "secondDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.second")
    public DataSource secondDataSource() {
        return new DruidDataSource();
    }
    @Bean(name = "secondDataSourceProxy")
    public DataSourceProxy secondDataSourceProxy(@Qualifier("secondDataSource") DataSource dataSource) {
        return new DataSourceProxy(dataSource);
    }

    @Bean(name = "secondTransactionManager")
    public DataSourceTransactionManager secondTransactionManager(@Qualifier("secondDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "secondSqlSessionFactory")
    public SqlSessionFactory secondSqlSessionFactory(@Qualifier("secondDataSourceProxy") DataSourceProxy secondDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(secondDataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(SecondDataSourceConfig.MAPPER_LOCATION));
        sessionFactory.setTypeHandlersPackage("com.ht.project.typehandler");
        return sessionFactory.getObject();
    }


}
