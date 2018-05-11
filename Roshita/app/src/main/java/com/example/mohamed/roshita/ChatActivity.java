package com.example.mohamed.roshita;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private String receiverId ,myId ,receiverType;
    private DatabaseReference ref;
    private String message;
    private EditText messageEdit;
    private TextView receiverHeader;
    private ArrayList<Messages> messageList = new ArrayList<>();
    private MessageAdapter adapter;
    private RecyclerView chatList;
    private static final int GALERY_PICK = 1;
    private StorageReference imagePath;
    private ProgressBar bar;
    private UserModel myModel ,receiverModel;
    private boolean iamSELLER = false;
    private ArrayList<String> notfChatList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = (Toolbar)findViewById(R.id.chatTollbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        receiverHeader = (TextView)findViewById(R.id.receiverEmail);
        chatList = (RecyclerView)findViewById(R.id.chatList);

        ImageView sendIcon ,addPhoto;
        sendIcon = (ImageView)findViewById(R.id.sendIcon);
        addPhoto = (ImageView)findViewById(R.id.imageIcon);
        messageEdit = (EditText)findViewById(R.id.messageEdit);

        bar = (ProgressBar) findViewById(R.id.progBar);
        bar.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.gold), PorterDuff.Mode.SRC_IN );

        myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        updateOnlineState();

        final Intent intent = getIntent();
        receiverId = intent.getStringExtra("receiverId");
        receiverType = intent.getStringExtra("receiverType");

        getChatersInfo();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (! intent.hasExtra("receiverName") )
                    receiverHeader.setText(receiverModel.getUserName() );
                else
                    receiverHeader.setText(intent.getStringExtra("receiverName") );
            }
        } ,1000);


        chatList.setLayoutManager(new LinearLayoutManager(getApplicationContext() ,LinearLayoutManager.VERTICAL ,false) );
        adapter = new MessageAdapter(getApplicationContext() ,messageList);
            chatList.setAdapter(adapter);

        loadMessages();
        deleteMessageNotifications();


    //Listeners
        sendIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = messageEdit.getText().toString();

                if (! TextUtils.isEmpty(message) )
                    sendMessage();
            }
        });

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser( galleryIntent ,"SELECT IMAGE") ,GALERY_PICK);
            }
        });
    }

    private void deleteMessageNotifications() {
        DatabaseReference messRef = FirebaseDatabase.getInstance().getReference("notifications").child(myId).child("messages");
        messRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() ){
                    dataSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        messRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String senderId = dataSnapshot.child("senderId").getValue(String.class);
                notfChatList.remove(senderId);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }


    //Functions
    private void sendMessage(){
        Map messageMap = new HashMap();

        messageMap.put("message" ,message);
        messageMap.put("type" ,"text");
        messageMap.put("from" ,myId);

        ref = FirebaseDatabase.getInstance().getReference("messages");
        String messageId = ref.child(myId).child(receiverId).push().getKey();

        ref.child(myId).child(receiverId).child(messageId).setValue(messageMap);
        ref.child(receiverId).child(myId).child(messageId).setValue(messageMap);

        messageEdit.setText("");

        //send notification to other user
        sendNotificationToUser();
    }

    private void loadMessages() {

        ref = FirebaseDatabase.getInstance().getReference("messages").child(myId).child(receiverId);
        ref.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages m = dataSnapshot.getValue(Messages.class);
                messageList.add(m);

                chatList.setAdapter(adapter);
                chatList.scrollToPosition( messageList.size()-1 );
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    protected void onActivityResult(int requestCode ,int resultCode ,Intent data){
        super.onActivityResult(requestCode ,resultCode ,data);

        if (requestCode == GALERY_PICK && resultCode == RESULT_OK){
            Toast.makeText(getApplicationContext() ,"جاري تحميل الصورة" ,Toast.LENGTH_LONG).show();
            bar.setVisibility(View.VISIBLE);

            Uri imageUri = data.getData();

            ref = FirebaseDatabase.getInstance().getReference("messages");
            final String messageId = ref.child(myId).child(receiverId).push().getKey();

            imagePath = FirebaseStorage.getInstance().getReference("message_images").child(messageId+".jpg");

            imagePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful() ){
                        String downloadImage = task.getResult().getDownloadUrl().toString();

                        Map messageMap = new HashMap();
                        messageMap.put("message" ,downloadImage);
                        messageMap.put("type" ,"image");
                        messageMap.put("from" ,myId);

                        ref.child(myId).child(receiverId).child(messageId).setValue(messageMap);
                        ref.child(receiverId).child(myId).child(messageId).setValue(messageMap);

                        sendNotificationToUser();

                        bar.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }

    private void updateOnlineState() {
        final DatabaseReference sellerRef = FirebaseDatabase.getInstance().getReference("sellers");

        sellerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid() )) {   //i'am seller

                    final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("sellers").child(FirebaseAuth.getInstance().getCurrentUser().getUid() );
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            userRef.child("online").onDisconnect().setValue(false);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }

                else {
                    final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("clients").child(FirebaseAuth.getInstance().getCurrentUser().getUid() );
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            userRef.child("online").onDisconnect().setValue(false);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                final DatabaseReference sellerRef = FirebaseDatabase.getInstance().getReference("sellers");
                final DatabaseReference clientRef = FirebaseDatabase.getInstance().getReference("clients");

                sellerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid() )) {   //i'am seller
                            iamSELLER = true;
                            sellerRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(true);
                        }

                        else
                            clientRef.child( FirebaseAuth.getInstance().getCurrentUser().getUid() ).child("online").setValue(true);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
        } ,1000);
    }

    @Override
    protected void onStop() {
        super.onStop();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        if (iamSELLER)
            myRef.child("sellers").child(FirebaseAuth.getInstance().getCurrentUser().getUid() ).child("online").setValue(false);
        else
            myRef.child("clients").child(FirebaseAuth.getInstance().getCurrentUser().getUid() ).child("online").setValue(false);
    }

    private void sendNotificationToUser(){
        if (! notfChatList.contains(receiverId) ) {     //don't create notification if i have one for this user
            String date = Calendar.getInstance().getTime().toString();

            HashMap map = new HashMap();
            map.put("sender", myModel.getUserName() );
            map.put("senderId" ,myId);

            ref = FirebaseDatabase.getInstance().getReference("notifications").child(receiverId).child("messages");
            ref.child(date).setValue(map);
            notfChatList.add(receiverId);
        }
    }

    private void getChatersInfo(){

        DatabaseReference sellerRef = FirebaseDatabase.getInstance().getReference("sellers");
        DatabaseReference clientRef = FirebaseDatabase.getInstance().getReference("clients");

        if (receiverType.equals("seller") ) {

            sellerRef.child(receiverId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    receiverModel = dataSnapshot.getValue(UserModel.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

            clientRef.child(myId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot2) {
                    myModel = dataSnapshot2.getValue(UserModel.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
        else {
            clientRef.child(receiverId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot1) {
                    receiverModel = dataSnapshot1.getValue(UserModel.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

            sellerRef.child(myId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot3) {
                    myModel = dataSnapshot3.getValue(UserModel.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }
}