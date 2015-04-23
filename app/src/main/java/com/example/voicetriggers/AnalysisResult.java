package com.example.voicetriggers;

import java.util.LinkedList;

import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.FloatData;

/**
 * Created by akanji1 on 23/04/15.
 */
public class AnalysisResult {
    String tag;
    LinkedList<FloatData> vectors;
    Double res;

    public AnalysisResult(String t, LinkedList<FloatData> v, Double r) {
        this.tag = t;
        this.vectors = v;
        this.res = r;
    }


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public LinkedList<FloatData> getVectors() {
        return vectors;
    }

    public void setVectors(LinkedList<FloatData> vectors) {
        this.vectors = vectors;
    }
}
