package com.TopR.tools;

import com.MybatisUtils;
import com.TopR.algo.Nodes;
import com.alibaba.fastjson.JSON;
import com.dao.mooc_nodesMapper;
import com.dao.mooc_visualMapper;
import com.enums.ProjectEnums;
import com.model.mooc_nodes;
import lombok.Data;
import org.apache.ibatis.session.SqlSession;

import java.util.*;

@Data
public class CountTools {
    private Map<Integer, ArrayList<Nodes>> nodesMap;

    public void countNodes(Map<Integer, BitSet[]> count) {
        nodesMap = new HashMap<>();
        for (int i = 0; i < 8; i++) {
            BitSet[] in = count.get(i);
            ArrayList<Nodes> nodesArr = new ArrayList<>();
            for (int j = 0; j < in.length; j++) {
                Nodes nodes = new Nodes();
                if (null == in[j]) {
                    nodes.setNum(j);
                    nodes.setCount(0);
                    nodesArr.add(nodes);
                } else {
                    nodes.setNum(j);
                    nodes.setCount(in[j].cardinality());
                    nodesArr.add(nodes);
                }
            }
            nodesMap.put(i, nodesArr);
        }
        saveNodes(nodesMap);
    }

    public void saveNodes(Map<Integer, ArrayList<Nodes>> nodesMap) {
        ArrayList<Nodes> nodes = new ArrayList<>();
        ArrayList<ArrayList<Integer>> arrayLists = new ArrayList<>();
        for (int i = 0; i < nodesMap.get(0).size(); i++) {
            ArrayList<Integer> integers = new ArrayList<>();
            for (int j = 0; j < nodesMap.size(); j++) {
                integers.add(nodesMap.get(j).get(i).getCount());
            }
            arrayLists.add(integers);
        }
        int thisNum = 0;
        for (ArrayList<Integer> in :
                arrayLists) {
            int max = Collections.max(in);
            int index = in.indexOf(max);
            Nodes node = new Nodes();
            node.setNum(thisNum);
            node.setCount(max);
            node.setCatyCray(index);
            nodes.add(node);
            thisNum++;
        }
        insertNodes(nodes);
//        for (i = 0; i < nodesMap.get(0).size(); i++) {
////            max=nodesMap.get(0).get(i).getCount();
////            t=0;
////            for (j = 0; j < nodesMap.size() - 1; j++) {
////                if (max< nodesMap.get(t+1).get(i).getCount()) {
////                    max = nodesMap.get(t).get(i + 1).getCount();
////                    t++;
////                }
////            }
////            Nodes node = new Nodes();
////            node.setNum(i);
////            node.setCount(max);
////            nodes.add(node);
////        }
    }


    public void insertNodes(ArrayList<Nodes> nodes) {
        SqlSession sqlSeesion = MybatisUtils.getSqlSession();
        mooc_nodesMapper moocnodesMapper = sqlSeesion.getMapper(mooc_nodesMapper.class);
        for (Nodes in : nodes) {
            mooc_nodes req = new mooc_nodes();
            req.setProjectid(String.valueOf(in.getNum()));
            for (ProjectEnums e : ProjectEnums.values()) {
                if (in.getNum().equals(e.getProjectId())) {
                    req.setProjectname(e.getProjectName());
                }
            }
            req.setProjectcount(in.getCount());
            req.setCatycray(in.getCatyCray());
            moocnodesMapper.insert(req);
        }
//        System.out.println("stop");
        sqlSeesion.commit();
        sqlSeesion.close();
    }
}
