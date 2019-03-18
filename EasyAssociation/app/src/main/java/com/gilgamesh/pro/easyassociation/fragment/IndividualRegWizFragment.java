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
 * Created by pro on 16/11/7.
 */
public class IndividualRegWizFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_individual_regwiz, container, false);
        ImageButton scanButton = (ImageButton)view.findViewById(R.id.fragment_regwiz_scanbutton);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * 此处填充代码：二维码扫描
                 * 如果可能的话，应该有显示自己的二维码并让他人扫描的功能
                 * 此时的中心图片就是用户自己的二维码
                 * 以下部分是测试代码
                 */
                Intent intent = new Intent(ACTION_IMAGE_CAPTURE);
                getActivity().startActivity(intent);
                /**
                 * 测试代码到此结束
                 */
            }
        });
        return view;
    }

}
