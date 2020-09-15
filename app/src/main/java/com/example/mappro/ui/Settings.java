package com.example.mappro.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mappro.R;
import com.example.mappro.ResetPassActivity;
import com.example.mappro.SettingsOptionActivity;

public class Settings extends Fragment {

    TextView resetPass,resetName;



    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.fragment_settings,container,false);
        resetPass=view.findViewById(R.id.resetPass);
        resetName=view.findViewById(R.id.preferenceSettings);
        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ResetPassActivity.class));
                Toast.makeText(getActivity(), "Reset Your Password", Toast.LENGTH_SHORT).show();
            }
        });
        resetName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingsOptionActivity.class));
                Toast.makeText(getActivity(), "Settings Other Options", Toast.LENGTH_SHORT).show();
            }
        });

        return view;

    }

}
