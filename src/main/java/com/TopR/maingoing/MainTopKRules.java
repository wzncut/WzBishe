package com.TopR.maingoing;

import com.TopR.algo.AlgoTopKRules;
import com.TopR.algo.Database;

import java.io.UnsupportedEncodingException;
import java.net.URL;


public class MainTopKRules {

    public static void main(String [] arg) throws Exception{
        Database database = new Database();
        database.loadFile(fileToPath("/mooc3.txt"));
        int k = 200;
        double minConf = 0.4; //
        AlgoTopKRules algo = new AlgoTopKRules();
        algo.runAlgorithm(k, minConf, database);
        algo.printStats();
        algo.insertInDb();
        algo.writeResultTofile(".//mooc.txt");
    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException{
        URL url = MainTopKRules.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }
}
