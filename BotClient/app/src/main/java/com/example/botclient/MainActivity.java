package com.example.botclient;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity
{
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);
        Intent i1= new Intent(MainActivity.this,Services.class);
        startService(i1);
    }

//    public Bitmap getBitmap()
//    {
//        AssetManager assetManager = this.context.getAssets();
//        InputStream input = null;
//        Bitmap responseBitmap = null;
//
//        //Retrieve an image from the assets folder
//        System.out.println("Accessing assets folder to retrieve image");
//
//        try {
//            input = assetManager.open("logo.png");
//            //Convert it to bitmap
//             responseBitmap = BitmapFactory.decodeStream(input);
//            System.out.println("retrieved");
//            input.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return responseBitmap;
//    }
}
