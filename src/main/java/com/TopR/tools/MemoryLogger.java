package com.TopR.tools;

 //日志文件，输出内存占用情况，主要用于输出程序额运行开始时间，结束时间
public class MemoryLogger {

    private static MemoryLogger instance = new MemoryLogger();


    private double maxMemory = 0;
    //返回当前 的 MemoryLogger 实例
    public static MemoryLogger getInstance(){
        return instance;
    }
    //返回最大的内存
    public double getMaxMemory() {
        return maxMemory;
    }

    /**
     * 重置记录的最大内存量。
     */
    public void reset(){
        maxMemory = 0;
    }

    /**
     *检查内存使用情况，如果大于先前的量，则记录下来。并记录
     */
    public void checkMemory() {
        double currentMemory = (Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory())
                / 1024d / 1024d;
        if (currentMemory > maxMemory) {
            maxMemory = currentMemory;
        }
    }
}
