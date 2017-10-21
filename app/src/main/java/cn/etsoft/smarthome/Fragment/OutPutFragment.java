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
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.BoardInOutAdapter;
import cn.etsoft.smarthome.adapter.GroupList_OutAdapter;
import cn.etsoft.smarthome.domain.GroupList_BoardDevData;
import cn.etsoft.smarthome.domain.UdpProPkt;
import cn.etsoft.smarthome.domain.WareBoardChnout;
import cn.etsoft.smarthome.domain.WareDev;
import cn.etsoft.smarthome.ui.ParlourFourOutActivity;
import cn.etsoft.smarthome.ui.Setting.Devs_Detail_Activity;
import cn.etsoft.smarthome.utils.ToastUtil;

/**
 * Created by Say GoBay on 2016/8/25.
 * 组合设置--输出页面
 */
public class OutPutFragment extends Fragment {
    private ExpandableListView mGroupListView;
    private List<GroupList_BoardDevData> GroupListDatas;
    private GroupList_OutAdapter outAdapter;
    private Activity mActivity;
    private View view;

    public OutPutFragment(Activity activity) {
        mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group, container, false);
        initData();
        return view;
    }

    @Override
    public void onResume() {
        initData();
        outAdapter.notifyDataSetChanged(GroupListDatas);
        super.onResume();
    }

    private void initData() {
        if (MyApplication.getWareData().getBoardChnouts() == null || MyApplication.getWareData().getBoardChnouts().size() == 0) {
            ToastUtil.showText("没有输出板信息,请在主页刷新数据");
            return;
        }
        List<WareBoardChnout> boardChnouts = MyApplication.getWareData().getBoardChnouts();
        GroupListDatas = new ArrayList<>();
        List<WareDev> devs = MyApplication.getWareData().getDevs();
        for (int i = 0; i < boardChnouts.size(); i++) {
            GroupList_BoardDevData boardDevData = new GroupList_BoardDevData();
            boardDevData.setBoardName(boardChnouts.get(i).getBoardName());
            boardDevData.setBoardType(boardChnouts.get(i).getBoardType());
            boardDevData.setbOnline(boardChnouts.get(i).getbOnline());
            boardDevData.setChnCnt(boardChnouts.get(i).getChnCnt());
            boardDevData.setDevUnitID(boardChnouts.get(i).getDevUnitID());
            boardDevData.setRev2(boardChnouts.get(i).getRev2());
            List<WareDev> devList = new ArrayList<>();
            for (int j = 0; j < devs.size(); j++) {
                if (devs.get(j).getCanCpuId().equals(boardDevData.getDevUnitID())) {
                    devList.add(devs.get(j));
                }
            }
            boardDevData.setDevs(devList);
            GroupListDatas.add(boardDevData);
        }
        //初始化ListView
        initListView();
    }

    /**
     * 初始化ListView
     */
    private void initListView() {
        mGroupListView = (ExpandableListView) view.findViewById(R.id.group_lv);
        if (outAdapter == null) {
            outAdapter = new GroupList_OutAdapter(getActivity(), GroupListDatas);
            mGroupListView.setAdapter(outAdapter);
        } else outAdapter.notifyDataSetChanged(GroupListDatas);

        mGroupListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                Log.i("onChildClick", "onChildClick: " + i + "--" + i1);
                Intent intent = new Intent(mActivity, Devs_Detail_Activity.class);
                intent.putExtra("id", GroupListDatas.get(i).getDevs().get(i1).getDevId());
                intent.putExtra("type", GroupListDatas.get(i).getDevs().get(i1).getType());
                intent.putExtra("cpu", GroupListDatas.get(i).getDevs().get(i1).getCanCpuId());
                startActivity(intent);
                return true;
            }
        });
    }
}
