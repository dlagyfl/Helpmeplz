package com.hufsice.nulltime;

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

import com.hufsice.nulltime.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateGroup extends AppCompatActivity implements MemberAdapter.OnMemberCheckedChangeListener {

    //레이아웃에서 사용하는 뷰 요소들의 변수 선언
    private EditText editTextGroupName;
    private Button buttonCreateGroup;
    private RecyclerView recyclerViewFriend;
    //Firebase 관련 객체들의 변수 선언
    private DatabaseReference reference;
    private FirebaseAuth firebaseAuth;
    //선택된 멤버를 저장하기 위한 리스트
    private ArrayList<Member> selectedMembers;
    //MemberAdapter 객체와 인터페이스를 구현한 메소드를 사용하기 위한 변수 선언
    private MemberAdapter friendAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        //변수 연결
        editTextGroupName = findViewById(R.id.editTextGroupName);
        buttonCreateGroup = findViewById(R.id.buttonCreateGroup);
        recyclerViewFriend = findViewById(R.id.rv_members);

        //Firebase 실시간 데이터베이스와 Firebase 인증을 사용하기 위해 필요한 초기화
        reference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        //선택된 멤버를 저장하기 위한 ArrayList 초기화
        selectedMembers = new ArrayList<>();
        //MemberAdapter와 RecyclerView 설정
        friendAdapter = new MemberAdapter(selectedMembers, this);
        recyclerViewFriend.setLayoutManager(new LinearLayoutManager(this));
        //친구 목록 recyclerVIew로 보여주기
        recyclerViewFriend.setAdapter(friendAdapter);

        //친구 목록을 로드하여 RecyclerView에 표시
        loadFriendList();

        buttonCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //사용자가 입력한 정보 groupName에 저장
                String groupName = editTextGroupName.getText().toString();

                //선택된 멤버들을 가져옴
                ArrayList<Member> selectedMembers = friendAdapter.getSelectedMembers();

                if (selectedMembers.isEmpty()) {
                    //선택된 멤버가 없을 경우 Toast 메시지 출력
                    Toast.makeText(CreateGroup.this, "그룹 멤버를 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    //그룹 생성 메소드 호출
                    GroupNameInfo newGroup = new GroupNameInfo(groupName, selectedMembers);
                    createGroup(newGroup);
                }
            }
        });

        //엔터(완료) 클릭시에 키보드 숨기는 동작
        editTextGroupName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    // 키보드 숨기기
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editTextGroupName.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }

    //그룹생성 메소드
    private void createGroup(GroupNameInfo group) {
        //선택된 멤버들을 가져옴
        ArrayList<Member> selectedMembers = friendAdapter.getSelectedMembers();
        //현재 로그인 되어 있는 사용자 UID를 가져옴
        String userId = firebaseAuth.getCurrentUser().getUid();
        //그룹에 대한 고유한 식별자 생성
        String groupId = reference.child("groups").child("users").child(userId).push().getKey();
        //DB에 저장된 로그인한 사용자 이름 접근
        DatabaseReference nameRef = reference.child("users").child(userId).child("name");

        //방장 지정 hashMap 생성
        HashMap<String, Object> hostMap = new HashMap<>();
        hostMap.put("host", userId);

        nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //로그인한 사용자 이름 userName에 저장
                String userName = dataSnapshot.getValue(String.class);

                //그룹명 저장 HashMap 생성
                HashMap<String, Object> groupMap = new HashMap<>();
                groupMap.put("name", group.getName());

                //로그인한 사용자 하위 노드에 그룹명 저장
                reference.child("groups").child("users").child(userId).child(groupId).updateChildren(groupMap);
                //로그인한 사용자 하위 노드에 그룹명 저장
                reference.child("groups").child("users").child(userId).child(groupId).updateChildren(hostMap);

                //로그인한 사용자 이름 저장 HashMap 생성
                HashMap<String, Object> currentUserMap = new HashMap<>();
                currentUserMap.put("name", userName);

                //checkbox 선택된 멤버에 대해서 반복문 실행
                //지정된 멤버 하위 노드에 멤버 정보와 그룹 정보 저장
                for (Member member : selectedMembers) {
                    //선택된 멤버의 id memberId에 저장
                    String memberId = member.getId();

                    //멤버 이름 저장 HashMap 생성
                    HashMap<String, Object> memberMap = new HashMap<>();
                    memberMap.put("name", member.getName());

                    //자기자신에 대한 정보 저장
                    reference.child("groups").child("users").child(memberId).child(groupId).child("members").child(memberId).setValue(memberMap);
                    //방장 정보 저장
                    reference.child("groups").child("users").child(memberId).child(groupId).updateChildren(hostMap);
                    //현재 로그인한 사용자에 대한 정보 저장
                    reference.child("groups").child("users").child(memberId).child(groupId).child("members").child(userId).setValue(currentUserMap);

                    //다른 멤버들에 대해서도 그룹과 멤버 정보 저장
                    for (Member otherMember : selectedMembers) {
                        String otherMemberId = otherMember.getId();

                        if (!otherMemberId.equals(memberId)) {
                            //선택된 멤버와 다른 멤버 간의 그룹 및 멤버 정보 저장
                            reference.child("groups").child("users").child(otherMemberId).child(groupId).child("members").child(memberId).setValue(memberMap);
                            reference.child("groups").child("users").child(otherMemberId).child(groupId).child("members").child(userId).setValue(currentUserMap);
                        }
                    }

                    reference.child("groups").child("users").child(userId).child(groupId).child("members").child(memberId).setValue(memberMap);
                    reference.child("groups").child("users").child(memberId).child(groupId).child("name").setValue(group.getName());
                }

                //로그인한 사용자 자기자신에 대한 정보 저장
                reference.child("groups").child("users").child(userId).child(groupId).child("members").child(userId).setValue(currentUserMap);

                //그룹생성 완료 버튼 클릭하면 성공 toast 발생
                Toast.makeText(CreateGroup.this, "그룹을 생성하였습니다", Toast.LENGTH_SHORT).show();
                //editText 초기화
                editTextGroupName.setText(null);

                //그룹생성 완료 버튼 클릭시에 GroupList Activity로 intent
                Intent intent = new Intent(CreateGroup.this, GroupList.class);
                startActivity(intent);
                finish();

            }

            //실패 오류 메시지 발생
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", databaseError.getMessage());
            }
        });
    }



    //친구 목록을 불러와서 RecyclerView에 표시
    private void loadFriendList() {
        String userId = firebaseAuth.getCurrentUser().getUid();
        //DB에 저장된 친구 목록에 접근
        DatabaseReference friendRef = reference.child("users").child(userId).child("friendlist");

        friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                selectedMembers.clear();

                //dataSnapshot으로 friendlist에 저장된 친구 목록 읽어옴
                for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                    String friendId = friendSnapshot.getKey();
                    String friendName = friendSnapshot.child("name").getValue(String.class);
                    selectedMembers.add(new Member(friendName, friendId));
                }
                //RecyclerView 업데이트
                friendAdapter.notifyDataSetChanged();
            }

            //실패 오류 메시지 발생
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", databaseError.getMessage());
            }
        });

    }


    //멤버의 체크 상태가 변경될 때 호출 되는 메소드
    @Override
    public void onMemberCheckedChange(Member member, boolean isChecked) {

        //체크 되었을 경우
        if (isChecked) {
            //selectedMembers 리스트에 멤버가 포함 되어 있지 않은 경우에만 추가
            if (!selectedMembers.contains(member)) {
                selectedMembers.add(member);
            }
        } else {
            //체크 해제 되었을 경우
            selectedMembers.remove(member);
        }
    }
}