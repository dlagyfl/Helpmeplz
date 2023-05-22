package com.example.helpmeplz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class GroupList extends AppCompatActivity {

    private ListView listViewGroup;
    private Button btn_makeGroup;
    private ArrayAdapter<String> groupListAdapter;
    private DatabaseReference database;
    private FirebaseAuth firebaseAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        listViewGroup = findViewById(R.id.listViewGroup);
        btn_makeGroup = findViewById(R.id.btn_makeGroup);

        database = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        btn_makeGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupList.this, CreateGroup.class);
                startActivity(intent);
            }
        });


        groupListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listViewGroup.setAdapter(groupListAdapter);

        getGroups();
    } //onCreate

    private void getGroups() {
        String userId = firebaseAuth.getCurrentUser().getUid();

        database.child("groups").child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> groupList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroupNameInfo group = snapshot.getValue(GroupNameInfo.class);
                    String groupName = group.getName();
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


}