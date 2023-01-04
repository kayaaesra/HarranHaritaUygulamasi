package com.example.harranharitauygulamasi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.harranharitauygulamasi.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    String apiKey = "AIzaSyD9dS9JjCNxP60GxFGXLYxxkmbkgHKKG-c";
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    LatLng MuhendislikFakultesi = new LatLng(37.1723435, 39.0017346);
    LatLng ZiraatFakultesi = new LatLng(37.1705391, 38.9902499);
    LatLng FenEdebiyatFakultesi = new LatLng(37.1683471, 38.9996805);
    LatLng EgitimFakultesi = new LatLng(37.1685617, 38.9994006);
    LatLng HarranUniversitesiElBattaniKutuphanesi = new LatLng(37.1690399, 38.9976809);
    LatLng HarranUniversitesiTipFakultesi = new LatLng(37.1686331, 38.9969374);

    LatLng konumum;
    Polyline polyline;
    PolylineOptions polylineOptions;

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            konumum = new LatLng(location.getLatitude(), location.getLongitude());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMyLocationChangeListener(myLocationChangeListener);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {

                double enlem = marker.getPosition().latitude;
                double boylam = marker.getPosition().longitude;
                LatLng hedef = new LatLng(enlem, boylam);

                String url = GuzergahUrlOlustur(konumum, hedef, apiKey);

                new GuzergahOlustur(url).execute();

                return false;
            }
        });

        mMap.addMarker(new MarkerOptions().position(MuhendislikFakultesi).title("Mühendsilik Fakültesi"));
        mMap.addMarker(new MarkerOptions().position(ZiraatFakultesi).title("Ziraat Fakültesi"));
        mMap.addMarker(new MarkerOptions().position(FenEdebiyatFakultesi).title("Fen Edebiyat Fakültesi"));
        mMap.addMarker(new MarkerOptions().position(EgitimFakultesi).title("Eğitim Fakültesi"));
        mMap.addMarker(new MarkerOptions().position(HarranUniversitesiElBattaniKutuphanesi).title("Harran Üniversitesi El Battani Kütüphanesi"));
        mMap.addMarker(new MarkerOptions().position(HarranUniversitesiTipFakultesi).title("Harran Üniversitesi Tıp Fakültesi"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(MuhendislikFakultesi));

        konumumuBul();
    }

    private void konumumuBul() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            konumumuBul();
        }
    }

    private String GuzergahUrlOlustur(LatLng baslangicKonuu, LatLng hedefKonumu, String apiKey) {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=" + baslangicKonuu.latitude + "," +baslangicKonuu.longitude +
                "&destination=" + hedefKonumu.latitude + "," + hedefKonumu.longitude +
                "&sensor=false" +
                "&mode=driving" +
                "&key=" + apiKey;
    }

    private ArrayList<LatLng> decodePolyline(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<>();
        int index = 0;
        int len = encoded.length();
        int lat = 0;
        int lng = 0;

        while(index < len) {
            int b;
            int shift = 0;
            int result = 0;

            do {
                char var9 = encoded.charAt(index++);
                b = var9 - 63;
                result |= (b & 31) << shift;
                shift += 5;
            } while(b >= 32);

            int dlat = (result & 1) != 0 ? ~(result >> 1) : result >> 1;
            lat += dlat;
            shift = 0;
            result = 0;

            do {
                char var10 = encoded.charAt(index++);
                b = var10 - 63;
                result |= (b & 31) << shift;
                shift += 5;
            } while(b >= 32);

            int dlng = (result & 1) != 0 ? ~(result >> 1) : result >> 1;
            lng += dlng;
            LatLng latLng = new LatLng((double)lat / 100000.0D, (double)lng / 100000.0D);
            poly.add(latLng);
        }

        return poly;
    }

    @SuppressLint("StaticFieldLeak")
    class  GuzergahOlustur extends AsyncTask<Void, Void, List<List<LatLng>>> {
        private final String url;

        GuzergahOlustur(String url) {
            this.url = url;
            polylineOptions = null;
            polylineOptions = new PolylineOptions();

            if(polyline != null)
                polyline.remove();

        }

        @Override
        protected List<List<LatLng>> doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = null;
            try {
                response = client.newCall(request).execute();

                String data = response.body().string();

                ArrayList<List<LatLng>> result = new ArrayList<>();
                try{
                    MapData mapData = new Gson().fromJson(data, MapData.class);
                    ArrayList<LatLng> path = new ArrayList<>();

                    for (int i  = 0; i < mapData.routes.get(0).legs.get(0).steps.size(); i++) {
                        path.addAll(decodePolyline(mapData.routes.get(0).legs.get(0).steps.get(i).polyline.points));
                    }
                    result.add(path);

                }catch (Exception e){
                    e.printStackTrace();
                }

                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<List<LatLng>> result) {
            for (int i = 0; i < result.size(); i++) {

                polylineOptions.addAll(result.get(i));
                polylineOptions.width(10f);
                polylineOptions.color(Color.CYAN);
                polylineOptions.geodesic(true);

            }

            polyline = mMap.addPolyline(polylineOptions);
        }
    }
}
