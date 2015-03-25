package com.example.voicetriggers;

import java.util.ArrayList;

import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.FloatData;

/**
 * Created by akanji1 on 24/03/15.
 */
public class ResultCollector {
    public static ArrayList<FloatData> list = new ArrayList<FloatData>();
    private static boolean lock = false;
    private static boolean hasNew = false;

    public static boolean CollectResult(FloatData d) {
        //do computation

        list.add(d);

        //release lock
        lock = false;
        //update new data flag
        hasNew = true;
        return false;
    }

    public static boolean hasNewResult() {
        return hasNew;
    }

    public static FloatData fetchNewResult() {
        hasNew = false;
        return list.get(list.size() - 1);

    }
}
