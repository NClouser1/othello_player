package com.atomicobject.othello;

import java.net.Socket;

public class Main {

    public static void main ( final String[] args ) {
        final String ip = args.length > 0 ? args[0] : "127.0.0.1";
        final int port = args.length > 1 ? parsePort( args[1] ) : 1337;
        try {
            System.out.println( "Connecting to " + ip + " at " + port );
            final Socket socket = new Socket( ip, port );
            new Client( socket ).start();
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }
    }

    private static int parsePort ( final String port ) {
        return Integer.parseInt( port );
    }
}
