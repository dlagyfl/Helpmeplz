package com.example.helpmeplz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyFriend extends AppCompatActivity {
    private ListView listViewFriend;
    private ArrayAdapter<String> groupListAdapter;

    long childrencount = 0;
    private DatabaseReference database;
    private FirebaseAuth firebaseAuth;

    @SuppressLint("MissingInflatedId") //모름..
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friend);

        listViewFriend = findViewById(R.id.listViewFriend);
        ImageButton button_bellButton = (ImageButton) findViewById(R.id.bellImageButton);
        ImageButton button_moreButton = (ImageButton) findViewById(R.id.moreImageButton);

        database = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        button_bellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (childrencount != 0) {
                    Log.d("MainActivity", "onClick - onDataChange : " + "true");
                    Intent myIntent = new Intent(MyFriend.this, AddFriend.class);
                    startActivity(myIntent);
                    finish();
                }
                else {
                    Log.d("MainActivity", "onClick - onDataChange : " + "false");
                    Intent myIntent = new Intent(MyFriend.this, NoFriendRequest.class);
                    startActivity(myIntent);
                    finish();
                }
            }
        });

        button_moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MyFriend.this, FindFriend.class);
                startActivity(myIntent);
                finish();
            }
        });

        groupListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listViewFriend.setAdapter(groupListAdapter);

        getGroups();
        getRequest();
    }

    private void getGroups() {
        database.child("users").child("IYFpUCw26AQRYfKTVgoRazAR1oC2").child("friendlist").addValueEventListener(new ValueEventListener() {

//        database.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> groupList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Log.d("MainActivity", "ChildEventListener - onChildChanged : " + snapshot.getKey());
                    FriendNameInfo group = snapshot.getValue(FriendNameInfo.class);
                    String groupName = group.getName();
                    Log.d("MainActivity", "ChildEventListener - onChildChanged : " + groupName);
                    groupList.add(groupName);
                }
                groupListAdapter.clear();
                groupListAdapter.addAll(groupList);
                groupListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching groups: " + databaseError.getMessage());
            }
        });
    }

    private void getRequest() {
        database.child("users").child("IYFpUCw26AQRYfKTVgoRazAR1oC2").child("friendrequest").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                childrencount = dataSnapshot.getChildrenCount();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching groups: " + databaseError.getMessage());
            }
        });
    }
}
