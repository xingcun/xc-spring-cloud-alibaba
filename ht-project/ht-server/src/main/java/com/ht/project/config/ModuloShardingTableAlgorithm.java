package com.ht.project.config;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

public class ModuloShardingTableAlgorithm implements PreciseShardingAlgorithm<Long> {

    @Override
    public String doSharding(Collection dbCollection, PreciseShardingValue preciseShardingValue) {
        for (Object each : dbCollection) {
           // if (each.toString().endsWith(Long.valueOf(preciseShardingValue.getValue().toString()) % 2 + "")) {
                return each.toString();
           // }
        }
        throw new UnsupportedOperationException();

    }
}
