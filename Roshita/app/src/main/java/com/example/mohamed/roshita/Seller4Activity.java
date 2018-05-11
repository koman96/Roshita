package com.example.mohamed.roshita;
//showing suggested medicines
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Seller4Activity extends AppCompatActivity {
    Button confirmBtn;
    RecyclerView listView;
    ArrayList<Integer>indexList;
    ArrayList<String> medList;
    String userId;
    ProgressBar bar;
    private MyConnection myConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller4);

        myConnection = new MyConnection(getApplicationContext() );
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        bar = (ProgressBar)findViewById(R.id.progBar);
        bar.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.gold), PorterDuff.Mode.SRC_IN );

        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        confirmBtn = (Button)findViewById(R.id.confirmBtn);
        listView = (RecyclerView)findViewById(R.id.listView);
        listView.setLayoutManager(new LinearLayoutManager(Seller4Activity.this ,LinearLayoutManager.VERTICAL ,false) );

        getSuggestedMeds();     //and display in list

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            //if he saved what he choosed
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() ) {
                    bar.setVisibility(View.VISIBLE);

                    if (checkListEntry()) {    //check if he selected meds
                        if (indexList.size() > 0) {      //index of meds selected at recyclerView
                            bar.setVisibility(View.VISIBLE);

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("store");

                            for (int i = 0; i < indexList.size(); i++) {
                                RecyclerView.ViewHolder holder = listView.getChildViewHolder(listView.getChildAt(indexList.get(i) ));
                                View row = holder.itemView;

                                TextView medName = (TextView) row.findViewById(R.id.medName);
                                EditText medPrice = (EditText) row.findViewById(R.id.medPrice);

                                ref.child(userId).child(medName.getText().toString() ).setValue(medPrice.getText().toString() );
                            }

                            Toast.makeText(Seller4Activity.this, "تم تسجيـل أدويتـك بنجاح", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext() ,Seller5Activity.class));
                        }
                    }
                    bar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void getSuggestedMeds(){
        StorageReference ref = FirebaseStorage.getInstance().getReference("sugMeds.txt");

        try {
            final File sugFile = File.createTempFile("sugMeds" ,"txt");
            ref.getFile(sugFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //file has downloaded succesfully
                    medList = new ArrayList<>();

                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(sugFile) );
                        String medName;

                        while ( (medName = reader.readLine() ) != null){
                            medList.add(medName);
                        }
                        reader.close();

                        Adapter adapter = new Adapter(medList);
                        listView.setAdapter(adapter);

                        bar.setVisibility(View.INVISIBLE);

                    } catch (IOException e) {
                        Toast.makeText(Seller4Activity.this ,"حدث خطأ أثناء عـرض الأدوية المقترحة" ,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (IOException e) {
            Toast.makeText(Seller4Activity.this ,"حـدث خطأ أثناء عـرض الأدويـة المقتـرحة" ,Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkListEntry(){
        int listLength = listView.getAdapter().getItemCount();

        boolean confirm = false;
        indexList = new ArrayList<>();

        for (int i=0 ; i<listLength ; i++){
            RecyclerView.ViewHolder holder = listView.getChildViewHolder(listView.getChildAt(i) );
            View row = holder.itemView;

            CheckBox available = (CheckBox) row.findViewById(R.id.available);

            if (available.isChecked() ){
                EditText medPrice = (EditText) row.findViewById(R.id.medPrice);

                if ( ! medPrice.getText().toString().equals("") ){
                    confirm = true;
                    indexList.add(i);
                }
                else {
                    Toast.makeText(Seller4Activity.this ,"برجاء إدخال أسعار الأدويـة التي تم تحديدهـا" ,Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        if (!confirm)
            Toast.makeText(Seller4Activity.this ,"لم يتم تحـديـد أي دواء" ,Toast.LENGTH_SHORT).show();

        return confirm;
    }

    @Override
    public void onBackPressed() {
        myConnection.exitApp();
    }
}