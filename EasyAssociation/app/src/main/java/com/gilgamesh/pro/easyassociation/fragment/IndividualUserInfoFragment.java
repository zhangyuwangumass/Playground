package com.gilgamesh.pro.easyassociation.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.gilgamesh.pro.easyassociation.R;

import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;

/**
 * Created by Administrator on 2016/11/9.
 */

public class IndividualUserInfoFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_individual_userinfo, container, false);
        return view;
    }

}
