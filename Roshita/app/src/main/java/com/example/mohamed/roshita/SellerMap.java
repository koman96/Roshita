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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SellerMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        handler = new Handler();

        final Intent intent = getIntent();
        double[] point = intent.getDoubleArrayExtra("centerMap");

        LatLng centerMap = new LatLng(point[0] ,point[1] );

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerMap ,14) );

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SellerMap.this ,"إضغـط علـي مكـان الصـيدليـة الخاصـة بك" ,Toast.LENGTH_LONG).show();
            }
        } ,2000);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("العنـوان") );

                final AlertDialog dialog = new AlertDialog.Builder(SellerMap.this).setTitle("عـنوان الصيـدلية")
                        .setMessage("تأكيد إختيار هذا المـكان ؟")
                        .setPositiveButton("نعـم", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String pharmName = intent.getExtras().getString("pharmName");
                                String pharmPhone = intent.getExtras().getString("pharmPhone");
                                Boolean delivery = intent.getExtras().getBoolean("delivery");

                                PharmModel model = new PharmModel(pharmName ,pharmPhone ,latLng.longitude ,latLng.latitude ,delivery);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("pharmacy");
                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                ref.child(userId).setValue(model);

                                startActivity(new Intent(SellerMap.this ,Seller4Activity.class ));
                                Toast.makeText(SellerMap.this ,"تم إنشـاء الصيـدلية الخاصة بك بنجاح" ,Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("لا", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mMap.clear();
                                dialog.dismiss();
                            }
                        }).create();

                handler.postDelayed(new Runnable() {    //give user adv to see the point he choose
                    @Override
                    public void run() {
                        dialog.show();
                    }
                } ,2000);
            }
        });
    }
}