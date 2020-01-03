package com.example.meteoservice;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpRequest extends AsyncTask<String, Void, String> {

    public static final String GET_METHOD = "GET";
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECT_TIMEOUT = 15000;


//  There was a need to add Clear Text Permission to internet traffic (Since Android 8) - Added to Android Manifest
    @Override
    protected String doInBackground(String... params) {
        //Url get from params array
        String url = params[0];
        String result;
        byte[] resultBytes;
        String inputLine;

        try {
            //Create URL obj from string URL
            URL urlObj = new URL(url);
            //Create connection
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

            //Set connection attributes
            connection.setRequestMethod(GET_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECT_TIMEOUT);

            //Connect
            connection.connect();
            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
            //New Buffered Reader and String Builder
            BufferedReader bufferedReader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();

            //Read all lines to last one which is empty
            while ((inputLine = bufferedReader.readLine()) != null){
                stringBuilder.append(inputLine);
            }

            //Close stream and reader
            bufferedReader.close();
            streamReader.close();

            result = stringBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
            result = null;
        }

        if(result != null){
            result = fixEncode(result);
        }

        return result;
    }


    //Method to fix encoding provided by Meteo Stations API - http://infomet.nazwa.pl/data/stations.php
    private String fixEncode(String in)
    {
        in = in.replace("\\u00c5\\u0082","ł");
        in = in.replace("\\u00c3\\u00b3","ó");
        in = in.replace("\\u00c5\\u0084","ń");
        in = in.replace("\\u00c5\\u00bc","ż");
        in = in.replace("\\u00c5\\u0081","Ł");
        in = in.replace("\\u00c4\\u0099","ę");
        in = in.replace("\\u00c5\\u00ba","ź");
        in = in.replace("\\u00c5\\u009a","Ś");
        in = in.replace("\\u00c4\\u0085","ą");
        in = in.replace("\\u00c5\\u009b", "ś");

        return in;
    }
}
