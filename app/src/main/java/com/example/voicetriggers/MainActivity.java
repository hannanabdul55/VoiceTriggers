package com.example.voicetriggers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;

import android.media.AudioManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    Intent chooseInputFileIntent = null;
    boolean isFileChosen = false;
    public static Context con;


    //SPHINX specific variables
    DataProcessor last_d;
    AudioFileDataSource dataSource;
    AudioManager manager;
    URL audioURL;
    ConfigurationManager cm;
    FrontEnd frontend;
    URL configURL;


    /**
     * @param savedInstanceState onCreate function
     * @throws java.io.IOException when any of the files (Config file or Input file is not present
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        initActivityparams();

        try {
            init_sphinx_params();
        } catch (IOException e) {
            Toast.makeText(this, "Unable to Initialize Sphinx parameters, try reinstalling the application", Toast.LENGTH_LONG).show();
            ;
            Log.d("SPHINX_LOG", "PARAM_INIT_FAILURE");
            e.printStackTrace();
        }
        con = this;
        Data d = last_d.getData();
        Log.d("SPEECH_DATA", d.toString());
        String str = "";
        String tmp = "";
        FileWriter writer;
        long time = System.currentTimeMillis();
        frontend.initialize();
        last_d = frontend.getLastDataProcessor();
        while (!((d = last_d.getData()) instanceof DataEndSignal)) {

            if (d instanceof FloatData) {
                tmp = Arrays.toString(((FloatData) d).getValues()) + "\n";
                //Log.d("SPEECH_LOG", tmp);
                //str.append(Arrays.toString(((FloatData) d).getValues()) + "\n");
                //str += tmp;
            } else if (d instanceof SpeechStartSignal) {
                Log.d("SPEECH_LOG", "Start of a speech!\n");
                //str +="Start of a speech!\n";
                //str.append("Start of a speech!\n");

            } else if (d instanceof SpeechEndSignal) {
                Log.d("SPEECH_LOG", "End of a speech\n");
                //str+="Start of a speech!\n";
                //str.append("End of a speech!\n");
            }
        }
        time = System.currentTimeMillis() - time;
        tv.setText("TIME TAKEN: " + time + "ms");
        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();

    }

    public boolean start_recognition() {

        return false;
    }

    /**
     * Initialize the parameters used by the Activity class
     */
    public void initActivityparams() {
        manager = (AudioManager) getSystemService(AUDIO_SERVICE);
        tv = (TextView) findViewById(R.id.log);
        btn_choose = (Button) findViewById(R.id.button_choose);
    }


    /**
     * Initialize all the Sphinx specific parameters which is used later
     */
    public void init_sphinx_params() throws IOException {
        if (audioURL == null)
            audioURL = new File("/sdcard/Download/hello.wav").toURI().toURL();

        Log.d("AUDIO_URL", audioURL.getFile());
        InputStream is = getResources().openRawResource(R.raw.hello);

        configURL = new File("/sdcard/Download/config.xml").toURI().toURL();
        Log.d("CONFIG_URL_NULL", (configURL == null) ? "NULL" : "NOT_NULL");

        cm = new ConfigurationManager(configURL);
        frontend = (FrontEnd) cm.lookup("epFrontEnd");
        dataSource = (AudioFileDataSource) cm.lookup("audioFileDataSource");
        dataSource.setAudioFile(audioURL, null);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    /**
     * @param requestCode
     * @param resultCode
     * @param data
     * @see android.app.Activity for more info
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constant.RESULT_INPUT_FILE: {
                try {
                    audioURL = new File(data.getData().getPath()).toURI().toURL();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    audioURL = null;
                    Log.d("InputFileError", "Malformed URL Input file");
                    isFileChosen = true;
                }
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Temporary function for doing some dummy stuff
     */
    private void helper() {
        //            AudioFileDataSource dataSource = new AudioFileDataSource();
//            DataBlocker blocker = new DataBlocker();
//            SpeechClassifier sClassifier = new SpeechClassifier();
//            NonSpeechDataFilter nSpeech = new NonSpeechDataFilter();
//            SpeechMarker sMarker = new SpeechMarker();
//            Preemphasizer preemp = new Preemphasizer();
//            RaisedCosineWindower windower = new RaisedCosineWindower();
//            DiscreteFourierTransform fft = new DiscreteFourierTransform();
//            MelFrequencyFilterBank melFreq = new MelFrequencyFilterBank();
//            DiscreteFourierTransform dct = new DiscreteFourierTransform();
//            LiveCMN liveCMN = new LiveCMN();
//            DeltasFeatureExtractor featext = new DeltasFeatureExtractor();
//            List<DataProcessor> procs = new ArrayList<DataProcessor>();
//            procs.add(dataSource);
//            procs.add(sClassifier);
//            procs.add(nSpeech);
//            procs.add(sMarker);
//            procs.add(preemp);
//            procs.add(windower);
//            procs.add(fft);
//            procs.add(melFreq);
//            procs.add(dct);
//            procs.add(liveCMN);
//            procs.add(featext);
//            dataSource.setAudioFile(audioURL, null);
//            FrontEnd frontend = new FrontEnd(procs);

    }


    class OnFileChooseListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            chooseInputFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
            chooseInputFileIntent.setType("audio.x-wav");
            startActivityForResult(chooseInputFileIntent, Constant.RESULT_INPUT_FILE);
            Log.d("FILE_CHOOSE_CLICK", "File Choose Intent shown");
        }
    }
}

