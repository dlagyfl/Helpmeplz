package com.hufsice.nulltime;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hufsice.nulltime.R;

import java.util.ArrayList;

//멤버 목록을 보여주기 위한 RecyclerView의 어댑터
public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    //멤버 목록 데이터를 담는 리스트
    private ArrayList<Member> memberList;
    //멤버 체크 변경 이벤트 리스너
    private OnMemberCheckedChangeListener listener;
    //선택된 멤버 리스트
    private ArrayList<Member> selectedMembers;

    public MemberAdapter(ArrayList<Member> memberList, OnMemberCheckedChangeListener listener) {
        this.memberList = memberList;
        this.listener = listener;
        this.selectedMembers = new ArrayList<>();
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //뷰 홀더를 생성하는 메소드
        //레이아웃 파일을 inflate하여 뷰 객체를 생성하고, 이를 MemberViewHolder에 넘겨줌
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_members, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        //뷰 홀더와 데이터를 바인딩하는 메소드
        //해당 position에 있는 멤버들을 가져와서 뷰 홀더에 표시함
        Member member = memberList.get(position);
        holder.bind(member);
    }

    @Override
    public int getItemCount() {
        //데이터 개수 반환
        return memberList.size();
    }

    public ArrayList<Member> getSelectedMembers() {
        //선택된 멤버 리스트를 반환하는 메소드
        return selectedMembers;
    }

    public interface OnMemberCheckedChangeListener {
        //멤버 체크 변경 이벤트 리스너 인터페이스
        void onMemberCheckedChange(Member member, boolean isChecked);
    }


    public class MemberViewHolder extends RecyclerView.ViewHolder {

        //멤버를 나타내는 체크박스
        private CheckBox checkBoxMember;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxMember = itemView.findViewById(R.id.checkbox_member);

            checkBoxMember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Member member = memberList.get(position);
                        listener.onMemberCheckedChange(member, isChecked);

                        //선택된 멤버 업데이트
                        if (isChecked) {
                            selectedMembers.add(member);
                        } else {
                            selectedMembers.remove(member);
                        }
                    }
                }
            });
        }


        public void bind(Member member) {
            checkBoxMember.setText(member.getName());
            checkBoxMember.setChecked(selectedMembers.contains(member));

            checkBoxMember.setOnCheckedChangeListener(null);  //기존의 리스너 해제

            checkBoxMember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Member member = memberList.get(position);
                        listener.onMemberCheckedChange(member, isChecked);

                        // Update selectedMembers list
                        if (isChecked) {
                            selectedMembers.add(member);
                        } else {
                            selectedMembers.remove(member);
                        }
                    }
                }
            });
        }
    }
}
