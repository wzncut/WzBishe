package com.dao;

import com.model.mooc_nodes;
import java.util.List;

public interface mooc_nodesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(mooc_nodes record);

    mooc_nodes selectByPrimaryKey(Integer id);

    List<mooc_nodes> selectAll();

    int updateByPrimaryKey(mooc_nodes record);
}