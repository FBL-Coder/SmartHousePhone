package cn.etsoft.smarthomephone.Fragment;

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

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.adapter.KeySceneAdapter;
import cn.etsoft.smarthomephone.pullmi.entity.WareSceneEvent;
import cn.etsoft.smarthomephone.ui.KeySceneActivity;
import cn.etsoft.smarthomephone.utils.ToastUtil;

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
        event = new ArrayList<>();
        event.clear();
        if (MyApplication.getWareData().getSceneEvents().size() > 0) {
            for (int i = 0; i < MyApplication.getWareData().getSceneEvents().size(); i++) {
                event.add(MyApplication.getWareData().getSceneEvents().get(i));
            }
            keySceneAdapter = new KeySceneAdapter(event, getActivity());
            lv.setAdapter(keySceneAdapter);
        } else if (MyApplication.getWareData().getSceneEvents() == null || MyApplication.getWareData().getSceneEvents().size() == 0) {
            ToastUtil.showToast(getActivity(), "没有收到情景信息");
            return;
        }
        lv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        int listSize = event.size();
        if (listSize > 0) {
            if (position < listSize) {
                Intent intent = new Intent(getActivity(), KeySceneActivity.class);
                Bundle bundle = new Bundle();
                bundle.putByte("sceneId", event.get(position).getEventld());
                bundle.putString("sceneName", event.get(position).getSceneName());
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                ToastUtil.showToast(getActivity(), "数据异常");
            }
        }
    }
}
