package com.TopR.algo;

import java.util.LinkedList;
import java.util.List;

public class Transaction{
    private final List<Integer> items;

    public Transaction(int size){
        items = new LinkedList<Integer>();
    }

    public void addItem(Integer item){
        items.add(item);
    }

    public List<Integer> getItems() {
        return items;
    }
}
