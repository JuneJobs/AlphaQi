package com.example.minwoo.airound;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by p on 2016-02-04.
 */

/*  HttpConnectionThread extends AsyncTask
    App transfer some data to Web
    use Post

*/
public class HttpConnectionThread extends AsyncTask<String, Void, String> { // asyncTask 꼭 들어가니까 오버라이딩 해야되는 게 doInBa... 쓰레드 돌아가는
    Context connect_context;
    String echo_message;
    public HttpConnectionThread(Context context) {  //this is Constructor
        connect_context = context;
    }
    @Override
    protected String doInBackground(String... str) {
        // URL 연결이 구현될 부분
        URL urls;
        try {
            urls = new URL(str[0]);
            HttpURLConnection conn = (HttpURLConnection) urls.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            String line = null;
            StringBuilder sb = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            bufferedReader.close();
            echo_message = sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return echo_message;
    }
}