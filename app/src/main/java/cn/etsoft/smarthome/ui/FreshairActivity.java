package cn.etsoft.smarthome.ui;

import android.app.Activity;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.FreshAirAdapter;
import cn.etsoft.smarthome.adapter.Room_Select_Adapter;
import cn.etsoft.smarthome.domain.WareFreshAir;
import cn.etsoft.smarthome.utils.ToastUtil;
import cn.etsoft.smarthome.weidget.CustomDialog;

/**
 * Created by Say GoBay on 2016/9/1.
 * 新风控制界面
 */
public class FreshairActivity extends Activity implements View.OnClickListener {
    private GridView gridView;
    private LinearLayout ll;
    private ImageView back, title_bar_iv_or;
    private TextView title, title_bar_tv_room;

    private List<WareFreshAir> wareFreshAirs;
    private boolean IsCanClick = false;
    private FreshAirAdapter freshAirAdapter;
    private int position_room = -1;
    private List<WareFreshAir> AllFreshair;
    private List<String> room_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);
        //初始化标题栏
        initTitleBar();
        //初始化控件
        intView();
        initEvent();
        //初始化GridView
        initGridView();
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        title_bar_iv_or = (ImageView) findViewById(R.id.title_bar_iv_or);
        title_bar_iv_or.setVisibility(View.VISIBLE);
        title_bar_iv_or.setImageResource(R.drawable.fj1);
        title = (TextView) findViewById(R.id.title_bar_tv_title);
        title.setText("新风控制");
        title.setTextColor(0xffffffff);
        back.setImageResource(R.drawable.return2);
        title_bar_iv_or.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    /**
     * 初始化控件
     */
    private void intView() {
        ll = (LinearLayout) findViewById(R.id.ll);
        ll.setBackgroundResource(R.drawable.tu3);
        title_bar_tv_room = (TextView) findViewById(R.id.title_bar_tv_room);
        title_bar_tv_room.setVisibility(View.VISIBLE);
    }

    /**
     * 初始化GridView
     */
    private void initGridView() {

        gridView = (GridView) findViewById(R.id.light_gv);
        gridView.setSelector(R.drawable.selector_gridview_item);

        if (MyApplication.getWareData().getFreshAirs() != null && MyApplication.getWareData().getFreshAirs().size() > 0) {
            upData();
            IsCanClick = true;
        } else {
            Toast.makeText(this, "没有找到可控制新风", Toast.LENGTH_SHORT).show();
        }
    }

    long TimeExit = 0;


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
        dia_listView.setAdapter(new Room_Select_Adapter(FreshairActivity.this,
                MyApplication.getWareData().getRooms()));

        dia_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                position_room = position;
                upData();
            }
        });
    }


    private void initEvent() {
        MyApplication.mApplication.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int datType, int subtype1, int subtype2) {
                if (datType == 4||(datType == 3 && subtype2 == 7)) {
                    MyApplication.mApplication.dismissLoadDialog();
                    //更新界面
                    upData();
                }
            }
        });
    }

    private void upData() {
        if (MyApplication.getWareData().getFreshAirs().size() == 0) {
            ToastUtil.showText("没有新风，请添加");
            return;
        }
        //房间
        if (MyApplication.getWareData().getRooms().size() == 0)
            return;
        //所有灯
        AllFreshair = new ArrayList<>();
        for (int i = 0; i < MyApplication.getWareData().getFreshAirs().size(); i++) {
            AllFreshair.add(MyApplication.getWareData().getFreshAirs().get(i));
        }
        wareFreshAirs = new ArrayList<>();
        //房间集合
        room_list = MyApplication.getWareData().getRooms();
        //房间名称；
        if (position_room != -1)
            title_bar_tv_room.setText(room_list.get(position_room));
        else
            title_bar_tv_room.setText(room_list.get(getIntent().getIntExtra("viewPage_num", 0)));
        //根据房间id获取设备；
        for (int i = 0; i < AllFreshair.size(); i++) {
            if (AllFreshair.get(i).getDev().getRoomName().equals(title_bar_tv_room.getText())) {
                wareFreshAirs.add(AllFreshair.get(i));
            }
        }
        //房间里的灯
        if (AllFreshair.size() == 0) {
            freshAirAdapter = new FreshAirAdapter(new ArrayList<WareFreshAir>(), this);
            gridView.setAdapter(freshAirAdapter);
            ToastUtil.showText(title_bar_tv_room.getText() + "没有找到新风，请添加");
        } else {
            freshAirAdapter = new FreshAirAdapter(wareFreshAirs, this);
            gridView.setAdapter(freshAirAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == back)
            finish();
        if (IsCanClick) {
            switch (v.getId()) {
                case R.id.title_bar_iv_or:
                    if (room_list != null && room_list.size() > 0)
                        getRoomDialog();
                    break;
            }
        }
    }
}
