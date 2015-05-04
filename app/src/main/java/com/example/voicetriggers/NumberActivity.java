package com.example.voicetriggers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
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


public class NumberActivity extends Activity {

    //Activity View Objects
    Button btn_audio_add;
    Button btn_audio_test;
    ListView list;
    int cur_state = -1;
    TextView tv_num;

    //SPHINX specific variables
    DataProcessor last_d;
    Context con;
    AudioFileDataSource dataSource;
    URL audioURL;
    ConfigurationManager cm;
    FrontEnd frontend;
    URL configURL;
    boolean speechStarted = false;
    boolean speechEnded = false;
    boolean isRecognizerInit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numbers);
        con = this;
        initActivityParams();
        try {
            init_sphinx_params();
        } catch (IOException e) {
            Toast.makeText(this, "Unable to Initialize Sphinx parameters, try reinstalling the application", Toast.LENGTH_LONG).show();
            isRecognizerInit = false;
            Log.d("SPHINX_LOG", "PARAM_INIT_FAILURE");
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(this, "Unable to Initialize Sphinx parameters, try reinstalling the application", Toast.LENGTH_LONG).show();
            isRecognizerInit = false;
            Log.d("SPHINX_LOG", "PARAM_INIT_FAILURE");
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Config.voice_method = -1;

    }

    private void initActivityParams() {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        btn_audio_add = (Button) findViewById(R.id.btn_number_record);
        btn_audio_test = (Button) findViewById(R.id.btn_test_number);
        tv_num = (TextView) findViewById(R.id.number);
        btn_audio_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(con, RecordAudioSample.class), Constant.METHOD_ADD);

            }
        });
        btn_audio_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.voice_method = Constant.METHOD_TEST;
                startActivityForResult(new Intent(con, RecordAudioSample.class), Constant.METHOD_TEST);

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_speech, menu);
        return true;
    }

    /**
     * Perform Voice recognition and collects the vectors into the ResultCollector class.
     */
    void doRecognition() {
        try {
            init_sphinx_params();
        } catch (IOException e) {
            Log.d("IOEXC", "In doRecognition");
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("EXC", "In doRecognition");
            e.printStackTrace();
        }
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constant.METHOD_ADD: {
                SharedPreferences samples = getSharedPreferences(Constant.PREF_SAMPLES, MODE_PRIVATE);
                SharedPreferences.Editor samples_editor = samples.edit();
                String s = "";
                s = data.getStringExtra(Constant.PREF_KEY_TMP_FILE);
                try {
                    audioURL = new File(s).toURI().toURL();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Toast.makeText(con, "Malformed URL", Toast.LENGTH_SHORT).show();
                    return;
                }
                doRecognition();
                try {
                    cur_state++;
                    String ID = cur_state + "";

                    VoiceAnalyzer.samples.put(ID, ResultCollector.list);
                    Set<String> list = samples.getStringSet(Constant.PRES_SAMPLES_KEY, new HashSet<String>());
                    list.add(ID);
                    samples_editor.putStringSet(Constant.PRES_SAMPLES_KEY, list);
                    tv_num.setText((cur_state + 1) + "");
                    if (cur_state == 9) {
                        btn_audio_test.setEnabled(true);
                    }
                    Log.d("ACTION", ID);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), "Error on saving sample", Toast.LENGTH_SHORT).show();
                    Config.voice_method = -1;
                } finally {
                    if (samples_editor.commit())
                        Toast.makeText(getBaseContext(), "Successfully saved sample!", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getBaseContext(), "Error while adding sample to database", Toast.LENGTH_LONG).show();
                    Config.voice_method = -1;
                }
                break;
            }
            case Constant.METHOD_TEST: {
                String s = "";
                s = data.getStringExtra(Constant.PREF_KEY_TMP_FILE);
                try {
                    audioURL = new File(s).toURI().toURL();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Toast.makeText(con, "Malformed URL", Toast.LENGTH_SHORT).show();
                    return;
                }
                doRecognition();
                try {
                    AnalysisResult res = new AnalyzeTask().execute(ResultCollector.getList()).get();
                    Log.d("ANALYSIS_RESULT", res.tag);

                    Toast.makeText(getBaseContext(), "Testing", Toast.LENGTH_SHORT).show();
                } catch (InterruptedException e) {
                    Toast.makeText(getBaseContext(), "Error while adding sample to database", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    Toast.makeText(getBaseContext(), "Error while adding sample to database", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } finally {

                    Config.voice_method = -1;
                }
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * Initialize all the Sphinx specific parameters which is used later
     */
    public void init_sphinx_params() throws IOException, Exception {
        if (audioURL == null)
            audioURL = new File("/sdcard/Download/hello.wav").toURI().toURL();
        InputStream inputStream = getResources().openRawResource(R.raw.config);
        Log.d("AUDIO_URL", audioURL.getFile());
        String s = "";
        OutputStream outputStream = new FileOutputStream(new File(getFilesDir().getPath() + "/config.xml"));
        int read = 0;
        byte[] bytes = new byte[1024];

        while ((read = inputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, read);
        }
        inputStream.close();
        outputStream.close();
        configURL = new File(getFilesDir().getPath() + "/config.xml").toURI().toURL();

        cm = new ConfigurationManager(configURL);
        frontend = (FrontEnd) cm.lookup("epFrontEnd");
        dataSource = (AudioFileDataSource) cm.lookup("audioFileDataSource");
        dataSource.setAudioFile(audioURL, null);
        isRecognizerInit = true;

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
            try {
                if (con != null) {
                    Dialog d = new Dialog(con);
                    d.setCancelable(true);
                    d.setTitle("The max Match percentage is " + aDouble.res + "%! with tag: " + aDouble.tag);
                    if ((aDouble.res) < 100) {
                        new AlertDialog.Builder(con)
                                .setMessage("The Number mapped to the voice is " + aDouble.tag)
                                .setTitle("Trigger")
                                .setNeutralButton("Done", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .create()
                                .show();
                        Toast.makeText(getBaseContext(), "Action " + aDouble.tag + " triggered", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getBaseContext(), "No Trigger found!", Toast.LENGTH_SHORT).show();
                    }
                    Log.d("FILE_DELETE", "Deleted: " + audioURL.getFile());
                    try {
                        new File(audioURL.getPath()).delete();
                    } catch (Exception e) {
                        Toast.makeText(con, "Error while deleting file", Toast.LENGTH_SHORT).show();
                    }
                    super.onPostExecute(aDouble);
                }
            } catch (Exception e) {
                Log.e("ERROR ON TASK", e.getLocalizedMessage());
                Toast.makeText(getBaseContext(), "Error on running the analyzer task", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
