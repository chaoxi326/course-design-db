package com.example.supermarket.mapper;

import com.example.supermarket.entity.PurchaseDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PurchaseDetailMapper {
    int insertDetailBatch(@Param("detailList") List<PurchaseDetail> detailList);
    List<PurchaseDetail> selectAllDetails();
    int updateDetail(PurchaseDetail detail);
    int deleteDetailByOrderId(String oId); // 根据主表ID级联删除明细
}