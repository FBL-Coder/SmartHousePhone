package cn.etsoft.smarthome.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.GridViewAdapter;
import cn.etsoft.smarthome.ui.ConditionEventActivity;
import cn.etsoft.smarthome.ui.ControlActivity;
import cn.etsoft.smarthome.ui.EditDevActivity;
import cn.etsoft.smarthome.ui.Equipment_control;
import cn.etsoft.smarthome.ui.GroupSetActivity;
import cn.etsoft.smarthome.ui.ModuleDetailActivity;
import cn.etsoft.smarthome.ui.NewWorkSetActivity;
import cn.etsoft.smarthome.ui.SafetyActivity;
import cn.etsoft.smarthome.ui.SceneSetActivity;
import cn.etsoft.smarthome.ui.TimerActivity;
import cn.etsoft.smarthome.utils.SendDataUtil;

/**
 * Created by Say GoBay on 2016/9/1.
 * 设置碎片
 */
public class SettingFragment extends Fragment implements AdapterView.OnItemClickListener {
    private GridView gridView;
    private Activity mActivity;
    private String[] text = {"联网模块", "控制设置",
            "设备信息", "情景设置", "安防设置", "定时设置", "环境事件", "组合设置"};
    private int[] image = {R.drawable.net_set,
            R.drawable.control_set, R.drawable.equip_set,
            R.drawable.scene_set, R.drawable.safety_set, R.drawable.time_set,
            R.drawable.env_set, R.drawable.group_set};

    public SettingFragment(Activity activity){
        mActivity = activity;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set, container, false);
        //初始化GridView
        initGridView(view);
        return view;
    }

    /**
     * 初始化GridView
     *
     * @param view
     */
    private void initGridView(View view) {
        gridView = (GridView) view.findViewById(R.id.home_gv);
        gridView.setAdapter(new GridViewAdapter(image, text, mActivity));
        gridView.setSelector(R.drawable.selector_gridview_item);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        switch (position) {
            case 0:
                intent = new Intent(mActivity, NewWorkSetActivity.class);
                intent.putExtra("title", text[0]);
                startActivity(intent);
                break;
//            case 1:
//                intent = new Intent(mActivity, ModuleDetailActivity.class);
//                intent.putExtra("title", text[1]);
//                startActivity(intent);
//                break;
            case 1:
                intent = new Intent(mActivity, ControlActivity.class);
                intent.putExtra("title", text[1]);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(mActivity, EditDevActivity.class);
                intent.putExtra("title", text[2]);
                startActivity(intent);
                break;
            case 3:
                if (MyApplication.getWareData().getSceneEvents().size() == 0)
                    SendDataUtil.getSceneInfo();
                intent = new Intent(mActivity, SceneSetActivity.class);
                intent.putExtra("title", text[3]);
                startActivity(intent);
                break;
            case 4:
                intent = new Intent(mActivity, SafetyActivity.class);
                intent.putExtra("title", text[4]);
                startActivity(intent);
                break;
            case 5:
                intent = new Intent(mActivity, TimerActivity.class);
                intent.putExtra("title", text[5]);
                startActivity(intent);
                break;
            case 6:
                intent = new Intent(mActivity, ConditionEventActivity.class);
                intent.putExtra("title", text[6]);
                startActivity(intent);
                break;
            case 7:
                intent = new Intent(mActivity, GroupSetActivity.class);
                intent.putExtra("title", text[7]);
                startActivity(intent);
                break;
        }
    }
}
