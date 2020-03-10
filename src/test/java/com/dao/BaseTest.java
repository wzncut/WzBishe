package com.dao;

import com.MybatisUtils;
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
//        ResultMapper resultMapper=sqlSeesion.getMapper(ResultMapper.class);
//        Result result=new Result();
//        result.setSup(1);
//        result.setConf(11);
//        int num= resultMapper.insert(result);
//        if (num>=0){
//            System.out.println("sucess");
//        }
//        sqlSeesion.commit();
//        Result result1=resultMapper.selectByPrimaryKey(2);
//        System.out.println(result1.getSup());
//        System.out.println(result1.getConf());
    }
}
