package com.example.rucha.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@SuppressWarnings("deprecation")
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private static final int CAPImage_REQUEST_CODE = 100;
    private LocationManager locationManager;
    private LocationListener locationListener;

    ImageView resultphoto;
    private Uri fileUri;
    private String chosenProvider;
    Location location;
    public boolean k = false;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Log.e("List: ", locationManager.getProviders(true).toString());
        Log.e("GPS Status: ", locationManager.getGpsStatus(null).toString());
        Log.e("Location: ", String.valueOf(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)));

        LocationListener listener =  new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e("New location:", "lat:" + location.getLatitude() + " long:" + location.getLongitude());
                double latitude =location.getLatitude();
                double longitude =location.getLongitude();
                String str = getCurrentTimeStamp();
                JsonGPS sndGPS = new JsonGPS();
                int res= sndGPS.sendJson(latitude,longitude,str);
                Log.e("Sent or not sent", " "+res);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, listener);
                //        locationListener= new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                int lat = (int) (location.getLatitude());
//                int lng = (int)  (location.getLongitude());
//                Log.e("latitude"+lat, "longtitude "+lng);
//                String str = getCurrentTimeStamp();
//                JsonGPS sndGPS = new JsonGPS();
//                int res= sndGPS.sendJson(lat,lng,str);
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//
//            }
//        };
//        locationManager.requestLocationUpdates("gps", 2000, 0, locationListener);
//
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        locationManager.requestLocationUpdates("gps", 2000, 0, locationListener);


    }
    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.ctext)
        {
            Log.e("button","clicked");
            Intent i = new Intent(this,Updates.class );
            startActivity(i);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        locationManager.requestLocationUpdates("gps", 2000, 0, locationListener);
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {

        if(true)
        {
            mMap = googleMap;

        // Add a marker in Sydney and move the camera

            LatLng sydney = new LatLng(47.2483960, -122.4382950);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Itrajectory ").snippet("Itrajectory is the coolest"));
        /*  Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(47.2464440, -122.4395990), new LatLng(47.2448112, -122.4376078))
                .width(5)
                .color(Color.RED));
                Polyline line2 = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(47.2464440, -122.4395990), new LatLng(47.2448112, -122.4376078))
                .width(5)
                .color(Color.RED));
*/
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
    }
        else
        {

            mMap = googleMap;

            // Add a marker in Sydney and move the camera

            LatLng sydney = new LatLng(47.2483960, -122.4382950);
            mMap.addMarker(new MarkerOptions().position(sydney).title(" ").snippet(""));
      /*  Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(47.2464440, -122.4395990), new LatLng(47.2448112, -122.4376078))
                .width(5)
                .color(Color.RED));
        Polyline line2 = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(47.2464440, -122.4395990), new LatLng(47.2448112, -122.4376078))
                .width(5)
                .color(Color.RED));
*/
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));


        }
    }



    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.rucha.myapplication/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.rucha.myapplication/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }



    public static String getCurrentTimeStamp(){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");
        String currentTimeStamp= sdf.format(new Date());
        StringBuilder timestamp = new StringBuilder(currentTimeStamp);
        timestamp.insert(currentTimeStamp.length()-2, ':');

        return timestamp.toString();
    }


}
