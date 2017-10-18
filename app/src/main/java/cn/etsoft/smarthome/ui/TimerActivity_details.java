package cn.etsoft.smarthome.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.PopupWindowAdapter2;
import cn.etsoft.smarthome.domain.Timer_Data;
import cn.etsoft.smarthome.domain.WareAirCondDev;
import cn.etsoft.smarthome.domain.WareCurtain;
import cn.etsoft.smarthome.domain.WareDev;
import cn.etsoft.smarthome.domain.WareFloorHeat;
import cn.etsoft.smarthome.domain.WareFreshAir;
import cn.etsoft.smarthome.domain.WareLight;
import cn.etsoft.smarthome.domain.WareSetBox;
import cn.etsoft.smarthome.domain.WareTv;
import cn.etsoft.smarthome.pullmi.common.CommonUtils;
import cn.etsoft.smarthome.utils.SendDataUtil;
import cn.etsoft.smarthome.utils.ToastUtil;
import cn.etsoft.smarthome.view.Circle_Progress;
import cn.etsoft.smarthome.weidget.CustomDialog_comment;
import cn.etsoft.smarthome.weidget.MultiChoicePopWindow;

/**
 * Created by Say GoBay on 2017/6/1.
 * 系统设置--定时设置--详情
 */
public class TimerActivity_details extends Activity implements View.OnClickListener {
    private ImageView back;
    private TextView title, save, tv_time_week, tv_enabled, tv_time_start, tv_time_end, tv_week_repeat, tv_all_network, add_dev_timer, add_dev_Layout_close, tv_text_parlour;
    private int[] image = new int[]{R.drawable.kongtiao, R.drawable.tv_0, R.drawable.jidinghe, R.drawable.dengguang, R.drawable.chuanglian};
    private EditText et_name;
    private GridView gridView_timer;
    private ListView add_dev_Layout_lv;
    private LinearLayout add_dev_Layout_ll;
    private List<String> home_text;
    private List<WareDev> dev;
    private List<WareDev> mWareDev;
    private List<Timer_Data.TimerEventRowsBean.RunDevItemBean> common_dev;
    private GridViewAdapter_Timer gridViewAdapter_Timer;
    private TimerActivity_details.EquipmentAdapter equipmentAdapter;
    private PopupWindow popupWindow;
    private MultiChoicePopWindow mMultiChoicePopWindow;
    private TimePicker mTimePicker;
    private TimePickerDialog mTimePickerDialog;
    //添加设备房间position；
    private int home_position;
    //定时位置position
    private int Timer_position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_details);
        //初始化标题栏
        initTitleBar();
        //初始化组件
        initView();
        initGridView(Timer_position);
        initData(Timer_position);
        MyApplication.mApplication.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int datType, int subtype1, int subtype2) {

                if (datType == 17) {
                    MyApplication.mApplication.dismissLoadDialog();
                    initGridView(Timer_position);
                    initData(Timer_position);
                    if (Timer_position != 0) {
                        ToastUtil.showText("保存成功");
                    }
                    initTitleBar();
                }
                if (datType == 19) {
                    SendDataUtil.getTimerInfo();
                    if (Timer_position == 0) {
                        ToastUtil.showText("保存成功");
                    }
                    initTitleBar();
                }
            }
        });
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        title = (TextView) findViewById(R.id.title_bar_tv_title);
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        save = (TextView) findViewById(R.id.title_bar_tv_room);
        Timer_position = getIntent().getExtras().getInt("Timer_position");
        title.setTextColor(0xffffffff);
        title.setText("");
        title.setHint(MyApplication.getWareData().getTimer_data().getTimerEvent_rows().get(Timer_position).getTimerName());
        title.setHintTextColor(0xffffffff);
        back.setImageResource(R.drawable.return2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        save.setVisibility(View.VISIBLE);
        save.setTextColor(0xffffffff);
        save.setText("保存");
        save.setOnClickListener(this);
    }

    /**
     * 初始化组件
     */
    private void initView() {
        tv_enabled = (TextView) findViewById(R.id.enabled);
        tv_time_week = (TextView) findViewById(R.id.time_week);
        tv_time_start = (TextView) findViewById(R.id.time_start);
        tv_time_end = (TextView) findViewById(R.id.time_end);
        tv_week_repeat = (TextView) findViewById(R.id.week_repeat);
        tv_all_network = (TextView) findViewById(R.id.all_network);
        add_dev_timer = (TextView) findViewById(R.id.add_dev_timer);
        et_name = (EditText) findViewById(R.id.name);
        gridView_timer = (GridView) findViewById(R.id.gridView_timer);

        //添加设备布局
        add_dev_Layout_lv = (ListView) findViewById(R.id.equipment_loyout_lv);
        add_dev_Layout_close = (TextView) findViewById(R.id.equipment_close);
        tv_text_parlour = (TextView) findViewById(R.id.tv_equipment_parlour);
        add_dev_Layout_ll = (LinearLayout) findViewById(R.id.add_dev_Layout_ll);
        add_dev_Layout_close.setOnClickListener(this);
        tv_text_parlour.setOnClickListener(this);

        add_dev_timer.setOnClickListener(this);
        tv_enabled.setOnClickListener(this);
        tv_time_week.setOnClickListener(this);
        tv_time_start.setOnClickListener(this);
        tv_time_end.setOnClickListener(this);
        tv_week_repeat.setOnClickListener(this);
        tv_all_network.setOnClickListener(this);
        et_name.setOnClickListener(this);

    }

    int position_delete;

    /**
     * 初始化GridView
     */
    public void initGridView(int Timer_position) {
        if (MyApplication.getWareData().getTimer_data() == null || MyApplication.getWareData().getTimer_data().getTimerEvent_rows().size() == 0)
            return;
        common_dev = new ArrayList<>();
        for (int i = 0; i < MyApplication.getWareData().getTimer_data().getTimerEvent_rows().get(Timer_position).getRun_dev_item().size(); i++) {
            common_dev.add(MyApplication.getWareData().getTimer_data().getTimerEvent_rows().get(Timer_position).getRun_dev_item().get(i));
        }
//        common_dev = MyApplication.getWareData().getTimer_data().getTimerEvent_rows().get(Timer_position).getRun_dev_item();
        gridViewAdapter_Timer = new GridViewAdapter_Timer(common_dev);
        gridView_timer.setAdapter(gridViewAdapter_Timer);

        gridView_timer.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                position_delete = position;
                CustomDialog_comment.Builder builder = new CustomDialog_comment.Builder(TimerActivity_details.this);
                builder.setTitle("提示 :");
                builder.setMessage("您确定删除此设备?");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        common_dev.remove(position_delete);
                        gridViewAdapter_Timer.notifyDataSetChanged(common_dev);
                        ToastUtil.showText("删除成功");
                    }
                });
                builder.create().show();
                return true;
            }
        });
        gridView_timer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (common_dev.get(position).getBOnOff() == 0)
                    common_dev.get(position).setBOnOff(1);
                else
                    common_dev.get(position).setBOnOff(0);
                gridViewAdapter_Timer.notifyDataSetChanged(common_dev);
            }
        });
    }

    /**
     * 初始化数据
     *
     * @param timer_position
     */
    public void initData(int timer_position) {
        home_text = MyApplication.getWareData().getRooms();
        if (MyApplication.getWareData().getTimer_data().getTimerEvent_rows() == null && MyApplication.getWareData().getTimer_data().getTimerEvent_rows().size() == 0)
            return;
        et_name.setText("");
        et_name.setHint(MyApplication.getWareData().getTimer_data()
                .getTimerEvent_rows().get(timer_position).getTimerName());
        if (MyApplication.getWareData().getTimer_data().getTimerEvent_rows().get(timer_position).getRun_dev_item() == null
                || MyApplication.getWareData().getTimer_data().getTimerEvent_rows().get(timer_position).getRun_dev_item().size() == 0) {
            tv_enabled.setText("禁用");
            tv_time_start.setText("点击选择时间");
            tv_time_week.setText("点击选择星期");
            tv_time_end.setText("点击选择时间");
            tv_week_repeat.setText("否");
            tv_all_network.setText("是");
        } else {
            if (MyApplication.getWareData().getTimer_data().getTimerEvent_rows().get(timer_position).getValid() == 1)
                tv_enabled.setText("启用");
            else tv_enabled.setText("禁用");

            List<Integer> data_start = MyApplication.getWareData().getTimer_data().getTimerEvent_rows().get(timer_position).getTimSta();
            String startTime = data_start.get(0) + " : " + data_start.get(1);
            tv_time_start.setText(startTime);

            int weekSelect_10 = data_start.get(3);
            String weekSelect_2 = reverseString(Integer.toBinaryString(weekSelect_10));
            String weekSelect_2_data = "";
            for (int i = 0; i < weekSelect_2.toCharArray().length; i++) {
                if (weekSelect_2.toCharArray()[i] == '1')
                    weekSelect_2_data += " " + (i + 1);
            }
            tv_time_week.setText("星期集： " + weekSelect_2_data + "");

            List<Integer> data_end = MyApplication.getWareData().getTimer_data().getTimerEvent_rows().get(timer_position).getTimEnd();
            String endtime = data_end.get(0) + " : " + data_end.get(1);
            tv_time_end.setText(endtime);

            if (data_end.get(3) == 1) tv_week_repeat.setText("是");
            else tv_week_repeat.setText("否");
            tv_all_network.setText("是");
        }
        et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                title.setText(et_name.getText().toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (common_dev != null)
            common_dev.clear();
    }

    /**
     * 初始化GridView 数据
     *
     * @param home_position
     * @return
     */
    public List<WareDev> initGridViewData(int home_position) {
        dev = new ArrayList<>();
        mWareDev = new ArrayList<>();
        for (int i = 0; i < MyApplication.getWareData().getDevs().size(); i++) {
            mWareDev.add(MyApplication.getWareData().getDevs().get(i));
        }
        for (int i = 0; i < mWareDev.size(); i++) {
            if (home_text != null && home_text.size() > 0)
                if (mWareDev.get(i).getRoomName().equals(home_text.get(home_position)))
                    dev.add(mWareDev.get(i));
        }
        return dev;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_bar_tv_room://保存
                CustomDialog_comment.Builder builder = new CustomDialog_comment.Builder(TimerActivity_details.this);
                builder.setTitle("提示");
                builder.setMessage("您要保存这些设置吗？");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
                builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        try {
                            Timer_Data time_data = new Timer_Data();
                            List<Timer_Data.TimerEventRowsBean> timerEvent_rows = new ArrayList<>();
                            Timer_Data.TimerEventRowsBean bean = new Timer_Data.TimerEventRowsBean();
                            bean.setDevCnt(common_dev.size());
                            bean.setEventId(MyApplication.getWareData().getTimer_data().getTimerEvent_rows().get(Timer_position).getEventId());
                            bean.setRun_dev_item(common_dev);
                            if ("".equals(et_name.getText().toString())) {
                                bean.setTimerName(CommonUtils.bytesToHexString(MyApplication.getWareData().getTimer_data().getTimerEvent_rows().get(Timer_position).getTimerName().getBytes("GB2312")));
                            } else {
                                try {
                                    //触发器名称
                                    bean.setTimerName(CommonUtils.bytesToHexString(et_name.getText().toString().getBytes("GB2312")));
                                } catch (UnsupportedEncodingException e) {
                                    ToastUtil.showText("定时器名称不合适");
                                    return;
                                }
                            }

                            List<Integer> time_Data_start = new ArrayList<>();
                            String time_start = tv_time_start.getText().toString();
                            String time_end = tv_time_end.getText().toString();
                            if ("点击选择时间".equals(time_start) || "点击选择时间".equals(time_end)) {
                                ToastUtil.showText("请选择时间");
                                return;
                            }
                            time_Data_start.add(Integer.parseInt(time_start.substring(0, time_start.indexOf(" : "))));
                            time_Data_start.add(Integer.parseInt(time_start.substring(time_start.indexOf(" : ") + 3)));
                            time_Data_start.add(0);
                            if (str2num(tv_time_week.getText().toString()) == 0) {
                                ToastUtil.showText("请选择星期");
                                return;
                            }
                            time_Data_start.add(str2num(tv_time_week.getText().toString()));
                            bean.setTimSta(time_Data_start);
                            List<Integer> time_Data_end = new ArrayList<>();
                            time_Data_end.add(Integer.parseInt(time_end.substring(0, time_end.indexOf(" : "))));
                            time_Data_end.add(Integer.parseInt(time_end.substring(time_end.indexOf(" : ") + 3)));
                            time_Data_end.add(0);

                            if ("是".equals(tv_week_repeat.getText().toString()))
                                time_Data_end.add(1);
                            else
                                time_Data_end.add(0);
                            bean.setTimEnd(time_Data_end);

                            if (time_Data_start.get(0) > time_Data_end.get(0)) {
                                ToastUtil.showText("开始时间不能比结束时间迟");
                                return;
                            }

                            if ("启用".equals(tv_enabled.getText().toString()))
                                bean.setValid(1);
                            else
                                bean.setValid(0);
                            timerEvent_rows.add(bean);
                            time_data.setDatType(19);
                            time_data.setDevUnitID(GlobalVars.getDevid());
                            time_data.setItemCnt(1);
                            time_data.setSubType1(0);
                            time_data.setSubType2(0);
                            time_data.setTimerEvent_rows(timerEvent_rows);
                            Gson gson = new Gson();
                            Log.e("0000", gson.toJson(time_data));
                            MyApplication.mApplication.showLoadDialog(TimerActivity_details.this);
                            MyApplication.mApplication.getUdpServer().send(gson.toJson(time_data),19);
                        } catch (Exception e) {
                            MyApplication.mApplication.dismissLoadDialog();
                            Log.e("保存定时器数据", "保存数据异常" + e);
                            ToastUtil.showText("保存数据异常,请检查数据是否合适");
                        }
                    }
                });
                builder.create().show();
                break;
            case R.id.add_dev_timer://点击添加设备按钮事件

                //添加页面的item点击，以及listview的初始化
                equipmentAdapter = new TimerActivity_details.EquipmentAdapter(initGridViewData(home_position), this);
                add_dev_Layout_lv.setAdapter(equipmentAdapter);
                tv_text_parlour.setText(home_text.get(home_position));
                add_dev_Layout_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        WareDev item = dev.get(position);
                        boolean tag = true;
                        if (common_dev == null)
                            common_dev = new ArrayList<>();
                        if (common_dev.size() > 0) {
                            for (int i = 0; i < common_dev.size(); i++) {
                                if (common_dev.get(i).getDevType() == item.getType()
                                        && common_dev.get(i).getDevID() == item.getDevId()
                                        && common_dev.get(i).getCanCpuID().equals(item.getCanCpuId())) {
                                    tag = false;
                                    Toast.makeText(TimerActivity_details.this, "设备已存在！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        if (tag) {
                            if (common_dev.size() == 4) {
                                ToastUtil.showText("定时设备最多4个！");
                                return;
                            }
                            Timer_Data.TimerEventRowsBean.RunDevItemBean bean = new Timer_Data.TimerEventRowsBean.RunDevItemBean();
                            bean.setDevID(item.getDevId());
                            bean.setBOnOff(item.getbOnOff());
                            bean.setDevType(item.getType());
                            bean.setCanCpuID(item.getCanCpuId());
                            common_dev.add(bean);
                            if (gridViewAdapter_Timer != null)
                                gridViewAdapter_Timer.notifyDataSetChanged(common_dev);
                            else {
                                gridViewAdapter_Timer = new GridViewAdapter_Timer(common_dev);
                                gridView_timer.setAdapter(gridViewAdapter_Timer);
                            }
                        }
                    }
                });

                add_dev_Layout_lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        if (common_dev.size() > 0) {
                            common_dev.remove(position);
                            if (gridViewAdapter_Timer != null)
                                gridViewAdapter_Timer.notifyDataSetChanged(common_dev);
                            else {
                                gridViewAdapter_Timer = new GridViewAdapter_Timer(common_dev);
                                gridView_timer.setAdapter(gridViewAdapter_Timer);
                            }
                        }
                        return true;
                    }
                });
                add_dev_Layout_ll.setVisibility(View.VISIBLE);
                break;
            case R.id.enabled: //使能开关
                List<String> Enabled = new ArrayList<>();
                Enabled.add("禁用");
                Enabled.add("启用");
                initRadioPopupWindow(tv_enabled, Enabled);
                popupWindow.showAsDropDown(view, 0, 0);
                break;
            case R.id.time_week: //星期时间
                initWeekDialog(tv_time_week);
                break;
            case R.id.time_start://开始时间
                initTimePickerDialog(tv_time_start);
                break;
            case R.id.time_end:// 结束时间
                initTimePickerDialog(tv_time_end);
                break;
            case R.id.week_repeat://是否周重复
                List<String> Week_repeat = new ArrayList<>();
                Week_repeat.add("否");
                Week_repeat.add("是");
                initRadioPopupWindow(tv_week_repeat, Week_repeat);
                popupWindow.showAsDropDown(view, 0, 0);
                break;
            case R.id.all_network://是否开开全网
                List<String> All_network = new ArrayList<>();
                All_network.add("否");
                All_network.add("是");
                initRadioPopupWindow(tv_all_network, All_network);
                popupWindow.showAsDropDown(view, 0, 0);
                break;
            case R.id.tv_equipment_parlour://添加设备 选择房间
                initRadioPopupWindow(tv_text_parlour, home_text);
                popupWindow.showAsDropDown(view, 0, 0);

                break;
            case R.id.equipment_close: //关闭  添加设备界面
                add_dev_Layout_ll.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * GridView Adapter
     */
    class GridViewAdapter_Timer extends BaseAdapter {

        private List<Timer_Data.TimerEventRowsBean.RunDevItemBean> timer_list;

        GridViewAdapter_Timer(List<Timer_Data.TimerEventRowsBean.RunDevItemBean> list) {
            timer_list = list;
        }

        public void notifyDataSetChanged(List<Timer_Data.TimerEventRowsBean.RunDevItemBean> list) {
            timer_list = list;
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (timer_list == null)
                return 0;
            return timer_list.size();
        }

        @Override
        public Object getItem(int position) {
            if (timer_list == null)
                return null;
            return timer_list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(TimerActivity_details.this).inflate(R.layout.gridview_item_user, null);
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView) convertView.findViewById(R.id.equip_name);
                viewHolder.type = (ImageView) convertView.findViewById(R.id.equip_type);
                viewHolder.state = (TextView) convertView.findViewById(R.id.equip_style);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            int type_dev = timer_list.get(position).getDevType();
            if (type_dev == 0) {
                for (int j = 0; j < MyApplication.getWareData().getAirConds().size(); j++) {
                    WareAirCondDev AirCondDev = MyApplication.getWareData().getAirConds().get(j);
                    if (timer_list.get(position).getDevID() == AirCondDev.getDev().getDevId() &&
                            timer_list.get(position).getCanCpuID().endsWith(AirCondDev.getDev().getCanCpuId())) {
                        viewHolder.name.setText(AirCondDev.getDev().getDevName());
                        if (timer_list.get(position).getBOnOff() == 0) {
                            viewHolder.type.setImageResource(R.drawable.kongtiao1);
                            viewHolder.state.setText("关闭");
                        } else {
                            viewHolder.type.setImageResource(R.drawable.kongtiao2);
                            viewHolder.state.setText("打开");
                        }
                    }
                }
            } else if (type_dev == 1) {
                for (int j = 0; j < MyApplication.getWareData().getTvs().size(); j++) {
                    WareTv tv = MyApplication.getWareData().getTvs().get(j);
                    if (timer_list.get(position).getDevID() == tv.getDev().getDevId() &&
                            timer_list.get(position).getCanCpuID().endsWith(tv.getDev().getCanCpuId())) {
                        viewHolder.name.setText(tv.getDev().getDevName());
                        if (timer_list.get(position).getBOnOff() == 0) {
                            viewHolder.type.setImageResource(R.drawable.ds);
                            viewHolder.state.setText("关闭");
                        } else {
                            viewHolder.type.setImageResource(R.drawable.ds);
                            viewHolder.state.setText("打开");
                        }
                    }
                }
            } else if (type_dev == 2) {
                for (int j = 0; j < MyApplication.getWareData().getStbs().size(); j++) {
                    WareSetBox box = MyApplication.getWareData().getStbs().get(j);
                    if (timer_list.get(position).getDevID() == box.getDev().getDevId() &&
                            timer_list.get(position).getCanCpuID().endsWith(box.getDev().getCanCpuId())) {
                        viewHolder.name.setText(box.getDev().getDevName());
                        if (timer_list.get(position).getBOnOff() == 0) {
                            viewHolder.type.setImageResource(R.drawable.jidinghe);
                            viewHolder.state.setText("关闭");
                        } else {
                            viewHolder.type.setImageResource(R.drawable.jidinghe);
                            viewHolder.state.setText("打开");
                        }
                    }
                }
            } else if (type_dev == 3) {
                for (int j = 0; j < MyApplication.getWareData().getLights().size(); j++) {
                    WareLight Light = MyApplication.getWareData().getLights().get(j);
                    if (timer_list.get(position).getDevID() == Light.getDev().getDevId() &&
                            timer_list.get(position).getCanCpuID().endsWith(Light.getDev().getCanCpuId())) {
                        viewHolder.name.setText(Light.getDev().getDevName());
                        if (timer_list.get(position).getBOnOff() == 0) {
                            viewHolder.type.setImageResource(R.drawable.dengguan);
                            viewHolder.state.setText("关闭");
                        } else {
                            viewHolder.type.setImageResource(R.drawable.dengkai);
                            viewHolder.state.setText("打开");
                        }
                    }
                }
            } else if (type_dev == 4) {
                for (int j = 0; j < MyApplication.getWareData().getCurtains().size(); j++) {
                    WareCurtain Curtain = MyApplication.getWareData().getCurtains().get(j);
                    if (timer_list.get(position).getDevID() == Curtain.getDev().getDevId() &&
                            timer_list.get(position).getCanCpuID().endsWith(Curtain.getDev().getCanCpuId())) {
                        viewHolder.name.setText(Curtain.getDev().getDevName());
                        if (timer_list.get(position).getBOnOff() == 0) {
                            viewHolder.type.setImageResource(R.drawable.quanguan);
                            viewHolder.state.setText("关闭");
                        } else {
                            viewHolder.type.setImageResource(R.drawable.quankai);
                            viewHolder.state.setText("打开");
                        }
                    }
                }
            } else if (type_dev == 7) {
                for (int j = 0; j < MyApplication.getWareData().getFreshAirs().size(); j++) {
                    WareFreshAir freshAir = MyApplication.getWareData().getFreshAirs().get(j);
                    if (timer_list.get(position).getDevID() == freshAir.getDev().getDevId() &&
                            timer_list.get(position).getCanCpuID().endsWith(freshAir.getDev().getCanCpuId())) {
                        viewHolder.name.setText(freshAir.getDev().getDevName());
                        if (timer_list.get(position).getBOnOff() == 0) {
                            viewHolder.type.setImageResource(R.drawable.freshair_close);
                            viewHolder.state.setText("关闭");
                        } else {
                            viewHolder.type.setImageResource(R.drawable.freshair_open);
                            viewHolder.state.setText("打开");
                        }
                    }
                }
            } else if (type_dev == 9) {
                for (int j = 0; j < MyApplication.getWareData().getFloorHeat().size(); j++) {
                    WareFloorHeat floorHeat = MyApplication.getWareData().getFloorHeat().get(j);
                    if (timer_list.get(position).getDevID() == floorHeat.getDev().getDevId() &&
                            timer_list.get(position).getCanCpuID().endsWith(floorHeat.getDev().getCanCpuId())) {
                        viewHolder.name.setText(floorHeat.getDev().getDevName());
                        if (timer_list.get(position).getBOnOff() == 0) {
                            viewHolder.type.setImageResource(R.drawable.floorheat_close);
                            viewHolder.state.setText("关闭");
                        } else {
                            viewHolder.type.setImageResource(R.drawable.floorheat_open);
                            viewHolder.state.setText("打开");
                        }
                    }
                }
            }

            return convertView;
        }

        private class ViewHolder {
            private TextView name, state;
            private ImageView type;
        }
    }

    /**
     * 得到字符串中的数字和
     *
     * @param str
     * @return
     */
    public int str2num(String str) {
        str = str.replaceAll("\\D", "");
        List<Integer> number = new ArrayList<>();
        if (str != null && !"".equals(str)) {
            for (int i = 0; i < str.length(); i++) {
                number.add(Integer.valueOf(String.valueOf(str.charAt(i))));
            }
        }
        String s = "";
        byte[] week_byte = new byte[8];
        for (int j = 0; j < 8; j++) {
            for (int i = 0; i < number.size(); i++) {
                if (j == number.get(i) - 1) {
                    week_byte[j] = 1;
                }
            }
            s += week_byte[j];
        }
        s = reverseString(s);
        return Integer.valueOf(s, 2);
    }


    /**
     * 倒置字符串
     *
     * @param str
     * @return
     */
    public static String reverseString(String str) {
        char[] arr = str.toCharArray();
        int middle = arr.length >> 1;//EQ length/2
        int limit = arr.length - 1;
        for (int i = 0; i < middle; i++) {
            char tmp = arr[i];
            arr[i] = arr[limit - i];
            arr[limit - i] = tmp;
        }
        return new String(arr);
    }

    /**
     * 选择星期dialog
     *
     * @param view 显示控件
     */
    public void initWeekDialog(final View view) {
        List<String> Time_week = new ArrayList<>();
        Time_week.add("星期一");
        Time_week.add("星期二");
        Time_week.add("星期三");
        Time_week.add("星期四");
        Time_week.add("星期五");
        Time_week.add("星期六");
        Time_week.add("星期日");
        if (mMultiChoicePopWindow == null)
            mMultiChoicePopWindow = new MultiChoicePopWindow(this, tv_time_week,
                    Time_week, new boolean[7]);
        mMultiChoicePopWindow.setTitle("请选择星期");
        mMultiChoicePopWindow.setOnOKButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean[] selItems = mMultiChoicePopWindow.getSelectItem();
                int size = selItems.length;
                int data_to_network = 0;
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < size; i++) {
                    if (selItems[i]) {
                        stringBuffer.append(i + 1 + " ");
                        data_to_network += Math.pow(2, i);
                    }
                }
                ((TextView) view).setText("星期集：" + stringBuffer.toString());
            }
        });
        mMultiChoicePopWindow.show();
    }

    /**
     * 初始化 时间选择器
     *
     * @param view 时间选择完成后显示时间的控件
     */
    public void initTimePickerDialog(final View view) {
        Time time = new Time();
        time.setToNow();
        mTimePickerDialog = new TimePickerDialog(TimerActivity_details.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Log.i("=====", "=====END==hour=====" + hourOfDay + "====END===minute=======" + minute);
            }
        }, time.HOUR, time.MINUTE, true);
        mTimePickerDialog.show();
        View inflate = LayoutInflater.from(this).inflate(
                R.layout.timepicker_layout, null);
        mTimePicker = (TimePicker) inflate.findViewById(R.id.timePicker);
        mTimePicker.setIs24HourView(true);
        Button btn_ok = (Button) inflate.findViewById(R.id.time_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TIME", "小时" + mTimePicker.getCurrentHour() + "分钟" + mTimePicker.getCurrentMinute());
                ((TextView) view).setText(mTimePicker.getCurrentHour() + " : " + mTimePicker.getCurrentMinute());
                mTimePickerDialog.dismiss();
            }
        });
        Button btn_cancel = (Button) inflate.findViewById(R.id.time_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimePickerDialog.dismiss();
            }
        });
        mTimePickerDialog.setContentView(inflate);
    }

    /**
     * 初始化自定义设备的状态以及设备PopupWindow
     */
    private void initRadioPopupWindow(final View view_parent, final List<String> text) {

        //获取自定义布局文件pop.xml的视图
        final View customView = view_parent.inflate(this, R.layout.popupwindow_equipment_listview, null);
        customView.setBackgroundResource(R.drawable.selectbg);
        customView.setFocusable(true);
        customView.setFocusableInTouchMode(true);
        customView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (popupWindow != null) {
                        popupWindow.dismiss();
                    }
                }
                return false;
            }
        });
        // 创建PopupWindow实例
        popupWindow = new PopupWindow(view_parent.findViewById(R.id.popupWindow_equipment_sv), view_parent.getWidth(), 300);
        popupWindow.setContentView(customView);
        ListView list_pop = (ListView) customView.findViewById(R.id.popupWindow_equipment_lv);
        PopupWindowAdapter2 adapter = new PopupWindowAdapter2(text, this);
        list_pop.setAdapter(adapter);
        list_pop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view_parent;
                tv.setText(text.get(position));
                popupWindow.dismiss();

                if (view_parent.getId() == R.id.tv_equipment_parlour) {
                    dev = new ArrayList<>();
                    for (int i = 0; i < mWareDev.size(); i++) {
                        if (mWareDev.get(i).getRoomName().equals(home_text.get(position)))
                            dev.add(mWareDev.get(i));
                    }
                    home_position = position;
                    if (equipmentAdapter != null)
                        equipmentAdapter.notifyDataSetChanged(dev);
                    else {
                        equipmentAdapter = new TimerActivity_details.EquipmentAdapter(dev, TimerActivity_details.this);
                        gridView_timer.setAdapter(equipmentAdapter);
                    }
                }
            }
        });
        //popupwindow页面之外可点
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.update();
        // 自定义view添加触摸事件
        customView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
                return false;
            }
        });
    }

    /**
     * 添加设备然后设备列表适配器
     */
    class EquipmentAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private List<WareDev> listViewItems;

        public EquipmentAdapter(List<WareDev> title, Context context) {
            super();
            listViewItems = title;
            mInflater = LayoutInflater.from(context);
        }

        public void notifyDataSetChanged(List<WareDev> title) {
            listViewItems = title;
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (null != listViewItems)
                return listViewItems.size();
            else
                return 0;
        }

        @Override
        public Object getItem(int position) {
            return listViewItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            TimerActivity_details.EquipmentAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.equipment_listview_item, null);
                viewHolder = new TimerActivity_details.EquipmentAdapter.ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.equipment_tv);
                viewHolder.image = (ImageView) convertView.findViewById(R.id.equipment_iv);
                convertView.setTag(viewHolder);
            } else
                viewHolder = (TimerActivity_details.EquipmentAdapter.ViewHolder) convertView.getTag();
            viewHolder.title.setText(listViewItems.get(position).getDevName());
            if (listViewItems.get(position).getType() == 0)
                viewHolder.image.setImageResource(image[0]);
            else if (listViewItems.get(position).getType() == 1)
                viewHolder.image.setImageResource(image[1]);
            else if (listViewItems.get(position).getType() == 2)
                viewHolder.image.setImageResource(image[2]);
            else if (listViewItems.get(position).getType() == 3)
                viewHolder.image.setImageResource(image[3]);
            else
                viewHolder.image.setImageResource(image[4]);
            return convertView;
        }

        public class ViewHolder {
            public TextView title;
            public ImageView image;
        }
    }
}
