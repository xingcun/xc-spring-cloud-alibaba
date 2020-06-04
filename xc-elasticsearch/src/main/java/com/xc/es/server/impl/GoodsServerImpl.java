package com.xc.es.server.impl;


import com.xc.es.pojo.Goods;
import com.xc.es.repository.GoodsRepository;
import com.xc.es.server.GoodsServer;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GoodsServerImpl implements GoodsServer {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public Goods findById(String id) {
        Optional<Goods> goods = goodsRepository.findById(id);
        return goods.isPresent()?goods.get():null;
    }

    @Override
    public Goods save(Goods goods) {

        return goodsRepository.save(goods);
    }

    @Override
    public void delete(String id) {
        goodsRepository.deleteById(id);
    }

    @Override
    public List<Goods> findAll() {
/**
 * must 多条件 &（并且）
 * mustNot 多条件 != (非)
 * should 多条件 || (或)
 */
        QueryBuilder query = QueryBuilders.boolQuery()

              //  .must(QueryBuilders.termQuery("id","2019040499"))  //精确查询
//                .must(QueryBuilders.termQuery("name","好"))    //模糊查询
                .must(QueryBuilders.matchQuery("desc","软件产品"))    //模糊查询

//                .must(QueryBuilders.wildcardQuery("tags","*好"))
//                .must(QueryBuilders.rangeQuery("date")
//                        .from("2020-01-06 16:52:17")
//                        .to("2020-01-09 16:52:17"))
                ;  //范围查询
        List<String> properties = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC,"createDate");//排序

        //多条件排序
    /*Sort.Order order1 = new Sort.Order(Sort.Direction.DESC,"userId");
    Sort.Order order2 = new Sort.Order(Sort.Direction.ASC,"userId");
    Sort sortList = Sort.by(order1,order2);*/

        Pageable pageable = PageRequest.of(0, 10, sort);
        Page<Goods> userPage = goodsRepository.search(query, pageable);
        List<Goods> pageContent= userPage.getContent();

        return pageContent;
    }

    @Override
    public Page<Goods> findByName(String name, PageRequest pageRequest) {
        return goodsRepository.findByName(name,pageRequest);
    }

    @Override
    public Page<Goods> findByDesc(String desc, PageRequest pageRequest) {
        return goodsRepository.findByDesc(desc,pageRequest);
    }
}
