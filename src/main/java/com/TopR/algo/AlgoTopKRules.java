package com.TopR.algo;

import com.MybatisUtils;
import com.TopR.tools.MemoryLogger;
import com.alibaba.fastjson.JSON;
import com.dao.ResultMapper;
import com.model.Result;
import com.sun.javafx.css.Rule;
import org.apache.ibatis.session.SqlSession;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;

public class AlgoTopKRules {
    /*
    程序开始时间
     */
    long timeStart = 0;
    /*
    程序结束时间
     */
    long timeEnd = 0;
    /*
    最小置信度
     */
    double minConfidence;
    /*
    规定返回结果数量
     */
    int k = 0;
    /*
    数据集
     */
    Database database;
    /*
    最小支持度
     */
    int minsuppRelative;
    /*
    数据集数据结构，统计个数用
     */
    BitSet[] tableItemTids;
    /*
    统计数据项个数
     */
    int[] tableItemCount;
    /*
    结果集优先队列
     */
    PriorityQueue<RuleG> kRules;
    /*
    候选集优先队列
     */
    PriorityQueue<RuleG> candidates;
    /*
    最大候选数
     */
    int maxCandidateCount = 0;
    /*
    启始项最大数量
     */
    int maxAntecedentSize = Integer.MAX_VALUE;
    /*
    结束项最大数量
     */
    int maxConsequentSize = Integer.MAX_VALUE;

    /*
    一共有几条数据
     */
    int itemsnum=0;
    /*
     格式化
     */
    DecimalFormat df=new DecimalFormat("0.00");

    /*
    b初始值
     */
    Double thisLift=0.1;

    public AlgoTopKRules() {
    }

    /*
    算法开始
     */
    public void runAlgorithm(int k, double minConfidence, Database database) {
        MemoryLogger.getInstance().reset();
        maxCandidateCount = 0;

        this.minConfidence = minConfidence;
        this.database = database;
        this.k = k;

        this.itemsnum=database.tidsCount;
        this.minsuppRelative = 1;
        tableItemTids = new BitSet[database.maxItem + 1];
        tableItemCount = new int[database.maxItem + 1];
        kRules = new PriorityQueue<RuleG>();
        candidates = new PriorityQueue<RuleG>(new Comparator<RuleG>(){
            @Override
            public int compare(RuleG o1, RuleG o2) {
                return - (o1.compareTo(o2));
            }});
        //记录算法的开始时间
        timeStart = System.currentTimeMillis();

        if(maxAntecedentSize >=1 && maxConsequentSize >=1){
            //扫描数据库存入数据结构
            scanDatabase(database);
            //主算法逻辑
            start();
        }
        //记录算法的结束时间
        timeEnd = System.currentTimeMillis();
    }

    /*
    算法主逻辑
     */
    private void start() {
        // 一个项走完再走另一个项
        main: for (int itemI = 0; itemI <= database.maxItem; itemI++) {
            //第一趟:把[0]赋值给tidsI
            if (tableItemCount[itemI] < minsuppRelative) {
                continue main;
            }
            BitSet tidsI = tableItemTids[itemI];
            main2: for (int itemJ = itemI + 1; itemJ <= database.maxItem; itemJ++) {
                if (tableItemCount[itemJ] < minsuppRelative) {
                    continue main2;
                }

                //把[1]赋值给tidsJ
                BitSet tidsJ = tableItemTids[itemJ];

                //共享事务集
                BitSet commonTids = (BitSet) tidsI.clone();
                commonTids.and(tidsJ);

                //统计规则支持度
                int support = commonTids.cardinality();
                if (support >= minsuppRelative) {
                    //创建新的1*1规则
                    generateRuleSize11(itemI, tidsI, itemJ, tidsJ, commonTids, support);
                }
            }
        }

        findRule(candidates);
        while (candidates.size() > 0) {
            RuleG rule = candidates.poll();
            if (rule.getAbsoluteSupport() < minsuppRelative) {
                break;
            }
            if (rule.expandLR) {
                expandLR(rule);
            } else {
                expandR(rule);
            }
        }
    }

    /*
        创建1*1规则
        存入候选集合和结果集合
        @cardinality:支持度
     */
    private void generateRuleSize11(Integer item1, BitSet tid1, Integer item2, BitSet tid2, BitSet commonTids, int cardinality) {
        Integer[] itemset1 = new Integer[1];
        itemset1[0] = item1;
        Integer[] itemset2 = new Integer[1];
        itemset2[0] = item2;
        RuleG ruleLR = new RuleG(itemset1, itemset2, cardinality, tid1, commonTids, item1, item2);

        //计算规则的置信度
        double confidenceIJ = ((double) cardinality) / (tableItemCount[item1]);

        //如果大于最小置信度
        if (confidenceIJ >= minConfidence) {

            thisLift =confidenceIJ/((double) cardinality/(double) itemsnum);
            if (thisLift>1){
                save(ruleLR, cardinality);
            }
            //执行save
//            save(ruleLR, cardinality);
        }

        if(ruleLR.getItemset1().length < maxAntecedentSize || ruleLR.getItemset2().length < maxConsequentSize){

            thisLift =confidenceIJ/((double) cardinality/(double) itemsnum);
            if (thisLift>1) {
                registerAsCandidate(true, ruleLR);
            }
        }

        //反过来再得到所有1*1规则
        double confidenceJI = ((double) cardinality) / (tableItemCount[item2]);
        RuleG ruleRL = new RuleG(itemset2, itemset1, cardinality, tid2,
                commonTids, item2, item1);

        if (confidenceJI >= minConfidence) {
           thisLift = confidenceJI/(cardinality/(double) itemsnum);
            if (thisLift>1){
                save(ruleLR, cardinality);
            }
            //执行save
//            save(ruleRL, cardinality);
        }

        if(ruleRL.getItemset1().length < maxAntecedentSize || ruleRL.getItemset2().length < maxConsequentSize	){
            thisLift =confidenceIJ/((double) cardinality/(double) itemsnum);
            if (thisLift>1) {
                registerAsCandidate(true, ruleRL);
            }
        }
    }

    /*
     * 讲规则插入到候选优先队列中
     * 并排序
     */
    private void registerAsCandidate(boolean expandLR, RuleG rule) {
        rule.expandLR = expandLR;
        candidates.add(rule);

        if (candidates.size() >= maxCandidateCount) {
            maxCandidateCount = candidates.size();
        }
        MemoryLogger.getInstance().checkMemory();
    }

    /*
     * expand过程
     * expandLR方法
     * 先执行做扩展在进行右扩展，避免找到重复的规则
     */
    private void expandLR(RuleG ruleG) {
        if(ruleG.getItemset2().length == maxConsequentSize && ruleG.getItemset1().length == maxAntecedentSize){
            return;
        }

        Map<Integer, BitSet> mapCountLeft = new HashMap<Integer, BitSet>();
        Map<Integer, BitSet> mapCountRight = new HashMap<Integer, BitSet>();

        //遍历bitset中所有true位索引
        for (int tid = ruleG.common.nextSetBit(0); tid >= 0; tid = ruleG.common.nextSetBit(tid + 1)){
            //定位同时出现Rule中项目的所有项集并扫描
            Iterator<Integer> iter = database.getTransactions().get(tid).getItems().iterator();

            while (iter.hasNext()) {
                //从大到小，之前已排好序
                Integer item = iter.next();

                if (item < ruleG.maxLeft && item < ruleG.maxRight) {
                    break;
                }

                //小于最小支持度=>移除
                if (tableItemCount[item] < minsuppRelative) {
                    iter.remove();
                    continue;
                }
                if (item > ruleG.maxLeft && !containsLEX(ruleG.getItemset2(), item, ruleG.maxRight)) {
                    BitSet tidsItem = mapCountLeft.get(item);
                    if (tidsItem == null) {
                        tidsItem = new BitSet();
                        mapCountLeft.put(item, tidsItem);
                    }
                    tidsItem.set(tid);
                }
                if (item > ruleG.maxRight && !containsLEX(ruleG.getItemset1(), item, ruleG.maxLeft)) {
                    BitSet tidsItem = mapCountRight.get(item);
                    if (tidsItem == null) {
                        tidsItem = new BitSet();
                        mapCountRight.put(item, tidsItem);
                    }
                    tidsItem.set(tid);
                }
            }
        }

        // for each item c found in the previous step, we create a rule
        // I  ==> J U {c} if the support is enough
        if(ruleG.getItemset2().length < maxConsequentSize){
            for (Entry<Integer, BitSet> entry : mapCountRight.entrySet()) {
                BitSet tidsRule = entry.getValue();
                int ruleSupport = tidsRule.cardinality();

                // if the support is enough
                if (ruleSupport >= minsuppRelative) {
                    Integer itemC = entry.getKey();

                    // create new right part of rule
                    Integer[] newRightItemset = new Integer[ruleG.getItemset2().length + 1];
                    System.arraycopy(ruleG.getItemset2(), 0, newRightItemset, 0,
                            ruleG.getItemset2().length);
                    newRightItemset[ruleG.getItemset2().length] = itemC;

                    // recompute maxRight
                    int maxRight = (itemC >= ruleG.maxRight) ? itemC
                            : ruleG.maxRight;

                    // calculate the confidence of the rule
                    double confidence = ((double) ruleSupport)
                            / ruleG.tids1.cardinality();

                    // create the rule
                    RuleG candidate = new RuleG(ruleG.getItemset1(),
                            newRightItemset, ruleSupport, ruleG.tids1, tidsRule,
                            ruleG.maxLeft, maxRight);

                    // if the confidence is enough
                    if (confidence >= minConfidence) {
                        // save the rule in current top-k rules
                        thisLift =confidence/(ruleSupport/(double) itemsnum);
                        if (thisLift>1) {
                            save(candidate, ruleSupport);
                        }
                    }
                    // register the rule as a candidate for future expansion
                    if(candidate.getItemset2().length < maxConsequentSize){
                        registerAsCandidate(false, candidate);
                    }
                }
            }
        }

        // for each item c found in the previous step, we create a rule
        // I  U {c} ==> J if the support is enough
        if(ruleG.getItemset1().length < maxAntecedentSize){
            for (Entry<Integer, BitSet> entry : mapCountLeft.entrySet()) {
                BitSet tidsRule = entry.getValue();
                int ruleSupport = tidsRule.cardinality();

                // if the support is enough
                if (ruleSupport >= minsuppRelative) {
                    Integer itemC = entry.getKey();

                    // The tidset of the left itemset is calculated
                    BitSet tidsLeft = (BitSet) ruleG.tids1.clone();
                    tidsLeft.and(tableItemTids[itemC]);

                    // create new left part of rule
                    Integer[] newLeftItemset = new Integer[ruleG.getItemset1().length + 1];
                    System.arraycopy(ruleG.getItemset1(), 0, newLeftItemset, 0,
                            ruleG.getItemset1().length);
                    newLeftItemset[ruleG.getItemset1().length] = itemC;

                    // recompute maxLeft
                    int maxLeft = itemC >= ruleG.maxLeft ? itemC : ruleG.maxLeft;

                    // calculate the confidence of the rule
                    double confidence = ((double) ruleSupport)
                            / tidsLeft.cardinality();

                    // create the rule
                    RuleG candidate = new RuleG(newLeftItemset,
                            ruleG.getItemset2(), ruleSupport, tidsLeft, tidsRule,
                            maxLeft, ruleG.maxRight);

                    // if the confidence is high enough
                    if (confidence >= minConfidence) {
                        // save the rule to the top-k rules
                        thisLift =confidence/(ruleSupport/(double) itemsnum);
                        if (thisLift>1) {
                            save(candidate, ruleSupport);
                        }
                    }
                    // register the rule as a candidate for further expansions
                    if(candidate.getItemset1().length < maxAntecedentSize ||
                            candidate.getItemset2().length < maxConsequentSize	){
                        registerAsCandidate(true, candidate);
                    }
                }
            }
        }
    }

    /**
     * Try to expand a rule by right expansion only.
     * @param ruleG the rule
     */
    private void expandR(RuleG ruleG) {
        if(ruleG.getItemset2().length == maxConsequentSize){
            return;
        }

        // map to record the potential item to expand the right side of the rule
        // Key: item   Value: bitset indicating the IDs of the transaction containing the item
        // from the transactions containing the rule.
        Map<Integer, BitSet> mapCountRight = new HashMap<Integer, BitSet>();

        // for each transaction containing the rule
        for (int tid = ruleG.common.nextSetBit(0); tid >= 0; tid = ruleG.common
                .nextSetBit(tid + 1)) {

            // iterate over the items in this transaction
            Iterator<Integer> iter = database.getTransactions().get(tid)
                    .getItems().iterator();
            while (iter.hasNext()) {
                Integer item = iter.next();

                // if  that item is not frequent, then remove it from the transaction
                if (tableItemCount[item] < minsuppRelative) {
                    iter.remove();
                    continue;
                }

                //If the item is smaller than the largest item in the right side
                // of the rule, we can stop this loop because items
                // are sorted in lexicographical order.
                if (item < ruleG.maxRight) {
                    break;
                }

                // if the item is larger than the maximum item in the right side
                // and is not contained in the left side of the rule
                if (item > ruleG.maxRight
                        && !containsLEX(ruleG.getItemset1(), item,
                        ruleG.maxLeft)) {

                    // update the tidset of the item
                    BitSet tidsItem = mapCountRight.get(item);
                    if (tidsItem == null) {
                        tidsItem = new BitSet();
                        mapCountRight.put(item, tidsItem);
                    }
                    tidsItem.set(tid);
                }
            }
        }

        // for each item c found in the previous step, we create a rule
        // I ==> J U {c} if the support is enough
        for (Entry<Integer, BitSet> entry : mapCountRight.entrySet()) {
            BitSet tidsRule = entry.getValue();
            int ruleSupport = tidsRule.cardinality();

            // if the support is enough
            if (ruleSupport >= minsuppRelative) {
                Integer itemC = entry.getKey();

                // create new right part of rule
                Integer[] newRightItemset = new Integer[ruleG.getItemset2().length + 1];
                System.arraycopy(ruleG.getItemset2(), 0, newRightItemset, 0,
                        ruleG.getItemset2().length);
                newRightItemset[ruleG.getItemset2().length] = itemC;

                //recompute maxRight
                int maxRight = itemC >= ruleG.maxRight ? itemC : ruleG.maxRight;

                // calculate confidence
                double confidence = ((double) ruleSupport)
                        / ruleG.tids1.cardinality();

                // create the rule
                RuleG candidate = new RuleG(ruleG.getItemset1(),
                        newRightItemset, ruleSupport, ruleG.tids1, tidsRule,
                        ruleG.maxLeft, maxRight);

                // if the confidence is enough
                if (confidence >= minConfidence) {
                    // save the rule to the current top-k rules
//                    save(candidate, ruleSupport);
                    thisLift = confidence / (ruleSupport / (double) itemsnum);
                    if (thisLift > 1) {
                        save(candidate, ruleSupport);
                    }
                }
                // register the rule as a candidate for future expansion(s)
                if(candidate.getItemset2().length < maxConsequentSize	){
                    registerAsCandidate(false, candidate);
                }
            }
        }
    }

    private void save(RuleG rule, int support) {
        kRules.add(rule);

        if (kRules.size() > k) {

            if (support > this.minsuppRelative) {
                do {
                    kRules.poll();
                } while (kRules.size() > k);
            }
            this.minsuppRelative = kRules.peek().getAbsoluteSupport();
        }
    }

    /*
     * 扫描数据集
     * 保存在内存中
     */
    private void scanDatabase(Database database) {
        for (int j = 0; j < database.getTransactions().size(); j++) {
            //循环每一条数据
            Transaction transaction = database.getTransactions().get(j);

            for (Integer item : transaction.getItems()) {//item=1..2..3...
                BitSet ids = tableItemTids[item];//下标从1开始

                if (ids == null) {
                    tableItemTids[item] = new BitSet(database.tidsCount);
                }
                tableItemTids[item].set(j);
                tableItemCount[item] = tableItemCount[item] + 1;
            }
        }
    }

    public void insertInDb(){
        SqlSession sqlSeesion= MybatisUtils.getSqlSession();
        ResultMapper resultMapper=sqlSeesion.getMapper(ResultMapper.class);
        resultMapper.dropAll();
        if(kRules.size() > 0){
            Object[] rules = kRules.toArray();
            Arrays.sort(rules);
            for(Object ruleObj : rules){
                RuleG rule = (RuleG) ruleObj;
                Result result=new Result();
                result.setSup(rule.getAbsoluteSupport());
                result.setConf(rule.getConfidence());
                resultMapper.insert(result);
            }
            sqlSeesion.commit();
            sqlSeesion.close();
        }
    }
    /*
     * 控制台打印算法的执行结束时间
     * 占用内存等信息
     */
    public void printStats() {
        System.out.println("=============  TOP-K RULES SPMF v.2.10 - STATS =============");
        System.out.println("Minsup : " + minsuppRelative);
        System.out.println("Rules count: " + kRules.size());
        System.out.println("Memory : " + MemoryLogger.getInstance().getMaxMemory() + " mb");
        System.out.println("Total time : " + (timeEnd - timeStart) + " ms");
        System.out.println("===================================================");
    }

    /*
     * 把最后得到的规则写入TXT文件
     */
    public void writeResultTofile(String path) throws IOException {
        // Prepare the file
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));

        if(kRules.size() > 0){
            Object[] rules = kRules.toArray();
            Arrays.sort(rules);
            for(Object ruleObj : rules){
                RuleG rule = (RuleG) ruleObj;
                StringBuilder buffer = new StringBuilder();
                buffer.append(rule.toString());
                buffer.append(" #SUP: ");
                buffer.append(rule.getAbsoluteSupport());
                buffer.append(" #CONF: ");
                buffer.append(rule.getConfidence());
                writer.write(buffer.toString());
                writer.newLine();
            }
        }
        writer.close();
    }


    public static boolean containsLEX(Integer itemset[], Integer item, int maxItemInArray) {
        if (item > maxItemInArray) {
            return false;
        }

        for (Integer itemI : itemset) {
            if (itemI.equals(item)) {
                return true;
            }
            else if (itemI > item) {
                return false;
            }
        }

        return false;
    }

    public void findRule(PriorityQueue<RuleG> candidates){
        for (RuleG i : candidates){
            for(RuleG j:candidates){
                if (i.getItemset1()[0]==j.getItemset2()[0]&&i.getItemset2()[0]==j.getItemset1()[0]){
//                    System.out.println(JSON.toJSONString(i));
//                    System.out.println(JSON.toJSONString(j));
                }
            }
        }
    }
}
