package com.example.taskbuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DashBoardRecyclerAdapter extends RecyclerView.Adapter<DashBoardRecyclerAdapter.ViewHolder> {
    List<TaskItem> taskItems= new ArrayList<>();
    Context context;
OnTaskClickedListener onTaskClickedListener;

    public DashBoardRecyclerAdapter(List<TaskItem> taskItems,Context context) {
        this.taskItems = taskItems;
        this.context=context;
    }
    public void setOnTaskClickedListener(OnTaskClickedListener givenListener)
    {this.onTaskClickedListener=givenListener;}

    public interface OnTaskClickedListener{
        void onTaskClicked(TaskItem taskItem,int pos);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.task_card, parent, false);
        return new ViewHolder(v,onTaskClickedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskItem taskItem= taskItems.get(position);
        holder.taskName.setText(taskItem.getTaskName());
        holder.taskDesc.setText(taskItem.getTaskDesc());
        holder.taskStatus.setText("Status: "+taskItem.getTaskStatus());
        holder.dueDate.setText("Due: "+taskItem.getDueDate());
        if (taskItem.getAssignedTo() != "") {
            holder.assignedTo.setVisibility(View.VISIBLE);
            holder.assignedTo.setText(taskItem.getAssignedTo());
        }
    }

    @Override
    public int getItemCount() {
        return taskItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView taskName,taskDesc,taskStatus,dueDate,assignedTo;
    public ViewHolder(@NonNull View itemView,OnTaskClickedListener onTaskClickedListener) {
        super(itemView);
        assignedTo=itemView.findViewById(R.id.assignedTo);
        dueDate=itemView.findViewById(R.id.dueDate);
        taskName=itemView.findViewById(R.id.taskName);
        taskDesc=itemView.findViewById(R.id.taskDesc);
        taskStatus=itemView.findViewById(R.id.taskStatusDesc);
        itemView.setOnClickListener(v->{
            onTaskClickedListener.onTaskClicked(taskItems.get(getAdapterPosition()),getAdapterPosition());
        });
    }
}
}
