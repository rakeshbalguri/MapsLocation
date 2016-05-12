package com.rakesh.mapslocation;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private static final LatLng SYDNEY = new LatLng(-33.88,151.21);
    private static final LatLng MOUNTAIN_VIEW = new LatLng(37.4, -122.1);

    private List<Marker> markers = new ArrayList<Marker>();
    private Marker selectedMarker;

    private Polyline polyLine;
    private PolylineOptions rectOptions = new PolylineOptions();

    private SupportMapFragment mapFragment;

    int currentPt;
    TextView info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        addDefaultLocations();

//        final View mapView = mapFragment.getView();
//
//        if (mapView.getViewTreeObserver().isAlive()) {
//            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//                        mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                    } else {
//                        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                    }
//                    fixZoomForMarkers(mMap,markers);
//                }
//            });
//        }

        mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(markers.get(0).getPosition(), 15.5f),
                5000,
                MyCancelableCallback);

        currentPt = 0-1;

        Toast.makeText(getApplicationContext(),
                "Animation Started",
                Toast.LENGTH_LONG).show();

    }

    private void addDefaultLocations() {
        addMarkerToMap(new LatLng(50.961813797827055,3.5168474167585373));
        addMarkerToMap(new LatLng(50.96085423274633,3.517405651509762));
        addMarkerToMap(new LatLng(50.96020550146382,3.5177918896079063));
        addMarkerToMap(new LatLng(50.95936754348453,3.518972061574459));
        addMarkerToMap(new LatLng(50.95877285446026,3.5199161991477013));
        addMarkerToMap(new LatLng(50.958179213755905,3.520646095275879));
        addMarkerToMap(new LatLng(50.95901719316589,3.5222768783569336));
        addMarkerToMap(new LatLng(50.95954430150347,3.523542881011963));
        addMarkerToMap(new LatLng(50.95873336312275,3.5244011878967285));
        addMarkerToMap(new LatLng(50.95955781702322,3.525688648223877));
        addMarkerToMap(new LatLng(50.958855004782116,3.5269761085510254));
    }


    GoogleMap.CancelableCallback MyCancelableCallback =
            new GoogleMap.CancelableCallback(){

                @Override
                public void onCancel() {
                    System.out.println("############# Cancelled the Animation");
                    Toast.makeText(getApplicationContext(),
                            "Animation Cancelled",
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFinish() {

                    if(++currentPt < markers.size()){

                        //Get the current location
                        Location startingLocation = new Location("starting point");
                        startingLocation.setLatitude(mMap.getCameraPosition().target.latitude);
                        startingLocation.setLongitude(mMap.getCameraPosition().target.longitude);
                        final LatLng startingLatLng = new LatLng(startingLocation.getLatitude(),startingLocation.getLongitude());

                        //Get the target location
                        Location endingLocation = new Location("ending point");
                        endingLocation.setLatitude(markers.get(currentPt).getPosition().latitude);
                        endingLocation.setLongitude(markers.get(currentPt).getPosition().longitude);
                        final LatLng endingLatLng = new LatLng(endingLocation.getLatitude(),endingLocation.getLongitude());

                        //Find the Bearing from current location to next location
                        float targetBearing = startingLocation.bearingTo(endingLocation);

                        LatLng targetLatLng = markers.get(currentPt).getPosition();
                        float targetZoom = 20;

                        //Create a new CameraPosition
                        CameraPosition cameraPosition =
                                new CameraPosition.Builder()
                                        .target(targetLatLng)
                                        .bearing(targetBearing)
                                        .zoom(targetZoom)
                                        .build();

                        mMap.animateCamera(
                                CameraUpdateFactory.newCameraPosition(cameraPosition),
                                10000,
                                MyCancelableCallback);

                        mMap
                                .addPolyline((new PolylineOptions())
                                        .add(startingLatLng, endingLatLng
                                        ).width(5).color(Color.GREEN)
                                        .geodesic(true));

                        Toast.makeText(getApplicationContext(),
                                "Animation Running",
                                Toast.LENGTH_LONG).show();


                    }else{
                        Toast.makeText(getApplicationContext(),
                                "Animation Running",
                                Toast.LENGTH_LONG).show();
                    }

                }

            };


    public void addMarkerToMap(LatLng latLng) {
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
                .title("title")
                .snippet("snippet"));
        markers.add(marker);

    }

    public  void fixZoomForMarkers(GoogleMap googleMap, List<Marker> markers) {
        if (markers!=null && markers.size() > 0) {
            LatLngBounds.Builder bc = new LatLngBounds.Builder();

            for (Marker marker : markers) {
                bc.include(marker.getPosition());
            }

            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50),4000,null);
        }
    }




}
