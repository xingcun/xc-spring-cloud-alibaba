package com.xc.es.api;

import com.xc.es.pojo.Goods;
import com.xc.es.server.GoodsServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("goods")
public class GoodsController {

    @Autowired
    private GoodsServer goodsServer;

    @RequestMapping("/add")
    @ResponseBody
    public Goods addMovie(@RequestBody Goods goods) {
        Goods savedGoods = goodsServer.save(goods);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path(
                "/{id}").buildAndExpand(savedGoods.getId()).toUri();
        System.out.println(location);
        return savedGoods;
    }


    @RequestMapping("/delete")
    @ResponseBody
    public ResponseEntity<String> deleteMovie(String id) {
        goodsServer.delete(id);
        return ResponseEntity.ok("Deleted");
    }


    @RequestMapping("/find")
    @ResponseBody
    public List<Goods> findMovieByName(Goods goods) {
        return goodsServer.findAll();
    }


}
