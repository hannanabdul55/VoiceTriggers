package com.example.voicetriggers;

import java.util.HashMap;
import java.util.LinkedList;

import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.FloatData;

/**
 * Created by akanji1 on 07/04/15.
 */
public class VoiceAnalyzer {

    static HashMap<String, LinkedList<FloatData>> samples = new HashMap<String, LinkedList<FloatData>>();

    static AnalysisResult getActionForVoice(LinkedList<FloatData> query) {
        Double max_val = Double.MIN_VALUE;
        String selected = "";
        for (String t : samples.keySet()) {
            LinkedList<FloatData> samp = samples.get(t);

            analyze runner = new analyze(samp, query);
            Double temp = runner.newPrintAnalysis();
            if (temp > max_val) {
                max_val = temp;
                selected = t;
            }
//            return runner.newPrintAnalysis();
        }
        return new AnalysisResult(selected, samples.get(selected), max_val);
    }

    public static void addSample(String key, LinkedList<FloatData> d) {
        samples.put(key, d);
    }


}
