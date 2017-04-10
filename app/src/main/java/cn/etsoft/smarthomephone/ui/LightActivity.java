package cn.etsoft.smarthomephone.ui;

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

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.UiUtils.ToastUtil;
import cn.etsoft.smarthomephone.adapter.LightAdapter;
import cn.etsoft.smarthomephone.adapter.Room_Select_Adapter;
import cn.etsoft.smarthomephone.pullmi.app.GlobalVars;
import cn.etsoft.smarthomephone.pullmi.entity.UdpProPkt;
import cn.etsoft.smarthomephone.pullmi.entity.WareLight;
import cn.etsoft.smarthomephone.weidget.CustomDialog;

/**
 * Created by Say GoBay on 2016/9/1.
 */
public class LightActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private GridView gridView;
    private LinearLayout ll;
    private ImageView back, title_bar_iv_or;
    private TextView title, title_bar_tv_room;

    private List<WareLight> wareLight;
    private boolean IsCanClick = false;
    private LightAdapter lightAdapter;
    private int position_room = -1;
    private List<WareLight> AllLight;
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
        title.setText(getIntent().getStringExtra("title") + "控制");
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
        gridView.setOnItemClickListener(this);

        if (MyApplication.getWareData().getLights() != null && MyApplication.getWareData().getLights().size() > 0) {
            upData();
            IsCanClick = true;
        } else {
            Toast.makeText(this, "没有找到可控制灯具", Toast.LENGTH_SHORT).show();
        }
    }

    long TimeExit = 0;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (IsCanClick) {
            if (System.currentTimeMillis() - TimeExit < 1000) {
                TimeExit = System.currentTimeMillis();
                return;
            }
            MyApplication.mInstance.getSp().play(MyApplication.mInstance.getMusic(), 1, 1, 0, 0, 1);
            if (wareLight.get(position).getbTuneEn() == 0) {
                String ctlStr;
                if (wareLight.get(position).getbOnOff() == 0) {

                    ctlStr = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
                            ",\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue() +
                            ",\"subType1\":0" +
                            ",\"subType2\":0" +
                            ",\"canCpuID\":\"" + wareLight.get(position).getDev().getCanCpuId() +
                            "\",\"devType\":" + wareLight.get(position).getDev().getType() +
                            ",\"devID\":" + wareLight.get(position).getDev().getDevId() +
                            ",\"cmd\":0" +
                            "" +
                            "}";
                    MyApplication.sendMsg(ctlStr);
                } else {

                    ctlStr = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
                            ",\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue() +
                            ",\"subType1\":0" +
                            ",\"subType2\":0" +
                            ",\"canCpuID\":\"" + wareLight.get(position).getDev().getCanCpuId() +
                            "\",\"devType\":" + wareLight.get(position).getDev().getType() +
                            ",\"devID\":" + wareLight.get(position).getDev().getDevId() +
                            ",\"cmd\":1" +
                            "}";

                    MyApplication.sendMsg(ctlStr);

                }

                System.out.println(ctlStr);
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
        dia_listview = (ListView) dialog.findViewById(R.id.air_select);
        dia_listview.setAdapter(new Room_Select_Adapter(LightActivity.this, room_list));

        dia_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                position_room = position;
                upData();
            }
        });
    }


    private void initEvent() {
        MyApplication.mInstance.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int what) {
                if (what == 4 || what == 35)
                    //更新界面
                    upData();
            }
        });
    }

    private void upData() {
        if (MyApplication.getWareData().getLights().size() == 0) {
            ToastUtil.showToast(LightActivity.this, "没有灯具，请添加");
            return;
        }
        if (MyApplication.getRoom_list().size() == 0)
            return;
        AllLight = new ArrayList<>();
        for (int i = 0; i < MyApplication.getWareData().getLights().size(); i++) {
            AllLight.add(MyApplication.getWareData().getLights().get(i));
        }
        wareLight = new ArrayList<>();
        //房间集合
        room_list = MyApplication.getRoom_list();
        //房间名称；
        if (position_room != -1)
            title_bar_tv_room.setText(MyApplication.getRoom_list().get(position_room));
        else
            title_bar_tv_room.setText(MyApplication.getRoom_list().get(getIntent().getIntExtra("viewpage_num", 0)));
        //根据房间id获取设备；
        for (int i = 0; i < AllLight.size(); i++) {
            if (AllLight.get(i).getDev().getRoomName().equals(title_bar_tv_room.getText())) {
                wareLight.add(AllLight.get(i));
            }
        }

        if (wareLight.size() == 0) {
            lightAdapter = new LightAdapter(new ArrayList<WareLight>(), this);
            gridView.setAdapter(lightAdapter);
            ToastUtil.showToast(LightActivity.this, title_bar_tv_room.getText() + "没有找到灯具，请添加");
        } else {
            lightAdapter = new LightAdapter(wareLight, this);
            gridView.setAdapter(lightAdapter);
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
//                case R.id.light_open_all:
//                    String open_str = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
//                            ",\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrl_allDevs.getValue() +
//                            ",\"subType1\":0" +
//                            ",\"subType2\":0" +
//                            ",\"canCpuID\":0\"" +
//                            "\",\"devType\":" + UdpProPkt.E_WARE_TYPE.e_ware_light +
//                            ",\"devID\":0" +
//                            ",\"cmd\": 1" +
//                            "}";
//                    MyApplication.sendMsg(open_str);
//                    break;
//                case R.id.light_close_all:
//                    String close_str = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
//                            ",\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrl_allDevs.getValue() +
//                            ",\"subType1\":0" +
//                            ",\"subType2\":0" +
//                            ",\"canCpuID\":0\"" +
//                            "\",\"devType\":" + UdpProPkt.E_WARE_TYPE.e_ware_light +
//                            ",\"devID\":0" +
//                            ",\"cmd\": 0" +
//                            "}";
//                    MyApplication.sendMsg(close_str);
//                    break;
            }
        }
    }
}
