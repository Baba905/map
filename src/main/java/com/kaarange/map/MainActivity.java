package com.kaarange.map;


import java.util.List;
import android.os.Bundle;
import android.content.Context;
import android.location.Location;
import com.mapbox.mapboxsdk.Mapbox;
import androidx.annotation.NonNull;
import android.annotation.SuppressLint;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.geometry.LatLng;
import androidx.appcompat.app.AppCompatActivity;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;


/**
 * The most basic example of adding a map to an activity.
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener {

    private MapView mapView;
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the map view.
        Mapbox.getInstance(this, getString(R.string.access_token));
        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments.
                    }
                });
            }
        });
    }
/*----------------------------------------------------------------------------------------*/
    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        map = mapboxMap;
        enableLocation();
    }

    private void enableLocation() {
        Context context;
        if(PermissionsManager.areLocationPermissionsGranted(this)){
            initializeLocationEngine();
            initializeLocationLayer();
        }else{
            permissionsManager= new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }
    @SuppressWarnings("MissingPermission")
    private void initializeLocationEngine(){
        locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();
        Location lastLocation =locationEngine.getLastLocation();
        if (lastLocation!=null){
            originLocation =lastLocation;
            setCameraPosition(lastLocation);
        }else{
            locationEngine.addLocationEnginelistener(this);
        }
    }
    @SuppressLint("WrongConstant")
    @SuppressWarnings("MissingPermission")
    private  void initializeLocationLayer(){
        locationLayerPlugin= new LocationLayerPlugin(mapView,map,locationEngine);
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
        locationLayerPlugin.setRenderMode((RenderMode.NORMAL));
    }

    private void setCameraPosition(Location location){
        double zoom;
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),13.0));
    }

    @Override
    @SuppressWarnings("MissingPermission")
    public void onConnected(){
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location){
        if (location != null){
            originLocation = location;
            setCameraPosition(location);
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        //dialogue
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if(granted){
            enableLocation();
        }

    }
    /*-------------------------------------------------------------------------------*/

    // Add the mapView lifecycle to the activity's lifecycle methods
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }
    @SuppressWarnings("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();
        if(locationEngine!=null){
            locationEngine.requestLocationUpdates();

        }
        if(locationLayerPlugin!=null){
            locationLayerPlugin.onStart();
        }
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(locationEngine != null){
            locationEngine.removeLocationUpdates();
        }
        if(locationLayerPlugin != null){
            locationLayerPlugin.onStop();
        }
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(locationEngine!=null){
            locationEngine.deactivate();
        }cationLayerPlugin;

        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
