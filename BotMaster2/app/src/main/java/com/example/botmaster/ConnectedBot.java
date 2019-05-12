package com.example.botmaster;

import java.net.Socket;

public class ConnectedBot
{
    private Socket socket;
    int i;

    public ConnectedBot(Socket socket, int i)
    {
        this.socket = socket;
        this.i = i;
    }

    public Socket getSocket() {
        return socket;
    }
}
