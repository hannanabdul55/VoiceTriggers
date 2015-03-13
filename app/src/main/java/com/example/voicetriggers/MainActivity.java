package com.example.voicetriggers;

import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.media.AudioManager;
import android.widget.Toast;

import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.Data;
import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.DataBlocker;
import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.DataEndSignal;
import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.DataProcessor;
import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.FloatData;
import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.FrontEnd;
import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.endpoint.NonSpeechDataFilter;
import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.endpoint.SpeechClassifier;
import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.endpoint.SpeechEndSignal;
import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.endpoint.SpeechMarker;
import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.endpoint.SpeechStartSignal;
import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.feature.DeltasFeatureExtractor;
import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.feature.LiveCMN;
import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.filter.Preemphasizer;
import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.frequencywarp.MelFrequencyFilterBank;
import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.transform.DiscreteFourierTransform;
import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.util.AudioFileDataSource;
import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.window.RaisedCosineWindower;
import SphinxDemo.sphinx4.edu.cmu.sphinx.util.props.ConfigurationManager;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        URL audioURl;
        AudioManager manager = (AudioManager) getSystemService(AUDIO_SERVICE);

        try {

            audioURl = new File("/sdcard/Download/hello.wav").toURI().toURL();
            Log.d("AUDIO_URL",audioURl.getFile());
            InputStream is = getResources().openRawResource(R.raw.hello);

            //InputStream stream = new FileInputStream("hello.txt");
            //Log.d("INPUT_STREAM_NULL", (is == null) ? "NULL" : "NOT_NULL");
            //InputStream i = audioURl.openStream();
            //Log.d("First Byte", (String) Character.toString((char) stream.read()));
            URL configURL = new File("/sdcard/Download/config.xml").toURI().toURL();
            Log.d("CONFIG_URL_NULL", (configURL==null)?"NULL":"NOT_NULL");

            ConfigurationManager cm = new ConfigurationManager(configURL);
            FrontEnd frontend = (FrontEnd)cm.lookup("epFrontEnd");
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
//            dataSource.setAudioFile(audioURl, null);
//            FrontEnd frontend = new FrontEnd(procs);
            AudioFileDataSource dataSource = (AudioFileDataSource)cm.lookup("audioFileDataSource");
            dataSource.setAudioFile(audioURl,null);
            frontend.initialize();
            DataProcessor last_d = frontend.getLastDataProcessor();
            //DataProcessor last_d = frontend.getLastDataProcessor();
            Data d = last_d.getData();
          Log.d("SPEECH_DATA",d.toString());
            //Toast.makeText(getBaseContext(), d.toString(), Toast.LENGTH_SHORT).show();
            FileWriter writer;
//            try {
                //writer = new FileWriter(new File("output-blind-1.txt"));

                while (!((d = last_d.getData()) instanceof DataEndSignal)) {
                    if (d instanceof FloatData)
                        Log.d("SPEECH_LOG", Arrays.toString(((FloatData) d).getValues()) + "\n");
                    else if (d instanceof SpeechStartSignal) {
                        Log.d("SPEECH_LOG", "Start of a speech!\n");
                    } else if (d instanceof SpeechEndSignal) {
                        Log.d("SPEECH_LOG", "End of a speech\n");
                    }
                }
                Toast.makeText(this,"Hello",Toast.LENGTH_SHORT).show();
                //writer.close();
//            } catch (MalformedURLException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                audioURl = null;
//            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
