package cn.etsoft.smarthome.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthome.Helper.WareDataHliper;
import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.KeySceneAdapter;
import cn.etsoft.smarthome.domain.WareSceneEvent;
import cn.etsoft.smarthome.ui.KeySceneActivity;
import cn.etsoft.smarthome.utils.ToastUtil;

/**
 * Created by Say GoBay on 2016/8/25.
 * 组合设置--按键情景--情景
 */
public class KeySceneFragment extends Fragment implements AdapterView.OnItemClickListener {
    private ScrollView sv;
    private ListView lv;
    private List<WareSceneEvent> event;
    private KeySceneAdapter keySceneAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        //初始化ListView
        initListView(view);
        return view;
    }

    /**
     * 初始化ListView
     */
    private void initListView(View view) {
        lv = (ListView) view.findViewById(R.id.group_lv);
        sv = (ScrollView) view.findViewById(R.id.group_sv);
        sv.smoothScrollTo(0, 0);
        event = WareDataHliper.initCopyWareData().getSceneControlData();
        keySceneAdapter = new KeySceneAdapter(event,getActivity());
        lv.setAdapter(keySceneAdapter);
        lv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        int listSize = event.size();
        if (listSize > 0) {
            if (position < listSize) {
                Intent intent = new Intent(getActivity(), KeySceneActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("sceneId", event.get(position).getEventId());
                bundle.putString("sceneName", event.get(position).getSceneName());
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                ToastUtil.showText("数据异常");
            }
        }
    }
}
