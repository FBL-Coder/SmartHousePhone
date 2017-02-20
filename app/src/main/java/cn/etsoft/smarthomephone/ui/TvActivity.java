package cn.etsoft.smarthomephone.ui;

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

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.UiUtils.ToastUtil;
import cn.etsoft.smarthomephone.adapter.Room_Select_Adapter;
import cn.etsoft.smarthomephone.adapter.StbAdapter;
import cn.etsoft.smarthomephone.pullmi.app.GlobalVars;
import cn.etsoft.smarthomephone.pullmi.entity.UdpProPkt;
import cn.etsoft.smarthomephone.pullmi.entity.WareTv;
import cn.etsoft.smarthomephone.weidget.CustomDialog;

/**
 * Created by Say GoBay on 2016/9/1.
 * 电视页面
 */
public class TvActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private GridView gridView;
    private int[] image = {R.drawable.television4, R.drawable.television5, R.drawable.television6, R.drawable.television7, R.drawable.television8, R.drawable.television9, R.drawable.television10, R.drawable.television11, R.drawable.television12, R.drawable.television13, R.drawable.television14, R.drawable.television15};
    private ImageView back, choose, add, subtract, up, down, title_bar_iv_or;
    private TextView title, title_bar_tv_room;
    private boolean IsCanClick = false;
    private List<WareTv> AllTv;
    private List<WareTv> Tvs;
    private WareTv tv;
    private int position_room = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv);
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
        title_bar_iv_or.setImageResource(R.drawable.qing);
        title_bar_iv_or.setVisibility(View.VISIBLE);
        title_bar_iv_or.setOnClickListener(this);
        title.setText(getIntent().getStringExtra("title") + "控制");
        back.setOnClickListener(this);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        upData();
        choose = (ImageView) findViewById(R.id.tv_choose);
        add = (ImageView) findViewById(R.id.tv_add);
        subtract = (ImageView) findViewById(R.id.tv_subtract);
        up = (ImageView) findViewById(R.id.tv_up);
        down = (ImageView) findViewById(R.id.tv_down);
        choose.setOnClickListener(this);
        add.setOnClickListener(this);
        subtract.setOnClickListener(this);
        up.setOnClickListener(this);
        down.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.title_bar_iv_back)//返回
            finish();
        if (v == title_bar_iv_or)
            getRoomDialog();
        if (IsCanClick && tv != null) {
            String str_Fixed = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
                    ",\"datType\":4" +
                    ",\"subType1\":0" +
                    ",\"subType2\":0" +
                    ",\"canCpuID\":" + tv.getDev().getCanCpuId() +
                    ".\"devType\":" + tv.getDev().getType() +
                    ".\"devID\":" + tv.getDev().getDevId();
            int Value = -1;
            switch (v.getId()) {
                //开关
                case R.id.tv_choose:
                    Value = UdpProPkt.E_TV_CMD.e_tv_offOn.getValue();
                    break;
                //加
                case R.id.tv_add:
                    Value = UdpProPkt.E_TV_CMD.e_tv_numRt.getValue();
                    break;
                //减
                case R.id.tv_subtract:
                    Value = UdpProPkt.E_TV_CMD.e_tv_numLf.getValue();
                    break;
                //上
                case R.id.tv_up:
                    Value = UdpProPkt.E_TV_CMD.e_tv_numLf.getValue();
                    break;
                //下
                case R.id.tv_down:
                    Value = UdpProPkt.E_TV_CMD.e_tv_numDn.getValue();
                    break;
            }
            if (Value != -1) {
                str_Fixed = str_Fixed +
                        ".\"cmd:" + Value + "}";
                MyApplication.sendMsg(str_Fixed);
            }
        }
    }


    /**
     * 初始化自定义dialog
     */
    CustomDialog dialog;

    public void getRoomDialog() {
        ListView dia_listview;
        dialog = new CustomDialog(this, R.style.customDialog_null, R.layout.air_select_item);
        TextView textView = (TextView) dialog.getView().findViewById(R.id.select_room);
        textView.setText("请选择房间");
        //获得当前窗体
        Window window = dialog.getWindow();
        //重新设置
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.RIGHT | Gravity.TOP);
        lp.x = 0; // 新位置X坐标
        lp.y = 40; // 新位置Y坐标
        lp.width = 300; // 宽度
        lp.height = 300; // 高度
        // dialog.onWindowAttributesChanged(lp);
        //(当Window的Attributes改变时系统会调用此函数)
        window.setAttributes(lp);
        dialog.show();
        dia_listview = (ListView) dialog.findViewById(R.id.air_select);
        dia_listview.setAdapter(new Room_Select_Adapter(TvActivity.this, MyApplication.getRoom_list()));

        dia_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                position_room = position;
                upData();
            }
        });

    }


    private void upData() {

        //房间名称；
        if (position_room != -1)
            title_bar_tv_room.setText(MyApplication.getRoom_list().get(position_room));
        else
            title_bar_tv_room.setText(MyApplication.getRoom_list().get(getIntent().getIntExtra("viewpage_num", 0)));
        if (MyApplication.getWareData().getTvs().size() == 0) {
            ToastUtil.showToast(TvActivity.this, "请添加电视");
            return;
        }
        AllTv = new ArrayList<>();
        for (int i = 0; i < MyApplication.getWareData().getTvs().size(); i++) {
            AllTv.add(MyApplication.getWareData().getTvs().get(i));
        }
        Tvs = new ArrayList<>();

        //根据房间id获取设备；
        for (int i = 0; i < AllTv.size(); i++) {
            if (AllTv.get(i).getDev().getRoomName().equals(title_bar_tv_room.getText())) {
                Tvs.add(AllTv.get(i));
            }
        }
        if (Tvs.size() == 0) {//如果这个房间没有电视，则不显示设备；
            IsCanClick = false;
            ToastUtil.showToast(TvActivity.this, title_bar_tv_room.getText() + "没有电视，请添加");
            return;
        } else if (Tvs.size() == 1) {//电视是一个，刚刚好；
            IsCanClick = true;
            tv = Tvs.get(0);
            title_bar_tv_room.setText(title_bar_tv_room.getText() + "  (" + Tvs.get(0).getDev().getDevName() + ")");
        } else if (Tvs.size() > 1) {//如果一个房间多个电视，则继续选择提示。
            IsCanClick = false;
            ToastUtil.showToast(TvActivity.this,"一个房间最多一个电视");
            return;
        }
    }


    /**
     * 初始化GridView
     */
    private void initGridView() {
        gridView = (GridView) findViewById(R.id.tv_gv);
        gridView.setAdapter(new StbAdapter(image, this));
        gridView.setSelector(R.drawable.selector_gridview_item);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (IsCanClick && tv != null) {
            String str_Fixed = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
                    ",\"datType\":4" +
                    ",\"subType1\":0" +
                    ",\"subType2\":0" +
                    ",\"canCpuID\":" + tv.getDev().getCanCpuId() +
                    ".\"devType\":" + tv.getDev().getType() +
                    ".\"devID\":" + tv.getDev().getDevId();
            int Value = -1;
            switch (position) {
                //1
                case 0:
                    Value = UdpProPkt.E_TV_CMD.e_tv_num1.getValue();
                    break;
                //2
                case 1:
                    Value = UdpProPkt.E_TV_CMD.e_tv_num2.getValue();
                    break;
                //3
                case 2:
                    Value = UdpProPkt.E_TV_CMD.e_tv_num3.getValue();
                    break;
                //4
                case 3:
                    Value = UdpProPkt.E_TV_CMD.e_tv_num4.getValue();
                    break;
                //5
                case 4:
                    Value = UdpProPkt.E_TV_CMD.e_tv_num5.getValue();
                    break;
                //6
                case 5:
                    Value = UdpProPkt.E_TV_CMD.e_tv_num6.getValue();
                    break;
                //7
                case 6:
                    Value = UdpProPkt.E_TV_CMD.e_tv_num7.getValue();
                    break;
                //8
                case 7:
                    Value = UdpProPkt.E_TV_CMD.e_tv_num8.getValue();
                    break;
                //9
                case 8:
                    Value = UdpProPkt.E_TV_CMD.e_tv_num9.getValue();
                    break;
                //+100
                case 9:
                    break;
                //0
                case 10:
                    Value = UdpProPkt.E_TV_CMD.e_tv_num0.getValue();
                    break;
                //last
                case 11:
                    break;
            }
            if (Value != -1) {
                str_Fixed = str_Fixed +
                        ".\"cmd:" + Value + "}";
                MyApplication.sendMsg(str_Fixed);
            }
        }
    }
}
