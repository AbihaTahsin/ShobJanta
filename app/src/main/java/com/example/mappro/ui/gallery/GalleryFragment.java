package com.example.mappro.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mappro.Home;
import com.example.mappro.MainActivity;
import com.example.mappro.MapActivity;
import com.example.mappro.R;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;

    public GalleryFragment(){

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
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
