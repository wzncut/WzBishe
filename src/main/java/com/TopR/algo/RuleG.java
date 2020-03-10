package com.TopR.algo;

import java.util.BitSet;

public class RuleG implements Comparable<RuleG>{

    /*
     *左项集
     */
    private Integer[] itemset1;

    /*
     *右项集
     */
    private Integer[] itemset2;

    /*
     *前项集合
     */
    public BitSet tids1;

    public BitSet common;

    /*
     * 左项集最大数量
     */
    public int maxLeft;

    /*
     * 右项集最大数量
     */
    public int maxRight;

    /*
     * 标志位
     */
    public boolean expandLR = false;

    /*
     *
     */
    private int count;

    /*
     *初始化
     */
    public RuleG(Integer[] itemset1, Integer[] itemset2, int count, BitSet tids1, BitSet common, int maxLeft, int maxRight){
        this.count = count;
        this.itemset1 = itemset1;
        this.itemset2 = itemset2;
        this.common =  common;
        this.tids1 = tids1;
        this.maxLeft= maxLeft;
        this.maxRight= maxRight;
    }

    /*
     *  getter
     */
    public Integer[] getItemset1() {
        return itemset1;
    }

    public Integer[] getItemset2() {
        return itemset2;
    }

    public int getAbsoluteSupport(){
        return count;
    }

    public double getConfidence() {
        return ((double)count) / tids1.cardinality();
    }

    /*
     * 重写compareto
     */
    @Override
    public int compareTo(RuleG o) {
        if(o == this){
            return 0;
        }
        int compare = this.getAbsoluteSupport() - o.getAbsoluteSupport();
        if(compare !=0){
            return compare;
        }

        int itemset1sizeA = this.itemset1 == null ? 0 : this.itemset1.length;
        int itemset1sizeB = o.itemset1 == null ? 0 : o.itemset1.length;
        int compare2 = itemset1sizeA - itemset1sizeB;
        if(compare2 !=0){
            return compare2;
        }

        int itemset2sizeA = this.itemset2 == null ? 0 : this.itemset2.length;
        int itemset2sizeB = o.itemset2 == null ? 0 : o.itemset2.length;
        int compare3 = itemset2sizeA - itemset2sizeB;
        if(compare3 !=0){
            return compare3;
        }

        int compare4 = (int)(this.getConfidence()  - o.getConfidence());
        if(compare !=0){
            return compare4;
        }

        return this.hashCode() - o.hashCode();
    }

    /*
     * 重写equals
     */
    @Override
    public boolean equals(Object o){
        RuleG ruleX = (RuleG)o;
        if(ruleX.itemset1.length != this.itemset1.length){
            return false;
        }
        if(ruleX.itemset2.length != this.itemset2.length){
            return false;
        }
        for(int i=0; i< itemset1.length; i++){
            if(this.itemset1[i] != ruleX.itemset1[i]){
                return false;
            }
        }
        for(int i=0; i< itemset2.length; i++){
            if(this.itemset2[i] != ruleX.itemset2[i]){
                return false;
            }
        }
        return true;
    }

    /*
     * 重写toString
     */
    @Override
    public String toString(){
        return toString(itemset1) +  " ==> " + toString(itemset2);
    }

    private String toString(Integer[] itemset) {
        StringBuilder temp = new StringBuilder();
        for(int item : itemset){
            temp.append(item + " ");
        }
        return temp.toString();
    }
}
