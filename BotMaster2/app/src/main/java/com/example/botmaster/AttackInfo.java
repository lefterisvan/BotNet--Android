package com.example.botmaster;

import java.io.Serializable;

public class AttackInfo implements Serializable
{

    private String Starget,attackType;
    int sthreads,sport,stimer;

    public AttackInfo(){}

    public AttackInfo(String starget, String attackType, int sthreads, int sport, int stimer) {
        this.Starget = starget;
        this.attackType = attackType;
        this.sthreads = sthreads;
        this.sport = sport;
        this.stimer = stimer;
    }

    public AttackInfo(String attackType, String starget, int sthreads, int stimer)
    {
        this.Starget = starget;
        this.attackType = attackType;
        this.sthreads = sthreads;
        this.stimer = stimer;
    }

    @Override
    public String toString()
    {
        if(attackType.equals("UDP"))
        {
            return attackType + " " + Starget + " " + sthreads + " " + stimer;
        }
        else if(attackType.equals("HTTP"))
        {
            return attackType + " " + Starget + " " + sthreads + " " + stimer;
        }
        else
        {
            return attackType + " " + sthreads + " " + sport + " " + stimer + " " + Starget;
        }
    }
}
