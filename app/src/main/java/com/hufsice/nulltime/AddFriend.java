package com.hufsice.nulltime;

import static android.os.SystemClock.sleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hufsice.nulltime.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddFriend extends AppCompatActivity {
    private String friendId;
    private String friendName;
    private String userId;
    private String userName;

    private DatabaseReference database;
    TextView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        view = findViewById(R.id.textView_friend_list);
        Button button_Accept = findViewById(R.id.button_accept);
        Button button_Refuse = findViewById(R.id.button_refuse);

        database = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            userId = user.getUid();
            Log.d("uid", userId);
        }
        database.child("users").child(userId).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("MainActivity", "ValueEventListener - onDataChange : " + (String) dataSnapshot.getValue());
                userName = (String) dataSnapshot.getValue();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching friends: " + databaseError.getMessage());
            }
        });

        button_Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MainActivity", "onCreate - onClick : " + friendId);
                // 친구 DB에 내 UID 넣기
                database.child("users").child(friendId).child("friendlist").child(userId).child("name").setValue(userName);
                // 내 DB에 친구 UID 넣기
                database.child("users").child(userId).child("friendlist").child(friendId).child("name").setValue(friendName);
                // 내 DB의 friendrequest에서 친구 추가한 UID 삭제하기
                Log.d("MainActivity", "onCreate - onClick : " + 1);
                sleep(500);
                database.getRef().child("users").child(userId).child("friendrequest").child(friendId).removeValue();
                Log.d("MainActivity", "onCreate - onClick : " + 2);

                Log.d("MainActivity", "onCreate - onClick : " + friendName + " before intent");
                Intent intent = new Intent(AddFriend.this, CheckFriend.class);
                intent.putExtra("nickname", friendName);
                startActivity(intent);
                database.getRef().child("users").child(userId).child("friendrequest").child(friendId).removeValue();
                finish();
                Log.d("MainActivity", "onCreate - onClick : " + friendName + " after intent");
            }
        });
        button_Refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.child("users").child(userId).child("friendrequest").child(friendId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
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

        getUID();
    }

    private void getUID() {

        database.child("users").child(userId).child("friendrequest").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    friendId = snapshot.getKey();
                    Log.d("MainActivity", "ValueEventListener - onDataChange : " + friendId);
                }
                Log.d("MainActivity", "ValueEventListener - onDataChange : " + friendId);
                getName(friendId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching friends: " + databaseError.getMessage());
            }
        });
    }
    private void getName(String UID) {
        database.child("users").child(UID).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("MainActivity", "ValueEventListener - onDataChange : " + (String) dataSnapshot.getValue());
                friendName = (String) dataSnapshot.getValue();
                view.setText(friendName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching friends: " + databaseError.getMessage());
            }
        });
    }
}
