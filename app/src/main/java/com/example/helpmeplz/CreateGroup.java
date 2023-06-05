package com.example.helpmeplz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateGroup extends AppCompatActivity implements MemberAdapter.OnMemberCheckedChangeListener {

    private EditText editTextGroupName;
    private Button buttonCreateGroup;
    private DatabaseReference reference;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Member> selectedMembers;

    private MemberAdapter friendAdapter;
    private RecyclerView recyclerViewFriend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        editTextGroupName = findViewById(R.id.editTextGroupName);
        buttonCreateGroup = findViewById(R.id.buttonCreateGroup);

        reference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        selectedMembers = new ArrayList<>();
        friendAdapter = new MemberAdapter(selectedMembers, this);
        recyclerViewFriend = findViewById(R.id.rv_members);
        recyclerViewFriend.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFriend.setAdapter(friendAdapter);

        loadFriendList();

        buttonCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = editTextGroupName.getText().toString();

                ArrayList<Member> selectedMembers = friendAdapter.getSelectedMembers();

                if (selectedMembers.isEmpty()) {
                    Toast.makeText(CreateGroup.this, "그룹 멤버를 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    GroupNameInfo newGroup = new GroupNameInfo(groupName, selectedMembers);
                    createGroup(newGroup);
                }
            }
        });

        editTextGroupName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editTextGroupName.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }

    private void createGroup(GroupNameInfo group) {
        ArrayList<Member> selectedMembers = friendAdapter.getSelectedMembers();
        String userId = firebaseAuth.getCurrentUser().getUid();
        String groupId = reference.child("groups").child("users").child(userId).push().getKey();
        DatabaseReference nameRef = reference.child("users").child(userId).child("name");

        HashMap<String, Object> hostMap = new HashMap<>();
        hostMap.put("host", userId);

        nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.getValue(String.class);

                HashMap<String, Object> groupMap = new HashMap<>();
                groupMap.put("name", group.getName());


                reference.child("groups").child("users").child(userId).child(groupId).updateChildren(groupMap);

                reference.child("groups").child("users").child(userId).child(groupId).updateChildren(hostMap);

                for (Member member : selectedMembers) {
                    String memberId = member.getId();
                    HashMap<String, Object> memberMap = new HashMap<>();
                    memberMap.put("name", member.getName());

                    reference.child("groups").child("users").child(memberId).child(groupId).child("members").child(memberId).setValue(memberMap);

                    reference.child("groups").child("users").child(memberId).child(groupId).updateChildren(hostMap);

                    HashMap<String, Object> currentUserMap = new HashMap<>();
                    currentUserMap.put("name", userName);
                    reference.child("groups").child("users").child(memberId).child(groupId).child("members").child(userId).setValue(currentUserMap);

                    for (Member otherMember : selectedMembers) {
                        String otherMemberId = otherMember.getId();
                        if (!otherMemberId.equals(memberId)) {
                            reference.child("groups").child("users").child(otherMemberId).child(groupId).child("members").child(memberId).setValue(memberMap);

                            currentUserMap = new HashMap<>();
                            currentUserMap.put("name", userName);
                            reference.child("groups").child("users").child(otherMemberId).child(groupId).child("members").child(userId).setValue(currentUserMap);
                        }
                    }
                }

                HashMap<String, Object> currentUserMap = new HashMap<>();
                currentUserMap.put("name", userName);
                reference.child("groups").child("users").child(userId).child(groupId).child("members").child(userId).setValue(currentUserMap);
                HashMap<String, Object> memberMap = new HashMap<>();
                for (Member member : selectedMembers) {
                    String memberId = member.getId();
                    memberMap.put("name", member.getName());
                    reference.child("groups").child("users").child(userId).child(groupId).child("members").child(memberId).setValue(memberMap);
                }

                for (Member member : selectedMembers) {
                    String memberId = member.getId();
                    reference.child("groups").child("users").child(memberId).child(groupId).child("name").setValue(group.getName());
                }

                Toast.makeText(CreateGroup.this, "그룹을 생성하였습니다", Toast.LENGTH_SHORT).show();
                editTextGroupName.setText(null);

                Intent intent = new Intent(CreateGroup.this, GroupList.class);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching name: " + databaseError.getMessage());
            }
        });
    }




    private void loadFriendList() {
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference friendRef = reference.child("users").child(userId).child("friendlist");

        friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                selectedMembers.clear(); // Clear the existing friend list

                // Read friend data from dataSnapshot and add them to friendList
                for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                    String friendId = friendSnapshot.getKey();
                    String friendName = friendSnapshot.child("name").getValue(String.class);
                    selectedMembers.add(new Member(friendName, friendId));
                }
                // Update the RecyclerView
                friendAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

    }


    @Override
    public void onMemberCheckedChange(Member member, boolean isChecked) {

        if (isChecked) {
            if (!selectedMembers.contains(member)) {
                selectedMembers.add(member);
            }
        } else {
            selectedMembers.remove(member);
        }
    }
}