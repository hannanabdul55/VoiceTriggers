package com.example.voicetriggers;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.ListIterator;

import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.FloatData;

public class analyze {
    private LinkedList<float[]> mData1 = new LinkedList<float[]>(), mData2 = new LinkedList<float[]>();
    private int tol_btw_indices = 15;
    private int tol_btw_consec = 10;
    private int lengthOfOutput = 0;

    // constructor to read from files
    public analyze(String file1, String file2) {
        mData1 = getData(file1);
//        System.out.println(file1 + ": " + mData1.size() + " rows");
        mData2 = getData(file2);
//        System.out.println(file2 + ": " + mData2.size() + " rows");
        int len1 = mData1.size();
        int len2 = mData2.size();
        if (len1 < len2)
            lengthOfOutput = len2;
        else
            lengthOfOutput = len1;
    }

    // constructor to directly take float values
    public analyze(LinkedList<FloatData> m1, LinkedList<FloatData> m2) {

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
        int len1 = mData1.size();
        int len2 = mData2.size();
        if (len1 < len2)
            lengthOfOutput = len2;
        else
            lengthOfOutput = len1;
    }

    public double newPrintAnalysis() {
        ListIterator<float[]> it = mData1.listIterator(0);
        LinkedList<Integer> ans = new LinkedList<Integer>();
        int i = 0;
        int total = 0;
        int present = -1;
        int counter = -1;
        int prev = -1;
        int correct = 0;
        int setVal = -1;
        int mindiff = 999;
        while (it.hasNext()) {
            boolean isValSet = false;
            counter++;
            int prePlusOne = -1;
            float[] u = it.next();
            ListIterator<Integer> li = closest(u, mData2, 1.0f).listIterator(0);
            while (li.hasNext()) {
                setVal = li.next();
//                System.out.print(setVal + "  ");
            }
            total++;
//            System.out.println(" ] ");
            if (Math.abs(counter - setVal) < tol_btw_indices
                    || Math.abs(prev - setVal) < tol_btw_consec) {
                prev = setVal;
                correct += 1;
//                System.out.println("correct");
            } else {
//                System.out.println("XXX");
            }
            i++;
        }

        double result = 0.0;

        result = (100.0 * correct / lengthOfOutput);
//            System.out.println("Total Correct: "
//                    + result + " %");
//
//            System.out.println("Correct: " + correct + "\nTotal: " + total
//                    + "\nLength of output: " + lengthOfOutput);

        return result;
    }

    // old method to find the closest distance vector

    private LinkedList<Integer> closest(float[] u, LinkedList<float[]> data,
                                        float alpha) {
        float bestDistance = Float.MAX_VALUE;
        float[] v;
        ListIterator<float[]> it = data.listIterator(0);
        while (it.hasNext()) {
            v = it.next();
            float temp = d_square(u, v);
            if (temp < bestDistance) {
                bestDistance = temp;
            }
        }
//        System.out.print("(" + bestDistance + ") ");
        LinkedList<Integer> ans = new LinkedList<Integer>();
        it = data.listIterator(0);
        int i = 0;
        while (it.hasNext()) {
            v = it.next();
            if (d_square(u, v) <= alpha * bestDistance) {
                ans.addLast(Integer.valueOf(i));
            }
            i++;
        }
        return ans;
    }

    // old method to find the distance between two vectors

    private float d_square(float[] v1, float[] v2)
            throws IllegalArgumentException {
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

    private LinkedList<float[]> getData(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            LinkedList<float[]> ans = new LinkedList<float[]>();
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
                ans.addLast(data);
            }
            return ans;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}