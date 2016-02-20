package com.example.rucha.myapplication;
/*Xinhehe*/

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.Button;
import android.provider.MediaStore;
import android.net.Uri;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;
import android.os.StrictMode;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.content.Context;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class Updates extends AppCompatActivity implements View.OnClickListener {
    ImageView imageToUpload;
    Button bSendAll;
    EditText textView;
    Button bImgUpload;
    TextView coordinates;
    JSONObject geoinfo = new JSONObject();
    private static final int RESULT_LOAD_IMAGE = 1;
    public static final String SERVER_ADDRESS = "http://10.0.0.9:3000";
    public static final String TAG = "STATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.posts);

        imageToUpload = (ImageView) findViewById(R.id.imageToUpload);
        bSendAll = (Button) findViewById(R.id.bSendAll);
        textView = (EditText) findViewById(R.id.textInfo);
        bImgUpload = (Button) findViewById(R.id.bUploadImg);
        coordinates = (TextView) findViewById(R.id.displayCoordinates);
        getGeoInfo(getLastKnownLocation());

        Log.e(TAG, geoinfo.toString());
        imageToUpload.setOnClickListener(this);
        bSendAll.setOnClickListener(this);
    }
    // method to get the current geo coordinates
    // and set up the text to coordinate display
    private void getGeoInfo(Location loc) {

//        MyLocationListener locationListener = new MyLocationListener();
        if(loc == null) Log.e(TAG,"INVALID LOCATION");
        Toast.makeText(
                getBaseContext(),
                "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                        + loc.getLongitude(), Toast.LENGTH_SHORT).show();
        String longitude = "Longitude: " + loc.getLongitude();
        Log.e(TAG, longitude);
        String latitude = "Latitude: " + loc.getLatitude();
        Log.e(TAG, latitude);

        /*------- To get city name from coordinates -------- */
        String cityName = null;
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            if (addresses.size() > 0) {
                System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String s = longitude + ", " + latitude + " City: " + cityName;
        Log.e(TAG,s);
        coordinates.setText(s);
        try {
            geoinfo.put("lat",latitude);
            geoinfo.put("long",longitude);
            geoinfo.put("timestamp",getCurrentTimeStamp());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager;
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        Location l = null;
        for (String provider : providers) {
            try {
                l = mLocationManager.getLastKnownLocation(provider);
            }catch(SecurityException e) {
                Log.e(TAG,"Security Exception");
                e.printStackTrace();
            }
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
    private Location getLocationByProvider(String provider) {
        Location location = null;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            if (locationManager.isProviderEnabled(provider)) {
                Log.e(TAG,"Try to connect");
                location = locationManager.getLastKnownLocation(provider);
            }
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Cannot access Provider " + provider);
        } catch (SecurityException e) {
            Log.e(TAG,"security exception");
        }
        return location;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent galleryIntent;
        switch(v.getId()) {
            case R.id.imageToUpload:
                galleryIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                imageToUpload.setVisibility(View.VISIBLE);
                break;
            case R.id.bSendAll:
                Bitmap image;
                if(imageToUpload!=null) {
                    image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
                }
                else {
                    image = null;
                }
                String t = textView.getText().toString();
                new UploadAll(image,t).execute();
                break;
            case R.id.bUploadImg:
                galleryIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imageToUpload.setImageURI(selectedImage);
        }
    }

    private class UploadAll extends AsyncTask<Void, Void,Void> {

        Bitmap image;
        String text;
        // data input json format:
        String ImageText(String text, byte[] imageBytes) {
            try {
                String imgStr = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("geoinfo",geoinfo);
                jsonObject.put("text", text);
                jsonObject.put("image",imgStr);
                return jsonObject.toString();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        public final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public UploadAll(Bitmap image, String text) {
            this.image = image;
            this.text = text;
        }
        @Override
        protected Void doInBackground(Void... params) {
            byte[] imageBytes;
            if(image!=null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                imageBytes = byteArrayOutputStream.toByteArray();

            }
            else {
                imageBytes = null;
            }
            String json = ImageText(text,imageBytes);
            String link = SERVER_ADDRESS + "/api/app/upload";
            Log.e(TAG,json.length()+"");
            try {
                RequestBody requestBody = RequestBody.create(JSON,json);
                Request request = new Request.Builder()
                        .url(link)
                        .post(requestBody)
                        .build();

                OkHttpClient client = new OkHttpClient();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        e.printStackTrace();
                        Log.e(TAG,request+"FAIL");
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if(!response.isSuccessful()) {
                            Log.e(TAG,"FAIL TO GET RESPONSE");
                        }
                        else {
                            System.out.println(response.body().string());
                        }
                    }
                });
            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
            bImgUpload.setEnabled(false);
        }

    }
    public String getCurrentTimeStamp(){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");
        String currentTimeStamp= sdf.format(new Date());
        StringBuilder timestamp = new StringBuilder(currentTimeStamp);
        timestamp.insert(currentTimeStamp.length() - 2, ':');

        return timestamp.toString();
    }


}

