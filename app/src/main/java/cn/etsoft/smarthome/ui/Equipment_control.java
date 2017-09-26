package cn.etsoft.smarthome.ui;

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

import cn.etsoft.smarthome.Fragment.EditDevFragment;
import cn.etsoft.smarthome.Fragment.EditModuleFragment;
import cn.etsoft.smarthome.R;

/**
 * Created by fbl on 16-11-17.
 * 设备控制
 */
public class Equipment_control extends FragmentActivity {
//    private RadioGroup radioGroup;
    private ImageView bacd;
    private TextView title;
    public static final String fragment1Tag = "fragment1";
    public static final String fragment2Tag = "fragment2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_control);

        bacd = (ImageView) findViewById(R.id.title_bar_iv_back);
        title = (TextView) findViewById(R.id.title_bar_tv_title);

        bacd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        title.setText("设备编辑");
//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//        Fragment fragment = new EditDevFragment(Equipment_control.this);
//        ft.add(R.id.group, fragment, fragment1Tag);
//        ft.show(fragment);
//        ft.commit();
//        radioGroup = (RadioGroup) findViewById(R.id.rg_group);
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                FragmentManager fm = getSupportFragmentManager();
//                FragmentTransaction ft = fm.beginTransaction();
//                Fragment fragment1 = fm.findFragmentByTag(fragment1Tag);
//                Fragment fragment2 = fm.findFragmentByTag(fragment2Tag);
//                if (fragment1 != null) {
//                    ft.hide(fragment1);
//                }
//                if (fragment2 != null) {
//                    ft.hide(fragment2);
//                }
//                switch (checkedId) {
//                    case R.id.edit_dev:
//                        if (fragment1 == null) {
//                            fragment1 = new EditDevFragment(Equipment_control.this);
//                            ft.add(R.id.group, fragment1, fragment1Tag);
//                        } else {
//                            ft.show(fragment1);
//                            title.setText("设备编辑");
//                        }
//                        break;
//                    case R.id.edit_module:
//                        if (fragment2 == null) {
//                            fragment2 = new EditModuleFragment(Equipment_control.this);
//                            ft.add(R.id.group, fragment2, fragment2Tag);
//                        } else {
//                            ft.show(fragment2);
//                            title.setText("输入板编辑");
//                        }
//                        break;
//                    default:
//                        break;
//                }
//                ft.commit();
//            }
//        });
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = new EditDevFragment(Equipment_control.this);
            fragmentManager.beginTransaction()
                    .replace(R.id.group, fragment, fragment1Tag).commit();
        }
    }

//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        for (int i = 0; i < radioGroup.getChildCount(); i++) {
//            RadioButton mTab = (RadioButton) radioGroup.getChildAt(i);
//            FragmentManager fm = getSupportFragmentManager();
//            Fragment fragment = fm.findFragmentByTag((String) mTab.getTag());
//            FragmentTransaction ft = fm.beginTransaction();
//            if (fragment != null) {
//                if (!mTab.isChecked()) {
//                    ft.hide(fragment);
//                }
//            }
//            ft.commit();
//        }
//    }
}
