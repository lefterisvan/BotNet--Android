package com.example.botmaster;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Looper;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.System.out;

public class SenderAsync extends AsyncTask <Bitmap,Void,String>
{
    private DataOutputStream dos;
    private ArrayList<ConnectedBot> conBot = new ArrayList<>() ;

//    {
//        try {
//            dos = new DataOutputStream(MainActivity.getSocket().getOutputStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    protected String doInBackground(Bitmap... bitmaps)
    {
        try
        {
            int botsCount = MainActivity.getCount();
            conBot = MainActivity.getConBot();
            int i = 0;

            Looper.prepare();
            new HeartBeatUI().StopTimer();

            while(botsCount > 0)
            {
                try
                {
                    dos = new DataOutputStream((conBot.get(i).getSocket()).getOutputStream());
                    System.out.println("Socket number "+i);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Convert Bitmap to byte array:
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmaps[0].compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
                byte bitmapBytes[] = byteArrayOutputStream.toByteArray();
                out.println("Sending.....");
                dos.writeInt(bitmapBytes.length);
                out.println("Bytes sent ok!");
                dos.write(bitmapBytes, 0, bitmapBytes.length);
                out.println("Image sent ok!");

                botsCount--;
                i++;
            }
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }

        try {
            MainActivity.getSocket().shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Finished";
    }
}
