package com.example.botmaster;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.botmaster.desteganography.TextDecoding;


public class Desteganography extends AppCompatActivity {

    private ImageView decodedView;
    private Bitmap original_image;
    Button b;
    Context con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_de_steganography);
        con=getApplication();
        b=(Button) findViewById(R.id.DeButton);

        decodedView = (ImageView) findViewById(R.id.Deimage);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageReceiver();


            }
        });



    }


    private void ImageReceiver() {
       /**edw prepei na alamvanei tin eikona ws bitamp*/
        //original_image = edw vale na lambanei tin eikona


        Images imageSteganography = new Images(" ",
                original_image);


        TextDecoding textDecoding = new TextDecoding(Desteganography.this);


        textDecoding.execute(imageSteganography);
    }


}
