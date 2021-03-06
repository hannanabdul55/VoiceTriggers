package com.example.voicetriggers;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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


public class SpeechActivity extends Activity {

    //Activity View Objects
    Button btn_test;
    Button btn_add;
    ListView list;
    ApplicationListAdapter adapter;
    ResolveInfo info;
    String ID = "";
    EditText txt_id;

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

    //Action Map
    Map<String, ResolveInfo> action_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);
        startActivity(new Intent(getBaseContext(), NumberActivity.class));
        con = this;
        action_map = new HashMap<String, ResolveInfo>();
        initActivityParams();
        try {
            init_sphinx_params();
        } catch (IOException e) {
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
        switch (Config.voice_method) {
            case Constant.METHOD_ADD: {
                ID = txt_id.getText().toString();
                if (ID.length() < 1) {
                    Toast.makeText(getBaseContext(), "ID is not entered", Toast.LENGTH_SHORT).show();
                    Config.voice_method = -1;
                }

            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Config.voice_method = -1;

    }

    private void initActivityParams() {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        list = (ListView) findViewById(R.id.app_list);
        List pkgAppsList = con.getPackageManager().queryIntentActivities(mainIntent, 0);
        if (pkgAppsList == null || pkgAppsList.size() < 1) {
            Toast.makeText(getBaseContext(), "No applications found or error in loading application list", Toast.LENGTH_SHORT).show();

        } else {
            adapter = new ApplicationListAdapter(pkgAppsList, con);
            info = adapter.getItem(0);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //startApp(adapter.getItem(position));
                    Log.d("ITEM_CLICK", "Item " + position + " clicked");
                    info = adapter.getItem(position);
                }
            });
        }
        btn_add = (Button) findViewById(R.id.btn_add_sample);
        btn_test = (Button) findViewById(R.id.test_button);
        txt_id = (EditText) findViewById(R.id.txt_id);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.voice_method = Constant.METHOD_ADD;
                if (txt_id.getText().toString().length() < 1) {
                    Toast.makeText(getBaseContext(), "Please do give a unique ID for the application", Toast.LENGTH_SHORT).show();
                    Config.voice_method = -1;
                } else {
                    startActivityForResult(new Intent(con, RecordAudioSample.class), Constant.METHOD_ADD);
                    //startActivity(new Intent(con, RecordAudioSample.class));
                }

            }
        });
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.voice_method = Constant.METHOD_TEST;
                startActivityForResult(new Intent(con, RecordAudioSample.class), Constant.METHOD_TEST);

            }
        });
    }

    public void startApp(ResolveInfo item) {
        ActivityInfo activity = item.activityInfo;
        ComponentName name = new ComponentName(activity.applicationInfo.packageName,
                activity.name);
        Intent i = new Intent(Intent.ACTION_MAIN);

        i.addCategory(Intent.CATEGORY_LAUNCHER);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        i.setComponent(name);

        startActivity(i);
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
                    VoiceAnalyzer.samples.put(ID, ResultCollector.list);
                    Log.d("ANALYSIS_RESULT", ID);
                    Set<String> list = samples.getStringSet(Constant.PRES_SAMPLES_KEY, new HashSet<String>());
                    list.add(ID);
                    samples_editor.putStringSet(Constant.PRES_SAMPLES_KEY, list);
                    action_map.put(ID, info);
                    Toast.makeText(getBaseContext(), "Added trigger to hashmap!", Toast.LENGTH_SHORT).show();
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
                    if ((aDouble.res) > 50.0 && action_map.containsKey(aDouble.tag)) {
                        startApp(action_map.get(aDouble.tag));
                        Toast.makeText(getBaseContext(), "Action " + aDouble.tag + " triggered", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getBaseContext(), "No Trigger found!", Toast.LENGTH_SHORT).show();
                    }
                    Log.d("FILE_DELETE", "Deleted: " + audioURL.getFile());
                    new File(audioURL.getPath()).delete();
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
