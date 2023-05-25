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
import android.widget.ImageButton;
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
    private ImageButton btn_makeGroup;
    private ArrayAdapter<Object> groupListAdapter;
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

        groupListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listViewGroup.setAdapter(groupListAdapter);

        getGroups();

        listViewGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedGroup = (String)groupListAdapter.getItem(position);

                Intent intent = new Intent(GroupList.this, MyGroup.class);
                intent.putExtra("groupName", selectedGroup);
                startActivity(intent);
            }
        });

        btn_makeGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupList.this, CreateGroup.class);
                startActivity(intent);
                finish();
            }
        });
    } //onCreate

    private void getGroups() {
        String userId = firebaseAuth.getCurrentUser().getUid();

        database.child("groups").child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Object> groupList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String groupName = snapshot.getValue(String.class);
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