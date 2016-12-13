package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.pullmi.app.GlobalVars;

/**
 * Created by fbl on 16-11-11.
 */
public class Add_long_ip extends Activity {

    Button long_ip_save;
    EditText long_ip_num_1, long_ip_num_2, long_ip_num_3, long_ip_num_4;
    private TextView title;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_longip);

        intTitleBar();

        initView();

        event();
    }

    private void intTitleBar() {
        title = (TextView) findViewById(R.id.title_bar_tv_title);
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        title.setText("远程控制");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void event() {

        long_ip_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ip = long_ip_num_1.getText().toString() + "." + long_ip_num_2.getText().toString() + "."
                        + long_ip_num_3.getText().toString() + "." + long_ip_num_4.getText().toString();
                GlobalVars.setDstip(ip);
                MyApplication.setRcuDevIDtoLocal();
                finish();
            }
        });
    }

    private void initView() {
        long_ip_save = (Button) findViewById(R.id.long_ip_save);
        long_ip_num_1 = (EditText) findViewById(R.id.long_ip_num_1);
        long_ip_num_2 = (EditText) findViewById(R.id.long_ip_num_2);
        long_ip_num_3 = (EditText) findViewById(R.id.long_ip_num_3);
        long_ip_num_4 = (EditText) findViewById(R.id.long_ip_num_4);
    }
}
