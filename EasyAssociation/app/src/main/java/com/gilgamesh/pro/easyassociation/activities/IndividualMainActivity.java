package com.gilgamesh.pro.easyassociation.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.gilgamesh.pro.easyassociation.R;
import com.gilgamesh.pro.easyassociation.fragment.IndividualIHereFragment;
import com.gilgamesh.pro.easyassociation.fragment.IndividualISpaceFragment;
import com.gilgamesh.pro.easyassociation.fragment.IndividualRegWizFragment;
import com.gilgamesh.pro.easyassociation.fragment.IndividualUserInfoFragment;

/**
 * Created by pro on 16/11/7.
 */
public class IndividualMainActivity extends AppCompatActivity {

    public Context context = this;
    private Fragment individualRegWizFragment;
    private Fragment individualIHereFragment;
    private Fragment individualISpaceFragment;
    private Fragment individualUserInfoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_individual);

        ImageButton regWizButton = (ImageButton)findViewById(R.id.main_individual_regwiz_button);
        ImageButton iHereButton = (ImageButton)findViewById(R.id.main_individual_ihere_button);
        ImageButton iSpaceButton = (ImageButton)findViewById(R.id.main_individual_ispace_button);
        ImageButton userInfoButton = (ImageButton)findViewById(R.id.main_individual_user_button);

        View.OnClickListener mainSlideButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ID = view.getId();
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                switch (ID){
                    case R.id.main_individual_regwiz_button:
                        hideFragment(transaction);
                        individualRegWizFragment = new IndividualRegWizFragment();
                        transaction.replace(R.id.fragment_individual_main_activity, individualRegWizFragment);
                        transaction.commit();
                        break;
                    case R.id.main_individual_ihere_button:
                        hideFragment(transaction);
                        individualIHereFragment = new IndividualIHereFragment();
                        transaction.replace(R.id.fragment_individual_main_activity, individualIHereFragment);
                        transaction.commit();
                        break;
                    case R.id.main_individual_ispace_button:
                        hideFragment(transaction);
                        individualISpaceFragment = new IndividualISpaceFragment();
                        transaction.replace(R.id.fragment_individual_main_activity, individualISpaceFragment);
                        transaction.commit();
                        break;
                    case R.id.main_individual_user_button:
                        hideFragment(transaction);
                        individualUserInfoFragment = new IndividualUserInfoFragment();
                        transaction.replace(R.id.fragment_individual_main_activity, individualUserInfoFragment);
                        transaction.commit();
                        break;
                }
            }

            private void hideFragment(FragmentTransaction transaction) {
                if (individualRegWizFragment != null) {

                    transaction.remove(individualRegWizFragment);
                }
                if (individualIHereFragment != null) {

                    transaction.remove(individualIHereFragment);
                }
                if (individualISpaceFragment != null) {

                    transaction.remove(individualISpaceFragment);
                }
                if (individualUserInfoFragment != null) {

                    transaction.remove(individualUserInfoFragment);
                }

            }
        };

        regWizButton.setOnClickListener(mainSlideButtonOnClickListener);
        iHereButton.setOnClickListener(mainSlideButtonOnClickListener);
        iSpaceButton.setOnClickListener(mainSlideButtonOnClickListener);
        userInfoButton.setOnClickListener(mainSlideButtonOnClickListener);
    }
}
