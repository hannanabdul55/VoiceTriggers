package com.example.voicetriggers;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

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
 * @author Abdul Hannan kanji
 *         To know more about the Activity class refer to
 * @see android.app.Activity
 */
public class MainActivity extends Activity {


    //Declare local methods used
    TextView tv;
    Button btn_choose;
    Button btn_record;
    Button btn_analyze;
    Intent chooseInputFileIntent = null;
    boolean isFileChosen = false;
    public static Context con;
    Button btn_run;
    boolean isRecognizerInit = false;
    //SPHINX specific variables
    DataProcessor last_d;
    AudioFileDataSource dataSource;
    AudioManager manager;
    URL audioURL;
    ConfigurationManager cm;
    FrontEnd frontend;
    URL configURL;
    MediaRecorder mediaRecorder;

    boolean speechStarted = false;
    boolean speechEnded = false;

    /**
     * @param savedInstanceState onCreate function
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initialize the Activity layout fields
        startActivity(new Intent(this, SpeechActivity.class));
        finish();

        initActivityParams();
        //Try Initializing the Sphinx parameters
        try {
            init_sphinx_params();
        } catch (IOException e) {
            Toast.makeText(this, "Unable to Initialize Sphinx parameters, try reinstalling the application", Toast.LENGTH_LONG).show();
            isRecognizerInit = false;
            Log.d("SPHINX_LOG", "PARAM_INIT_FAILURE");
            e.printStackTrace();
        }
        con = this;


    }

    @Override
    protected void onResume() {
        SharedPreferences prefs = (SharedPreferences) getSharedPreferences(Constant.PREF_RECORDER, MODE_PRIVATE);
        String s = "";
        if (prefs != null)
            s = prefs.getString(Constant.PREF_KEY_TMP_FILE, "");
        if (s != "") {
            if (tv != null) {
                tv.setText(tv.getText() + "\n" + "TAG: " + s);
            }
            try {
                audioURL = new File(s).toURI().toURL();
                init_sphinx_params();
            } catch (Exception e) {
                audioURL = null;
                Toast.makeText(this, "ERROR GETTING RECORDED FILE, ABORTING!", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            try {
                init_sphinx_params();
            } catch (IOException e) {
                Toast.makeText(this, "Error Initializing Sphinx parameters", Toast.LENGTH_SHORT).show();
                return;
            }
            frontend.initialize();
            ResultCollector.resetData();
            Data d;
            last_d = frontend.getLastDataProcessor();
            while (!((d = last_d.getData()) instanceof DataEndSignal)) {

                if (d instanceof FloatData) {
                    if (speechStarted && !speechEnded)
                        ResultCollector.CollectResult((FloatData) d);
                } else if (d instanceof SpeechStartSignal) {
                    Log.d("SPEECH_LOG", "Start of a speech!\n");
                    speechStarted = true;
                } else if (d instanceof SpeechEndSignal) {
                    Log.d("SPEECH_LOG", "End of a speech\n");
                    speechEnded = true;
                }
            }
            speechStarted = false;
            speechEnded = false;
            LinkedList<FloatData> l = ResultCollector.list;
            switch (Config.voice_method) {
                case Constant.METHOD_ADD: {
                    VoiceAnalyzer.samples.put(s, l);

                    break;
                }

                case Constant.METHOD_TEST: {
                    AnalysisResult res = null;
                    try {
                        res = new AnalyzeTask().execute(l).get();
                    } catch (InterruptedException e) {
                        Log.d("ERROR", "error in execution of task");
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        Log.d("ERROR", "error in execution of task");
                        e.printStackTrace();
                    }
                    executeActionForTag(res.tag);
                    Config.voice_method = Constant.METHOD_ADD;
                    break;
                }
                default: {

                    break;
                }
            }
        }
        super.onResume();
    }

    private void executeActionForTag(String tag) {
        //TODO Implement this action
        Toast.makeText(this, "ACTION TRIGGERED for tag: " + tag, Toast.LENGTH_SHORT).show();
    }

    public boolean start_recognition() {
        if (!isRecognizerInit) {
            Toast.makeText(this, "The recognizer failed to initialize. Please restart the app, if problem still persists, please reinstall the application", Toast.LENGTH_LONG).show();

            return false;
        } else if (!isFileChosen) {
            Toast.makeText(this, "Please choose an audio file and continue", Toast.LENGTH_LONG).show();
            return false;
        }
        //Start recognition after check for successful init of recognizer variables
        Data d;
        long time = System.currentTimeMillis();
        doRecognition();
        VoiceAnalyzer.samples.put("SAMPLE", ResultCollector.list);
        time = System.currentTimeMillis() - time;
        tv.setText("TIME TAKEN: " + time + "ms");

        return false;
    }

    void doRecognition() {

        ResultCollector.resetData();
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
        Log.d("SPEECH_SIZE", "" + s);
        speechStarted = false;
        speechEnded = false;
    }

    /**
     * Initialize the parameters used by the Activity class
     */
    public void initActivityParams() {
        manager = (AudioManager) getSystemService(AUDIO_SERVICE);
        tv = (TextView) findViewById(R.id.log);
        btn_choose = (Button) findViewById(R.id.button_choose);
        btn_run = (Button) findViewById(R.id.btn_run);
        btn_choose.setOnClickListener(new
                OnFileChooseListener());
        btn_run.setOnClickListener(new OnRunClickListener());
        btn_record = (Button) findViewById(R.id.record);
        btn_record.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Config.voice_method = Constant.METHOD_ADD;
                startActivity(new Intent(con, RecordAudioSample.class));
            }
        });
        btn_analyze = (Button) findViewById(R.id.analyze);
        btn_analyze.setOnClickListener(new OnAnalyzeClickListener());
    }

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    /**
     * @param requestCode the request code passed as argument when calling onActivityResult
     * @param resultCode  Result code for the request
     * @param data        The intent data in a bundle which consists if the resultant data
     * @see android.app.Activity for more info
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constant.RESULT_INPUT_FILE: {
                try {
                    if (data == null || data.getData() == null) {
                        throw new MalformedURLException();
                    }
                    audioURL = new File(data.getData().getPath()).toURI().toURL();
                    isFileChosen = true;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    audioURL = null;
                    Log.d("InputFileError", "Malformed URL Input file");
                    isFileChosen = false;
                } catch (Exception e) {
                    e.printStackTrace();
                    audioURL = null;
                    isFileChosen = false;
                }
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void record_audio() {

    }

//    /**
//     * Temporary function for doing some dummy stuff
//     */
//    private void helper() {
//        //            AudioFileDataSource dataSource = new AudioFileDataSource();
////            DataBlocker blocker = new DataBlocker();
////            SpeechClassifier sClassifier = new SpeechClassifier();
////            NonSpeechDataFilter nSpeech = new NonSpeechDataFilter();
////            SpeechMarker sMarker = new SpeechMarker();
////            Preemphasizer preemp = new Preemphasizer();
////            RaisedCosineWindower windower = new RaisedCosineWindower();
////            DiscreteFourierTransform fft = new DiscreteFourierTransform();
////            MelFrequencyFilterBank melFreq = new MelFrequencyFilterBank();
////            DiscreteFourierTransform dct = new DiscreteFourierTransform();
////            LiveCMN liveCMN = new LiveCMN();
////            DeltasFeatureExtractor featext = new DeltasFeatureExtractor();
////            List<DataProcessor> procs = new ArrayList<DataProcessor>();
////            procs.add(dataSource);
////            procs.add(sClassifier);
////            procs.add(nSpeech);
////            procs.add(sMarker);
////            procs.add(preemp);
////            procs.add(windower);
////            procs.add(fft);
////            procs.add(melFreq);
////            procs.add(dct);
////            procs.add(liveCMN);
////            procs.add(featext);
////            dataSource.setAudioFile(audioURL, null);
////            FrontEnd frontend = new FrontEnd(procs);
//
//    }


    class OnFileChooseListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            chooseInputFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
            chooseInputFileIntent.setType("audio/x-wav");
            startActivityForResult(chooseInputFileIntent, Constant.RESULT_INPUT_FILE);
            Log.d("FILE_CHOOSE_CLICK", "File Choose Intent shown");
        }
    }

    class OnRunClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Log.d("SPHINX_LOG", "RECOGNIZER_STARTED");
            Config.voice_method = Constant.METHOD_TEST;
            startActivity(new Intent(con, RecordAudioSample.class));
        }
    }


    class OnAnalyzeClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Log.d("SPHINX_LOG", "RECOGNIZER_STARTED");
            SharedPreferences prefs = (SharedPreferences) getSharedPreferences(Constant.PREF_RECORDER, MODE_PRIVATE);
            String s = prefs.getString(Constant.PREF_KEY_TMP_FILE, "");
            if (s != "") {
                try {
                    audioURL = new File(s).toURI().toURL();
                    init_sphinx_params();
                } catch (Exception e) {
                    audioURL = null;
                    Toast.makeText(con, "ERROR GETTING RECORDED FILE, ABORTING!", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                try {
                    init_sphinx_params();
                } catch (IOException e) {
                    Toast.makeText(con, "Error Initializing Sphinx parameters", Toast.LENGTH_SHORT).show();
                    return;
                }
                frontend.initialize();
                ResultCollector.resetData();
                Data d;
                last_d = frontend.getLastDataProcessor();
                LinkedList<FloatData> li = new LinkedList<FloatData>();
                while (!((d = last_d.getData()) instanceof DataEndSignal)) {

                    if (d instanceof FloatData) {

                        if (speechStarted && !speechEnded)
                            li.add((FloatData) d);

                    } else if (d instanceof SpeechStartSignal) {
                        Log.d("SPEECH_LOG", "Start of a speech!\n");
                        speechStarted = true;

                    } else if (d instanceof SpeechEndSignal) {
                        Log.d("SPEECH_LOG", "End of a speech\n");
                        speechEnded = true;
                    }
                }
                speechStarted = false;
                speechEnded = false;
                Log.d("LIST_LENGTH", "" + li.size());
                new AnalyzeTask().execute(li);

            }
        }
    }


    class AnalyzeTask extends AsyncTask<LinkedList<FloatData>, Void, AnalysisResult> {

        ProgressDialog progressDialog;

        @Override
        protected AnalysisResult doInBackground(LinkedList<FloatData>... params) {
            LinkedList<FloatData> list = params[0];
            AnalysisResult r = VoiceAnalyzer.getActionForVoice(list);
            return r;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(con);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Analyzing Voice Sample");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(AnalysisResult aDouble) {
            progressDialog.dismiss();
            Toast.makeText(getBaseContext(), "Confidence: " + aDouble.res, Toast.LENGTH_LONG).show();
            if (con != null) {
                Dialog d = new Dialog(con);
                d.setCancelable(true);
                d.setTitle("The max Match percentage is " + aDouble.res + "%! with tag: " + aDouble.tag);
                super.onPostExecute(aDouble);
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}

