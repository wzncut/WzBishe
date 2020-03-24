package com.dao;

import com.MybatisUtils;
import com.model.Result;
import com.model.mooc_visual;
import com.model.test;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BaseTest {
    @org.junit.Test
    public void test() {
        System.out.println("junit test");
    }

    @org.junit.Test
    public void MBG() throws Exception {
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        File configFile = new File("src/main/resources/generatorConfig.xml");
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
    }

    @Test
    public void test1(){
//        SqlSession sqlSeesion=MybatisUtils.getSqlSession();
//        mooc_visualMapper moocVisualMapper = sqlSeesion.getMapper(mooc_visualMapper.class);
//        mooc_visual result=new mooc_visual();
//        result.setSup("1");
//        result.setConf("11");
//        int num= moocVisualMapper.insert(result);
//        if (num>=0){
//            System.out.println("sucess");
//        }
//        sqlSeesion.commit();
//        result=moocVisualMapper.selectByPrimaryKey(2);
//        System.out.println(result.getSup());
//        System.out.println(result.getConf());

        SqlSession sqlSeesion=MybatisUtils.getSqlSession();
        testMapper test1 = sqlSeesion.getMapper(testMapper.class);
        test res=new test();
        res.setAaa(2);
        res.setBbb("2");

        test1.insert(res);
        sqlSeesion.commit();
        sqlSeesion.close();
    }

}
