package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cn.etsoft.smarthomephone.R;

/**
 * Created by Say GoBay on 2016/9/5.
 */
public class RegisterActivity extends Activity implements View.OnClickListener{
    private TextView title;
    private ImageView back;
    private EditText username,password,confirm,phone,check;
    private Button sure;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //初始化标题栏
        initTitleBar();
        //初始化控件
        initView();
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        title = (TextView) findViewById(R.id.title_bar_tv_title);
        title.setText("新用户注册");
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        back.setOnClickListener(this);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        username = (EditText) findViewById(R.id.register_username);
        password = (EditText) findViewById(R.id.register_password);
        confirm = (EditText) findViewById(R.id.register_confirm);
        phone = (EditText) findViewById(R.id.register_phone);
        check = (EditText) findViewById(R.id.register_check);
        sure = (Button) findViewById(R.id.register_sure);
        sure.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_bar_iv_back:
                finish();
                break;
            case R.id.register_sure:
                break;
        }

    }
}
