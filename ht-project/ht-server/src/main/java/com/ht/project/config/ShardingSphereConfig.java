package com.ht.project.config;

import io.seata.rm.datasource.DataSourceProxy;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.shardingsphere.api.config.sharding.KeyGeneratorConfiguration;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.api.sharding.ShardingAlgorithm;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@tk.mybatis.spring.annotation.MapperScan(basePackages = ShardingSphereConfig.PACKAGE, sqlSessionFactoryRef = "shardingSphereSqlSessionFactory")
public class ShardingSphereConfig {

    static final String PACKAGE = "com.ht.project.mapper.sharding";
    static final String MAPPER_LOCATION = "classpath:mapper/sharding/*.xml";


    @Autowired
    @Qualifier("firstDataSource")
    private DataSource firstDataSource;

    @Autowired
    @Qualifier("secondDataSource")
    private DataSource secondDataSource;

    @Bean("shardingSphereDataSource")
    public DataSource getShardingDataSource() throws SQLException {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(getOrderTableRuleConfiguration());
        //shardingRuleConfig.getTableRuleConfigs().add(getOrderItemTableRuleConfiguration());
        shardingRuleConfig.getBindingTableGroups().add("base_user");
        shardingRuleConfig.getBroadcastTables().add("t_config");
        shardingRuleConfig.setDefaultDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration("source", "ds${source }"));
        shardingRuleConfig.setDefaultTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("source", new ModuloShardingTableAlgorithm()));
        return ShardingDataSourceFactory.createDataSource(createDataSourceMap(), shardingRuleConfig, new Properties());
    }

    private static KeyGeneratorConfiguration getKeyGeneratorConfiguration() {
        KeyGeneratorConfiguration result = new KeyGeneratorConfiguration("SNOWFLAKE", "id");
        return result;
    }

    TableRuleConfiguration getOrderTableRuleConfiguration() {
        TableRuleConfiguration result = new TableRuleConfiguration("base_user", "ds${1..2}.base_user");
        result.setKeyGeneratorConfig(getKeyGeneratorConfiguration());
        return result;
    }

    TableRuleConfiguration getOrderItemTableRuleConfiguration() {
        TableRuleConfiguration result = new TableRuleConfiguration("t_order_item", "ds${1..2}.t_order_item${0..1}");
        return result;
    }

   public Map<String, DataSource> createDataSourceMap() {
        Map<String, DataSource> result = new HashMap<>();
        result.put("ds1", firstDataSource);
        result.put("ds2", secondDataSource);
        return result;
    }


    @Bean(name = "shardingSphereDataSourceProxy")
    public DataSourceProxy shardingSphereDataSourceProxy(@Qualifier("shardingSphereDataSource") DataSource dataSource) {
        return new DataSourceProxy(dataSource);
    }

    @Bean(name = "shardingSphereTransactionManager")
    public DataSourceTransactionManager shardingSphereTransactionManager(@Qualifier("shardingSphereDataSourceProxy") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "shardingSphereSqlSessionFactory")
    public SqlSessionFactory shardingSphereSqlSessionFactory(@Qualifier("shardingSphereDataSourceProxy") DataSource dataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(ShardingSphereConfig.MAPPER_LOCATION));
        sessionFactory.setTypeHandlersPackage("com.ht.project.typehandler");
        return sessionFactory.getObject();
    }


}
