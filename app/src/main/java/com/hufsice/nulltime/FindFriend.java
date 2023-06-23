package com.hufsice.nulltime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        //button, 파이어베이스 데이터베이스 변수선언
        buttonBack = findViewById(R.id.button_prev);
        buttonAddFriend = findViewById(R.id.button_next);

        database = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        check = true;

        //로그인 되어있는 유저의 UID를 받아옴
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
//            Log.d("uid", userId);
        }

        //뒤로가기 버튼
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent6 = new Intent(FindFriend.this, MyFriend.class);
                startActivity(myIntent6);
                finish();
            }
        });

        //친구추가 버튼
        buttonAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                addFriend();
            }
        });
    }

    //친구추가 함수
    private void addFriend() {
        String nickname = ((EditText) findViewById(R.id.editText_friend_id)).getText().toString();
//        Log.d("MainActivity", "FindFriend - addFriend : " + nickname);

        //친구추가할 유저의 UID를 받아옴
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

                //친구찾기에 실패하면 FindFriendFailed로 이동
                Intent myIntent = new Intent(FindFriend.this, FindFriendFailed.class);
                startActivity(myIntent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching groups: " + databaseError.getMessage());
            }
        });
    }

    //친구추가 성공여부 확인및 액티비티이동
    private void validation() {
        database.child("users").child(userId).child("friendlist").child(requestUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("MainActivity", "ChildEventListener - onChildChanged : " + dataSnapshot.exists() + " friendcheck");
                if (check && dataSnapshot.exists()) {//친구찾기에 성공시 FindFriendExists로 이동
                    Intent myIntent = new Intent(FindFriend.this, FindFriendExists.class);
                    startActivity(myIntent);
                    finish();
                } else {
                    // friendrequest에 내 UID 넣기
                    database.child("users").child(requestUID).child("friendrequest").child(userId).setValue("");
                    Log.d("MainActivity", "addFriend - onDataChange : " + 1111);
                    check = false;

                    Intent myIntent = new Intent(FindFriend.this, FindFriendComplete.class);
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