package com.dao;

import com.model.Result;
import java.util.List;

public interface ResultMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Result record);

    Result selectByPrimaryKey(Integer id);

    List<Result> selectAll();

    int updateByPrimaryKey(Result record);

    void dropAll();
}