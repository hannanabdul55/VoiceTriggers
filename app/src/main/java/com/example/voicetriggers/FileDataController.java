package com.example.voicetriggers;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import SphinxDemo.sphinx4.edu.cmu.sphinx.frontend.FloatData;
/* IMPORT FLOAT DATA HERE*/

//import FileManagement.FloatData;
public class FileDataController {
    /*public static void main(String args[])
    {

        FileDataController fd= new FileDataController();
        float values[]=new float[39];
        for(int i=1;i<39;i++)
            values[i]=(float)values[i-1]+0.002f;
        FloatData f1= new FloatData(values,2,2,2);
        fd.writeToFile(f1,"second");

        FloatData vec1= fd.readFromFile(new String("first"));
        if(vec1!=null)
        {
            System.out.println(vec1.getCollectTime());
            System.out.println(vec1.getFirstSampleNumber());
            System.out.println(vec1.getSampleRate());
        }
        else
        {
            System.out.println("Could not be found");
        }

    }*/
    public void writeToFile(FloatData vector, String id) {
        try {
            FileTuple toAdd = new FileTuple(vector, id);
            FileOutputStream fileOut =
                    new FileOutputStream("vectorDB.ser", true);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(toAdd);
            out.close();
            fileOut.close();
            System.out.printf("Saved");
        } catch (IOException i) {
            i.printStackTrace();
        } catch (CloneNotSupportedException i) {
            i.printStackTrace();
        }

    }

    public FloatData readFromFile(String id) {
        FloatData toReturn = null;
        FileTuple tuple = null;
        try {
            boolean foundMatch = false;
            FileInputStream fileIn = new FileInputStream("vectorDB.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            while (!foundMatch) {
                System.out.println("here");
                tuple = (FileTuple) in.readObject();
                if (tuple.get_id().equals(id))
                    foundMatch = true;
            }
            if (tuple == null) {
                System.out.println("Entry non-existent");
                //return null;
            } else {
                toReturn = tuple.vector.clone();
            }
            in.close();
            fileIn.close();
        } catch (EOFException e) {
        } catch (IOException i) {
            i.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            System.out.println("Employee class not found");
            c.printStackTrace();
            return null;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
        return toReturn;

    }
}


class FileTuple implements Serializable {
    public FloatData vector;
    private String tuple_id;

    public FileTuple(FloatData vector, String tuple_id) throws CloneNotSupportedException {
        this.vector = vector.clone();
        this.tuple_id = new String(tuple_id);
    }

    public String get_id() {
        return tuple_id;
    }
}

