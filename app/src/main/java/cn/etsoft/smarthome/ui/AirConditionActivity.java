package cn.etsoft.smarthome.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.AirConditionAdapter;
import cn.etsoft.smarthome.adapter.GridViewAdapter_air;
import cn.etsoft.smarthome.adapter.Room_Select_Adapter;
import cn.etsoft.smarthome.domain.UdpProPkt;
import cn.etsoft.smarthome.domain.WareAirCondDev;
import cn.etsoft.smarthome.utils.SendDataUtil;
import cn.etsoft.smarthome.utils.ToastUtil;
import cn.etsoft.smarthome.weidget.CustomDialog;

/**
 * Created by Say GoBay on 2016/9/1.
 * 空调页面
 */
public class AirConditionActivity extends Activity {
    private ListView airListview;
    private ImageView back, select;
    private AirConditionAdapter airConditionAdapter;
    private static final int MSG_REFRSH_INFO = 1000;
    private TextView title, title_bar_tv_room;
    /**
     * 没有数据控制按钮不可点击；
     */
    private boolean IsCanClick = false;
    private List<WareAirCondDev> AllAic;
    private List<WareAirCondDev> AirConds;
    private List<String> room_list;
    /**
     * 主页ViewPage的选择，以及后面选择房间的Position；
     */
    private int position_room = -1;

    /**
     * 一个房间多个空调的话，选择其中一个记录，以便数据刷新页面刷新；
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aircondition);
        //初始化控件
        initView();
    }

    private void initEvent() {

        MyApplication.mApplication.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int datType, int subtype1, int subtype2) {
                if (datType == 4 || datType == 3)
                    //初始化控件
                    upData();
            }
        });
    }


    private void upData() {
        //房间
        if (MyApplication.getWareData().getRooms().size() == 0)
            return;
        //空调
        if (MyApplication.getWareData().getAirConds().size() == 0) {
            ToastUtil.showText("请添加空调");
            return;
        }
        //所有空调
        AllAic = new ArrayList<>();
        for (int i = 0; i < MyApplication.getWareData().getAirConds().size(); i++) {
            AllAic.add(MyApplication.getWareData().getAirConds().get(i));
        }
        AirConds = new ArrayList<>();
        //房间集合
        room_list = new ArrayList<>();
        room_list.add("全部");
        room_list.addAll(MyApplication.getWareData().getRooms());
        //房间按钮的点击
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (room_list.size() == 0)
                    return;
                getRoomDialog();
            }
        });

        //房间名称；
        if (position_room != -1)
            title_bar_tv_room.setText(room_list.get(position_room));
        else
            title_bar_tv_room.setText(MyApplication.getWareData().getRooms().get(getIntent().getIntExtra("viewPage_num", 0)));
        //根据房间id获取设备；
        if ("全部".equals(title_bar_tv_room.getText())) {
            AirConds.addAll(AllAic);
        } else {
            for (int i = 0; i < AllAic.size(); i++) {
                if (AllAic.get(i).getDev().getRoomName().equals(title_bar_tv_room.getText())) {
                    AirConds.add(AllAic.get(i));
                }
            }
        }
        if (airConditionAdapter == null) {
            airConditionAdapter = new AirConditionAdapter(this, AirConds);
            airListview.setAdapter(airConditionAdapter);
        } else airConditionAdapter.notifyDataSetChanged(AirConds);
    }

    /**
     * 选择房间的dialog
     */
    CustomDialog dialog;

    public void getRoomDialog() {
        ListView dia_listView;
        dialog = new CustomDialog(this, R.style.customDialog_null, R.layout.air_select_item);

        //获得当前窗体
        Window window = dialog.getWindow();
        //重新设置
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.RIGHT | Gravity.TOP);
        lp.x = 0; // 新位置X坐标
        lp.y = 65; // 新位置Y坐标
        lp.width = 300; // 宽度
        lp.height = 300; // 高度
        // dialog.onWindowAttributesChanged(lp);
        //(当Window的Attributes改变时系统会调用此函数)
        window.setAttributes(lp);
        dialog.show();
        TextView textView = (TextView) dialog.findViewById(R.id.select_room);
        textView.setText("请选择房间");
        dia_listView = (ListView) dialog.findViewById(R.id.air_select);
        dia_listView.setAdapter(new Room_Select_Adapter(AirConditionActivity.this, room_list));

        dia_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                position_room = position;
                upData();
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initView() {

        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        title = (TextView) findViewById(R.id.title_bar_tv_title);
        title_bar_tv_room = (TextView) findViewById(R.id.title_bar_tv_room);
        title_bar_tv_room.setVisibility(View.VISIBLE);
        select = (ImageView) findViewById(R.id.title_bar_iv_or);
        select.setVisibility(View.VISIBLE);
        select.setImageResource(R.drawable.fj1);

        airListview = (ListView) findViewById(R.id.airCondition_gv);
        back.setImageResource(R.drawable.return2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (MyApplication.getWareData() != null) {
            if (MyApplication.getWareData().getAirConds() != null
                    && MyApplication.getWareData().getAirConds().size() > 0) {
                upData();
                IsCanClick = true;
            }
        } else {
            ToastUtil.showText("没有找到可控空调");
        }
        title.setText(getIntent().getStringExtra("title") + "控制");
        title.setTextColor(0xffffffff);
        initEvent();
    }
}


