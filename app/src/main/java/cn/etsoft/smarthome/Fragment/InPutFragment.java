package cn.etsoft.smarthome.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.List;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.GroupList_InputAdapter;
import cn.etsoft.smarthome.domain.WareBoardKeyInput;
import cn.etsoft.smarthome.utils.ToastUtil;

/**
 * Created by Say GoBay on 2016/8/25.
 * 组合设置--输出页面
 */
public class InPutFragment extends Fragment {
    private ExpandableListView mGroupListView;
    private List<WareBoardKeyInput> GroupInputListDatas;
    private GroupList_InputAdapter inputAdapter;
    private Activity mActivity;
    private View view;

    public InPutFragment(Activity activity) {
        mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group, container, false);
        initData();
        return view;
    }

    private void initData() {
        if (MyApplication.getWareData().getKeyInputs() == null
                || MyApplication.getWareData().getKeyInputs().size() == 0) {
            ToastUtil.showText("没有输出板信息,请在主页刷新数据");
            return;
        }
        GroupInputListDatas = MyApplication.getWareData().getKeyInputs();
        //初始化ListView
        initListView();
    }

    @Override
    public void onResume() {
        MyApplication.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int datType, int subtype1, int subtype2) {
                if (datType == 9 && subtype1 == 1 && subtype2 == 1) {
                    MyApplication.mApplication.dismissLoadDialog();
                    ToastUtil.showText("操作成功");
                    initData();
                }
            }
        });
        super.onResume();
    }

    /**
     * 初始化ListView
     */
    private void initListView() {
        mGroupListView = (ExpandableListView) view.findViewById(R.id.group_lv);
        if (inputAdapter == null) {
            inputAdapter = new GroupList_InputAdapter(mActivity, GroupInputListDatas);
            mGroupListView.setAdapter(inputAdapter);
        } else inputAdapter.notifyDataSetChanged();

        mGroupListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                Log.i("onChildClick", "onChildClick: " + i + "--" + i1);
                return true;
            }
        });
    }
}
