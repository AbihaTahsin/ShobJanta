package com.example.mappro;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mappro.ui.Settings;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    Fragment fragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(isServicesOK()){
            init();
        }
    }
    public void addFragment()
    {
         fragment= new Settings();
         fragmentManager=getSupportFragmentManager();
         fragmentTransaction=fragmentManager.beginTransaction();
         fragmentTransaction.add(R.id.fragment_container,fragment);
         fragmentTransaction.commit();
    }
    private void init(){

        Button btnHome = (Button) findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MainActivity.this, Home.class);
                startActivity(intent2);
            }
        });

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent3);
            }
        });

    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //Everything is ok and user can make map requests
            Log.d(TAG,"isServicesOK: Google Play Services is Working");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //An error occured but we can resolve it
            Log.d(TAG, "isServicesOK: An error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            //error occured and cannot be fixed
            Toast.makeText(this,"You can't access google maps",Toast.LENGTH_SHORT).show();
        }
        return false;
    }


}
