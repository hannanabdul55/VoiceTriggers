package com.example.voicetriggers;

import android.util.Log;

import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.FloatData;

/**
 * Created by akanji1 on 24/03/15.
 */
public class RecogntionRunner implements Runnable {
    @Override
    public void run() {
        while (true) {
            if (ResultCollector.hasNewResult()) {
                FloatData d = ResultCollector.fetchNewResult();
                Log.d("NEW_RESULT", d.toString());
            }
        }
    }
}
