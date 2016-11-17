package cn.etsoft.smarthomephone.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import cn.etsoft.smarthomephone.Fragment.HomeFragment;
import cn.etsoft.smarthomephone.Fragment.SettingFragment;
import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.pullmi.utils.Dtat_Cache;

public class HomeActivity extends FragmentActivity implements View.OnClickListener{
    private RadioGroup homeRadioGroup;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private Fragment homeFragment, settingFragment;
    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //初始化控件
        initView();
        //初始化数据
        initData();
    }
    /**
     * 初始化控件
     */
    private void initView() {
        fragmentManager = getSupportFragmentManager();
        homeRadioGroup = (RadioGroup) findViewById(R.id.rg_home);
        ((RadioButton) homeRadioGroup.findViewById(R.id.rb_home_home)).setChecked(true);
        transaction = fragmentManager.beginTransaction();
    }
    /**
     * 初始化数据
     */
    private void initData() {
        homeFragment = new HomeFragment();
        settingFragment = new SettingFragment();

        transaction.replace(R.id.home, homeFragment).commit();
        homeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                transaction = fragmentManager.beginTransaction();
                switch (checkedId) {
                    case R.id.rb_home_home:
                        transaction.replace(R.id.home, homeFragment);
                        break;
                    case R.id.rb_home_setting:
                        transaction.replace(R.id.home, settingFragment);
                        break;
                }
                transaction.commit();
            }
        });
    }

    private long TimeExit = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (System.currentTimeMillis() - TimeExit < 1500) {
            Dtat_Cache.writeFile(MyApplication.getWareData());
            MyApplication.mInstance.getActivity().finish();
            System.exit(0);
        } else {
            Toast.makeText(HomeActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            TimeExit = System.currentTimeMillis();
        }
        return false;
    }

    @Override
    public void onClick(View v) {

    }
}
