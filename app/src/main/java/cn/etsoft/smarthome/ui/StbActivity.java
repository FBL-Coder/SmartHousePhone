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
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.Room_Select_Adapter;
import cn.etsoft.smarthome.adapter.StbAdapter;
import cn.etsoft.smarthome.domain.UdpProPkt;
import cn.etsoft.smarthome.domain.WareSetBox;
import cn.etsoft.smarthome.utils.SendDataUtil;
import cn.etsoft.smarthome.utils.ToastUtil;
import cn.etsoft.smarthome.weidget.CustomDialog;

/**
 * Created by Say GoBay on 2016/9/1.
 */
public class StbActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private GridView gridView;
    private int[] image = {R.drawable.television4, R.drawable.television5, R.drawable.television6,
            R.drawable.television7, R.drawable.television8, R.drawable.television9, R.drawable.television10,
            R.drawable.television11, R.drawable.television12, R.drawable.television13,
            R.drawable.television14, R.drawable.television15};
    private ImageView back, choose, choose1, add, subtract, up, down, title_bar_iv_or;
    private TextView title, title_bar_tv_room, name_stb;
    private List<WareSetBox> wareSetBoxs;
    private List<WareSetBox> AllWareSetBox;
    private WareSetBox wareSetBox;
    private boolean IsCanClick = false;
    private int position_room = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stb);
        //初始化标题栏
        initTitleBar();
        //初始化控件
        initView();
        //初始化GridView
        initGridView();
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        title = (TextView) findViewById(R.id.title_bar_tv_title);
        title_bar_tv_room = (TextView) findViewById(R.id.title_bar_tv_room);
        title_bar_tv_room.setVisibility(View.VISIBLE);
        title_bar_tv_room.setTextColor(Color.BLACK);
        title_bar_iv_or = (ImageView) findViewById(R.id.title_bar_iv_or);
        title_bar_iv_or.setImageResource(R.drawable.fj);
        title_bar_iv_or.setVisibility(View.VISIBLE);
        name_stb = (TextView) findViewById(R.id.name_stb);
        title_bar_iv_or.setOnClickListener(this);
        title.setText(getIntent().getStringExtra("title") + "控制");
        back.setOnClickListener(this);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        upData();
        choose = (ImageView) findViewById(R.id.stb_choose);
        choose1 = (ImageView) findViewById(R.id.stb_choose1);
        add = (ImageView) findViewById(R.id.stb_add);
        subtract = (ImageView) findViewById(R.id.stb_subtract);
        up = (ImageView) findViewById(R.id.stb_up);
        down = (ImageView) findViewById(R.id.stb_down);
        choose.setOnClickListener(this);
        choose1.setOnClickListener(this);
        add.setOnClickListener(this);
        subtract.setOnClickListener(this);
        up.setOnClickListener(this);
        down.setOnClickListener(this);
    }

    private void upData() {
        //房间
        if (MyApplication.getWareData().getRooms().size() == 0)
            return;
        //房间名称；
        if (position_room != -1)
            title_bar_tv_room.setText(MyApplication.getWareData().getRooms().get(position_room));
        else
            title_bar_tv_room.setText(MyApplication.getWareData().getRooms().get(getIntent().getIntExtra("viewPage_num", 0)));

        if (MyApplication.getWareData().getTvs().size() == 0) {
            ToastUtil.showText("请添加机顶盒");
            return;
        }
        //所有机顶盒
        AllWareSetBox = new ArrayList<>();
        for (int i = 0; i < MyApplication.getWareData().getStbs().size(); i++) {
            AllWareSetBox.add(MyApplication.getWareData().getStbs().get(i));
        }
        wareSetBoxs = new ArrayList<>();
        //根据房间id获取设备；
        for (int i = 0; i < AllWareSetBox.size(); i++) {
            if (AllWareSetBox.get(i).getDev().getRoomName().equals(title_bar_tv_room.getText())) {
                wareSetBoxs.add(AllWareSetBox.get(i));
            }
        }
        if (wareSetBoxs.size() == 0) {//如果这个房间没有机顶盒，则不显示设备；
            IsCanClick = false;
            name_stb.setText(title_bar_tv_room.getText() + "没有找到机顶盒");
            return;
        } else if (wareSetBoxs.size() == 1) {//机顶盒是一个，刚刚好；
            IsCanClick = true;
            wareSetBox = wareSetBoxs.get(0);
            title_bar_tv_room.setText(title_bar_tv_room.getText());
            name_stb.setText("机顶盒名称 : " + wareSetBoxs.get(0).getDev().getDevName());
        } else if (wareSetBoxs.size() > 1) {//如果一个房间多个电视，则继续选择提示。
            IsCanClick = false;
            ToastUtil.showText("一个房间最多一个机顶盒");
            return;
        }
    }

    long TimeExit = 0;

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.title_bar_iv_back)//返回
            finish();
        if (v == title_bar_iv_or && MyApplication.getWareData().getRooms().size() > 0)
            getRoomDialog();
        if (IsCanClick && wareSetBox != null) {
            //连续点击，间隔小于1秒，不做反应
            if (System.currentTimeMillis() - TimeExit < 1000) {
                TimeExit = System.currentTimeMillis();
                return;
            }
            //给点击按钮添加点击音效
            MyApplication.mApplication.getSp().play(MyApplication.mApplication.getMusic(), 1, 1, 0, 0, 1);
            int Value = -1;
            switch (v.getId()) {
                //开关
                case R.id.stb_choose:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_offOn.getValue();
                    break;
                //开关
                case R.id.stb_choose1:
                    Value = UdpProPkt.E_TV_CMD.e_tv_offOn.getValue();
                    break;
                //加
                case R.id.stb_add:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_numVInc.getValue();
                    break;
                //减
                case R.id.stb_subtract:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_numLf.getValue();
                    break;
                //上
                case R.id.stb_up:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_numUp.getValue();
                    break;
                //下
                case R.id.stb_down:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_numDn.getValue();
                    break;
            }
            if (Value != -1) {
                SendDataUtil.controlDev(wareSetBox.getDev(), Value);
            }
        }
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
        dia_listView.setAdapter(new Room_Select_Adapter(StbActivity.this, MyApplication.getWareData().getRooms()));

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
     * 初始化GridView
     */
    private void initGridView() {
        gridView = (GridView) findViewById(R.id.stb_gv);
        gridView.setAdapter(new StbAdapter(image, this));
        gridView.setSelector(R.drawable.selector_gridview_item);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (IsCanClick && wareSetBox != null) {
            int Value = -1;
            switch (position) {
                case 0:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_num1.getValue();
                    break;
                case 1:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_num2.getValue();
                    break;
                case 2:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_num3.getValue();
                    break;
                case 3:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_num4.getValue();
                    break;
                case 4:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_num5.getValue();
                    break;
                case 5:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_num6.getValue();
                    break;
                case 6:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_num7.getValue();
                    break;
                case 7:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_num8.getValue();
                    break;
                case 8:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_num9.getValue();
                    break;
                case 9:
                    break;
                case 10:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_num0.getValue();
                    break;
                case 11:
                    break;
            }
            if (Value != -1) {
                SendDataUtil.controlDev(wareSetBox.getDev(), Value);
            }
        }
    }
}
