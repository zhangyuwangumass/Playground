package com.gilgamesh.pro.easyassociation.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gilgamesh.pro.easyassociation.activities.IndividualMainActivity;
import com.gilgamesh.pro.easyassociation.R;
import com.gilgamesh.pro.easyassociation.activities.InstitutionMainActivity;

/**
 * Created by pro on 16/11/6.
 */
public class EntryDialog extends Dialog{

    public EntryDialog(Context context){
        super(context);
    }

    public static class Builder{
        private Context context;
        private String userType;

        public Builder(Context context, String userType) {
            this.context = context;
            this.userType = userType;
        }

        public EntryDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //instantiate the dialog with the custom Theme
            final EntryDialog dialog = new EntryDialog(context);
            if(userType.equals("individual")) {
                View layout = inflater.inflate(R.layout.dialog_login_individual, null);

                dialog.setContentView(layout);

                //instantiate the views in the dialog
                final EditText studentIDEntry = (EditText)layout.findViewById(R.id.login_individual_dialog_studentID_entry);
                final EditText passwordEntry = (EditText)layout.findViewById(R.id.login_individual_dialog_password_entry);
                Button positiveButton = (Button)layout.findViewById(R.id.login_individual_dialog_button);

                //set button listener
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String studentID = studentIDEntry.getText().toString();
                        String password = passwordEntry.getText().toString();

                        /**此处需要将studentID与password发送到服务器进行验证，由服务器返回一个boolean值和一个打包好的用户信息
                         * （包括姓名，学号，等级等等），如果为true则进入MainActivity
                         * 以下代码为测试代码，待服务器端写好后要替换掉
                         */

                        Intent intent = new Intent(context, IndividualMainActivity.class);
                        context.startActivity(intent);
                        dialog.dismiss();
                        /**
                         * 测试代码到此结束
                         */

                    }
                });
            }
            else if(userType.equals("institution")){
                View layout = inflater.inflate(R.layout.dialog_login_institution, null);

                dialog.setContentView(layout);

                //instantiate the views in the dialog
                final EditText nameEntry = (EditText)layout.findViewById(R.id.login_institution_dialog_name_entry);
                final EditText passwordEntry = (EditText)layout.findViewById(R.id.login_institution_dialog_password_entry);
                Button positiveButton = (Button)layout.findViewById(R.id.login_institution_dialog_button);

                //set button listener
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = nameEntry.getText().toString();
                        String password = passwordEntry.getText().toString();

                        /**此处需要将studentID与password发送到服务器进行验证，由服务器返回一个boolean值和一个打包好的用户信息
                         * （包括姓名，学号，等级等等），如果为true则进入MainActivity
                         * 以下代码为测试代码，待服务器端写好后要替换掉
                         */

                        Intent intent = new Intent(context, InstitutionMainActivity.class);
                        context.startActivity(intent);
                        dialog.dismiss();
                        /**
                         * 测试代码到此结束
                         */

                    }
                });
            }

            return dialog;
        }
    }
}
