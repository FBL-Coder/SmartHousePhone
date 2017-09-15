package cn.etsoft.smarthomephone.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.adapter.GridViewAdapter;
import cn.etsoft.smarthomephone.ui.ConditionEventActivity;
import cn.etsoft.smarthomephone.ui.ControlActivity;
import cn.etsoft.smarthomephone.ui.Equipment_control;
import cn.etsoft.smarthomephone.ui.GroupSetActivity;
import cn.etsoft.smarthomephone.ui.IpActivity;
import cn.etsoft.smarthomephone.ui.ModuleDetailActivity;
import cn.etsoft.smarthomephone.ui.NetWorkActivity;
import cn.etsoft.smarthomephone.ui.SafetyActivity;
import cn.etsoft.smarthomephone.ui.SceneSetActivity;
import cn.etsoft.smarthomephone.ui.TimerActivity;
import cn.etsoft.smarthomephone.ui.UserActivity;
import cn.etsoft.smarthomephone.utils.ToastUtil;

/**
 * Created by Say GoBay on 2016/9/1.
 * 设置碎片
 */
public class SettingFragment extends Fragment implements AdapterView.OnItemClickListener{
    private GridView gridView;
    private String[] text = {"联网模块", "远程IP", "模块详情", "用户界面", "控制设置",
            "设备信息", "情景设置", "安防设置", "定时设置", "环境事件", "组合设置"};
    private int[] image = {R.drawable.net_set, R.drawable.ip_set, R.drawable.module_set, 
            R.drawable.user_set, R.drawable.control_set, R.drawable.equip_set,
            R.drawable.scene_set, R.drawable.safety_set,R.drawable.time_set, 
            R.drawable.env_set, R.drawable.group_set};
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
     * @param view
     */
    private void initGridView(View view) {
        gridView = (GridView) view.findViewById(R.id.home_gv);
        gridView.setAdapter(new GridViewAdapter(image, text, getActivity()));
        gridView.setSelector(R.drawable.selector_gridview_item);
        gridView.setOnItemClickListener(this);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        switch (position) {
            case 0:
                intent = new Intent(getActivity(), NetWorkActivity.class);
                MyApplication.getActivities().add(getActivity());
                intent.putExtra("title", text[0]);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(getActivity(), IpActivity.class);
                intent.putExtra("title", text[1]);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(getActivity(), ModuleDetailActivity.class);
                intent.putExtra("title", text[2]);
                startActivity(intent);
                break;
            case 3:
                if (MyApplication.mInstance.isSkip()) {
                    ToastUtil.showToast(getActivity(), "跳过登录不能体验此功能");
                    return;
                }
                intent = new Intent(getActivity(), UserActivity.class);
                intent.putExtra("title", text[3]);
                intent.putExtra("tag","user");
                startActivity(intent);
                break;
            case 4:
                intent = new Intent(getActivity(), ControlActivity.class);
                intent.putExtra("title", text[4]);
                startActivity(intent);
                break;
            case 5:
                intent = new Intent(getActivity(), Equipment_control.class);
                intent.putExtra("title", text[5]);
                startActivity(intent);
                break;
            case 6:
                intent = new Intent(getActivity(), SceneSetActivity.class);
                intent.putExtra("title", text[6]);
                startActivity(intent);
                break;
            case 7:
                intent = new Intent(getActivity(), SafetyActivity.class);
                intent.putExtra("title", text[7]);
                startActivity(intent);
                break;
            case 8:
                intent = new Intent(getActivity(), TimerActivity.class);
                intent.putExtra("title", text[8]);
                startActivity(intent);
                break;
            case 9:
                intent = new Intent(getActivity(), ConditionEventActivity.class);
                intent.putExtra("title", text[9]);
                startActivity(intent);
                break;
            case 10:
                intent = new Intent(getActivity(), GroupSetActivity.class);
                intent.putExtra("title", text[10]);
                startActivity(intent);
                break;

        }
    }
}
