package com.hufsice.nulltime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyFriend extends AppCompatActivity {
    private long cnt = 0;
    private String userId;
    private ArrayAdapter<String> friendListAdapter;
    private ListView listViewFriend;
    private DatabaseReference database;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friend);

        listViewFriend = findViewById(R.id.listViewFriend);
        ImageButton button_bellButton = (ImageButton) findViewById(R.id.bellImageButton);
        ImageButton button_moreButton = (ImageButton) findViewById(R.id.moreImageButton);

        database = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            userId = user.getUid();
            Log.d("uid", userId);
        }

        button_moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MyFriend.this, FindFriend.class);
                startActivity(myIntent);
                finish();
            }
        });

        button_bellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cnt != 0) {
                    Log.d("MainActivity", "onClick - onDataChange : " + "true");
                    Intent myIntent = new Intent(MyFriend.this, AddFriend.class);
                    startActivity(myIntent);
                    finish();
                }
                else {
                    Log.d("MainActivity", "onClick - onDataChange : " + "false");
                    Intent myIntent = new Intent(MyFriend.this, AddFriendFailed.class);
                    startActivity(myIntent);
                    finish();
                }
            }
        });

        friendListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listViewFriend.setAdapter(friendListAdapter);

        getFriends();
        getRequest();
    }

    private void getFriends() {
        database.child("users").child(userId).child("friendlist").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> friendList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FriendNameInfo friend = snapshot.getValue(FriendNameInfo.class);
                    assert friend != null;
                    String friendName = friend.getName();
                    Log.d("MainActivity", "ChildEventListener - onChildChanged : " + friendName);
                    friendList.add(friendName);
                }
                friendListAdapter.clear();
                friendListAdapter.addAll(friendList);
                friendListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching friends: " + databaseError.getMessage());
            }
        });
    }

    private void getRequest() {
        database.child("users").child(userId).child("friendrequest").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cnt = dataSnapshot.getChildrenCount();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching friends: " + databaseError.getMessage());
            }
        });
    }
}
