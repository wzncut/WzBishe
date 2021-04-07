package com.TopR.algo;
/*
    根据算法改进，加入新的度量标准，希望提高算法准确度
 */
import com.TopR.tools.ArraysAlgos;
import com.TopR.tools.MemoryLogger;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

public class AlgoTopK {
    long timeStart = 0;//起始时间
    long timeEnd = 0;
    double minConfidence;
    int k = 0;
    Database database;
    int minsuppRelative;
    BitSet[] tableItemTids;//Bitset数组
    int[] tableItemCount;
    PriorityQueue<RuleG> kRules;
    PriorityQueue<RuleG> candidates;
    int maxCandidateCount = 0;
    int maxAntecedentSize = 10;
    int maxConsequentSize = 10;

    public AlgoTopK() {
    }

    public void runAlgorithm(int k, double minConfidence, Database database) {

        MemoryLogger.getInstance().reset();
        maxCandidateCount = 0;

        this.minConfidence = minConfidence;//最小置信度
        this.database = database;
        this.k = k;

        this.minsuppRelative = 1;
        tableItemTids = new BitSet[database.maxItem + 1]; // 所有项集中拥有最多项目的个数
        tableItemCount = new int[database.maxItem + 1];// 所有项集中拥有最多项目的个数
        kRules = new PriorityQueue<RuleG>();
        candidates = new PriorityQueue<RuleG>(new Comparator<RuleG>() {
            @Override
            public int compare(RuleG o1, RuleG o2) {
                return -(o1.compareTo(o2));
            }
        });

        // 记录开始时间
        timeStart = System.currentTimeMillis();

        if (maxAntecedentSize >= 1 && maxConsequentSize >= 1) {
            scanDatabase(database);
            start();
        }
        //记录结束时间
        timeEnd = System.currentTimeMillis();
    }

    private void start() {
        main:
        for (int itemI = 0; itemI <= database.maxItem; itemI++) {
            if (tableItemCount[itemI] < minsuppRelative) {
                continue main;
            }
            BitSet tidsI = tableItemTids[itemI];

            main2:
            for (int itemJ = itemI + 1; itemJ <= database.maxItem; itemJ++) {
                if (tableItemCount[itemJ] < minsuppRelative) {
                    continue main2;
                }
                BitSet tidsJ = tableItemTids[itemJ];

                BitSet commonTids = (BitSet) tidsI.clone();
                commonTids.and(tidsJ);
                int support = commonTids.cardinality();

                if (support >= minsuppRelative) {
                    generateRuleSize11(itemI, tidsI, itemJ, tidsJ, commonTids,
                            support);
                }
            }
        }
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

    private void generateRuleSize11(Integer item1, BitSet tid1, Integer item2,
                                    BitSet tid2, BitSet commonTids, int cardinality) {
        Integer[] itemset1 = new Integer[1];
        itemset1[0] = item1;
        Integer[] itemset2 = new Integer[1];
        itemset2[0] = item2;
        RuleG ruleLR = new RuleG(itemset1, itemset2, cardinality, tid1,
                commonTids, item1, item2);

        double confidenceIJ = ((double) cardinality) / (tableItemCount[item1]);

        if (confidenceIJ >= minConfidence) {
            save(ruleLR, cardinality);
        }
        if (ruleLR.getItemset1().length < maxAntecedentSize ||
                ruleLR.getItemset2().length < maxConsequentSize) {
            registerAsCandidate(true, ruleLR);
        }

        double confidenceJI = ((double) cardinality) / (tableItemCount[item2]);

        RuleG ruleRL = new RuleG(itemset2, itemset1, cardinality, tid2,
                commonTids, item2, item1);
        if (confidenceJI >= minConfidence) {
            save(ruleRL, cardinality);
        }
        if (ruleRL.getItemset1().length < maxAntecedentSize ||
                ruleRL.getItemset2().length < maxConsequentSize) {
            registerAsCandidate(true, ruleRL);
        }

    }

    private void registerAsCandidate(boolean expandLR, RuleG rule) {
        rule.expandLR = expandLR;
        candidates.add(rule);

        if (candidates.size() >= maxCandidateCount) {
            maxCandidateCount = candidates.size();
        }
        MemoryLogger.getInstance().checkMemory();
    }

    private void expandLR(RuleG ruleG) {
        if (ruleG.getItemset2().length == maxConsequentSize && ruleG.getItemset1().length == maxAntecedentSize) {
            return;
        }

        Map<Integer, BitSet> mapCountLeft = new HashMap<Integer, BitSet>();
        Map<Integer, BitSet> mapCountRight = new HashMap<Integer, BitSet>();


        for (int tid = ruleG.common.nextSetBit(0); tid >= 0; tid = ruleG.common
                .nextSetBit(tid + 1)) {
            Iterator<Integer> iter = database.getTransactions().get(tid)
                    .getItems().iterator();
            while (iter.hasNext()) {
                Integer item = iter.next();
                if (item < ruleG.maxLeft && item < ruleG.maxRight) { //
                    break;
                }
                if (tableItemCount[item] < minsuppRelative) {
                    iter.remove();
                    continue;
                }
                if (item > ruleG.maxLeft
                        && !ArraysAlgos.containsLEX(ruleG.getItemset2(), item,
                        ruleG.maxRight)) {
                    BitSet tidsItem = mapCountLeft.get(item);
                    if (tidsItem == null) {
                        tidsItem = new BitSet();
                        mapCountLeft.put(item, tidsItem);
                    }
                    tidsItem.set(tid);
                }
                if (item > ruleG.maxRight
                        && !ArraysAlgos.containsLEX(ruleG.getItemset1(), item,
                        ruleG.maxLeft)) {
                    BitSet tidsItem = mapCountRight.get(item);
                    if (tidsItem == null) {
                        tidsItem = new BitSet();
                        mapCountRight.put(item, tidsItem);
                    }
                    tidsItem.set(tid);
                }
            }
        }


        if (ruleG.getItemset2().length < maxConsequentSize) {
            for (Entry<Integer, BitSet> entry : mapCountRight.entrySet()) {
                BitSet tidsRule = entry.getValue();
                int ruleSupport = tidsRule.cardinality();

                if (ruleSupport >= minsuppRelative) {
                    Integer itemC = entry.getKey();

                    Integer[] newRightItemset = new Integer[ruleG.getItemset2().length + 1];
                    System.arraycopy(ruleG.getItemset2(), 0, newRightItemset, 0,
                            ruleG.getItemset2().length);
                    newRightItemset[ruleG.getItemset2().length] = itemC;

                    int maxRight = (itemC >= ruleG.maxRight) ? itemC
                            : ruleG.maxRight;

                    double confidence = ((double) ruleSupport)
                            / ruleG.tids1.cardinality();

                    RuleG candidate = new RuleG(ruleG.getItemset1(),
                            newRightItemset, ruleSupport, ruleG.tids1, tidsRule,
                            ruleG.maxLeft, maxRight);

                    if (confidence >= minConfidence) {
                        save(candidate, ruleSupport);
                    }
                    if (candidate.getItemset2().length < maxConsequentSize) {
                        registerAsCandidate(false, candidate);
                    }
                }
            }
        }

        if (ruleG.getItemset1().length < maxAntecedentSize) {
            for (Entry<Integer, BitSet> entry : mapCountLeft.entrySet()) {
                BitSet tidsRule = entry.getValue();
                int ruleSupport = tidsRule.cardinality();

                if (ruleSupport >= minsuppRelative) {
                    Integer itemC = entry.getKey();

                    BitSet tidsLeft = (BitSet) ruleG.tids1.clone();
                    tidsLeft.and(tableItemTids[itemC]);

                    Integer[] newLeftItemset = new Integer[ruleG.getItemset1().length + 1];
                    System.arraycopy(ruleG.getItemset1(), 0, newLeftItemset, 0,
                            ruleG.getItemset1().length);
                    newLeftItemset[ruleG.getItemset1().length] = itemC;

                    int maxLeft = itemC >= ruleG.maxLeft ? itemC : ruleG.maxLeft;

                    double confidence = ((double) ruleSupport)
                            / tidsLeft.cardinality();

                    RuleG candidate = new RuleG(newLeftItemset,
                            ruleG.getItemset2(), ruleSupport, tidsLeft, tidsRule,
                            maxLeft, ruleG.maxRight);

                    if (confidence >= minConfidence) {
                        save(candidate, ruleSupport);
                    }
                    if (candidate.getItemset1().length < maxAntecedentSize ||
                            candidate.getItemset2().length < maxConsequentSize) {
                        registerAsCandidate(true, candidate);
                    }
                }
            }
        }
    }

    private void expandR(RuleG ruleG) {
        if (ruleG.getItemset2().length == maxConsequentSize) {
            return;
        }

        Map<Integer, BitSet> mapCountRight = new HashMap<Integer, BitSet>();

        for (int tid = ruleG.common.nextSetBit(0); tid >= 0; tid = ruleG.common
                .nextSetBit(tid + 1)) {

            Iterator<Integer> iter = database.getTransactions().get(tid)
                    .getItems().iterator();
            while (iter.hasNext()) {
                Integer item = iter.next();

                if (tableItemCount[item] < minsuppRelative) {
                    iter.remove();
                    continue;
                }

                if (item < ruleG.maxRight) {
                    break;
                }

                if (item > ruleG.maxRight
                        && !ArraysAlgos.containsLEX(ruleG.getItemset1(), item,
                        ruleG.maxLeft)) {

                    BitSet tidsItem = mapCountRight.get(item);
                    if (tidsItem == null) {
                        tidsItem = new BitSet();
                        mapCountRight.put(item, tidsItem);
                    }
                    tidsItem.set(tid);
                }
            }
        }

        for (Entry<Integer, BitSet> entry : mapCountRight.entrySet()) {
            BitSet tidsRule = entry.getValue();
            int ruleSupport = tidsRule.cardinality();

            if (ruleSupport >= minsuppRelative) {
                Integer itemC = entry.getKey();

                Integer[] newRightItemset = new Integer[ruleG.getItemset2().length + 1];
                System.arraycopy(ruleG.getItemset2(), 0, newRightItemset, 0,
                        ruleG.getItemset2().length);
                newRightItemset[ruleG.getItemset2().length] = itemC;

                int maxRight = itemC >= ruleG.maxRight ? itemC : ruleG.maxRight;

                double confidence = ((double) ruleSupport)
                        / ruleG.tids1.cardinality();

                RuleG candidate = new RuleG(ruleG.getItemset1(),
                        newRightItemset, ruleSupport, ruleG.tids1, tidsRule,
                        ruleG.maxLeft, maxRight);

                if (confidence >= minConfidence) {
                    save(candidate, ruleSupport);
                }
                if (candidate.getItemset2().length < maxConsequentSize) {
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
     *方便计数，使用bitset可以大大提高效率，以及后面计算方便。
     */
    private void scanDatabase(Database database) {
        // 对于transaction链表中的每一项
        for (int j = 0; j < database.getTransactions().size(); j++) {
            Transaction transaction = database.getTransactions().get(j);
            // 每一项中的每个项目
            for (Integer item : transaction.getItems()) {

                //没什么用
                BitSet ids = tableItemTids[item];
                if (ids == null) {
                    tableItemTids[item] = new BitSet(database.tidsCount);
                }

                tableItemTids[item].set(j);//将tableItemsTids中的第item项中的第j位置为true
                // 每设置一个true则计数+1
                tableItemCount[item] = tableItemCount[item] + 1;
            }
        }
    }

    /**
     * 打印程序运行状态
     */
    public void printStats() {
        System.out.println("=============  TOP-K RULES SPMF v.2.10 - STATS =============");
        System.out.println("Minsup : " + minsuppRelative);
        System.out.println("Rules count: " + kRules.size());
        System.out.println("Memory : " + MemoryLogger.getInstance().getMaxMemory() + " mb");
        System.out.println("Total time : " + (timeEnd - timeStart) + " ms");
        System.out
                .println("===================================================");
    }

    public void writeResultTofile(String path) throws IOException {
        // Prepare the file
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));

        if (kRules.size() > 0) {
            Object[] rules = kRules.toArray();
            Arrays.sort(rules);

            for (Object ruleObj : rules) {
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
        // close the file
        writer.close();
    }
}


