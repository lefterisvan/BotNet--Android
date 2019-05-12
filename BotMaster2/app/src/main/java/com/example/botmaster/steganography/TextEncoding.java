package com.example.botmaster.steganography;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.example.botmaster.Images;
import com.example.botmaster.Utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.List;

public class TextEncoding extends AsyncTask <Images, Integer , Images> implements Serializable
{
    private static final String TAG = TextEncoding.class.getName();
    private int maximumProgress;
    private final ProgressDialog progressDialog;
    private Images result;
    private AfterEncoding afterEncoding;

    public TextEncoding(Activity activity, AfterEncoding afterEncoding)
    {
        this.progressDialog=new ProgressDialog(activity);
        this.afterEncoding=afterEncoding;
        this.result= new Images();
    }


    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        //setting parameters of progress dialog
        if (progressDialog != null)
        {
            progressDialog.setMessage("Loading, Please Wait...");
            progressDialog.setTitle("Encoding Message");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    @Override
    protected void onPostExecute(Images images)
    {
        super.onPostExecute(images);
        //dismiss progress dialog
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        afterEncoding.finisher(images);
    }

    @Override
    protected void onProgressUpdate(Integer... values)
    {
        super.onProgressUpdate(values);
        if (progressDialog != null) {
            progressDialog.incrementProgressBy(values[0]);
        }
    }


    @Override
    protected Images doInBackground(Images... images)
    {
        maximumProgress = 0;

        if (images.length > 0)
        {
            Images textStegnography = images[0];

            //getting image bitmap
            Bitmap bitmap = textStegnography.getImage();

            //getting height and width of original image
            int originalHeight = bitmap.getHeight();
            int originalWidth = bitmap.getWidth();

            //splitting bitmap
            List<Bitmap> src_list = Utility.splitImage(bitmap);

            //encoding encrypted compressed message into image

            List<Bitmap> encoded_list = Encode.encodeMessage(src_list, textStegnography.getEncrypted_message(), new Encode.ProgressHandler() {

                //Progress Handler
                @Override
                public void setTotal(int tot)
                {
                    maximumProgress = tot;
                    progressDialog.setMax(maximumProgress);
                    Log.d(TAG, "Total Length : " + tot);
                }

                @Override
                public void increment(int inc) {
                    publishProgress(inc);
                }

                @Override
                public void finished() {
                    Log.d(TAG, "Message Encoding end....");
                    progressDialog.setIndeterminate(true);
                }
            });

            //free Memory
            for (Bitmap bitm : src_list)
                bitm.recycle();

            //Java Garbage collector
            System.gc();

            //merging the split encoded image
            Bitmap srcEncoded = Utility.mergeImage(encoded_list, originalHeight, originalWidth);

            //Setting encoded image to result
            result.setEncoded_image(srcEncoded);
            result.setEncoded(true);
        }
        return result;
    }
}