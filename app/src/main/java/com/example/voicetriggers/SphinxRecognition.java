package com.example.voicetriggers;

import android.media.AudioManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.LinkedList;

import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.Data;
import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.DataEndSignal;
import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.DataProcessor;
import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.FloatData;
import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.FrontEnd;
import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.endpoint.SpeechEndSignal;
import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.endpoint.SpeechStartSignal;
import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.util.AudioFileDataSource;
import SphinxDemo.sphinx4.edu.cmu.sphinx.util.props.ConfigurationManager;

/**
 * Created by akanji1 on 25/04/15.
 */
public class SphinxRecognition {

    private boolean isRecognizerInit;
    DataProcessor last_d;
    AudioFileDataSource dataSource;
    AudioManager manager;
    URL audioURL;
    ConfigurationManager cm;
    FrontEnd frontend;
    URL configURL;
    boolean speechStarted = false;
    boolean speechEnded = false;

    /**
     * Initialize all the Sphinx specific parameters which is used later
     */
    public void init_sphinx_params() throws IOException {
        if (audioURL == null)
            audioURL = new File("/sdcard/Download/hello.wav").toURI().toURL();

        Log.d("AUDIO_URL", audioURL.getFile());

        configURL = new File("/sdcard/Download/config.xml").toURI().toURL();

        cm = new ConfigurationManager(configURL);
        frontend = (FrontEnd) cm.lookup("epFrontEnd");
        dataSource = (AudioFileDataSource) cm.lookup("audioFileDataSource");
        dataSource.setAudioFile(audioURL, null);
        isRecognizerInit = true;

    }

    public boolean start_recognition() {
        //Start recognition after check for successful init of recognizer variables
        Data d;
        long time = System.currentTimeMillis();
        frontend.initialize();
        last_d = frontend.getLastDataProcessor();
        int s = 0;
        while (!((d = last_d.getData()) instanceof DataEndSignal)) {

            if (d instanceof FloatData) {
                if (speechStarted && !speechEnded) {
                    ResultCollector.CollectResult((FloatData) d);
                    s++;
                }
            } else if (d instanceof SpeechStartSignal) {
                Log.d("SPEECH_LOG", "Start of a speech!\n");
                speechStarted = true;

            } else if (d instanceof SpeechEndSignal) {
                Log.d("SPEECH_LOG", "End of a speech\n");
                speechEnded = true;
            }
        }
        Log.d("SPEECH_SIZE", "" + s);
        speechStarted = false;
        speechEnded = false;
        VoiceAnalyzer.samples.put("SAMPLE", ResultCollector.list);


        time = System.currentTimeMillis() - time;

        return false;
    }

    public LinkedList<FloatData> getVectors(URI file) {
        ResultCollector.resetData();
        try {
            audioURL = file.toURL();
            init_sphinx_params();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Data d;
        frontend.initialize();
        last_d = frontend.getLastDataProcessor();
        int s = 0;
        while (!((d = last_d.getData()) instanceof DataEndSignal)) {

            if (d instanceof FloatData) {
                if (speechStarted && !speechEnded) {
                    ResultCollector.CollectResult((FloatData) d);
                    s++;
                }
            } else if (d instanceof SpeechStartSignal) {
                Log.d("SPEECH_LOG", "Start of a speech!\n");
                speechStarted = true;

            } else if (d instanceof SpeechEndSignal) {
                Log.d("SPEECH_LOG", "End of a speech\n");
                speechEnded = true;
            }
        }
        return ResultCollector.getList();
    }
}
