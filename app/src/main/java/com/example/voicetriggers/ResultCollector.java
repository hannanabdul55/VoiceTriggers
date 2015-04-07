package com.example.voicetriggers;

import java.util.ArrayList;
import java.util.LinkedList;

import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.FloatData;

/**
 * Created by akanji1 on 24/03/15.
 */
public class ResultCollector {
    public static LinkedList<FloatData> list = new LinkedList<FloatData>();
    private static boolean lock = false;
    private static boolean hasNew = false;

    public static synchronized boolean CollectResult(FloatData d) {
        //do computation

        list.add(d);

        //release lock
        lock = false;
        //update new data flag
        hasNew = true;
        return false;
    }

    public static synchronized boolean hasNewResult() {
        return hasNew;
    }

    public static synchronized FloatData fetchNewResult() {
        hasNew = false;
        return list.get(list.size() - 1);

    }

    public static void resetData() {
        list = new LinkedList<FloatData>();
    }

    public static LinkedList<FloatData> getList() {
        return list;
    }
}
