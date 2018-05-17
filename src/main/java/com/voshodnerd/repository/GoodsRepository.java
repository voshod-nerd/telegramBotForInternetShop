package com.voshodnerd.repository;

import com.voshodnerd.model.Goods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface GoodsRepository extends JpaRepository<Goods, Long> {

    @Query(value = "SELECT * FROM tbl_goods ", nativeQuery = true)
    public List<Goods> findAllGoods();

    @Query(value = "SELECT * FROM tbl_goods where type=:type", nativeQuery = true)
    public List<Goods> findByType(@Param("type") String type);

    @Query(value = "SELECT DISTINCT type FROM tbl_goods ", nativeQuery = true)
    public List<String> findTypes();



 }
