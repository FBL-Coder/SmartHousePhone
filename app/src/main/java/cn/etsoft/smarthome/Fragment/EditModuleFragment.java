package cn.etsoft.smarthome.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.BoardInOutAdapter;
import cn.etsoft.smarthome.domain.UdpProPkt;
import cn.etsoft.smarthome.ui.Setting.EditModuleActivity;
import cn.etsoft.smarthome.utils.ToastUtil;

/**
 * Created by Say GoBay on 2017/8/4.
 * 编辑按键属性
 */
public class EditModuleFragment extends Fragment implements AdapterView.OnItemClickListener {

    private FragmentActivity mActivity;
    private ScrollView sv;
    private ListView lv;
    private int keyInput_position= 0 ;
    public EditModuleFragment(FragmentActivity activity) {
        mActivity = activity;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        //初始化ListView
        initListView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        lv.setAdapter(new BoardInOutAdapter(getActivity(), null, MyApplication.getWareData().getKeyInputs(), UdpProPkt.E_BOARD_TYPE.e_board_keyInput.getValue()));
    }

    /**
     * 初始化ListView
     */
    private void initListView(View view) {
        lv = (ListView) view.findViewById(R.id.group_lv);
        sv = (ScrollView) view.findViewById(R.id.group_sv);
        sv.smoothScrollTo(0, 0);
        if (MyApplication.getWareData().getKeyInputs().size() > 0) {
            lv.setAdapter(new BoardInOutAdapter(getActivity(), null, MyApplication.getWareData().getKeyInputs(), UdpProPkt.E_BOARD_TYPE.e_board_keyInput.getValue()));
            lv.setOnItemClickListener(this);
        } else if (MyApplication.getWareData().getKeyInputs() == null || MyApplication.getWareData().getKeyInputs().size() == 0) {
            ToastUtil.showText("没有收到输入板信息");
            return;
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), EditModuleActivity.class);
        keyInput_position = position;
        Bundle bundle = new Bundle();
        bundle.putString("title", MyApplication.getWareData().getKeyInputs().get(position).getBoardName());
        bundle.putInt("keyInput_position",keyInput_position);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
