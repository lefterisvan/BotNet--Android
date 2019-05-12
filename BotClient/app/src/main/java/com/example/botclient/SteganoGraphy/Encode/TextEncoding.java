package com.example.botclient.SteganoGraphy.Encode;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import com.example.botclient.SteganoGraphy.Images;
import com.example.botclient.SteganoGraphy.Utility;
import java.util.List;

public class TextEncoding extends AsyncTask <Images , Integer , Images>
{
    private static final String TAG = TextEncoding.class.getName();
    private Images result;
    private final TextResponseEncoding responceEncoding;

    public TextEncoding(TextResponseEncoding responceEncoding)
    {
        this.responceEncoding=responceEncoding;
        this.result= new Images();
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Images images)
    {
        super.onPostExecute(images);
        responceEncoding.ImageEncoder(result);
        Log.e(TAG, "message = "+result.getMessage()+" toString()= "+result.toString());
    }

    @Override
    protected void onProgressUpdate(Integer... values)
    {
        super.onProgressUpdate(values);
    }

    @Override
    protected Images doInBackground(Images... images)
    {
        if (images.length > 0)
        {
            Images textStegnography = images[0];
            Bitmap bitmap = textStegnography.getImage();//getting image bitmap

            //getting height and width of original image
            int originalHeight = bitmap.getHeight();
            int originalWidth = bitmap.getWidth();

            List<Bitmap> src_list = Utility.splitImage(bitmap);//splitting bitmap

            //encoding encrypted compressed message into image
            List<Bitmap> encoded_list = Encode.encodeMessage(src_list, textStegnography.getEncrypted_message(), new Encode.ProgressHandler()
            {
                //Progress Handler
                @Override
                public void setTotal(int tot)
                {
                    Log.d(TAG, "Total Length : " + tot);
                }

                @Override
                public void increment(int inc) {
                    publishProgress(inc);
                }

                @Override
                public void finished() {
                    Log.d(TAG, "Message Encoding end....");
                }
            });


            for (Bitmap bitm : src_list)//free Memory
                bitm.recycle();

            System.gc();//Java Garbage collector

            Bitmap srcEncoded = Utility.mergeImage(encoded_list, originalHeight, originalWidth);//merging the split encoded image

            //Setting encoded image to result
            result.setEncoded_image(srcEncoded);
            result.setEncoded(true);
        }

        return result;
    }
}