package com.xc.es.server;

import com.xc.es.pojo.Goods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface GoodsServer {

    Goods findById(String id);

    Goods save(Goods goods);

    void delete(String id);


    List<Goods> findAll();

    Page<Goods> findByName(String author, PageRequest pageRequest);

    Page<Goods> findByDesc(String title, PageRequest pageRequest);


}
