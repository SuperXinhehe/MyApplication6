package com.example.rucha.myapplication;

/**
 * Created by XinhelovesMom on 2/19/16.
 */
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
/**
 * Created by Rucha on 2/14/2016.
 */
public class JsonGPS   {

    private static final String LOGIN_URL = "http://54.191.90.109:3000/api/mobile/geodata";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    public double latitude;
    public double longitude;
    public String timeStamp;

    public int id;
    public int result;

    public void JsonGPS(){}
    protected int sendJson(final double lat, final double lng , final String tStamp) {
        latitude  = lat;
        longitude = lng;
        timeStamp = tStamp;
        result = doInBackground();
        return  result;
    }

    protected int doInBackground() {
        String data = "";

        int tap;
        StringBuilder testB = new StringBuilder();
        String http ="http://54.191.90.109:3000/api/mobile/geodata";

        HttpURLConnection urlConnection = null;
        try {
            URL url2 = new URL(http);
            urlConnection = (HttpURLConnection) url2.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.connect();

            //Create JSONObject here
            JSONObject gpsPoint = new JSONObject();
            gpsPoint.put("lat", latitude);
            gpsPoint.put("long", longitude);
            gpsPoint.put("timestamp", timeStamp);

            Json userId =new Json();
            id = userId.getId();

            // Need to make a new array of locations.
            JSONArray locations = new JSONArray();
            locations.put(gpsPoint);

            JSONObject mainToSend =new JSONObject();
            mainToSend.put("usrid",id);
            mainToSend.put("location",locations);

            Log.e("JSON: ", mainToSend.toString());

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(mainToSend.toString());
            out.close();

            int HttpResult = urlConnection.getResponseCode();
            result=HttpResult;
            Log.e("test gps result", "test" + HttpResult);
            Log.e("test2gpsdata", ""+ HttpResult);
            // if (HttpResult == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream(), "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                testB.append(line + "\n");
            }
            Log.e("return", testB.toString());
            data=testB.toString();
            br.close();

            //get userid from returned string
            JSONObject jdat = new JSONObject(testB.toString());
            id = jdat.getInt("userid");
            Log.d("id"," " +id);

            //} else
            {
                System.out.println(urlConnection.getResponseMessage());
            }
        } catch (MalformedURLException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return result;
    }

}