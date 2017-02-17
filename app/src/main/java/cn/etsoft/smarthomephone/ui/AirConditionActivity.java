package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.UiUtils.ToastUtil;
import cn.etsoft.smarthomephone.adapter.GridViewAdapter;
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
    private TextView title, name, temp, state, temp1, wind;


    private WareAirCondDev wareAirCondDev;
    private static final int MSG_REFRSH_INFO = 1000;
    private int modelValue = 0, curValue = 0, cmdValue = 0;
    private boolean IsCanClick = false;
    private List<WareAirCondDev> list;
    private int positionId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aircondition);

        initView();

        //初始化GridView
        initGridView();

        if (MyApplication.getWareData() != null) {
            if (MyApplication.getWareData().getAirConds() != null
                    && MyApplication.getWareData().getAirConds().size() > 1) {
                getDialog();
            }
        }
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
        if (MyApplication.getWareData().getAirConds().size() == 0) {
            return;
        }
        list = MyApplication.getWareData().getAirConds();
        //这里的刷新不合适，刷新后不显示！
        Log.i("AAAA", "空调开关    " + list.get(0).getbOnOff());

        initdata();
    }

    private void setData() {
        if (MyApplication.getWareData().getAirConds().size() == 0) {
            return;
        }
        list = MyApplication.getWareData().getAirConds();
        //这里的刷新不合适，刷新后不显示！
        Log.i("AAAA", "空调开关    " + list.get(0).getbOnOff());

        initdata();
        select.setVisibility(View.VISIBLE);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog();
            }
        });
    }

    /**
     * 初始化自定义dialog
     */
    CustomDialog dialog;

    private ListView dia_listview;

    public void getDialog() {
        dialog = new CustomDialog(this, R.style.customDialog_null, R.layout.air_select_item);
        dialog.show();
        dia_listview = (ListView) dialog.findViewById(R.id.air_select);
        dia_listview.setAdapter(new Air_Select_Adapter());

        dia_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                positionId = position;
                initdata();
                dialog.dismiss();
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
        select = (ImageView) findViewById(R.id.title_bar_iv_or);
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
        if (MyApplication.getWareData() != null) {
            if (MyApplication.getWareData().getAirConds() != null
                    && MyApplication.getWareData().getAirConds().size() > 0) {
                setData();
                IsCanClick = true;
            }
        } else {
            ToastUtil.showToast(this, "没有找到可控空调");
        }

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
            if (list.size() == 0 || list == null) {
                ToastUtil.showToast(this, "没有数据");
                return;
            }
            wareAirCondDev = list.get(positionId);
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

        title.setText(getIntent().getStringExtra("title") + "控制");
        title.setTextColor(0xffffffff);


        wareAirCondDev = list.get(positionId);
        curValue = list.get(positionId).getSelTemp();
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

    class Air_Select_Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position).getDev().getDevName();
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

                convertView.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) convertView.getTag();

            viewHolder.title.setText(list.get(position).getDev().getDevName());
            viewHolder.image.setImageResource(image[4]);

            return convertView;
        }

        public class ViewHolder {
            public TextView title;
            public ImageView image;
        }
    }
}


