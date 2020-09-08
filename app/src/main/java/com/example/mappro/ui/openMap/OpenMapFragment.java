package com.example.mappro.ui.openMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mappro.MapActivity;
import com.example.mappro.R;

public class OpenMapFragment extends Fragment {


    public OpenMapFragment(){

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_openmap, container, false);

        Button mapbtn = (Button) view.findViewById(R.id.btnMap);
        mapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in= new Intent(getActivity(), MapActivity.class);
                startActivity(in);
            }
        });


        return view;



    }
}
