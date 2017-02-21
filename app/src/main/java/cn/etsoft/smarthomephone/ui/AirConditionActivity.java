package cn.etsoft.smarthomephone.ui;

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

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.UiUtils.ToastUtil;
import cn.etsoft.smarthomephone.adapter.GridViewAdapter;
import cn.etsoft.smarthomephone.adapter.Room_Select_Adapter;
import cn.etsoft.smarthomephone.pullmi.app.GlobalVars;
import cn.etsoft.smarthomephone.pullmi.entity.UdpProPkt;
import cn.etsoft.smarthomephone.pullmi.entity.WareAirCondDev;
import cn.etsoft.smarthomephone.weidget.CustomDialog;

/**
 * Created by Say GoBay on 2016/9/1.
 * 空调页面
 */
public class AirConditionActivity extends Activity implements AdapterView.OnItemClickListener {
    private GridView gridView;
    private int[] image = {R.drawable.airconditiononoff, R.drawable.airconditiontoohot, R.drawable.airconditionsocold, R.drawable.airconditionsf, R.drawable.airconditionrefrigeration, R.drawable.airconditionhot, R.drawable.airconditionfsg, R.drawable.airconditiofsz, R.drawable.airconditionfsd};
    private String[] text = {"开/关", "升温", "降温", "扫风", "制冷", "制热", "风速高", "风速中", "风速低"};
    private ImageView back, select;
    private TextView title, name, temp, state, temp1, wind, title_bar_tv_room;

    private WareAirCondDev wareAirCondDev;
    private static final int MSG_REFRSH_INFO = 1000;
    private int modelValue = 0, curValue = 0, cmdValue = 0;
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
    private int air_select_position = 0;
    private boolean IsSelectAir = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aircondition);

        initView();
        //初始化GridView
        initGridView();
    }

    private void initEvent() {

        MyApplication.mInstance.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int what) {
                if (what == 4)
                    //初始化控件
                    upData();
            }
        });
    }


    private void upData() {
        if (MyApplication.getRoom_list().size() == 0)
            return;
        if (MyApplication.getWareData().getAirConds().size() == 0) {
            ToastUtil.showToast(AirConditionActivity.this, "请添加空调");
            return;
        }
        AllAic = new ArrayList<>();
        for (int i = 0; i < MyApplication.getWareData().getAirConds().size(); i++) {
            AllAic.add(MyApplication.getWareData().getAirConds().get(i));
        }
        AirConds = new ArrayList<>();
        //房间集合
        room_list = MyApplication.getRoom_list();
        //房间名称；
        if (position_room != -1)
            title_bar_tv_room.setText(MyApplication.getRoom_list().get(position_room));
        else
            title_bar_tv_room.setText(MyApplication.getRoom_list().get(getIntent().getIntExtra("viewpage_num", 0)));
        //根据房间id获取设备；
        for (int i = 0; i < AllAic.size(); i++) {
            if (AllAic.get(i).getDev().getRoomName().equals(title_bar_tv_room.getText())) {
                AirConds.add(AllAic.get(i));
            }
        }
        if (AirConds.size() == 0) {//如果这个房间没有空调，则不显示设备；
            IsCanClick = false;
            name.setText("");
            temp.setText("");
            temp1.setText("");
            state.setText("");
            wind.setText("");
            ToastUtil.showToast(AirConditionActivity.this, title_bar_tv_room.getText() + "没有空调，请添加");
            return;
        } else if (AirConds.size() == 1) {//空调是一个，刚刚好；
            wareAirCondDev = AirConds.get(0);
            IsCanClick = true;
            initdata();
        } else if (AirConds.size() > 1) {//如果一个房间多个空调，继续选择。
            if (IsSelectAir) { //选择过后，控制空调，防止数据刷新时 继续弹出选择空调；
                wareAirCondDev = AirConds.get(air_select_position);
                IsCanClick = true;
                initdata();
            } else {
                IsCanClick = true;
                getSelectDialog(AirConds);
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
        lp.y = 40; // 新位置Y坐标
        lp.width = 300; // 宽度
        lp.height = 300; // 高度
        // dialog.onWindowAttributesChanged(lp);
        //(当Window的Attributes改变时系统会调用此函数)
        window.setAttributes(lp);
        dialog.show();
        TextView textView = (TextView) dialog.findViewById(R.id.select_room);
        textView.setText("请选择房间");
        textView.setTextColor(Color.BLACK);
        dia_listview = (ListView) dialog.findViewById(R.id.air_select);
        dia_listview.setAdapter(new Room_Select_Adapter(AirConditionActivity.this, room_list));

        dia_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                position_room = position;
                IsSelectAir = false;
                wareAirCondDev = null;
                upData();
            }
        });
    }

    /**
     * 初始化自定义dialog
     */

    public void getSelectDialog(final List<WareAirCondDev> AirConds) {
        ListView dia_listview;
        dialog = new CustomDialog(this, R.style.customDialog_null, R.layout.air_select_item);
        dialog.show();
        TextView view = (TextView) dialog.findViewById(R.id.select_room);
        view.setText("请选择空调");
        view.setTextColor(Color.BLACK);
        dia_listview = (ListView) dialog.findViewById(R.id.air_select);
        dia_listview.setAdapter(new Aic_Select_Adapter(AirConds));

        dia_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                air_select_position = position;
                IsSelectAir = true;
                wareAirCondDev = AirConds.get(position);
                initdata();
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initView() {

        name = (TextView) findViewById(R.id.airCondition_name);
        temp = (TextView) findViewById(R.id.airCondition_temp);
        state = (TextView) findViewById(R.id.airCondition_state);
        temp1 = (TextView) findViewById(R.id.airCondition_temp1);
        wind = (TextView) findViewById(R.id.airCondition_wind);

        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        title = (TextView) findViewById(R.id.title_bar_tv_title);
        title_bar_tv_room = (TextView) findViewById(R.id.title_bar_tv_room);
        title_bar_tv_room.setVisibility(View.VISIBLE);
        select = (ImageView) findViewById(R.id.title_bar_iv_or);
        select.setVisibility(View.VISIBLE);
        select.setImageResource(R.drawable.fj1);
        name = (TextView) findViewById(R.id.airCondition_name);
        temp = (TextView) findViewById(R.id.airCondition_temp);
        temp1 = (TextView) findViewById(R.id.airCondition_temp1);
        state = (TextView) findViewById(R.id.airCondition_state);
        wind = (TextView) findViewById(R.id.airCondition_wind);

        back.setImageResource(R.drawable.return2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (room_list.size() == 0)
                    return;
                getRoomDialog();
            }
        });
        if (MyApplication.getWareData() != null) {
            if (MyApplication.getWareData().getAirConds() != null
                    && MyApplication.getWareData().getAirConds().size() > 0) {
                upData();
                IsCanClick = true;
            }
        } else {
            ToastUtil.showToast(this, "没有找到可控空调");
        }
        title.setText(getIntent().getStringExtra("title") + "控制");
        title.setTextColor(0xffffffff);
        initEvent();
    }

    /**
     * 初始化GridView
     */
    private void initGridView() {
        gridView = (GridView) findViewById(R.id.airCondition_gv);
        gridView.setAdapter(new GridViewAdapter(image, text, this));
        gridView.setSelector(R.drawable.selector_gridview_item);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (IsCanClick) {
            String str_Fixed = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
                    ",\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue() +
                    ",\"subType1\":0" +
                    ",\"subType2\":0" +
                    ",\"canCpuID\":\"" + wareAirCondDev.getDev().getCanCpuId() +
                    "\",\"devType\":" + wareAirCondDev.getDev().getType() +
                    ",\"devID\":" + wareAirCondDev.getDev().getDevId();
            switch (position) {
                //开/关
                case 0:
                    if (wareAirCondDev.getbOnOff() == 0) {
                        cmdValue = UdpProPkt.E_AIR_CMD.e_air_pwrOn.getValue();//打开空调
                    } else {
                        cmdValue = UdpProPkt.E_AIR_CMD.e_air_pwrOff.getValue();//关闭空调
                    }
                    break;
                //升温
                case 1:
                    //设置升温
                    if (wareAirCondDev.getbOnOff() == UdpProPkt.E_AIR_CMD.e_air_pwrOn.getValue()) {
                        ToastUtil.showToast(this, "请先开机，再操作");
                        return;
                    }
                    curValue++;
                    if (curValue > 30) {
                        curValue = 30;
                    } else {
                        temp1.setText("设置温度 :" + curValue + "℃");
                        switch (curValue) {
                            case 14:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp14.getValue();
                                break;
                            case 15:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp15.getValue();
                                break;
                            case 16:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp16.getValue();
                                break;
                            case 17:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp17.getValue();
                                break;
                            case 18:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp18.getValue();
                                break;
                            case 19:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp19.getValue();
                                break;
                            case 20:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp20.getValue();
                                break;
                            case 21:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp21.getValue();
                                break;
                            case 22:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp22.getValue();
                                break;
                            case 23:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp23.getValue();
                                break;
                            case 24:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp24.getValue();
                                break;
                            case 25:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp25.getValue();
                                break;
                            case 26:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp26.getValue();
                                break;
                            case 27:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp27.getValue();
                                break;
                            case 28:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp28.getValue();
                                break;
                            case 29:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp29.getValue();
                                break;
                            case 30:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp30.getValue();
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                //降温
                case 2:
                    //设置降温
                    if (wareAirCondDev.getbOnOff() == UdpProPkt.E_AIR_CMD.e_air_pwrOn.getValue()) {
                        Toast.makeText(this, "请先开机，再操作", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    curValue--;
                    if (curValue < 14) {
                        curValue = 14;
                    } else {
                        temp1.setText("设置温度 :" + curValue + "℃");
                        switch (curValue) {
                            case 14:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp14.getValue();
                                break;
                            case 15:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp15.getValue();
                                break;
                            case 16:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp16.getValue();
                                break;
                            case 17:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp17.getValue();
                                break;
                            case 18:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp18.getValue();
                                break;
                            case 19:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp19.getValue();
                                break;
                            case 20:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp20.getValue();
                                break;
                            case 21:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp21.getValue();
                                break;
                            case 22:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp22.getValue();
                                break;
                            case 23:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp23.getValue();
                                break;
                            case 24:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp24.getValue();
                                break;
                            case 25:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp25.getValue();
                                break;
                            case 26:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp26.getValue();
                                break;
                            case 27:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp27.getValue();
                                break;
                            case 28:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp28.getValue();
                                break;
                            case 29:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp29.getValue();
                                break;
                            case 30:
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_temp30.getValue();
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                //扫风
                case 3:
                    cmdValue = UdpProPkt.E_AIR_CMD.e_air_drctLfRt1.getValue();
                    break;
                //制冷
                case 4:
                    if (wareAirCondDev.getbOnOff() == UdpProPkt.E_AIR_CMD.e_air_pwrOn.getValue()) {
                        Toast.makeText(this, "请先开机，再操作", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    modelValue = UdpProPkt.E_AIR_MODE.e_air_cool.getValue();
                    break;
                //制热
                case 5:
                    if (wareAirCondDev.getbOnOff() == UdpProPkt.E_AIR_CMD.e_air_pwrOn.getValue()) {
                        Toast.makeText(this, "请先开机，再操作", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    modelValue = UdpProPkt.E_AIR_MODE.e_air_hot.getValue();
                    break;
                //风速高
                case 6:
                    if (wareAirCondDev.getbOnOff() == UdpProPkt.E_AIR_CMD.e_air_pwrOn.getValue()) {
                        Toast.makeText(this, "请先开机，再操作", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    cmdValue = UdpProPkt.E_AIR_CMD.e_air_spdHigh.getValue();
                    break;
                //风速中
                case 7:
                    if (wareAirCondDev.getbOnOff() == UdpProPkt.E_AIR_CMD.e_air_pwrOn.getValue()) {
                        Toast.makeText(this, "请先开机，再操作", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    cmdValue = UdpProPkt.E_AIR_CMD.e_air_spdMid.getValue();
                    break;
                //风速低
                case 8:
                    if (wareAirCondDev.getbOnOff() == UdpProPkt.E_AIR_CMD.e_air_pwrOn.getValue()) {
                        Toast.makeText(this, "请先开机，再操作", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    cmdValue = UdpProPkt.E_AIR_CMD.e_air_spdLow.getValue();
                    break;
            }
            int value = (modelValue << 5) | cmdValue;

            str_Fixed = str_Fixed +
                    ",\"cmd\":" + value + "}";
            Log.i("CMD_空调", str_Fixed);
            MyApplication.sendMsg(str_Fixed);
        }
    }


    public void initdata() {
        if (wareAirCondDev == null) {
            return;
        }

        curValue = wareAirCondDev.getSelTemp();
        name.setText("空调名称 :" + wareAirCondDev.getDev().getDevName());
        temp.setText("当前温度 :" + wareAirCondDev.getSelTemp() + "℃");
        temp1.setText("设置温度 :" + wareAirCondDev.getSelTemp() + "℃");

        if (wareAirCondDev.getbOnOff() == 0) {
            state.setText("空调状态 : 关闭");
        } else {
            state.setText("空调状态 : 打开");
        }
        Log.i("WelCome", wareAirCondDev.getSelSpd() + "");
        if (wareAirCondDev.getSelSpd()
                == UdpProPkt.E_AIR_CMD.e_air_spdLow.getValue()) {
            wind.setText("风速 : 低风");
        } else if (wareAirCondDev.getSelSpd()
                == UdpProPkt.E_AIR_CMD.e_air_spdMid.getValue()) {
            wind.setText("风速 : 中风");
        } else if (wareAirCondDev.getSelSpd()
                == UdpProPkt.E_AIR_CMD.e_air_spdHigh.getValue()) {
            wind.setText("风速 : 高风");
        }
    }


    class Aic_Select_Adapter extends BaseAdapter {
        private List<WareAirCondDev> AirConds;

        Aic_Select_Adapter(List<WareAirCondDev> AirConds) {
            this.AirConds = AirConds;
        }

        @Override
        public int getCount() {
            return AirConds.size();
        }

        @Override
        public Object getItem(int position) {
            return AirConds.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(AirConditionActivity.this).
                        inflate(R.layout.equipment_listview_control_item, null);

                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.equipment_tv);
                viewHolder.image = (ImageView) convertView.findViewById(R.id.equipment_iv);
                viewHolder.title.setTextColor(Color.BLACK);

                convertView.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.title.setText(AirConds.get(position).getDev().getDevName());
            return convertView;
        }

        public class ViewHolder {
            public TextView title;
            public ImageView image;
        }
    }

}


