package com.example.taskbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.model.Document;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class TasksOfGroup extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
TextView heading;
RecyclerView recyclerView;
DashBoardRecyclerAdapter dashBoardRecyclerAdapter;
List<TaskItem> taskItemList= new ArrayList<>();
FirebaseFirestore fStore;
FirebaseAuth fAuth;
TextView date;
int flag,currentPos;
TextView dateUpdate;
String currDate;
TaskItem currItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_of_group);
        fAuth=FirebaseAuth.getInstance();
        findViewById(R.id.backBtn3).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),MyGroups.class)));
        fStore=FirebaseFirestore.getInstance();
        heading=findViewById(R.id.groupNameCode);
        heading.setText(getIntent().getStringExtra("groupName")+" - "+getIntent().getStringExtra("groupCode"));
        recyclerView=findViewById(R.id.taskOfGroupRecycler);
        dashBoardRecyclerAdapter= new DashBoardRecyclerAdapter(taskItemList,this);
        recyclerView.setAdapter(dashBoardRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        fStore.collection("All Groups").document(getIntent().getStringExtra("groupCode")).collection("allTasks")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    taskItemList.clear();
                   if(e!=null){
                       Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                       return;
                   }
                   taskItemList.clear();
                   for(DocumentSnapshot dc:queryDocumentSnapshots.getDocuments()){
                       taskItemList.add(new TaskItem(dc.getString("taskName"),dc.getString("taskDesc"),dc.getString("status"),dc.getString("dueDate"),dc.getString("assignedTo")));
                   }
                   dashBoardRecyclerAdapter.notifyDataSetChanged();
                });

        findViewById(R.id.backBtn3).setOnClickListener(v-> startActivity(new Intent(getApplicationContext(),MyGroups.class)));
        findViewById(R.id.addTask).setOnClickListener(v->
        {
            addNewTask();
        });
        dashBoardRecyclerAdapter.setOnTaskClickedListener((taskItem, pos) -> {
            currDate=taskItem.getDueDate();
            currentPos=pos;
            currItem=taskItem;
            Dialog dialog= new Dialog(TasksOfGroup.this,R.style.DialogTheme);
            dialog.setContentView(R.layout.task_view);
            TextView tName= dialog.findViewById(R.id.taskDetailName);
            TextView tDesc= dialog.findViewById(R.id.taskDetailDesc);
            TextView tStatus= dialog.findViewById(R.id.taskDetailStatus);
            TextView tDueDate= dialog.findViewById(R.id.updateDueDate);
            TextView tAssign=dialog.findViewById(R.id.taskAssignedTo);
            dialog.findViewById(R.id.assignLayout).setVisibility(View.VISIBLE);
            tName.setText(taskItem.getTaskName());
            tDesc.setText(taskItem.getTaskDesc());
            tStatus.setText(taskItem.getTaskStatus());
            tDueDate.setText(taskItem.getDueDate());
            tAssign.setText(taskItem.getAssignedTo());
            dialog.create();
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            dialog.findViewById(R.id.back).setOnClickListener(v->dialog.dismiss());
            dialog.findViewById(R.id.updateTaskName).setOnClickListener(v->{
                dialog.dismiss();
                Dialog dialog1= new Dialog(TasksOfGroup.this,R.style.DialogTheme);
                dialog1.setContentView(R.layout.change_detail_dialog);
                EditText detail= dialog1.findViewById(R.id.newDetail);
                detail.setHint("Change Task Name");
                dialog1.create();
                dialog1.show();
                dialog1.findViewById(R.id.saveNew).setOnClickListener(v1->
                {
                    if(detail.getText().toString().length()==0){
                        Toast.makeText(this, "Please enter something", Toast.LENGTH_SHORT).show();
                        dialog1.dismiss();
                        return;

                    }
                    updateTaskDetail(tName.getText().toString(),detail.getText().toString(),"taskName",currItem);
                    taskItemList.get(pos).setTaskName(detail.getText().toString());
                    dashBoardRecyclerAdapter.notifyDataSetChanged();
                    dialog1.dismiss();
                });
            });
            dialog.findViewById(R.id.updateTaskDesc).setOnClickListener(v->{
                dialog.dismiss();
                Dialog dialog1= new Dialog(TasksOfGroup.this,R.style.DialogTheme);
                dialog1.setContentView(R.layout.change_detail_dialog);
                EditText detail= dialog1.findViewById(R.id.newDetail);
                detail.setHint("Change Task Description");
                dialog1.create();
                dialog1.show();
                dialog1.findViewById(R.id.saveNew).setOnClickListener(v1->
                {
                    if(detail.getText().toString().length()==0){
                        Toast.makeText(this, "Please enter something", Toast.LENGTH_SHORT).show();
                        dialog1.dismiss();
                        return;

                    }
                    updateTaskDetail(tDesc.getText().toString(),detail.getText().toString(),"taskDesc",currItem);
                    taskItemList.get(pos).setTaskDesc(detail.getText().toString());
                    dashBoardRecyclerAdapter.notifyDataSetChanged();
                    dialog1.dismiss();
                });
            });
            dialog.findViewById(R.id.updateCardStatus).setOnClickListener(v->{
                dialog.dismiss();
                Dialog dialog1= new Dialog(TasksOfGroup.this,R.style.DialogTheme);
                dialog1.setContentView(R.layout.change_detail_dialog);
                EditText detail= dialog1.findViewById(R.id.newDetail);
                detail.setHint("Change Task Status");
                dialog1.create();
                dialog1.show();
                dialog1.findViewById(R.id.saveNew).setOnClickListener(v1->
                {
                    if(detail.getText().toString().length()==0){
                        Toast.makeText(this, "Please enter something", Toast.LENGTH_SHORT).show();
                        dialog1.dismiss();
                        return;

                    }
                    updateTaskDetail(tStatus.getText().toString(),detail.getText().toString(),"status",currItem);
                    taskItemList.get(pos).setTaskStatus(detail.getText().toString());
                    dashBoardRecyclerAdapter.notifyDataSetChanged();
                    dialog1.dismiss();
                });
            });
            dialog.findViewById(R.id.updateTaskAssign).setOnClickListener(v->{
                dialog.dismiss();
                Dialog dialog1= new Dialog(TasksOfGroup.this,R.style.DialogTheme);
                dialog1.setContentView(R.layout.change_detail_dialog);
                EditText detail= dialog1.findViewById(R.id.newDetail);
                detail.setHint("Change Assigned Person");
                dialog1.create();
                dialog1.show();
                dialog1.findViewById(R.id.saveNew).setOnClickListener(v1->
                {
                    if(detail.getText().toString().length()==0){
                        Toast.makeText(this, "Please enter something", Toast.LENGTH_SHORT).show();
                        dialog1.dismiss();
                        return;

                    }
                    updateTaskDetail(tAssign.getText().toString(),detail.getText().toString(),"assignedTo",currItem);
                    taskItemList.get(pos).setAssignedTo(detail.getText().toString());
                    dashBoardRecyclerAdapter.notifyDataSetChanged();
                    dialog1.dismiss();
                });
            });
            dialog.findViewById(R.id.updateDueDate).setOnClickListener(v -> {
                flag=1;
                dateUpdate=dialog.findViewById(R.id.updateDueDate);
                String prevDate=dateUpdate.getText().toString();
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            });
            dialog.findViewById(R.id.taskDone).setOnClickListener(v->{
                dialog.dismiss();
                Dialog loading = new Dialog(TasksOfGroup.this,R.style.DialogTheme);
                loading.setContentView(R.layout.loading_dialog);
                loading.create();
                loading.show();
                taskItemList.get(pos).setTaskStatus("Completed");
                dashBoardRecyclerAdapter.notifyDataSetChanged();
                fStore.collection("All Groups").document(getIntent().getStringExtra("groupCode"))
                        .collection("allTasks").get().addOnCompleteListener(task -> {
                   for(QueryDocumentSnapshot dc:task.getResult()){
                       if(dc.getString("taskName").equals(taskItemList.get(pos).getTaskName())&&
                               dc.getString("taskDesc").equals(taskItemList.get(pos).getTaskDesc())){
                           String id=dc.getId();
                           fStore.collection("All Groups").document(getIntent().getStringExtra("groupCode"))
                                   .collection("allTasks").document(id).update("status","Completed").
                                   addOnSuccessListener(aVoid -> loading.dismiss());
                       }
                   }
                });

            });
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }



        public void addNewTask()
    {
        Dialog dialog = new Dialog(TasksOfGroup.this, R.style.DialogTheme);
        dialog.setContentView(R.layout.new_task_detail_dialog_two);
        EditText name = dialog.findViewById(R.id.newTaskName2);
        EditText desc = dialog.findViewById(R.id.newTaskDesc2);
        EditText assignTo=dialog.findViewById(R.id.newTaskAssign);
        date = dialog.findViewById(R.id.datePicker2);
        dialog.create();
        dialog.show();
        dialog.findViewById(R.id.cancel2).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.datePicker2).setOnClickListener(v1 -> {
            flag=0;
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
        dialog.findViewById(R.id.saveNewDetail2).setOnClickListener(v1 -> {
            if ((name.getText().toString().length() == 0) || date.getText().toString() == "Select Due Date") {
                Toast.makeText(this, "Please enter a task name and date", Toast.LENGTH_SHORT).show();
                return;
            }
            dialog.dismiss();
            Dialog dialog2 = new Dialog(TasksOfGroup.this,R.style.DialogTheme);
            dialog2.setContentView(R.layout.loading_dialog);
            dialog2.create();
            dialog2.show();
            java.sql.Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Map<String, Object> newMap = new HashMap<>();
            newMap.put("taskName", name.getText().toString());
            newMap.put("taskDesc", desc.getText().toString());
            newMap.put("dueDate", date.getText().toString());
            newMap.put("status", "None");
            newMap.put("assignedTo",assignTo.getText().toString());
            newMap.put("timeStamp", timestamp.getTime());
            fStore.collection("All Groups").document(getIntent().getStringExtra("groupCode")).collection("allTasks")
                    .document().set(newMap)
                    .addOnSuccessListener(aVoid -> {
                        dialog2.dismiss();
                        Toast.makeText(this, "Task Created", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        int mon=month+1;
        if(flag==0){
            if(month<10){
                date.setText(dayOfMonth + "/0" + mon + "/" + year);
            }
            if(dayOfMonth<10){
                date.setText("0"+dayOfMonth + "/" + mon + "/" + year);
            }
            if(mon>=10&&dayOfMonth>=10){
                date.setText(dayOfMonth + "/" + mon + "/" + year);
            }
            if(mon<10&&dayOfMonth<10)
            {
                date.setText("0"+dayOfMonth + "/0" + mon + "/" + year);
            }
        }else if(flag==1){
            if(mon<10){
                dateUpdate.setText(dayOfMonth + "/0" + mon + "/" + year);
            }
            if(dayOfMonth<10){
                dateUpdate.setText("0"+dayOfMonth + "/" + mon + "/" + year);
            }
            if(mon>=10&&dayOfMonth>=10){
                dateUpdate.setText(dayOfMonth + "/" + mon + "/" + year);
            }
            if(mon<10&&dayOfMonth<10)
            {
                dateUpdate.setText("0"+dayOfMonth + "/0" + mon + "/" + year);
            }
            updateTaskDetail(currDate,dateUpdate.getText().toString(),"dueDate",currItem);
            taskItemList.get(currentPos).setDueDate(dateUpdate.getText().toString());
            dashBoardRecyclerAdapter.notifyDataSetChanged();
        }
    }
    public void updateTaskDetail(String prevDetail,String newDetail,String type,TaskItem taskItem){
        Dialog dialog = new Dialog(TasksOfGroup.this,R.style.DialogTheme);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.create();
        dialog.show();

        fStore.collection("All Groups").document(getIntent().getStringExtra("groupCode")).collection("allTasks")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    for(QueryDocumentSnapshot dc: task.getResult())
                    {
                        if(dc.getString("taskName").equals(taskItem.getTaskName())
                        && dc.getString("taskDesc").equals(taskItem.getTaskDesc())
                        ){
                            String id=dc.getId();
                            fStore.collection("All Groups").document(getIntent().getStringExtra("groupCode"))
                                    .collection("allTasks").document(id).update(type,newDetail)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }).addOnFailureListener(e -> {
                                Toast.makeText(this, "Some error occurred", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            });
                        }else{
                        }
                    }
                });
    }
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }


        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if(direction==ItemTouchHelper.RIGHT){
                int pos = viewHolder.getAdapterPosition();
                TaskItem deletedItem = taskItemList.get(pos);
                taskItemList.remove(viewHolder.getAdapterPosition());
                dashBoardRecyclerAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                Snackbar snackbar = Snackbar.make(recyclerView, "deleted " + deletedItem.getTaskName(), Snackbar.LENGTH_SHORT);
                snackbar.setAction("Undo", v -> {
                    taskItemList.add(pos, deletedItem);
                    dashBoardRecyclerAdapter.notifyItemInserted(pos);
                }).show();
                snackbar.addCallback(new Snackbar.Callback(){
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                            Dialog dialog = new Dialog(TasksOfGroup.this,R.style.DialogTheme);
                            dialog.setContentView(R.layout.loading_dialog);
                            dialog.create();
                            dialog.show();
                            fStore.collection("All Groups").document(getIntent().getStringExtra("groupCode")).collection("allTasks")
                                    .get()
                                    .addOnCompleteListener(task -> {
                                       for(QueryDocumentSnapshot dc:task.getResult()){
                                           if((dc.getString("taskName").equals(deletedItem.getTaskName()))){
                                               String id=dc.getId();
                                               fStore.collection("All Groups").document(getIntent().getStringExtra("groupCode")).collection("allTasks")
                                                       .document(id).delete()
                                                       .addOnSuccessListener(aVoid -> {
                                                           Toast.makeText(TasksOfGroup.this, "Deleted", Toast.LENGTH_SHORT).show();
                                                           dialog.dismiss();
                                                       });
                                           }
                                       }
                                    });
                        }
                        super.onDismissed(transientBottomBar, event);
                    }
                });
            }
        }
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(TasksOfGroup.this, R.color.colorAccent))
                    .addActionIcon(R.drawable.ic_baseline_delete_24)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
}