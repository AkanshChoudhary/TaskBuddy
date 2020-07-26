package com.example.taskbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView title= findViewById(R.id.mainHeading);
        Animation slideAnimation= AnimationUtils.loadAnimation(this,R.anim.left_to_right);
        title.setAnimation(slideAnimation);
        Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                    startActivity(new Intent(getApplicationContext(),MainUserDashboard.class));
                }else if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    startActivity(new Intent(getApplicationContext(),UserSignUpScreen.class));
                }

            }
        }, 2000);

    }
}