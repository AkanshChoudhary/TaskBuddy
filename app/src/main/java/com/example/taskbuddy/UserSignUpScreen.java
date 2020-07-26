package com.example.taskbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Batch;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserSignUpScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up_screen);
        EditText name=findViewById(R.id.nameField);
        findViewById(R.id.getStarted).setOnClickListener(v -> {
            if(name.getText().toString().length()==0){
                Toast.makeText(this, "Please Enter a name", Toast.LENGTH_SHORT).show();
                return;
            }
            Dialog loading = new Dialog(UserSignUpScreen.this,R.style.DialogTheme);
            loading.setContentView(R.layout.loading_dialog);
            loading.create();
            loading.show();
            FirebaseAuth.getInstance().signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Map<String,Object> nameMap= new HashMap<>();
                    nameMap.put("name",name.getText().toString());
                    Map<String,Object> grps= new HashMap<>();
                    List<String> grpsList= new ArrayList<>();
                    grpsList.add(" ");
                    grps.put("Groups",grpsList);
                    WriteBatch batch = FirebaseFirestore.getInstance().batch();
                    batch.set(FirebaseFirestore.getInstance().collection("user+"+FirebaseAuth.getInstance().getCurrentUser().getUid()).document("Id"),nameMap);
                    batch.set(FirebaseFirestore.getInstance().collection("user+"+FirebaseAuth.getInstance().getCurrentUser().getUid()).document("sharedTasks"),grps);
                    batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(UserSignUpScreen.this, "All Done", Toast.LENGTH_SHORT).show();
                            loading.dismiss();
                            startActivity(new Intent(getApplicationContext(),MainUserDashboard.class));
                        }
                    });
                }
            });
        });
    }
}