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

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.adapter.BoardInOutAdapter;
import cn.etsoft.smarthomephone.pullmi.entity.UdpProPkt;
import cn.etsoft.smarthomephone.ui.ParlourFourOutActivity;

/**
 * Created by Say GoBay on 2016/8/25.
 */
public class OutPutFragment extends Fragment implements AdapterView.OnItemClickListener{
    private ScrollView sv;
    private ListView lv;
    private int[] image = {R.drawable.ketingsijian, R.drawable.chufangsanjian,
            R.drawable.ketingchuanglian, R.drawable.ketingkongtiao};
    private int[] hui = {R.drawable.huijiantou, R.drawable.huijiantou, R.drawable.huijiantou,
            R.drawable.huijiantou};
    private String[] title = {"一楼客厅四键", "厨房三键", "一楼客厅窗帘", "一楼客厅空调"};
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
        if(MyApplication.getWareData().getBoardChnouts().size() > 0) {
            lv.setAdapter(new BoardInOutAdapter(getActivity(), MyApplication.getWareData().getBoardChnouts() , null, UdpProPkt.E_BOARD_TYPE.e_board_chnOut.getValue()));
            lv.setOnItemClickListener(this);
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), ParlourFourOutActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("title", MyApplication.getWareData().getBoardChnouts().get(position).getBoardName());
        bundle.putString("uid", MyApplication.getWareData().getBoardChnouts().get(position).getDevUnitID());
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
