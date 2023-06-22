package com.hufsice.nulltime;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hufsice.nulltime.R;

import java.util.List;


//그룹 목록을 보여주기 위한 RecyclerView의 어댑터
//GroupList activity에서 그룹 목록을 표시하고, 각 항목의 이벤트를 처리하기 위해 필요
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    //그룹 목록 데이터를 담는 리스트
    private List<String> groupList;
    //그룹 항목 클릭 이벤트 리스너
    private GroupItemClickListener itemClickListener;

    public GroupAdapter(List<String> groupList, GroupItemClickListener itemClickListener) {
        this.groupList = groupList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //뷰 홀더 생성 시 호출되는 메소드
        //레이아웃 파일을 inflate하여 뷰 객체를 생성하고, 이를 GroupViewHolder에 넘겨줌
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        //뷰 홀더와 데이터를 바인딩하는 메소드
        //해당 position에 있는 그룹명을 가져와서 뷰 홀더에 표시함
        String groupName = groupList.get(position);
        holder.bind(groupName);
    }

    @Override
    public int getItemCount() {
        //데이터 개수 반환
        return groupList.size();
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        //그룹 항목에 대한 뷰 홀더 클래스

        //그룹명을 표시하는 텍스트뷰
        private TextView textViewGroupName;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            //뷰 홀더 생성 시 호출되는 메소드
            //뷰 객체의 참조를 가져와서 변수에 저장하고, 클릭 이벤트 리스너를 등록함
            textViewGroupName = itemView.findViewById(R.id.textViewGroupName);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(String groupName) {
            //뷰 홀더에 데이터를 바인딩하는 메소드
            //받아온 그룹명을 텍스트뷰에 설정함
            textViewGroupName.setText(groupName);
        }

        @Override
        public void onClick(View v) {
            //그룹 항목을 클릭했을 때 호출되는 메소드
            //해당 위치(position)을 이벤트 리스너에 전달함
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                itemClickListener.onGroupItemClick(position);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            //그룹 항목을 롱클릭했을 때 호출되는 메소드
            //해당 위치(position)을 이벤트 리스너에 전달하고, 롱클릭 이벤트를 소비함
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                itemClickListener.onGroupItemLongClick(position);
            }
            return true;
        }
    }

    public interface GroupItemClickListener {
        //그룹 항목 클릭 이벤트 리스너 인터페이스

        //그룹 항목을 클릭했을 때 호출되는 메소드
        void onGroupItemClick(int position);
        //그룹 항목을 롱클릭했을 때 호출되는 메소드
        void onGroupItemLongClick(int position);
    }
}
