package com.example.helpmeplz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddFriend extends AppCompatActivity {
    private String friendUID;
    private String friendName;
    private DatabaseReference database;
    private FirebaseAuth firebaseAuth;
    TextView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        database = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        view = findViewById(R.id.textView_friend_list);
        Button button_Accept = findViewById(R.id.button_accept);
        Button button_Refuse = findViewById(R.id.button_refuse);

        button_Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MainActivity", "onCreate - onClick : " + friendUID);
                // 친구 DB에 내 UID 넣기
                database.child("users").child(friendUID).child("friendlist").child("IYFpUCw26AQRYfKTVgoRazAR1oC2").child("name").setValue("test2");
                // 내 DB에 친구 UID 넣기
                database.child("users").child("IYFpUCw26AQRYfKTVgoRazAR1oC2").child("friendlist").child(friendUID).child("name").setValue(friendName);
                // 내 DB의 friendrequest에서 친구 추가한 UID 삭제하기
                Log.d("MainActivity", "onCreate - onClick : " + 1);
                database.getRef().child("users").child("IYFpUCw26AQRYfKTVgoRazAR1oC2").child("friendrequest").child(friendUID).removeValue();
                Log.d("MainActivity", "onCreate - onClick : " + 2);
//                database.child("users").child("IYFpUCw26AQRYfKTVgoRazAR1oC2").child("friendrequest").child(friendUID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Intent myIntent = new Intent(AddFriend.this, CheckFriend.class);
//                        startActivity(myIntent);
//                        finish();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });

                Intent myIntent3 = new Intent(AddFriend.this, CheckFriend.class);
                Log.d("MainActivity", "onCreate - onClick : " + 3);
                startActivity(myIntent3);
                finish();
            }
        });
        button_Refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.child("users").child("IYFpUCw26AQRYfKTVgoRazAR1oC2").child("friendrequest").child(friendUID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    // 38D30CEbX4ew1Hj3QcIuG0Ea6WH2
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

                Intent myIntent2 = new Intent(AddFriend.this, MyFriend.class);
                startActivity(myIntent2);
                finish();
            }
        });

//        getGroups();
        getUID();
    }

    private void getUID() {
        database.child("users").child("IYFpUCw26AQRYfKTVgoRazAR1oC2").child("friendrequest").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    friendUID = snapshot.getKey();
                    Log.d("MainActivity", "ValueEventListener - onDataChange : " + friendUID);
                }
                Log.d("MainActivity", "ValueEventListener - onDataChange : " + friendUID);
                getName(friendUID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching groups: " + databaseError.getMessage());
            }
        });
    }
    private void getName(String UID) {
        database.child("search").child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    friendName = (String) snapshot.getValue();
                    Log.d("MainActivity", "ValueEventListener - onDataChange : " + friendName);
                }
                Log.d("MainActivity", "ValueEventListener - onDataChange : " + friendName);
                view.setText(friendName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
