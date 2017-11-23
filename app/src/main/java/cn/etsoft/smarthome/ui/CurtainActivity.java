package cn.etsoft.smarthome.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.Cur_GridViewAdapter;
import cn.etsoft.smarthome.adapter.GridViewAdapter_air;
import cn.etsoft.smarthome.adapter.Room_Select_Adapter;
import cn.etsoft.smarthome.domain.UdpProPkt;
import cn.etsoft.smarthome.domain.WareCurtain;
import cn.etsoft.smarthome.utils.SendDataUtil;
import cn.etsoft.smarthome.utils.ToastUtil;
import cn.etsoft.smarthome.weidget.CustomDialog;

/**
 * Created by Say GoBay on 2016/9/1.
 * 窗帘页面
 */
public class CurtainActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private GridView gridView;
    private Cur_GridViewAdapter adapter;
    private LinearLayout ll;
    private ImageView back, title_bar_iv_or;
    private TextView title, name_cur, title_bar_tv_room;
    private List<WareCurtain> AllCurtain;
    private List<WareCurtain> Curtains;
    private List<String> room_list;
    private WareCurtain curtain;
    private int position_room = -1;
    private int position_room_banner = 0;
    private boolean IsCanClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cur);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.AppTheme_color));

        //初始化标题栏及控件
        initTitleBar();
        //初始化数据
        upData();
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        title = (TextView) findViewById(R.id.title_bar_tv_title);

        title_bar_tv_room = (TextView) findViewById(R.id.title_bar_tv_room);
        title_bar_tv_room.setVisibility(View.VISIBLE);
        title_bar_tv_room.setTextColor(Color.WHITE);
        title_bar_iv_or = (ImageView) findViewById(R.id.title_bar_iv_or);
        title_bar_iv_or.setImageResource(R.drawable.fj1);
        title_bar_iv_or.setVisibility(View.VISIBLE);
        title_bar_iv_or.setOnClickListener(this);
        name_cur = (TextView) findViewById(R.id.name_cur);
        ll = (LinearLayout) findViewById(R.id.ll);
        ll.setBackgroundResource(R.drawable.tu4);
        title.setText(getIntent().getStringExtra("title") + "控制");
        title.setTextColor(0xffffffff);
        back.setImageResource(R.drawable.return2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void upData() {
        position_room_banner = getIntent().getIntExtra("viewPage_num", 0);
        try {
            //房间
            if (MyApplication.getWareData().getRooms().size() == 0)
                return;
            //所有灯
            AllCurtain = new ArrayList<>();
            for (int i = 0; i < MyApplication.getWareData().getCurtains().size(); i++) {
                AllCurtain.add(MyApplication.getWareData().getCurtains().get(i));
            }
            Curtains = new ArrayList<>();
            //房间集合
            room_list = new ArrayList<>();
            room_list.add("全部");
            room_list.addAll(MyApplication.getWareData().getRooms());
            //房间名称；
            if (position_room != -1) {
                title_bar_tv_room.setText(room_list.get(position_room));
                if (position_room == 0) {
                    Curtains.addAll(AllCurtain);
                } else {
                    //根据房间id获取设备；
                    for (int i = 0; i < AllCurtain.size(); i++) {
                        if (AllCurtain.get(i).getDev().getRoomName().equals(title_bar_tv_room.getText())) {
                            Curtains.add(AllCurtain.get(i));
                        }
                    }
                }
            } else {
                title_bar_tv_room.setText(room_list.get(position_room_banner + 1));
                //根据房间id获取设备；
                for (int i = 0; i < AllCurtain.size(); i++) {
                    if (AllCurtain.get(i).getDev().getRoomName().equals(title_bar_tv_room.getText())) {
                        Curtains.add(AllCurtain.get(i));
                    }
                }
            }
            if (Curtains.size() == 0) {//如果这个房间没有窗帘，则显示"没有窗帘";
                adapter = new Cur_GridViewAdapter(new ArrayList<WareCurtain>(), this);
                gridView.setAdapter(adapter);
                ToastUtil.showText(title_bar_tv_room.getText() + "没有窗帘数据，请添加");
                return;
            } else {
                initGridView(Curtains);
            }
        } catch (Exception e) {
        }
    }


    /**
     * 初始化GridView
     */

    private void initGridView(List<WareCurtain> list) {
        adapter = new Cur_GridViewAdapter(list, this);
        gridView = (GridView) findViewById(R.id.light_gv);
        gridView.setAdapter(adapter);
        gridView.setSelector(R.drawable.selector_gridview_item);
    }

    long TimeExit = 0;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (IsCanClick && curtain != null) {
            //连续点击，间隔小于1秒，不做反应
            if (System.currentTimeMillis() - TimeExit < 1000) {
                TimeExit = System.currentTimeMillis();
                return;
            }
            //给点击按钮添加点击音效
            MyApplication.mApplication.getSp().play(MyApplication.mApplication.getMusic(), 1, 1, 0, 0, 1);
            int Value = -1;
            switch (position) {
                case 0:
                    Value = UdpProPkt.E_CURT_CMD.e_curt_offOn.getValue();
                    break;
                case 1:
                    Value = UdpProPkt.E_CURT_CMD.e_curt_stop.getValue();
                    break;
                case 2:
                    Value = UdpProPkt.E_CURT_CMD.e_curt_offOff.getValue();
                    break;
            }
            if (Value != -1) {
                SendDataUtil.controlDev(curtain.getDev(), Value);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view == title_bar_iv_or && MyApplication.getWareData().getRooms().size() > 0)
            getRoomDialog();
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
        dia_listView.setAdapter(new Room_Select_Adapter(CurtainActivity.this, room_list));

        dia_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                position_room = position;
                upData();
            }
        });
    }
}
