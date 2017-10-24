package cn.etsoft.smarthome.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.GroupList_InputAdapter;
import cn.etsoft.smarthome.adapter.GroupList_SceneAdapter;
import cn.etsoft.smarthome.domain.GroupList_BoardDevData;
import cn.etsoft.smarthome.domain.GroupList_Scene_KeyData;
import cn.etsoft.smarthome.domain.WareBoardChnout;
import cn.etsoft.smarthome.domain.WareBoardKeyInput;
import cn.etsoft.smarthome.domain.WareDev;
import cn.etsoft.smarthome.domain.WareSceneEvent;
import cn.etsoft.smarthome.ui.Scene_KeyActivity;
import cn.etsoft.smarthome.ui.Setting.KeySceneActivity_dev;
import cn.etsoft.smarthome.utils.ToastUtil;

/**
 * Created by Say GoBay on 2016/8/25.
 * 组合设置--输出页面
 */
public class KeySceneFragment extends Fragment {
    private ExpandableListView mGroupListView;
    private List<GroupList_Scene_KeyData> Scene_keyDatas;
    private GroupList_SceneAdapter inputAdapter;
    private Activity mActivity;

    public KeySceneFragment(Activity activity) {
        mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        initData(view);
        return view;
    }

    private void initData(View view) {
        if (MyApplication.getWareData().getKeyInputs() == null
                || MyApplication.getWareData().getKeyInputs().size() == 0) {
            ToastUtil.showText("没有输出板信息,请在主页刷新数据");
            return;
        }
        List<WareSceneEvent> sceneEvents = MyApplication.getWareData().getSceneEvents();
        Scene_keyDatas = new ArrayList<>();
        List<WareBoardKeyInput> keyInputs = MyApplication.getWareData().getKeyInputs();
        for (int i = 0; i < sceneEvents.size(); i++) {
            GroupList_Scene_KeyData scene_keyData = new GroupList_Scene_KeyData();
            scene_keyData.setSceneName(sceneEvents.get(i).getSceneName());
            scene_keyData.setEventId(sceneEvents.get(i).getEventId());
            scene_keyData.setKeyInputs(keyInputs);
            Scene_keyDatas.add(scene_keyData);
        }
        //初始化ListView
        initListView(view);
    }

    /**
     * 初始化ListView
     */
    private void initListView(View view) {
        mGroupListView = (ExpandableListView) view.findViewById(R.id.group_lv);
        if (inputAdapter == null) {
            inputAdapter = new GroupList_SceneAdapter(mActivity, Scene_keyDatas);
            mGroupListView.setAdapter(inputAdapter);
        } else inputAdapter.notifyDataSetChanged();

        mGroupListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                //设备点击进入设备配按键
                return true;
            }
        });

        mGroupListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                Intent intent = new Intent(mActivity, Scene_KeyActivity.class);
                intent.putExtra("title", Scene_keyDatas.get(i).getSceneName());
                intent.putExtra("SceneID", Scene_keyDatas.get(i).getEventId());
                startActivity(intent);
                return true;
            }
        });
    }
}
