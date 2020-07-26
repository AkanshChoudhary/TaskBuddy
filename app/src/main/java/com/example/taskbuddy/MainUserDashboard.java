package com.example.taskbuddy;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainUserDashboard extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ImageView openDrawer;
    NavigationView navigationView;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    DashBoardRecyclerAdapter adapter;
    List<TaskItem> taskItems = new ArrayList<>();
    TextView date,dateUpdate;
int flag=0,currentPos;
String currDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user_dashboard);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        openDrawer = findViewById(R.id.menu);
        navigationView = findViewById(R.id.navView);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        openDrawer.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new DashBoardRecyclerAdapter(taskItems,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));


        firebaseFirestore.collection("user+" + firebaseAuth.getCurrentUser().getUid()).document("allTasks")
                .collection("tasks").orderBy("timeStamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot dc : task.getResult()) {
                            taskItems.add(new TaskItem(dc.getString("taskName"), dc.getString("taskDesc"), dc.getString("status"), dc.getString("dueDate"),""));


                    }
                    adapter.notifyDataSetChanged();
                });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        navigationView.setBackgroundColor(getResources().getColor(R.color.mainDarkGreen));
        navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.mainLightGreen)));
        navigationView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.mainLightGreen)));
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.newTask) {
                drawerLayout.closeDrawer(GravityCompat.START);
                makeNewCard();
            }
            if(id==R.id.myGroup){
                startActivity(new Intent(getApplicationContext(),MyGroups.class));
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            if(id==R.id.createGroup){
                drawerLayout.closeDrawer(GravityCompat.START);
                createNewGroup();
            }
            if(id==R.id.joinGroup){
                drawerLayout.closeDrawer(GravityCompat.START);
                joinGroup();
            }
            return true;
        });

        adapter.setOnTaskClickedListener((taskItem,pos) -> {
            currentPos=pos;
            currDate=taskItem.getDueDate();
            Dialog dialog= new Dialog(MainUserDashboard.this,R.style.DialogTheme);
            dialog.setContentView(R.layout.task_view);
            TextView tName= dialog.findViewById(R.id.taskDetailName);
            TextView tDesc= dialog.findViewById(R.id.taskDetailDesc);
            TextView tStatus= dialog.findViewById(R.id.taskDetailStatus);
            TextView tDueDate= dialog.findViewById(R.id.updateDueDate);
            tName.setText(taskItem.getTaskName());
            tDesc.setText(taskItem.getTaskDesc());
            tStatus.setText(taskItem.getTaskStatus());
           tDueDate.setText(taskItem.getDueDate());
            Toast.makeText(this, taskItem.getTaskName(), Toast.LENGTH_SHORT).show();
            dialog.create();
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            dialog.findViewById(R.id.back).setOnClickListener(v->dialog.dismiss());
            dialog.findViewById(R.id.updateTaskName).setOnClickListener(v->{
                dialog.dismiss();
                Dialog dialog1= new Dialog(MainUserDashboard.this,R.style.DialogTheme);
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
                   updateTaskDetail(tName.getText().toString(),detail.getText().toString(),"taskName");
                   taskItems.get(pos).setTaskName(detail.getText().toString());
                   adapter.notifyDataSetChanged();
                   dialog1.dismiss();
                });
            });
            dialog.findViewById(R.id.updateTaskDesc).setOnClickListener(v->{
                dialog.dismiss();
                Dialog dialog1= new Dialog(MainUserDashboard.this,R.style.DialogTheme);
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
                    updateTaskDetail(tDesc.getText().toString(),detail.getText().toString(),"taskDesc");
                    taskItems.get(pos).setTaskDesc(detail.getText().toString());
                    adapter.notifyDataSetChanged();
                    dialog1.dismiss();
                });
            });
            dialog.findViewById(R.id.updateCardStatus).setOnClickListener(v->{
                dialog.dismiss();
                Dialog dialog1= new Dialog(MainUserDashboard.this,R.style.DialogTheme);
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
                    updateTaskDetail(tStatus.getText().toString(),detail.getText().toString(),"status");
                    taskItems.get(pos).setTaskStatus(detail.getText().toString());
                    adapter.notifyDataSetChanged();
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
                Toast.makeText(this, prevDate, Toast.LENGTH_SHORT).show();
                Toast.makeText(this,dateUpdate.getText().toString() , Toast.LENGTH_SHORT).show();
            });
            dialog.findViewById(R.id.taskDone).setOnClickListener(v->{
                dialog.dismiss();
                Dialog loading = new Dialog(MainUserDashboard.this,R.style.DialogTheme);
                loading.setContentView(R.layout.loading_dialog);
                loading.create();
                loading.show();
                taskItems.get(pos).setTaskStatus("Completed");
                adapter.notifyDataSetChanged();
                firebaseFirestore.collection("user+"+firebaseAuth.getCurrentUser().getUid()).document("allTasks")
                        .collection("tasks").get().addOnCompleteListener(task -> {
                   for(QueryDocumentSnapshot dc:task.getResult()){
                       if(dc.getString("taskName").equals(taskItems.get(pos).getTaskName())&&
                               dc.getString("taskDesc").equals(taskItems.get(pos).getTaskDesc())){
                           String id=dc.getId();
                           firebaseFirestore.collection("user+"+firebaseAuth.getCurrentUser().getUid()).document("allTasks")
                                   .collection("tasks").document(id).update("status","Completed")
                                   .addOnSuccessListener(aVoid -> {
                                       Toast.makeText(this, "Task Completed.", Toast.LENGTH_SHORT).show();
                                       loading.dismiss();
                                   });

                       }
                   }
                });
            });
        });
    }

    public void makeNewCard() {
        Dialog dialog = new Dialog(MainUserDashboard.this, R.style.DialogTheme);
        dialog.setContentView(R.layout.new_task_detail_dialog);
        EditText name = dialog.findViewById(R.id.newTaskName);
        EditText desc = dialog.findViewById(R.id.newTaskDesc);
        date = dialog.findViewById(R.id.datePicker);
        dialog.create();
        dialog.show();
        dialog.findViewById(R.id.cancelBox).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.datePicker).setOnClickListener(v -> {
           flag=0;
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();

        });
        dialog.findViewById(R.id.saveNewDetail).setOnClickListener(v -> {
            if ((name.getText().toString().length() == 0) || date.getText().toString() == "Select Due Date") {
                Toast.makeText(this, "Please enter a task name and date", Toast.LENGTH_SHORT).show();
                return;
            }
            java.sql.Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Map<String, Object> newMap = new HashMap<>();
            newMap.put("taskName", name.getText().toString());
            newMap.put("taskDesc", desc.getText().toString());
            newMap.put("dueDate", date.getText().toString());
            newMap.put("status", "None");
            newMap.put("timeStamp", timestamp.getTime());
            firebaseFirestore.collection("user+" + firebaseAuth.getCurrentUser().getUid()).document("allTasks")
                    .collection("tasks").document().set(newMap)
                    .addOnSuccessListener(aVoid -> {
                        dialog.dismiss();
                        Toast.makeText(this, "Task Created", Toast.LENGTH_SHORT).show();

                            taskItems.add(new TaskItem(name.getText().toString(), desc.getText().toString(), "None", date.getText().toString(),""));

                        adapter.notifyDataSetChanged();
                    });
        });
    }

    public void joinGroup()
    {
        Dialog dialog= new Dialog(MainUserDashboard.this,R.style.DialogTheme);
        dialog.setContentView(R.layout.change_detail_dialog);
        EditText detail= dialog.findViewById(R.id.newDetail);
        detail.setHint("Group Code");
        Button create= dialog.findViewById(R.id.saveNew);
        create.setText("Join Group");
        dialog.create();
        dialog.show();
        dialog.findViewById(R.id.cancel).setOnClickListener(v->dialog.dismiss());
        create.setOnClickListener(v->{
            Dialog loading = new Dialog(MainUserDashboard.this,R.style.DialogTheme);
            loading.setContentView(R.layout.loading_dialog);
            loading.create();
            loading.show();
            firebaseFirestore.collection("All Groups").document(detail.getText().toString()).get()
                    .addOnCompleteListener(task -> {
                       if(task.isSuccessful()){
                           DocumentSnapshot dc=task.getResult();
                           if(dc.exists()){
                               firebaseFirestore.collection("user+"+firebaseAuth.getCurrentUser().getUid()).document("sharedTasks")
                                       .update("Groups", FieldValue.arrayUnion(detail.getText().toString()+"/"+dc.getString("groupName")))
                                       .addOnSuccessListener(aVoid -> {
                                           Toast.makeText(this, "Group Joined.", Toast.LENGTH_SHORT).show();
                                           dialog.dismiss();
                                           loading.dismiss();
                                       });
                           }else{
                               Toast.makeText(this, "Wrong Group Code", Toast.LENGTH_SHORT).show();
                               loading.dismiss();
                           }
                       }
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
            updateTaskDetail(currDate,dateUpdate.getText().toString(),"dueDate");
            taskItems.get(currentPos).setDueDate(dateUpdate.getText().toString());
            adapter.notifyDataSetChanged();
        }


    }

    public void updateTaskDetail(String previousDetail,String newDetail,String type){
        //TODO: update list also
        Dialog dialog = new Dialog(MainUserDashboard.this,R.style.DialogTheme);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.create();
        dialog.show();
        firebaseFirestore.collection("user+" + firebaseAuth.getCurrentUser().getUid()).document("allTasks")
                .collection("tasks")
                .get()
                .addOnCompleteListener(task -> {
                   for(QueryDocumentSnapshot dc: task.getResult())
                   {
                       if(dc.getString(type).equals(previousDetail)){
                           Toast.makeText(this, "Success 2", Toast.LENGTH_SHORT).show();
                           String id=dc.getId();
                           firebaseFirestore.collection("user+"+firebaseAuth.getCurrentUser().getUid()).document("allTasks")
                                   .collection("tasks").document(id).update(type,newDetail)
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
            if (direction == ItemTouchHelper.RIGHT) {
                int pos = viewHolder.getAdapterPosition();
                TaskItem deletedItem = taskItems.get(pos);
                taskItems.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                Snackbar snackbar = Snackbar.make(recyclerView, "deleted " + deletedItem.getTaskName(), Snackbar.LENGTH_SHORT);
                snackbar.setAction("Undo", v -> {
                    taskItems.add(pos, deletedItem);
                    adapter.notifyItemInserted(pos);
                }).show();
                snackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                            // TODO: start loading
                            Dialog dialog = new Dialog(MainUserDashboard.this,R.style.DialogTheme);
                            dialog.setContentView(R.layout.loading_dialog);
                            dialog.create();
                            dialog.show();
                            firebaseFirestore.collection("user+" + firebaseAuth.getCurrentUser().getUid()).document("allTasks")
                                    .collection("tasks").orderBy("timeStamp", Query.Direction.DESCENDING)
                                    .get()
                                    .addOnCompleteListener(task ->
                                    {
                                        for (QueryDocumentSnapshot dc : task.getResult()) {
                                            if (dc.getString("taskName").equals(deletedItem.getTaskName())) {
                                                String docId = dc.getId();
                                                firebaseFirestore.collection("user+" + firebaseAuth.getCurrentUser().getUid()).document("allTasks")
                                                        .collection("tasks").document(docId).delete()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(MainUserDashboard.this, "Deleted", Toast.LENGTH_SHORT).show();
                                                                dialog.dismiss();
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onShown(Snackbar snackbar) {
                    }
                });

            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(MainUserDashboard.this, R.color.colorAccent))
                    .addActionIcon(R.drawable.ic_baseline_delete_24)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
        super.onBackPressed();
    }

    public void createNewGroup()
    {
        Dialog dialog= new Dialog(MainUserDashboard.this,R.style.DialogTheme);
        dialog.setContentView(R.layout.change_detail_dialog);
        EditText detail= dialog.findViewById(R.id.newDetail);
        detail.setHint("Group Name");
        Button create= dialog.findViewById(R.id.saveNew);
        create.setText("Create Group");
        dialog.create();
        dialog.show();
        dialog.findViewById(R.id.cancel).setOnClickListener(v->dialog.dismiss());
        create.setOnClickListener(v->{
            dialog.dismiss();
            Dialog loading = new Dialog(MainUserDashboard.this,R.style.DialogTheme);
            loading.setContentView(R.layout.loading_dialog);
            loading.create();
            loading.show();
            //first insert all details
            //then show the final dialog
            String code= generateRandomShareId();
            firebaseFirestore.collection("user+"+firebaseAuth.getCurrentUser().getUid()).document("sharedTasks")
                    .update("Groups", FieldValue.arrayUnion(code+"/"+detail.getText().toString()))
                    .addOnSuccessListener(aVoid -> {
                        Map<String,Object> newGroupMap= new HashMap<>();
                        newGroupMap.put("code",code);
                        newGroupMap.put("groupName",detail.getText().toString());
                        firebaseFirestore.collection("All Groups").document(code).set(newGroupMap)
                                .addOnSuccessListener(aVoid1 -> {
                                    loading.dismiss();
                                   showCodeShareDialog(code,detail.getText().toString());
                                });
                    });
        });
    }
    public void showCodeShareDialog(String code,String gName){
        Dialog dialog = new Dialog(MainUserDashboard.this, R.style.DialogTheme);
        dialog.setContentView(R.layout.code_show);
        TextView codeTxt = dialog.findViewById(R.id.codeView);
        codeTxt.setText(code);
        dialog.create();
        dialog.show();
        dialog.findViewById(R.id.closeCode).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.shareCode).setOnClickListener(v -> {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "You are invited to join the group "+gName+". Use the code below to join.\ncode: "+code);
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        });
    }
    public static String generateRandomShareId() {
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrst0123456789".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            char c = chars[random.nextInt(chars.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }
}
