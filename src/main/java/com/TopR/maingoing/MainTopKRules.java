package com.TopR.maingoing;

import com.TopR.algo.AlgoTopKRulesNew;
import com.TopR.algo.Database;
import com.TopR.tools.CountTools;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;


public class MainTopKRules {


    public static void main(String [] arg) throws Exception{
        int k = 50;
        //平均130
        int minsup=2;
        double minConf = 0.4; //
        int minLift=1;
        Map<Integer,BitSet[]> count = new HashMap<>();
        AlgoTopKRulesNew algo = new AlgoTopKRulesNew();
        for (int i=0;i<8;i++) {
            Database database = new Database();
            database.loadFile(fileToPath("/groupOutPut" + i + ".txt"));
            //database.loadFile(fileToPath("/sales1.txt"));
            System.out.println("-------------------算法运行中-------------------");
            algo.runAlgorithm(k, minsup,minConf,minLift, database,count,i);
            System.out.println("-------------------算法运行完毕-------------------");
            algo.printStats();
            System.out.println("-------------------结果写入文件-------------------");
            algo.writeResultTofile(".//result" + i + ".txt");
            //algo.writeResultTofile(".//SALES1RE.txt");
            System.out.println("-------------------写入文件完毕-------------------");
            System.out.println("-------------------写入数据库-------------------");
       //     algo.insertVisual(i);
            System.out.println("-------------------写入数据库完毕-------------------");
        }
        CountTools countTools =new CountTools();
        countTools.countNodes(count);
        System.out.println("over");
    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException{
        URL url = MainTopKRules.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }
}
