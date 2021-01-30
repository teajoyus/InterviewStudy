package com.example.modulea;

import android.content.Intent;
import android.os.Bundle;
 import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void resolver(View view) {
        startActivity(new Intent(this,ContentResolverActivity.class));
    }
}
