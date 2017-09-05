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

import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.adapter.GridViewAdapter_air;
import cn.etsoft.smarthomephone.ui.SystemSetActivity;

/**
 * Created by Say GoBay on 2016/9/1.
 * 设置碎片
 */
public class SettingFragment extends Fragment implements AdapterView.OnItemClickListener {
    private GridView gridView;
    private int[] image = {R.drawable.setgohome, R.drawable.setalloff, R.drawable.setfullopen, R.drawable.setlight, R.drawable.setcurtain, R.drawable.setsystemsetup};
    private String[] title = {"回家", "设备全关", "设备全开", "灯光总控", "窗帘总控", "系统设置"};

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
        gridView.setAdapter(new GridViewAdapter_air(image, title, getActivity()));
        gridView.setSelector(R.drawable.selector_gridview_item);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        switch (position) {
            //回家
            case 0:
                break;
            //设备全关
            case 1:
                break;
            //设备全开
            case 2:
                break;
            //灯光总控
            case 3:
                break;
            //窗帘总控
            case 4:
                break;
            //系统设置
            case 5:
                intent = new Intent(getActivity(), SystemSetActivity.class);
                intent.putExtra("title", title[5]);
                startActivity(intent);
                break;

        }
    }
}
