package com.TopR.algo;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Database {

    //最大项
    public int maxItem = 0;

    //总项集数
    public int tidsCount =0;

    public int getMaxItem() {
        return maxItem;
    }

    public void setMaxItem(int maxItem) {
        this.maxItem = maxItem;
    }

    public int getTidsCount() {
        return tidsCount;
    }

    public void setTidsCount(int tidsCount) {
        this.tidsCount = tidsCount;
    }

    //存储数据集中的每一行数据的表
    private final List<Transaction> transactions = new ArrayList<Transaction>();

    //读取文件
    public void loadFile(String path) throws IOException {
        BufferedReader  myInput = null;
        try {
            FileInputStream fin = new FileInputStream(new File(path));
            myInput = new BufferedReader(new InputStreamReader(fin));
            String thisLine;
            while ((thisLine = myInput.readLine()) != null) {
                if (thisLine.isEmpty() == true ||
                        thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%'
                        || thisLine.charAt(0) == '@') {
                    continue;
                }
                addTransaction(thisLine.split(" "));
                //System.out.println("  ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(myInput != null){
                myInput.close();
            }
        }
    }

    /*
     * 循环读取一行存储并按字典顺序排序，每一行为一个transaction，数组+链表形式存储
     */
    public void addTransaction(String itemsString[]){
        Transaction transaction = new Transaction(itemsString.length);
        for(String itemString : itemsString){
            if("".equals(itemString)){
                continue;
            }
            int item = Integer.parseInt(itemString);

            if(item >= maxItem){
                maxItem = item;
            }
          // System.out.println(item);
            transaction.addItem(item);
        }
        tidsCount++;
        transactions.add(transaction);

        Collections.sort(transaction.getItems(), new Comparator<Integer>(){
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2-o1;
            }});
    }

    /*
     * 事务集总个数（链表长度）。
     */
    public int size(){
        return transactions.size();
    }

    /*
     * getter
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }

//	public int checkDatabaseSize(String path) throws IOException {
//		int databaseSize =0;
//		String thisLine;
//		BufferedReader myInput = null;
//		try {
//			FileInputStream fin = new FileInputStream(new File(path));
//			myInput = new BufferedReader(new InputStreamReader(fin));
//			while ((thisLine = myInput.readLine()) != null) {
//				databaseSize++;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally {
//			if(myInput != null){
//				myInput.close();
//			}
//	    }
//		return databaseSize;
//	}
}
