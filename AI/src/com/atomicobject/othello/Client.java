package com.atomicobject.othello;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.google.gson.Gson;

public class Client {

    BufferedReader     input;
    OutputStreamWriter out;
    Gson               gson = new Gson();
    AI                 ai;

    public Client ( final Socket socket ) {
        try {
            ai = new AI();
            input = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
            out = new OutputStreamWriter( socket.getOutputStream() );
        }
        catch ( final IOException e ) {
            e.printStackTrace();
        }
    }

    public void start () {
        System.out.println( "Starting client processing ..." );
        GameState state;
        try {
            while ( ( state = readStateFromServer() ) != null ) {
                final int[] move = ai.computeMove( state );
                respondWithMove( move );
            }
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }
        closeStreams();
    }

    private GameState readStateFromServer () throws IOException {
        System.out.println( "Reading from server ..." );
        final String nextLine = input.readLine();
        System.out.println( "Read data: " + nextLine );
        if ( nextLine == null ) {
            return null;
        }
        return gson.fromJson( nextLine.trim(), GameState.class );
    }

    private void respondWithMove ( final int[] move ) throws IOException {
        final String encoded = gson.toJson( move );
        System.out.println( "Sending response: " + encoded );
        out.write( encoded );
        out.write( "\n" );
        out.flush();
    }

    private void closeStreams () {
        closeQuietly( input );
        closeQuietly( out );
    }

    private void closeQuietly ( final Closeable stream ) {
        try {
            stream.close();
        }
        catch ( final IOException e1 ) {
            e1.printStackTrace();
        }
    }
}
