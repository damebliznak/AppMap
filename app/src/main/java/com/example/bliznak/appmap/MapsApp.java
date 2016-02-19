package com.example.bliznak.appmap;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.internal.ClientApi;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.plus.Plus;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;

public class MapsApp extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    Button btn1;
    Location location = null;
    String lat, lon;
    double lat1, lon1;
    Thread vopoz, vobaza;
    ListView listalokali;
    String namekk, address1kk;
    String token=null;
   

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_app);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mGoogleApiClient = new GoogleApiClient.Builder(MapsApp.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .build();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        listalokali = (ListView) findViewById(R.id.listView);

        btn1 = (Button) findViewById(R.id.btn);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = null;
                mMap.clear();
                location = FusedLocationApi.getLastLocation(mGoogleApiClient);
                lat = String.valueOf(lat1 = location.getLatitude());
                lon = String.valueOf(lon1 = location.getLongitude());
                Log.w("ttttt", String.valueOf(location));
                mMap.addMarker(new MarkerOptions().position(new LatLng(lat1, lon1)).draggable(true));
                mMap.setMyLocationEnabled(true);
                vopoz = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        String result = "";
                        InputStream inputStream;
                        String kk = null;
                        try {
                            String URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat1 + "," + lon1 + "&radius=500&types=food&hasNextPage=true&nextPage()=true&key=AIzaSyCIo0Y9DqDyvK5aV_pLgYrprs4aPDoaH-8";
                            // create HttpClient
                            HttpClient httpclient = new DefaultHttpClient();
                            // make GET request to the given URL
                            HttpResponse httpResponse = httpclient.execute(new HttpGet(URL));
                            // receive response as inputStream
                            inputStream = httpResponse.getEntity().getContent();
                            Log.w("kkkkk1", URL);
                            // convert inputstream to string
                            if (inputStream != null) {
                                kk = OdIsvoJson(inputStream);
                                result = String.valueOf(inputStream);
                            } else
                                result = "Did not work!";
                        } catch (Exception e) {
                            Log.w("InputStream", e.getLocalizedMessage());
                        }
                        Handler handler = new Handler(Looper.getMainLooper());
                        final String finalKk = kk;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    OdStringJson(finalKk);
                                } catch (NullPointerException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MapsApp.this, "Problemi so wifi konekcijata,ili Google serverot nema podatoci za koordinatite ", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                            // your UI code here
                        });

                    }
                });
                vopoz.start();


            }

        });

        listalokali.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //dialog boks za ip adresa
                final String[] m_Text = {""};
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsApp.this);
                builder.setTitle("IP adresa");
                final EditText input = new EditText(MapsApp.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text[0] = input.getText().toString();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                //STL za cuvanje na podatocite za vo baza
                HashMap<String, Object> obj = (HashMap<String, Object>) listalokali.getAdapter().getItem(position);
                namekk = (String) obj.get("name");
                address1kk = (String) obj.get("address");
                final String httpurl = "http://" + m_Text + "/in.php";
                Log.w("Yourtag", namekk + "\n" + address1kk);
                //nitka za startuvanje na php fajlot na serverot
                vobaza = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                        nameValuePairs.add(new BasicNameValuePair("name", namekk));
                        nameValuePairs.add(new BasicNameValuePair("address", address1kk));
                        try {
                            HttpClient httpClient = new DefaultHttpClient();
                            HttpPost httpPost = new HttpPost(httpurl);
                            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                            HttpResponse response = httpClient.execute(httpPost);

                        } catch (ClientProtocolException e) {
                        } catch (IOException e) {
                        }
                    }
                });
                vobaza.start();
            }
        });
    }


    public boolean OdStringJson(String rez) {
        int i = 0;

        try {
            JSONObject jObject = new JSONObject(rez);
            JSONArray jArr = jObject.getJSONArray("results");
            token = (String) jObject.get("next_page_token");
            Log.w("token",token);
            List<Map<String, String>> data = new ArrayList<Map<String, String>>();

            for (i = 0; i < jArr.length(); i++) {
                JSONObject jObject1 = jArr.getJSONObject(i);
                JSONObject jib = new JSONObject(String.valueOf(jObject1.getJSONObject("geometry")));
                JSONObject jib1 = new JSONObject(String.valueOf(jib.getJSONObject("location")));

                String go = jib1.getString("lat");
                String go1 = jib1.getString("lng");
                String name = jObject1.getString("name");
                String adresa = jObject1.getString("vicinity");


                Map<String, String> lok = new HashMap<String, String>(2);
                lok.put("name", name);
                lok.put("address", adresa);
                lok.put("lat", go);
                lok.put("lng", go1);
                data.add(lok);

                SimpleAdapter adapter = new SimpleAdapter(this, data,
                        android.R.layout.simple_list_item_2,
                        new String[]{"name", "address", "lat", "lng"},
                        new int[]{android.R.id.text1,
                                android.R.id.text2});
                listalokali.setAdapter(adapter);
                Log.w("da vidime", name + "\n" + adresa + "\n" + go + "\t" + go1);
                LatLng lokacii = new LatLng(Double.valueOf(go), Double.valueOf(go1));
                mMap.addMarker(new MarkerOptions().position(new LatLng(lat1, lon1)).draggable(true));
                mMap.addMarker(new MarkerOptions().position(lokacii).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(lokacii));
                mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {
                        Thread.currentThread().interrupt();
                        vopoz.interrupt();
                        Log.w("za nitktite", "interapt");

                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        mMap.clear();
                        if (!vopoz.isAlive()) {
                            LatLng posti = marker.getPosition();
                            lat1 = posti.latitude;
                            lon1 = posti.longitude;
                            mMap.clear();
                            Thread rd = new Thread(vopoz);
                            rd.start();
                            Log.w("za nitktite", "it is alive");
                        }


                    }
                });
                Log.w("kolku ima", String.valueOf(data.lastIndexOf(lok)));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(token.equals(null)) {return false;}
        else {return true;}

    }

    public String OdIsvoJson(InputStream is) {
        InputStream is1 = is;
        String resultJsn = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line = "";
            resultJsn = "";
            while ((line = bufferedReader.readLine()) != null)
                resultJsn += line + "\n";
            is.close();
            Log.w("zajson", resultJsn);
        } catch (IOException e) {
            Log.w("odJson", e);
        }
        return resultJsn;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
                final WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
                if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) || !wifi.isWifiEnabled() ) Toast.makeText(MapsApp.this,"Pusti GPS i Wifi",Toast.LENGTH_SHORT).show();

                return false;
            }
        });

    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
