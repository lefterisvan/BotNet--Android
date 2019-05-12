package com.example.botmaster.desteganography;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import com.example.botmaster.Images;
import com.example.botmaster.Utility;

import java.util.List;

public class TextDecoding extends AsyncTask<Images, Void, Images> {

    private final static String TAG = TextDecoding.class.getName();

    private final Images result;
    //Callback interface for AsyncTask

    private ProgressDialog progressDialog;

    public TextDecoding(Activity activity) {
        this.result = new Images();
        this.progressDialog = new ProgressDialog(activity);
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //setting parameters of progress dialog
        if (progressDialog != null) {
            progressDialog.setMessage("Loading, Please Wait...");
            progressDialog.setTitle("Decoding Message");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();

        }
    }


    @Override
    protected void onPostExecute(Images images) {
        super.onPostExecute(images);
        if (progressDialog != null)
            progressDialog.dismiss();
        Log.e(TAG,"result="+result);
    }


    @Override
    protected Images doInBackground(Images... images) {
        //If it is not already decoded
        if (images.length > 0) {

            Images imageSteganography = images[0];

            //getting bitmap image from file
            Bitmap bitmap = imageSteganography.getImage();

            //return null if bitmap is null
//            if (bitmap == null)
//                return null;

            //splitting images
            List<Bitmap> srcEncodedList = Utility.splitImage(bitmap);

            //decoding encrypted zipped message
            String decoded_message = Decode.decodeMessage(srcEncodedList);

            Log.d(TAG, "Decoded_Message : " + decoded_message);

            //text decoded = true
            if (!Utility.isStringEmpty(decoded_message)) {
                result.setDecoded(true);
            }

            //decrypting the encoded message
            String decrypted_message = Images.decryptMessage(decoded_message, imageSteganography.getSecret_key());
            Log.d(TAG, "Decrypted message : " + decrypted_message);

            //If decrypted_message is null it means that the secret key is wrong otherwise secret key is right.
            if (!Utility.isStringEmpty(decrypted_message)) {

                //secret key provided is right
                result.setSecretKeyWrong(false);

                // Set Results

                result.setMessage(decrypted_message);


                //free memory
                for (Bitmap bitm : srcEncodedList)
                    bitm.recycle();

                //Java Garbage Collector
                System.gc();
            }
        }

        return result;
    }

}

