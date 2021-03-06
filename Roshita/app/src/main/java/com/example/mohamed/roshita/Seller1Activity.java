package com.example.mohamed.roshita;
//register seller
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import java.util.HashMap;

public class Seller1Activity extends AppCompatActivity {
    public static final int RequestSignInCode = 7;
    FirebaseAuth firebaseAuth;
    GoogleApiClient googleApiClient;
    SignInButton signInButton;
    DatabaseReference mDatabase;
    ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller1);

        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bar = (ProgressBar)findViewById(R.id.progBar);
        bar.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.gold), PorterDuff.Mode.SRC_IN );

        firebaseAuth = FirebaseAuth.getInstance();
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);

        // Creating and Configuring Google Sign In object.
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Creating and Configuring Google Api Client.
        googleApiClient = new GoogleApiClient.Builder(Seller1Activity.this)
                .enableAutoManage(Seller1Activity.this , new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();


        // Adding Click listener to User Sign in Google button.
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserSignInMethod();
            }
        });
    }

    // Sign In function Starts From Here.
    public void UserSignInMethod(){
        // Passing Google Api Client into Intent.
        Intent AuthIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(AuthIntent, RequestSignInCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bar.setVisibility(View.VISIBLE);

        if (requestCode == RequestSignInCode){
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (googleSignInResult.isSuccess()){
                GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
                FirebaseUserAuth(googleSignInAccount);
            }
        }
    }

    public void FirebaseUserAuth(GoogleSignInAccount googleSignInAccount) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);

        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(Seller1Activity.this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task AuthResultTask) {
                        if (AuthResultTask.isSuccessful()){
                            // Getting Current Login user details.
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference dataBaseRef = mDatabase.getRef();

                            HashMap userModel = new HashMap();
                            userModel.put("device_token" ,FirebaseInstanceId.getInstance().getToken() );
                            userModel.put("email" ,firebaseUser.getEmail() );
                            userModel.put("userName" ,firebaseUser.getDisplayName() );
                            userModel.put("online" ,false);

                            dataBaseRef.child("sellers").child(firebaseUser.getUid() ).setValue(userModel);

                            Toast.makeText(Seller1Activity.this,"تم تسـجـيـل الدخـول بنجـاح",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getBaseContext() ,Seller3Activity.class) );

                        }else {
                            Toast.makeText(Seller1Activity.this,"حـدث خـطـأ فـي المـصـادقـة",Toast.LENGTH_SHORT).show();
                            bar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}