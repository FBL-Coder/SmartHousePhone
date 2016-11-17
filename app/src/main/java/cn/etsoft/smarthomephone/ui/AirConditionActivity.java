package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.UiUtils.ToastUtil;
import cn.etsoft.smarthomephone.adapter.GridViewAdapter;
import cn.etsoft.smarthomephone.pullmi.common.CommonUtils;
import cn.etsoft.smarthomephone.pullmi.entity.UdpProPkt;
import cn.etsoft.smarthomephone.pullmi.entity.WareAirCondDev;

/**
 * Created by Say GoBay on 2016/9/1.
 */
public class AirConditionActivity extends Activity implements AdapterView.OnItemClickListener {
    private GridView gridView;
    private int[] image = {R.drawable.airconditiononoff, R.drawable.airconditiontoohot, R.drawable.airconditionsocold, R.drawable.airconditionsf, R.drawable.airconditionrefrigeration, R.drawable.airconditionhot, R.drawable.airconditionfsg, R.drawable.airconditiofsz, R.drawable.airconditionfsd};
    private String[] text = {"开/关", "升温", "降温", "扫风", "制冷", "制热", "风速高", "风速中", "风速低"};
    private ImageView back;
    private TextView title, name, temp, state, temp1, wind;


    private WareAirCondDev wareAirCondDev;
    private static final int MSG_REFRSH_INFO = 1000;
    private int modelValue = 0, curValue = 0, cmdValue = 0;
    private boolean IsCanClick = false;
    private List<WareAirCondDev> list;
    private ViewPager pager;
    private View pageView;
    private int positionId;

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
                //初始化控件
                initView();
            }
        });
    }
    MyViewPagerAdapter adapter;

    private void upData() {
        if (MyApplication.getWareData().getAirConds().size() == 0) {
            return;
        }

        list = MyApplication.getWareData().getAirConds();

        //这里的刷新不合适，刷新后不显示！
        Log.i("AAAA", "空调开关    " + list.get(0).getbOnOff());

//        if (adapter != null)
//            adapter.notifyDataSetChanged();
//        else {
            adapter = new MyViewPagerAdapter(list);
            pager.setAdapter(adapter);
//        }
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
        pager = (ViewPager) findViewById(R.id.page_view);

        pageView = LayoutInflater.from(this).inflate(R.layout.page_view, null);
        back = (ImageView) pageView.findViewById(R.id.title_bar_iv_back);
        title = (TextView) pageView.findViewById(R.id.title_bar_tv_title);
        title.setText(getIntent().getStringExtra("title") + "控制");
        title.setTextColor(0xffffffff);
        back.setImageResource(R.drawable.return2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (MyApplication.getWareData() != null) {
            if (MyApplication.getWareData().getAirConds() != null && MyApplication.getWareData().getAirConds().size() > 0) {
                upData();
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
            String str_Fixed = "{\"devUnitID\":\"37ffdb05424e323416702443\"" +
                    ",\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue() +
                    ",\"subType1\":0" +
                    ",\"subType2\":0" +
                    ",\"canCpuID\":\"" + MyApplication.getWareData().getAirConds().get(positionId).getDev().getCanCpuId() +
                    "\",\"devType\":" + MyApplication.getWareData().getAirConds().get(positionId).getDev().getType() +
                    ",\"devID\":" + MyApplication.getWareData().getAirConds().get(positionId).getDev().getDevId();
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
                        break;
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
                    //降温
                case 2:
                    //设置降温
                    if (wareAirCondDev.getbOnOff() == UdpProPkt.E_AIR_CMD.e_air_pwrOn.getValue()) {
                        Toast.makeText(this, "请先开机，再操作", Toast.LENGTH_SHORT).show();
                        break;
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
                        break;
                    }
                    modelValue = UdpProPkt.E_AIR_MODE.e_air_cool.getValue();
                    break;
                //制热
                case 5:
                    if (wareAirCondDev.getbOnOff() == UdpProPkt.E_AIR_CMD.e_air_pwrOn.getValue()) {
                        Toast.makeText(this, "请先开机，再操作", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    modelValue = UdpProPkt.E_AIR_MODE.e_air_hot.getValue();
                    break;
                //风速高
                case 6:
                    if (wareAirCondDev.getbOnOff() == UdpProPkt.E_AIR_CMD.e_air_pwrOn.getValue()) {
                        Toast.makeText(this, "请先开机，再操作", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    cmdValue = UdpProPkt.E_AIR_CMD.e_air_spdHigh.getValue();
                    break;
                //风速中
                case 7:
                    if (wareAirCondDev.getbOnOff() == UdpProPkt.E_AIR_CMD.e_air_pwrOn.getValue()) {
                        Toast.makeText(this, "请先开机，再操作", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    cmdValue = UdpProPkt.E_AIR_CMD.e_air_spdMid.getValue();
                    break;
                //风速低
                case 8:
                    if (wareAirCondDev.getbOnOff() == UdpProPkt.E_AIR_CMD.e_air_pwrOn.getValue()) {
                        Toast.makeText(this, "请先开机，再操作", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    cmdValue = UdpProPkt.E_AIR_CMD.e_air_spdLow.getValue();
                    break;
            }
            int value = (modelValue << 5) | cmdValue;

            str_Fixed = str_Fixed +
                    ",\"cmd\":" + value + "}";
            CommonUtils.sendMsg(str_Fixed);
        }
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private List<WareAirCondDev> listdata;
        private int mChildCount = 0;

        public MyViewPagerAdapter(List<WareAirCondDev> listdata) {
            this.listdata = listdata;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(pageView);
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {  //这个方法用来实例化页卡
            positionId = position;
            initPageView(position);
            container.addView(pageView, 0);
            return pageView;
        }

        @Override
        public void notifyDataSetChanged() {
            mChildCount = getCount();
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return listdata.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            if (mChildCount > 0) {
                mChildCount--;
                return POSITION_NONE;
            }
            return super.getItemPosition(object);
        }

        public void initPageView(int position) {
            name = (TextView) pageView.findViewById(R.id.airCondition_name);
            temp = (TextView) pageView.findViewById(R.id.airCondition_temp);
            temp1 = (TextView) pageView.findViewById(R.id.airCondition_temp1);
            state = (TextView) pageView.findViewById(R.id.airCondition_state);
            wind = (TextView) pageView.findViewById(R.id.airCondition_wind);




            wareAirCondDev = listdata.get(position);
            curValue = listdata.get(position).getSelTemp();
            name.setText("空调名称 :" + wareAirCondDev.getDev().getDevName());
            temp.setText("当前温度 :" + 10 + "℃");
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
    }
}

