package com.example.voicetriggers;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.FloatData;

/**
 * Created by akanji1 on 07/04/15.
 */
public class VoiceAnalyzer {

    static HashMap<String, LinkedList<FloatData>> samples = new HashMap<String, LinkedList<FloatData>>();

    public static AnalysisResult getActionForVoice(LinkedList<FloatData> query) {
        Double max_val = Double.MAX_VALUE;
        String selected = "";
        for (String t : samples.keySet()) {
            LinkedList<FloatData> samp = samples.get(t);
            Dtw runner = new Dtw(samp, query);
            Double temp = runner.newPrintAnalysis();
            if (temp < max_val) {
                max_val = temp;
                selected = t;
            }
        }
        return new AnalysisResult(selected, samples.get(selected), max_val);
    }

    public VoiceAnalyzer(Set<String> id_list, File fileDir) {
        File[] files = fileDir.listFiles();
        for (File i : files) {
            String id = i.getName().substring(0, i.getName().lastIndexOf('.'));
            if (id_list.contains(id)) {
                samples.put(id, FileDataController.readFromFile(id));
            }
        }
    }

    public static void addSample(String key, LinkedList<FloatData> d) {
        samples.put(key, d);
    }


}
