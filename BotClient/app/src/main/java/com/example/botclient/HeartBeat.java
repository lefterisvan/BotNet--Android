package com.example.botclient;

import android.util.Log;

import net.sf.runjva.sourceforge.jsocks.protocol.SocksSocket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

public class HeartBeat
{
    private TimerTask timerTask;
    static boolean stop = false;
    private  SocksSocket socksSocket;
    OutputStream out;
    DataOutputStream dos;
    Timer t = new Timer();

    public HeartBeat(SocksSocket socksSocket)
    {
        this.socksSocket = socksSocket;
        out = socksSocket.getOutputStream();
        dos = new DataOutputStream(out);
    }

    public HeartBeat() {
    }

    public void heart()
    {
        System.out.println("In heartbeat");

        t.schedule(timerTask=new TimerTask()
        {
            @Override
            public void run() {

                if(stop == false)
                {
                    String myIp = findIp();

                    if(myIp != null)
                    {
                        try {

                            dos.writeBytes(myIp+"\n");

                            Log.e("HeartBeat","heartbeat sent");
                            dos.flush(); //flushes the buffer
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        try {

                            dos.writeBytes("255.255.255.255"+"\n");

                            Log.e("HeartBeat","heartbeat sent");
                            dos.flush(); //flushes the buffer
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else
                {
                    System.out.println("Cancelling task and closing stream");
                    timerTask.cancel();
                    t.cancel();
//                    try {
//                        socksSocket.shutdownOutput();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    System.out.println("HeartBeat protocol terminated");

                }

            }
        },0,5000);
    }

    public void StopTimer()
    {
        stop = true;
    }

    public boolean getStop()
    {
        return stop;
    }


    public String findIp()
    {
        String myIp = null;
        Enumeration en;

        try
        {
            en = NetworkInterface.getNetworkInterfaces();

            while(en.hasMoreElements())
            {
                NetworkInterface ni=(NetworkInterface) en.nextElement();
                Enumeration ee = ni.getInetAddresses();

                while(ee.hasMoreElements())
                {
                    InetAddress ia= (InetAddress) ee.nextElement();

                    if (ia.getHostAddress().startsWith("192.168."))
                    {
                        myIp = ia.getHostAddress();
                    }
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }

        return myIp;
    }
}
