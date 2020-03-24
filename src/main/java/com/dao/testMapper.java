package com.dao;

import com.model.test;
import java.util.List;

public interface testMapper {
    int deleteByPrimaryKey(Integer aaa);

    int insert(test record);

    test selectByPrimaryKey(Integer aaa);

    List<test> selectAll();

    int updateByPrimaryKey(test record);
}