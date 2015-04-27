package com.example.voicetriggers;

/**
 * Created by akanji1 on 21/03/15.
 *
 * @author akanji1
 *         All Constants used in the Project go into this clas and hereby should be referenced
 *         for all constrants used globally.
 *         P.S. Please write comments for every entry into this file
 */
public class Constant {
    public static final int RESULT_INPUT_FILE = 1;


    public static int SampleRate = 44100;
    public static boolean BigEndian = false;
    public static int bytesPerVal = 4;
    public static boolean signedData = false;

    public static String tempOutFile = "t.wav";

    public static String PREF_RECORDER = "RECORDER";
    public static String PREF_KEY_TMP_FILE = "TEMP_FILE";

    public static String PREF_VOICE_LIST = "PREF_VOICE_LIST";

    public static String PREF_SAMPLES = "VOICE_SAMPLES";
    public static String PRES_SAMPLES_KEY = "VOICE_SAMPLES_KEY";

    public static final int METHOD_ADD = 0;
    public static final int METHOD_TEST = 1;
}
