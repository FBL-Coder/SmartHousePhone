package cn.etsoft.smarthome.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.etsoft.smarthome.Fragment.InPutFragment;
import cn.etsoft.smarthome.Fragment.KeySceneFragment;
import cn.etsoft.smarthome.Fragment.OutPutFragment;
import cn.etsoft.smarthome.R;

/**
 * Created by Say GoBay on 2016/9/5.
 * 输入、输出、按键情景页面
 */
public class DevManageActivity extends FragmentActivity {
    private RadioGroup group;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private Fragment outPutFragment, inPutFragment, keySceneFragment;
    private TextView title;
    private ImageView back;
    private RelativeLayout tab_rl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devmanage);
        //初始化标题栏
        initTitleBar();
        //初始化控件
        initView();
        //初始化数据
        initData();
    }
    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        title = (TextView) findViewById(R.id.title_bar_tv_title);
        tab_rl = (RelativeLayout) findViewById(R.id.tab_rl);
        tab_rl.setBackgroundColor(Color.WHITE);
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
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
        fragmentManager = getSupportFragmentManager();
        group = (RadioGroup) findViewById(R.id.rg_group);
        ((RadioButton) group.findViewById(R.id.group_input)).setChecked(true);
        transaction = fragmentManager.beginTransaction();
    }
    /**
     * 初始化数据
     */
    private void initData() {
        title.setText("设备管理 · 输入");
        inPutFragment = new InPutFragment(DevManageActivity.this);
        transaction.replace(R.id.group, inPutFragment).commit();
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                transaction = fragmentManager.beginTransaction();
                switch (checkedId) {
                    case R.id.group_output:
                        title.setText("设备管理 · 输出");
                        outPutFragment = new OutPutFragment(DevManageActivity.this);
                        transaction.replace(R.id.group, outPutFragment);
                        break;
                    case R.id.group_input:
                        title.setText("设备管理 · 输入");
                        inPutFragment = new InPutFragment(DevManageActivity.this);
                        transaction.replace(R.id.group, inPutFragment);
                        break;
                    case R.id.group_keyScene:
                        title.setText("设备管理 · 情景");
                        keySceneFragment = new KeySceneFragment(DevManageActivity.this);
                        transaction.replace(R.id.group, keySceneFragment);
                        break;
                }
                transaction.commit();
            }
        });
    }
}
