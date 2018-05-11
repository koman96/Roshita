package com.example.mohamed.roshita;

import android.app.*;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyConnection {
    private Context context;

    public MyConnection(Context context){   this.context = context;     }


    public boolean getConnection(){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if(netInfo == null || ! netInfo.isConnectedOrConnecting() ){
            Toast.makeText(context ,"تحقـق مـن إتصـال الإنتـرنـت" ,Toast.LENGTH_LONG).show();
            return false;
        }
        else
            return true;
    }

    public void checkConnection(){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnectedOrConnecting()){
            Toast.makeText(context ,"تحـقـق مـن إتصـال الإنتـرنـت" ,Toast.LENGTH_LONG).show();

            new Handler().postDelayed(new Runnable() {      //close app after 2 seconds
                @Override
                public void run() {
                    System.exit(0);
                }
            } ,2000);
        }
    }

    public void exitApp(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}