package com.example.helpmeplz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private ArrayList<Member> memberList;
    private OnMemberCheckedChangeListener listener;
    private ArrayList<Member> selectedMembers;

    public MemberAdapter(ArrayList<Member> memberList, OnMemberCheckedChangeListener listener) {
        this.memberList = memberList;
        this.listener = listener;
        this.selectedMembers = new ArrayList<>();
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_members, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        Member member = memberList.get(position);
        holder.bind(member);
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public ArrayList<Member> getSelectedMembers() {
        return selectedMembers;
    }

    public interface OnMemberCheckedChangeListener {
        void onMemberCheckedChange(Member member, boolean isChecked);
    }

    public class MemberViewHolder extends RecyclerView.ViewHolder {

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

        public void bind(Member member) {
            checkBoxMember.setText(member.getName());
        }
    }
}
