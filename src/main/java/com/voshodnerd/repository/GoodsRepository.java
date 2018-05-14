package com.voshodnerd.repository;

import com.voshodnerd.model.Goods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface GoodsRepository extends JpaRepository<Goods, Long> {

    @Query(value = "SELECT * FROM tbl_goods ", nativeQuery = true)
    public List<Goods> findAllGoods();
}
