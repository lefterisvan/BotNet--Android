package com.example.botmaster;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HeartBeatUI extends AppCompatActivity
{

    private static Context context;
    private Socket socket;
    private Timer t = new Timer();
    private TimerTask timerTask;
    private ArrayList<String> Mip = new ArrayList<>() ;
    private ArrayList<String> tempList = new ArrayList<>();
    private static int count = 0;
    private ArrayList<ConnectedBot> conBot;

    private LinearLayout linearLayout;


    private ArrayList<DataInputStream> dataInputStream = new ArrayList<>() ;

    private static boolean stop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot_ui);
        conBot = new ArrayList<>(MainActivity.getConBot()) ;
        context = getApplicationContext();
        linearLayout = (LinearLayout) findViewById(R.id.rootContainer);

        this.socket = MainActivity.getSocket();

        try
        {
            for(int i=0; i<conBot.size(); i++)
            {
                InputStream inputStream;
                inputStream = conBot.get(i).getSocket().getInputStream();
                dataInputStream.add( new DataInputStream(inputStream));
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if(!dataInputStream.isEmpty())
        {
            receive();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        StopTimer();
    }


    public void receive()
    {
        t.schedule(timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        linearLayout.removeAllViews();
                        linearLayout.invalidate();
                        linearLayout.requestLayout();
                    }});


                if(stop == false)
                {
                    try
                    {
                        for(int i = 0; i < conBot.size(); i++)
                        {
                            String received = dataInputStream.get(i).readLine();
                            Log.e("Received","Bot  is alive");

                            if (Mip.isEmpty())
                            {
                                Mip.add(received);
                                tempList.add(received);
                            }
                            else
                            {
                                for(int j = 0; j < Mip.size(); j++)
                                {
                                    try
                                    {
                                        if(!Mip.get(j).equals(received))
                                        {
                                            Mip.add(received);
                                            tempList.add(received);
                                        }
                                    } catch (NullPointerException nex) {
                                        nex.printStackTrace();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }

                        //this loops searches for IPs that did't send a heartbeat in this TimerTask, and removes them from the ArrayList
                        for(int i = 0; i < Mip.size(); i++)
                        {
                            if(!tempList.contains(Mip.get(i)))
                            {
                                Mip.remove(i);
                            }
                        }

                        tempList.clear(); //Completely clear the temporary list, to fill it again in the next TimerTask
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                   final ArrayList<CheckBox> checkedBots = new ArrayList<>();

                    Log.e("MIP arraylist","size = "+Mip.size());

                    for(int i=0; i<Mip.size(); i++)
                    {
                        CheckBox cbNew = new CheckBox(HeartBeatUI.getContext());
                        cbNew.setBackgroundColor(Color.GREEN);
                        cbNew.setText(Mip.get(i));
                        checkedBots.add(cbNew);
                        count++;
                    }

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {linearLayout.removeAllViews();

                            linearLayout.invalidate();
                            linearLayout.requestLayout();
                            Log.e("check box arraylist","size = "+checkedBots.size());

                            for(int i=0; i<checkedBots.size(); i++)
                            {
                                linearLayout.addView(checkedBots.get(i));
                            }
                        }
                    });
                }
                else
                {
                    System.out.println("Cancelling task and closing stream");
                    timerTask.cancel();
                    t.cancel();

                    System.out.println("HeartBeat protocol terminated");
                }

                Mip.clear();
            }

        },0,5000);
    }



    public void StopTimer()
    {
        stop = true;
    }

    public static Context getContext() {
        return context;
    }


}
