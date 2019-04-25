package com.xc.gate;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
/**
 * @author Eric Zhao
 */
@Configuration
public class GatewayConfiguration {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public XcGateExceptionHandler sentinelGatewayBlockExceptionHandler() {
        // Register the block exception handler for Spring Cloud Gateway.
        return new XcGateExceptionHandler();
    }

    @Bean
    @Order(-1)
    public GlobalFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }

    @PostConstruct
    public void doInit() {
        initCustomizedApis();
        initGatewayRules();
    }

    private void initCustomizedApis() {
        Set<ApiDefinition> definitions = new HashSet<>();
        ApiDefinition api1 = new ApiDefinition("some_customized_api")
            .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                add(new ApiPathPredicateItem().setPattern("/ahas"));
                add(new ApiPathPredicateItem().setPattern("/product/**")
                    .setMatchStrategy(SentinelGatewayConstants.PARAM_MATCH_STRATEGY_PREFIX));
            }});
        ApiDefinition api2 = new ApiDefinition("another_customized_api")
            .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                add(new ApiPathPredicateItem().setPattern("/**")
                    .setMatchStrategy(SentinelGatewayConstants.PARAM_MATCH_STRATEGY_PREFIX));
            }});
        definitions.add(api1);
        definitions.add(api2);
//        GatewayApiDefinitionManager.loadApiDefinitions(definitions);
    }

    private void initGatewayRules() {
        Set<GatewayFlowRule> rules = new HashSet<>();
        /*
        rules.add(new GatewayFlowRule("aliyun_route")
            .setCount(10)
            .setIntervalSec(1)
        );
        */
        /*
        rules.add(new GatewayFlowRule("aliyun_route")
            .setCount(1)
            .setIntervalSec(2)
            .setBurst(2)
            .setParamItem(new GatewayParamFlowItem()
                .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_CLIENT_IP)
            )
        );
         */
        GatewayRuleManager.loadRules(rules);
    }
    @Value("${xc.sentinel.groupId}")
    private String groupId="DEFAULT_GROUP";
    
    @Value("${xc.sentinel.serverAddr}")
    private String serverAddr;
    
    @Value("${xc.sentinel.dataId}")
    private String dataId;
    
    @Bean
	public ReadableDataSource<String, Set<GatewayFlowRule>> getReadableDataSource() {
		ReadableDataSource<String, Set<GatewayFlowRule>> flowRuleDataSource = new NacosDataSource<>(serverAddr,
				groupId, 
				dataId,//"gate-sentinel-json",
				source -> JSON.parseObject(source, new TypeReference<Set<GatewayFlowRule>>() {
				}));
		GatewayRuleManager.register2Property(flowRuleDataSource.getProperty());
		return flowRuleDataSource;
	}
    
}