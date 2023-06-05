package com.example.helpmeplz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FindFriend extends AppCompatActivity {
    private String userId;
    private String requestUID;
    private Button buttonBack;
    private Button buttonAddFriend;
    private DatabaseReference database;
    private FirebaseAuth firebaseAuth;

    private boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        buttonBack = findViewById(R.id.button_prev);
        buttonAddFriend = findViewById(R.id.button_next);

        database = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        check = true;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
//            Log.d("uid", userId);
        }

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent6 = new Intent(FindFriend.this, MyFriend.class);
                startActivity(myIntent6);
                finish();
            }
        });

        buttonAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                addFriend();
            }
        });
    }

    private void addFriend() {
        String nickname = ((EditText) findViewById(R.id.editText_friend_id)).getText().toString();
//        Log.d("MainActivity", "FindFriend - addFriend : " + nickname);

        database.child("search").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Log.d("MainActivity", "FindFriend - addFriend - onDataChange : " + nickname);
                    if (nickname.equals(snapshot.getKey())) {
//                        Log.d("MainActivity", "addFriend - onDataChange : " + snapshot.getKey());
//                        Log.d("MainActivity", "addFriend - onDataChange : " + snapshot.getValue().toString().substring(1, 29));
                        requestUID = snapshot.getValue().toString().substring(1, 29);
                        validation();
                    }
                }

                Intent myIntent = new Intent(FindFriend.this, AddFriendFailed.class);
                startActivity(myIntent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching groups: " + databaseError.getMessage());
            }
        });
    }

    private void validation() {
        database.child("users").child(userId).child("friendlist").child(requestUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("MainActivity", "ChildEventListener - onChildChanged : " + dataSnapshot.exists() + " friendcheck");
                if (check && dataSnapshot.exists()) {
                    Intent myIntent = new Intent(FindFriend.this, AddFriendExists.class);
                    startActivity(myIntent);
                    finish();
                } else {
                    // friendrequest에 내 UID 넣기
                    database.child("users").child(requestUID).child("friendrequest").child(userId).setValue("");
                    Log.d("MainActivity", "addFriend - onDataChange : " + 1111);
                    check = false;

                    Intent myIntent = new Intent(FindFriend.this, AddFriendComplete.class);
                    startActivity(myIntent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching friends: " + databaseError.getMessage());
            }
        });
    }
}