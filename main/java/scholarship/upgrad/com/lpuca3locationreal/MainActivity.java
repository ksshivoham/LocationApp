package scholarship.upgrad.com.lpuca3locationreal;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/*Create an android app and show the latitude and longitude
of user's entered location by using google location api.
  Also show the physical address in the map and check all
   necessary run time permission.*/

public class MainActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback {
    Button btLocation;
    TextView tvLongLat, tvText;
    Geocoder geocoder;
    private LocationManager locationManager;
    private static final int REQUEST_LOCATION = 123;
    private double longi;
    private double lat;
    List<Address> addresses;
    GoogleMap map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btLocation = findViewById(R.id.btLocation);
        tvLongLat = findViewById(R.id.tvLongLat);
        SupportMapFragment mapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder=new Geocoder(this, Locale.getDefault());
        tvText = findViewById(R.id.tvText);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        btLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("location")
                                .setMessage("location required")
                                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(MainActivity.this, "got it", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //Prompt the user once explanation has been shown
                                        ActivityCompat.requestPermissions(MainActivity.this,
                                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                REQUEST_LOCATION);
                                    }
                                })
                                .create()
                                .show();

                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_LOCATION);


                    }

                }
                else
                {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_LOCATION);
                }

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            0, 0, MainActivity.this);
                }else{
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                            .setCancelable(false)
                            .setPositiveButton("Goto Settings Page To Enable GPS",
                                    new DialogInterface.OnClickListener(){
                                        public void onClick(DialogInterface dialog, int id){
                                            Intent callGPSSettingIntent = new Intent(
                                                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            startActivity(callGPSSettingIntent);
                                        }
                                    });
                    alertDialogBuilder.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int id){
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();
                }            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {


            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission
                                (this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        0, 0, MainActivity.this);


            }}

    }


            @Override
            public void onLocationChanged (Location location){
longi=location.getLongitude();
lat=location.getLatitude();
tvLongLat.setText(longi+" "+lat);
                try {
                    addresses=geocoder.getFromLocation(lat,longi,1);
                String address =addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String fulladdress=address+","+city+","+state+","+country+","+
                            postalCode;
                    tvText.setText(fulladdress);
                } catch (IOException e) {
                    e.printStackTrace();
                }
onMapReady(map);

            }

            @Override
            public void onStatusChanged (String provider,int status, Bundle extras){

            }

            @Override
            public void onProviderEnabled (String provider){

            }

            @Override
            public void onProviderDisabled (String provider){
            }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
LatLng place=new LatLng(lat, longi);
map.addMarker(new MarkerOptions().position(place).title("current location"));
map.moveCamera(CameraUpdateFactory.newLatLng(place));
    }
}
