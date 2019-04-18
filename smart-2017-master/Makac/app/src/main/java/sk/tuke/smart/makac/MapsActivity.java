package sk.tuke.smart.makac;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<? extends List<Location>> finalList;
    private double lat[] = new double[300];
    private double lon[] = new double[300];
    private int count =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment fragment_maps_workoutmap = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        fragment_maps_workoutmap.getMapAsync(this);
        finalList = getIntent().getParcelableArrayListExtra("finalLocations");
        for (List<Location> list : finalList){
            for (Location triplet: list){
                lat[count] = triplet.getLatitude();
                lon[count] = triplet.getLongitude();
                count++;
            }

        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Jedlikova 9, Kosice;
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        ArrayList<LatLng> coordList = new ArrayList<LatLng>();

// Adding points to ArrayList
        for (int i = 0; i < count - 1; i++) {
            System.out.println(lat[i]);
            coordList.add(new LatLng(lat[i], lon[i]));
        }

        PolylineOptions polylineOptions = new PolylineOptions();

// Create polyline options with existing LatLng ArrayList
        polylineOptions.addAll(coordList);
        polylineOptions
                .width(5)
                .color(Color.RED);

// Adding multiple points in map using polyline and arraylist
        mMap.addPolyline(polylineOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat[count - 1], lon[count - 1]), 15));


    }

}
