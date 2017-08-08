package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.pullmi.app.GlobalVars;
import cn.etsoft.smarthomephone.utils.ToastUtil;

/**
 * Created by Say GoBay on 2016/10/13.
 * 房间页面
 */
public class IpActivity extends Activity{
    private ImageView back;
    private TextView title;
    private SharedPreferences sharedPreferences;
    Button long_ip_save;
    EditText long_ip_num_1, long_ip_num_2, long_ip_num_3, long_ip_num_4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip);
        sharedPreferences = this.getSharedPreferences("profile", Context.MODE_PRIVATE);
        //初始化标题栏
        initTitleBar();
        //初始化控件
        initView();
        //加载数据
        event();
    }
        /**
         * 初始化标题栏
         */
        private void initTitleBar(){
            back = (ImageView) findViewById(R.id.title_bar_iv_back);
            title = (TextView) findViewById(R.id.title_bar_tv_title);
            title.setText(getIntent().getStringExtra("title"));
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

    /**
     * 初始化控件
     */
    private void initView() {
        long_ip_save = (Button) findViewById(R.id.long_ip_save);
        long_ip_num_1 = (EditText) findViewById(R.id.long_ip_num_1);
        long_ip_num_2 = (EditText) findViewById(R.id.long_ip_num_2);
        long_ip_num_3 = (EditText) findViewById(R.id.long_ip_num_3);
        long_ip_num_4 = (EditText) findViewById(R.id.long_ip_num_4);
        long_ip_num_1.setFocusable(false);
        long_ip_num_1.setEnabled(false);
        long_ip_num_2.setFocusable(false);
        long_ip_num_2.setEnabled(false);
        long_ip_num_3.setFocusable(false);
        long_ip_num_3.setEnabled(false);
        long_ip_num_4.setFocusable(false);
        long_ip_num_4.setEnabled(false);
        String long_ip = sharedPreferences.getString("long_ip", "123.206.104.89");
        String num_1 = long_ip.substring(0,long_ip.indexOf("."));
        String long_ip_2 = long_ip.substring(long_ip.indexOf(".")+1);
        String num_2 = long_ip_2.substring(0, long_ip_2.indexOf("."));
        String long_ip_3 = long_ip_2.substring(long_ip.indexOf(".")+1);
        String num_3 = long_ip_3.substring(0, long_ip_3.indexOf("."));
        String num_4 = long_ip.substring(long_ip.lastIndexOf(".")+1);
        long_ip_num_1.setText(num_1);
        long_ip_num_2.setText(num_2);
        long_ip_num_3.setText(num_3);
        long_ip_num_4.setText(num_4);
    }

    /**
     * 加载数据
     */
    private void event() {
        long_ip_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = long_ip_num_1.getText().toString() + "." + long_ip_num_2.getText().toString() + "."
                        + long_ip_num_3.getText().toString() + "." + long_ip_num_4.getText().toString();
                GlobalVars.setDstip(ip);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("long_ip", ip);
                editor.commit();
//                MyApplication.setRcuDevIDtoLocal();
                ToastUtil.showToast(IpActivity.this,"保存成功");
            }
        });
    }
}
