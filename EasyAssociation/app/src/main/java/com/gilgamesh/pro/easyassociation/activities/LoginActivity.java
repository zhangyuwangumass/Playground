package com.gilgamesh.pro.easyassociation.activities;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import com.gilgamesh.pro.easyassociation.R;
import com.gilgamesh.pro.easyassociation.dialog.EntryDialog;

public class LoginActivity extends AppCompatActivity {

    public Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //instantiate views in this activity
        ImageButton individualLoginButton = (ImageButton)findViewById(R.id.login_individual_button);
        ImageButton institutionLoginButton = (ImageButton)findViewById(R.id.login_institution_button);

        //set button listener
        individualLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EntryDialog.Builder builder = new EntryDialog.Builder(context, "individual");
                builder.create().show();
            }
        });

        institutionLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EntryDialog.Builder builder = new EntryDialog.Builder(context, "institution");
                builder.create().show();
            }
        });
    }
}