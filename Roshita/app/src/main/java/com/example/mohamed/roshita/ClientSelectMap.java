package com.example.mohamed.roshita;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ClientSelectMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Intent intent;
    private Handler handler;
    private AlertDialog dialog;
    private double[] point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_select_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        intent = getIntent();
        point = intent.getDoubleArrayExtra("centerPoint");
        handler = new Handler();

        LatLng center = new LatLng(point[0] ,point[1] );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center ,13) );


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ClientSelectMap.this ,"إضغط علي المكان المراد البحث حوله" ,Toast.LENGTH_SHORT).show();
            }
        } ,3000);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("مكان البحث") );

                dialog = new AlertDialog.Builder(ClientSelectMap.this)
                        .setMessage("متأكد من إختيار هذا المكان للبحث حوله ؟")
                        .setPositiveButton("نعـم", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent2;
                                point = new double[]{ latLng.latitude ,latLng.longitude};

                                if (intent.hasExtra("cameFrom") )
                                    intent2 = new Intent(ClientSelectMap.this, Client5Activity.class);
                                else
                                    intent2 = new Intent(ClientSelectMap.this, Client3Activity.class);

                                intent2.putExtra("searchLoc", point);
                                intent2.putExtra("cameFrom", "ClientSelectMap");

                                dialog.dismiss();
                                startActivity(intent2);
                            }
                        })
                        .setNegativeButton("لا", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mMap.clear();
                                dialog.dismiss();
                            }
                        })
                        .create();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.show();
                    }
                } ,2000);
            }
        });
    }
}