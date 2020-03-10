package com.TopR.tools;

public class ArraysAlgos {

    public static boolean containsLEX(Integer itemset[], Integer item, int maxItemInArray) {
        if (item > maxItemInArray) {
            return false;
        }

        for (Integer itemI : itemset) {
            if (itemI.equals(item)) {
                // if yes return true
                return true;
            }
            else if (itemI > item) {
                return false;  // <-- xxxx
            }
        }

        return false;
    }

}
