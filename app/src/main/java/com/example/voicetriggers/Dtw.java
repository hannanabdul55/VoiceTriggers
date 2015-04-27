package com.example.voicetriggers;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;

import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.FloatData;

/**
 * Created by swathi-veeradhi on 24/03/15.
 */

public class Dtw {
    // constructor to directly take float values
    public Dtw(LinkedList<FloatData> m1, LinkedList<FloatData> m2) {
        //TEMPORARY HACK
        LinkedList<float[]> m1_t = new LinkedList<float[]>();
        if (m1 == null) {
            Log.d("M1_NULL", "TRUE");
        }
        for (FloatData f : m1) {
            m1_t.add(f.getValues());
        }

        LinkedList<float[]> m2_t = new LinkedList<float[]>();
        for (FloatData f : m2) {
            m2_t.add(f.getValues());
        }
        mData1 = m1_t;
        mData2 = m2_t;
    }

    private LinkedList<float[]> mData1, mData2;
    private int tol_btw_indices = 15;
    private int tol_btw_consec = 10;
    private int lengthOfOutput = 0;

    //constructor to read from files
    public Dtw(String file1, String file2) {
        mData1 = getData(file1);
        System.out.println(file1 + ": " + mData1.size() + " rows");
        mData2 = getData(file2);
        System.out.println(file2 + ": " + mData2.size() + " rows");
        int len1 = mData1.size();
        int len2 = mData2.size();
        if (len1 < len2)
            lengthOfOutput = len1;
        else
            lengthOfOutput = len2;
    }

    private LinkedList<float[]> getData(String filename) {
        try {
            BufferedReader reader =
                    new BufferedReader(new FileReader(filename));
            LinkedList<float[]> ans = new LinkedList<float[]>();
            float[] dummy = new float[39];
            for (int i = 0; i < 39; i++) {
                dummy[i] = 0.0f;
            }

            ans.add(dummy);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] num = line.split(",");
                if (num == null || num.length == 1) {
                    break;
                }
                float[] data = new float[num.length];
                for (int i = 0; i < num.length; i++) {
                    data[i] = Float.parseFloat(num[i].trim());
                }
                ans.add(data);
            }
            return ans;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    double newPrintAnalysis() {
        int i, j = 1;
        float cost = 0.0f;
        int n = mData1.size();
        int m = mData2.size();
        System.out.println("Size1: " + n + "  Size2: " + m);
        float dtw[][] = new float[n + 1][m + 1];
        for (i = 1; i < n; i++)
            dtw[i][0] = 999;
        for (i = 1; i < m; i++)
            dtw[0][i] = 999;
        dtw[0][0] = 0;
        for (i = 1; i < n; i++) {
            for (j = 1; j < m; j++) {
                //System.out.println("i:"+i+"   j:"+j);
                cost = d(mData1.get(i), mData2.get(j));
                dtw[i][j] = cost + min(dtw[i - 1][j], dtw[i][j - 1], dtw[i - 1][j - 1]);
                System.out.println("i: " + i + "    j:" + j + "   val:" + dtw[i][j]);
            }
        }
        return (double) (dtw[n - 1][m - 1]) * 2 / (i + j);

    }

    float min(float a, float b, float c) {
        if (a < b && a < c) {
            return a;
        } else if (b < c && b < a) {
            return b;
        } else {
            return c;
        }
    }

    float d(float v1[], float v2[]) throws IllegalArgumentException {
        if (v1.length != v2.length) {
            throw new IllegalArgumentException();
        }
        float ans = 0.0f;
        for (int i = 0; i < v1.length; i++) {
            float diff = v1[i] - v2[i];
            ans += diff * diff;
        }
        return ans;
    }

	 /* public static void main(String [] args) {
    if(args.length != 2) {
      System.out.println("Please specify two text files as arguments");
      System.exit(1);
    }
    DTW A = new DTW(args[0], args[1]);
    System.out.println(A.DTWDistance());
  } */

}


	  
