package com.TopR.maingoing;

import com.TopR.algo.AlgoTopK;
import com.TopR.algo.AlgoTopKRulesNew;
import com.TopR.algo.Database;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class MainCompare {
    public static void main(String [] arg) throws Exception{
        int k = 50;
        //平均130
        int minsup=2;
        double minConf = 0.5; //
        int minLift=1;
        Map<Integer, BitSet[]> count = new HashMap<>();
        AlgoTopKRulesNew algo = new AlgoTopKRulesNew();
        AlgoTopK algoTopK=new AlgoTopK();

        Database database1 = new Database();

        database1.loadFile(fileToPath("/sales1.txt"));
        System.out.println("-------------------新算法运行中-------------------");
        algo.runAlgorithm(k, minsup,minConf,minLift, database1,count,0);
        System.out.println("-------------------新算法运行完毕-------------------");
        algo.printStats();
//        Database database2 =new Database();
//        database2.loadFile(fileToPath("/sales1.txt"));
//        System.out.println("-------------------原算法运行中-------------------");
//        algoTopK.runAlgorithm(k,minConf,database2);
//        System.out.println("-------------------原算法运行完毕-------------------");
//        algoTopK.printStats();
//        System.out.println("over");
    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = MainTopKRules.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }
}
