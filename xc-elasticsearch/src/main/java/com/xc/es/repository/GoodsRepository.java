package com.xc.es.repository;

import com.xc.es.pojo.Goods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoodsRepository extends ElasticsearchRepository<Goods, String> {
    Optional<Goods> findById(String id);

    Page<Goods> findByName(String name, Pageable pageable);

    Page<Goods> findByDesc(String desc, Pageable pageable);

}
