package com.csipsimple.f5chat.map;//package com.csipsimple.f5chat.map;
//
//import android.content.Context;
//import android.location.Address;
//import android.location.Geocoder;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.app.FragmentActivity;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.csipsimple.R;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Locale;
//
//public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
//
//    private GoogleMap mMap;
//    GPSTracker gps;
//    double latitude,longitude;
//    TextView locAddress;
//    String locationAddress="Current Location";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.demo);
//        locAddress = (TextView)findViewById(R.id.address);
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        gps = new GPSTracker(MapsActivity.this);
//
//
//        // check if GPS enabled
//        if(gps.canGetLocation()){
//            latitude = gps.getLatitude();
//            longitude = gps.getLongitude();
//
//            getAddressFromLocation(latitude, longitude, MapsActivity.this, new GeocoderHandler());
//
//
//            // \n is for new line
//            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
//        }else{
//            // can't get location
//            // GPS or Network is not enabled
//            // Ask user to enable GPS/network in settings
//            gps.showSettingsAlert();
//        }
//
//    }
//
//    private class GeocoderHandler extends Handler {
//        @Override
//        public void handleMessage(Message message) {
//
//            switch (message.what) {
//                case 1:
//                    Bundle bundle = message.getData();
//                    locationAddress = bundle.getString("address");
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            locAddress.setText(locationAddress);
//                        }
//                    });
//
//                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                            .findFragmentById(R.id.map);
//                    mapFragment.getMapAsync(MapsActivity.this);
//
//                    break;
//                default:
//                    locationAddress = null;
//            }
//
//            Toast.makeText(getApplicationContext(), "Your Location address - " + locationAddress, Toast.LENGTH_LONG).show();
//        }
//    }
//
//    /**
//     * Manipulates the map once available.
//     * This callback is triggered when the map is ready to be used.
//     * This is where we can add markers or lines, add listeners or move the camera. In this case,
//     * we just add a marker near Sydney, Australia.
//     * If Google Play services is not installed on the device, the user will be prompted to install
//     * it inside the SupportMapFragment. This method will only be triggered once the user has
//     * installed Google Play services and returned to the app.
//     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        float cameraZoom = 18f;
//
//        // Add a marker in Sydney and move the camera
//        LatLng location = new LatLng(latitude, longitude);
//        mMap.addMarker(new MarkerOptions().position(location).title(locationAddress));
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, cameraZoom));
//        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//    }
//
//        public void getAddressFromLocation(final double latitude, final double longitude,
//                                                  final Context context, final Handler handler) {
//            Thread thread = new Thread() {
//                @Override
//                public void run() {
//                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
//                    String result = "";
//                    try {
//                        List<Address> addressList = geocoder.getFromLocation(
//                                latitude, longitude, 1);
//                        if (addressList != null && addressList.size() > 0) {
//                            Address address = addressList.get(0);
//                            StringBuilder sb = new StringBuilder();
//                            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
//                                sb.append(address.getAddressLine(i)).append("\n");
//                            }
//                            sb.append(address.getLocality()).append("\n");
//                            sb.append(address.getPostalCode()).append("\n");
//                            sb.append(address.getCountryName());
//                            result = sb.toString();
//                        }
//                    } catch (IOException e) {
//                        //Log.e(TAG, "Unable connect to Geocoder", e);
//                    } finally {
//                        Message message = Message.obtain();
//                        message.setTarget(handler);
//                        if (result != null) {
//                            message.what = 1;
//                            Bundle bundle = new Bundle();
////                            result = "Latitude: " + latitude + " Longitude: " + longitude + "\n\nAddress:\n" + result;
//                            bundle.putString("address", result.replaceAll("\n", " "));
//                            message.setData(bundle);
//                        } else {
//                            message.what = 1;
//                            Bundle bundle = new Bundle();
//                            result = "Latitude: " + latitude + " Longitude: " + longitude +
//                                    "\n Unable to get address for this lat-long.";
//                            bundle.putString("address", result);
//                            message.setData(bundle);
//                        }
//                        message.sendToTarget();
//                    }
//                }
//            };
//            thread.start();
//        }
//
//
//}
