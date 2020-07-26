package com.example.taskbuddy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GroupRecyclerAdapter extends RecyclerView.Adapter<GroupRecyclerAdapter.ViewHolder> {
    List<GroupItem> groupItemList=new ArrayList<>();
OnGroupClickedListener onGroupClickedListener;
    public GroupRecyclerAdapter(List<GroupItem> groupItemList) {
        this.groupItemList = groupItemList;
    }
    public void setOnGroupClickedListener(OnGroupClickedListener givenListener)
    {this.onGroupClickedListener=givenListener;}

    public interface OnGroupClickedListener{
        void onGroupClicked(String gCode,String gName);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.group_item, parent, false);
        return new ViewHolder(v,onGroupClickedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupItem groupItem= groupItemList.get(position);
        holder.gName.setText(groupItem.getGroupName());
        holder.gCode.setText(groupItem.getGroupCode());
    }

    @Override
    public int getItemCount() {
        return groupItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
TextView gName,gCode;
        public ViewHolder(@NonNull View itemView,OnGroupClickedListener listener) {
            super(itemView);
            gName=itemView.findViewById(R.id.groupName);
            gCode=itemView.findViewById(R.id.groupCode);
            itemView.setOnClickListener(v->{
                listener.onGroupClicked(gCode.getText().toString(),gName.getText().toString());
            });
        }
    }
}
