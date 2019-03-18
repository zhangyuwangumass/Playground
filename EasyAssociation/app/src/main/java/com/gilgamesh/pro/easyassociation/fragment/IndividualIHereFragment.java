package com.gilgamesh.pro.easyassociation.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.gilgamesh.pro.easyassociation.R;

import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;

/**
 * Created by Administrator on 2016/11/9.
 */

public class IndividualIHereFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_individual_ihere, container, false);
        ImageButton iHereButton = (ImageButton)view.findViewById(R.id.fragment_individual_ihere_button);
        final ImageView password1 = (ImageView)view.findViewById(R.id.fragment_individual_ihere_password_1);
        final ImageView password2 = (ImageView)view.findViewById(R.id.fragment_individual_ihere_password_2);
        final ImageView password3 = (ImageView)view.findViewById(R.id.fragment_individual_ihere_password_3);
        final ImageView password4 = (ImageView)view.findViewById(R.id.fragment_individual_ihere_password_4);

        ImageButton redNumber = (ImageButton)view.findViewById(R.id.fragment_individual_ihere_rednumber);
        ImageButton greenNumber = (ImageButton)view.findViewById(R.id.fragment_individual_ihere_greennumber);
        ImageButton yellowNumber = (ImageButton)view.findViewById(R.id.fragment_individual_ihere_yellownumber);
        ImageButton blueNumber = (ImageButton)view.findViewById(R.id.fragment_individual_ihere_bluenumber);
        ImageButton purpleNumber = (ImageButton)view.findViewById(R.id.fragment_individual_ihere_purplenumber);

        iHereButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * 此处填充代码：二维码扫描
                 * 如果可能的话，应该有显示自己的二维码并让他人扫描的功能
                 * 此时的中心图片就是用户自己的二维码
                 * 以下部分是测试代码
                 */

                password1.setImageDrawable(null);
                password2.setImageDrawable(null);
                password3.setImageDrawable(null);
                password4.setImageDrawable(null);

                password1.setTag("0");
                password2.setTag("0");
                password3.setTag("0");
                password4.setTag("0");
                /**
                 * 测试代码到此结束
                 */
            }
        });

        View.OnClickListener colorButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView display;
                if(password1.getTag().toString().equals("0"))
                    display = password1;
                else if(password2.getTag().toString().equals("0"))
                    display = password2;
                else if(password3.getTag().toString().equals("0"))
                    display = password3;
                else if(password4.getTag().toString().equals("0"))
                    display = password4;
                else display = null;

                if(display != null){
                    switch (view.getTag().toString()){
                        case "red":
                            display.setImageResource(R.drawable.number_red);
                            display.setTag("1");
                            break;
                        case "blue":
                            display.setImageResource(R.drawable.number_blue);
                            display.setTag("1");
                            break;
                        case "yellow":
                            display.setImageResource(R.drawable.number_yellow);
                            display.setTag("1");
                            break;
                        case "green":
                            display.setImageResource(R.drawable.number_green);
                            display.setTag("1");
                            break;
                        case "purple":
                            display.setImageResource(R.drawable.number_purple);
                            display.setTag("1");
                            break;
                    }
                }
            }
        };

        redNumber.setOnClickListener(colorButtonOnClickListener);
        greenNumber.setOnClickListener(colorButtonOnClickListener);
        yellowNumber.setOnClickListener(colorButtonOnClickListener);
        blueNumber.setOnClickListener(colorButtonOnClickListener);
        purpleNumber.setOnClickListener(colorButtonOnClickListener);

        return view;
    }
}
