package com.dao;

import com.model.mooc_visual;
import java.util.List;

public interface mooc_visualMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(mooc_visual record);

    mooc_visual selectByPrimaryKey(Integer id);

    List<mooc_visual> selectAll();

    int updateByPrimaryKey(mooc_visual record);
}