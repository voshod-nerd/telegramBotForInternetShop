package com.voshodnerd.repository;

import com.voshodnerd.model.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class InitDbService {

    @Autowired
    GoodsRepository rep;

    @PostConstruct
    public void init() {
       List<Goods> ls= rep.findAllGoods();
       ls.forEach(System.out::println);

    }
}
