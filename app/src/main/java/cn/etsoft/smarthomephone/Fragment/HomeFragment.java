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
import cn.etsoft.smarthomephone.adapter.GridViewAdapter;
import cn.etsoft.smarthomephone.ui.AirConditionActivity;
import cn.etsoft.smarthomephone.ui.CurtainActivity;
import cn.etsoft.smarthomephone.ui.HomeActivity;
import cn.etsoft.smarthomephone.ui.LightActivity;
import cn.etsoft.smarthomephone.ui.SafetyActivity_home;
import cn.etsoft.smarthomephone.ui.SceneActivity;
import cn.etsoft.smarthomephone.ui.StbActivity;
import cn.etsoft.smarthomephone.ui.TvActivity;
import cn.etsoft.smarthomephone.ui.UserActivity;
import cn.etsoft.smarthomephone.utils.ToastUtil;
import cn.semtec.community2.MyApplication;

/**
 * Created by Say GoBay on 2016/9/1.
 * 设备控制碎片
 */
public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener {

    private GridView gridView;
    private int[] image = {R.drawable.user_home,R.drawable.air_home, R.drawable.tv_home, R.drawable.stb_home, R.drawable.light_home,
            R.drawable.curtain_home, R.drawable.scene_home, R.drawable.control_home, R.drawable.socket_home, R.drawable.door_home,R.drawable.safety_home};
    private String[] title = {"用户界面","空调", "电视", "机顶盒", "灯光", "窗帘", "情景", "门禁", "插座", "门锁","安防"};
    private int HomeAct_viewPage_position;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //初始化GridView
        initGridView(view);

        MyApplication.getmHomeActivity().setOnGetViewPageNum(new HomeActivity.OnGetViewPageNum() {
            @Override
            public void getViewPageNum(int position) {
//                ToastUtil.showToast(getActivity(), "主页ViewPage数值:" + position + "");
                HomeAct_viewPage_position = position;
            }
        });
        return view;
    }

    /**
     * 初始化GridView
     * @param view
     */
    private void initGridView(View view) {
        gridView = (GridView) view.findViewById(R.id.home_gv);
        gridView.setAdapter(new GridViewAdapter(image, title, getActivity()));
        gridView.setSelector(R.drawable.selector_gridview_item);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = null;
        switch (position) {
            //用户界面
            case 0:
                if (MyApplication.mInstance.isSkip()) {
                    ToastUtil.showToast(getActivity(), "跳过登录不能体验此功能");
                    return;
                }
                intent = new Intent(getActivity(), UserActivity.class);
                intent.putExtra("title", title[0]);
                intent.putExtra("tag", "home");
                break;
            //空调
            case 1:
                intent = new Intent(getActivity(), AirConditionActivity.class);
                intent.putExtra("title", title[1]);
                break;
            //电视
            case 2:
                intent = new Intent(getActivity(), TvActivity.class);
                intent.putExtra("title", title[2]);
                break;
            //机顶盒
            case 3:
                intent = new Intent(getActivity(), StbActivity.class);
                intent.putExtra("title", title[3]);
                break;
            //灯光
            case 4:
                intent = new Intent(getActivity(), LightActivity.class);
                intent.putExtra("title", title[4]);
                break;
            //窗帘
            case 5:
                intent = new Intent(getActivity(), CurtainActivity.class);
                intent.putExtra("title", title[5]);
                break;
            //情景
            case 6:
                intent = new Intent(getActivity(), SceneActivity.class);
                intent.putExtra("title", title[6]);

                break;
            //门禁
            case 7:
                startActivity(new Intent(getActivity(), cn.semtec.community2.WelcomeActivity.class));
                break;
            case 10:
                startActivity(new Intent(getActivity(), SafetyActivity_home.class));
                break;
        }
        if (intent != null) {
            intent.putExtra("viewPage_num", HomeAct_viewPage_position);
            startActivity(intent);
        }
    }
}
