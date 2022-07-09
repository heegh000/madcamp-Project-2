package com.example.fivepiratesgame;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class Global {

    public static Socket socket;

    static {
        try {
            socket = IO.socket("http://192.249.18.135:443");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
