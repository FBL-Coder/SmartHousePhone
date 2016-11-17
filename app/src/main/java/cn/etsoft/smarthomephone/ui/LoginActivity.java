package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.pullmi.entity.RcuInfo;

/**
 * Created by Say GoBay on 2016/9/6.
 */
public class LoginActivity extends Activity implements View.OnClickListener{
    private EditText username,password;
    private Button login;
    private CheckBox check;
    private boolean isNewGw = true;
    private List<RcuInfo> mRcuInfos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //初始化控件
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        username = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_password);
        check = (CheckBox) findViewById(R.id.login_check);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                String id = username.getText().toString();
                String pass = password.getText().toString();

                List<RcuInfo> list = new ArrayList<RcuInfo>();
                RcuInfo info = new RcuInfo();
                info.setDevUnitID(id);
                info.setDevUnitPass(pass);
                info.setName("手机");

                list.add(info);
                Gson gson = new Gson();
                String str = gson.toJson(list);
                SharedPreferences sharedPreferences = getSharedPreferences("profile", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("list", str);
                editor.commit();
                startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
                finish();
                break;
        }
    }
}
