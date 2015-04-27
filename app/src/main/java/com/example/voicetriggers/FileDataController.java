package com.example.voicetriggers;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.FloatData;
/* IMPORT FLOAT DATA HERE*/

public class FileDataController {
    /*public static void main(String args[])
    {

        FileDataController fd= new FileDataController();
        float values[]=new float[39];
        for(int i=1;i<39;i++)
            values[i]=(float)values[i-1]+0.002f;
        FloatData f1= new FloatData(values,3,3,3);
        FloatData f2= new FloatData(values,2,2,2);
        FloatData f3= new FloatData(values,4,4,4);
        ArrayList<FloatData> list = new ArrayList<FloatData>();
        list.add(f1);
        list.add(f2);
        list.add(f3);
        fd.writeToFile(list,"second");

        ArrayList<FloatData> vec1= fd.readFromFile("first");
        if(vec1!=null)
        {
            for(FloatData d : vec1)
            {
                System.out.println(d.getSampleRate());
            }
        }
        else
        {
            System.out.println("Could not be found");
        }

    }*/

    public static void writeToFile(LinkedList<FloatData> vector, String id) {
        try {
            FileOutputStream fileOut =
                    new FileOutputStream("vectorDB.ser", true);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(vector);
            out.close();
            fileOut.close();
            System.out.printf("Saved");
        } catch (IOException i) {
            System.out.println("IOException");
            i.printStackTrace();
        }


    }

    public static LinkedList<FloatData> readFromFile(String id) {
        LinkedList<FloatData> toReturn = new LinkedList<FloatData>();
        try {
            boolean foundMatch = false;
            FileInputStream fileIn = new FileInputStream("vectorDB.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            while (!foundMatch) {
                System.out.println("here");

                toReturn = (LinkedList<FloatData>) in.readObject();
            }


            in.close();
            fileIn.close();
        } catch (EOFException e) {

        } catch (IOException i) {
            System.out.println("IOException");
            i.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            System.out.println("Employee class not found");
            c.printStackTrace();
            return null;
        }

        return toReturn;

    }
}

