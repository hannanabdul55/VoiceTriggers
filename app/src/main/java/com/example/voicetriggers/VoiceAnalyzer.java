package com.example.voicetriggers;

import java.util.HashMap;
import java.util.LinkedList;

import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.FloatData;

/**
 * Created by akanji1 on 07/04/15.
 */
public class VoiceAnalyzer {

    static HashMap<String, LinkedList<FloatData>> samples = new HashMap<String, LinkedList<FloatData>>();

    static double getActionForVoice(LinkedList<FloatData> query) {
        for (String t : samples.keySet()) {
            LinkedList<FloatData> samp = samples.get(t);

            analyze runner = new analyze(samp, query);
            return runner.newPrintAnalysis();

        }
        return 0;
    }

    public static void addSample(String key, LinkedList<FloatData> d) {
        samples.put(key, d);
    }


}
