package cn.etsoft.smarthomephone.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import cn.etsoft.smarthomephone.Fragment.InPutFragment;
import cn.etsoft.smarthomephone.Fragment.KeySceneFragment;
import cn.etsoft.smarthomephone.Fragment.OutPutFragment;
import cn.etsoft.smarthomephone.R;

/**
 * Created by Say GoBay on 2016/9/5.
 * 输入、输出、按键情景页面
 */
public class ControlActivity extends FragmentActivity {
    private RadioGroup group;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private Fragment outPutFragment, inPutFragment, keySceneFragment;
    private TextView title;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
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
        title.setText(getIntent().getStringExtra("title"));
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
        outPutFragment = new OutPutFragment();
        inPutFragment = new InPutFragment();
        keySceneFragment = new KeySceneFragment();

        transaction.replace(R.id.group, inPutFragment).commit();
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                transaction = fragmentManager.beginTransaction();
                switch (checkedId) {
                    case R.id.group_output:
                        transaction.replace(R.id.group, outPutFragment);
                        break;
                    case R.id.group_input:
                        transaction.replace(R.id.group, inPutFragment);
                        break;
                    case R.id.group_keyScene:
                        transaction.replace(R.id.group, keySceneFragment);
                        break;
                }
                transaction.commit();
            }
        });
    }
}
