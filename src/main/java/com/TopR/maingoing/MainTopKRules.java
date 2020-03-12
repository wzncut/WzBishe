package com.TopR.maingoing;

import com.TopR.algo.AlgoTopKRules;
import com.TopR.algo.Database;

import java.io.UnsupportedEncodingException;
import java.net.URL;


public class MainTopKRules {

    public static void main(String [] arg) throws Exception{
        Database database = new Database();
        database.loadFile(fileToPath("/mooc.txt"));
        int k = 100;
        double minConf = 0.4; //
        AlgoTopKRules algo = new AlgoTopKRules();
        System.out.println("-------------------算法运行中-------------------");
        algo.runAlgorithm(k, minConf, database);
        System.out.println("-------------------算法运行完毕-------------------");
        algo.printStats();
//        algo.insertInDb();
        System.out.println("-------------------结果写入文件-------------------");
        algo.writeResultTofile(".//mooc.txt");
        System.out.println("-------------------写入文件完毕-------------------");
    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException{
        URL url = MainTopKRules.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }
}
