package com.example.taskbuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyGroups extends AppCompatActivity {
ImageView back;
RecyclerView recyclerView;
List<GroupItem> groupItems= new ArrayList<>();
GroupRecyclerAdapter adapter;
FirebaseFirestore firebaseFirestore;
FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);
        back=findViewById(R.id.backBtn2);
        recyclerView=findViewById(R.id.myGroupsRecycler);
        adapter= new GroupRecyclerAdapter(groupItems);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseFirestore.collection("user+"+firebaseAuth.getCurrentUser().getUid()).document("sharedTasks")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                   List<String> items= (List<String>) documentSnapshot.get("Groups");
                   for(int i=1;i<items.size();i++)
                   {
                       groupItems.add(new GroupItem(items.get(i).substring(7),items.get(i).substring(0,6)));
                       adapter.notifyDataSetChanged();
                   }
                });
        findViewById(R.id.backBtn2).setOnClickListener(v->startActivity(new Intent(getApplicationContext(),MainUserDashboard.class)));
        adapter.setOnGroupClickedListener((gCode,gName) -> {
            Intent intent= new Intent(MyGroups.this,TasksOfGroup.class);
            intent.putExtra("groupName",gName);
            intent.putExtra("groupCode",gCode);
            startActivity(intent);
        });
    }

}