package cn.etsoft.smarthome.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.PopupWindowAdapter2;
import cn.etsoft.smarthome.domain.Condition_Event_Bean;
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

/**
 * Created by Say GoBay on 2017/6/1.
 * 系统设置--环境事件--详情
 */
public class ConditionEventActivity_details extends Activity implements View.OnClickListener {
    private ImageView back;
    private TextView title, save, tv_enabled, event_way, event_type, add_dev_condition, add_dev_Layout_close, tv_text_parlour;
    private EditText input_num, et_name;
    private GridView gridView_condition;
    private LinearLayout add_dev_Layout_ll;
    private ListView add_dev_Layout_lv;
    private List<String> home_text;
    //添加设备房间position；
    private List<WareDev> dev;
    private List<WareDev> mWareDev;
    //触发器位置position
    private int Condition_position = 0;
    private int[] image = new int[]{R.drawable.kongtiao, R.drawable.tv_0, R.drawable.jidinghe, R.drawable.dengguang, R.drawable.chuanglian};
    private PopupWindow popupWindow;
    private GridViewAdapter_Condition GridViewAdapter_Condition;
    private List<Condition_Event_Bean.EnvEventRowsBean.RunDevItemBean> common_dev;
    private ConditionEventActivity_details.EquipmentAdapter equipmentAdapter;
    private List<String> Event_type;
    private List<String> Event_Way;
    //添加设备房间position；
    private int home_position;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //解决弹出键盘压缩布局的问题
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_conditionevent);
        //初始化标题栏
        initTitleBar();
        //初始化组件
        initView();
        initGridView(Condition_position);
        initData(Condition_position);
        MyApplication.mApplication.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int datType, int subtype1, int subtype2) {
                if (datType == 27) {
                    MyApplication.mApplication.dismissLoadDialog();
                    if (Condition_position != 0) {
                        ToastUtil.showText("保存成功");
                    }
                    initTitleBar();
                }
                if (datType == 29) {
                    SendDataUtil.getConditionInfo();
                    if (Condition_position == 0) {
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
        Condition_position = getIntent().getExtras().getInt("Condition_position");
        title.setTextColor(0xffffffff);
        title.setText("");
        title.setHint(MyApplication.getWareData().getCondition_event_bean().getenvEvent_rows().get(Condition_position).getEventName());
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
        input_num = (EditText) findViewById(R.id.input_num);
        event_way = (TextView) findViewById(R.id.event_way);
        event_type = (TextView) findViewById(R.id.event_type);
        add_dev_condition = (TextView) findViewById(R.id.add_dev_condition);
        et_name = (EditText) findViewById(R.id.name);
        gridView_condition = (GridView) findViewById(R.id.gridView_condition);

        //添加设备布局
        add_dev_Layout_lv = (ListView) findViewById(R.id.equipment_loyout_lv);
        add_dev_Layout_close = (TextView) findViewById(R.id.equipment_close);
        tv_text_parlour = (TextView) findViewById(R.id.tv_equipment_parlour);
        add_dev_Layout_ll = (LinearLayout) findViewById(R.id.add_dev_Layout_ll);
        add_dev_Layout_close.setOnClickListener(this);
        tv_text_parlour.setOnClickListener(this);

        event_way.setOnClickListener(this);
        tv_enabled.setOnClickListener(this);
        event_type.setOnClickListener(this);
        add_dev_condition.setOnClickListener(this);
        et_name.setOnClickListener(this);
    }

    int position_delete;

    /**
     * 初始化GridView
     */
    public void initGridView(int Condition_position) {
        if (MyApplication.getWareData().getCondition_event_bean() == null || MyApplication.getWareData().getCondition_event_bean().getenvEvent_rows().size() == 0)
            return;
        common_dev = new ArrayList<>();
        for (int i = 0; i < MyApplication.getWareData().getCondition_event_bean().getenvEvent_rows().get(Condition_position).getRun_dev_item().size(); i++) {
            common_dev.add(MyApplication.getWareData().getCondition_event_bean().getenvEvent_rows().get(Condition_position).getRun_dev_item().get(i));
        }
//        common_dev = MyApplication.getWareData().getCondition_event_bean().getenvEvent_rows().get(Condition_position).getRun_dev_item();
        GridViewAdapter_Condition = new GridViewAdapter_Condition(common_dev);
        gridView_condition.setAdapter(GridViewAdapter_Condition);

        gridView_condition.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                position_delete = position;
                CustomDialog_comment.Builder builder = new CustomDialog_comment.Builder(ConditionEventActivity_details.this);
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
                        GridViewAdapter_Condition.notifyDataSetChanged(common_dev);
                        ToastUtil.showText("删除成功");
                    }
                });
                builder.create().show();
                return true;
            }
        });
        gridView_condition.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (common_dev.get(position).getBOnOff() == 0)
                    common_dev.get(position).setBOnOff(1);
                else
                    common_dev.get(position).setBOnOff(0);
                GridViewAdapter_Condition.notifyDataSetChanged(common_dev);
            }
        });
    }

    /**
     * 初始化数据
     *
     * @param condition_position
     */
    public void initData(int condition_position) {
        Event_Way = new ArrayList<>();
        Event_Way.add("大于阀值");
        Event_Way.add("小于阀值");
        Event_type = new ArrayList<>();
        Event_type.add("温度触发");
        Event_type.add("湿度触发");
        Event_type.add("P2.5触发");
        home_text = MyApplication.getWareData().getRooms();
        if (MyApplication.getWareData().getCondition_event_bean().getenvEvent_rows().size() == 0)
            return;
        et_name.setText("");
        et_name.setHint(MyApplication.getWareData().getCondition_event_bean().getenvEvent_rows().get(condition_position).getEventName());
        if (MyApplication.getWareData().getCondition_event_bean().getenvEvent_rows().get(condition_position).getRun_dev_item() == null
                || MyApplication.getWareData().getCondition_event_bean().getenvEvent_rows().get(condition_position).getRun_dev_item().size() == 0) {
            tv_enabled.setText("禁用");
            input_num.setText("");
            input_num.setHint("输入触发值");
            input_num.setHintTextColor(Color.WHITE);
            event_way.setText("选择触发方式");
            event_type.setText("点击选择触发类别");
        } else {
            if (MyApplication.getWareData().getCondition_event_bean().getenvEvent_rows().get(condition_position).getValid() == 1)
                tv_enabled.setText("启用");
            else tv_enabled.setText("禁用");
            input_num.setText("" + MyApplication.getWareData().getCondition_event_bean().getenvEvent_rows().get(condition_position).getValTh());
            event_way.setText("" + Event_Way.get(MyApplication.getWareData().getCondition_event_bean().getenvEvent_rows().get(condition_position).getThType()));
            event_type.setText("" + Event_type.get(MyApplication.getWareData().getCondition_event_bean().getenvEvent_rows().get(condition_position).getEnvType()));
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_bar_tv_room://保存
                CustomDialog_comment.Builder builder = new CustomDialog_comment.Builder(ConditionEventActivity_details.this);
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
                            Condition_Event_Bean time_data = new Condition_Event_Bean();
                            List<Condition_Event_Bean.EnvEventRowsBean> envEvent_rows = new ArrayList<>();
                            Condition_Event_Bean.EnvEventRowsBean bean = new Condition_Event_Bean.EnvEventRowsBean();
                            //  "devCnt": 1,
                            bean.setDevCnt(common_dev.size());
                            //"eventId":	0,
                            bean.setEventId(MyApplication.getWareData().getCondition_event_bean().getenvEvent_rows().get(Condition_position).getEventId());
                            // "run_dev_item":
                            bean.setRun_dev_item(common_dev);

                            if ("".equals(et_name.getText().toString())) {
                                bean.setEventName(CommonUtils.bytesToHexString(MyApplication.getWareData().getCondition_event_bean().getenvEvent_rows().get(Condition_position).getEventName().getBytes("GB2312")));
                            } else {
                                try {
                                    //触发器名称
                                    bean.setEventName(CommonUtils.bytesToHexString(et_name.getText().toString().getBytes("GB2312")));
                                } catch (UnsupportedEncodingException e) {
                                    ToastUtil.showText("触发器名称不合适");
                                    return;
                                }
                            }
                            if (et_name.getText().toString().length() > 24) {
                                ToastUtil.showText("触发器名称不能过长");
                                return;
                            }


                            // "valTh":
                            if ("输入触发值".equals(input_num.getText().toString()) || "".equals(input_num.getText().toString())) {
                                ToastUtil.showText("触发器阀值不能为空");
                                return;
                            }
                            bean.setValTh(Integer.parseInt(input_num.getText().toString()));

                            //触发器是否启用   "valid":
                            if ("启用".equals(tv_enabled.getText().toString()))
                                bean.setValid(1);
                            else
                                bean.setValid(0);

                            //触发器触发方式  "thType":
                            if ("点击选择触发方式".equals(event_way.getText().toString())) {
                                ToastUtil.showText("请选择触发方式");
                                return;
                            }
                            for (int i = 0; i < Event_Way.size(); i++) {
                                if (Event_Way.get(i).equals(event_way.getText().toString())) {
                                    bean.setThType(i);
                                }
                            }

                            //触发器触发类别  "envType"

                            if ("点击选择触发类别".equals(event_type.getText().toString())) {
                                ToastUtil.showText("请选择触发类别");
                                return;
                            }
                            for (int i = 0; i < Event_type.size(); i++) {
                                if (Event_type.get(i).equals(event_type.getText().toString())) {
                                    bean.setEnvType(i);
                                }
                            }

                            // "uidSrc":
                            bean.setUidSrc(MyApplication.getWareData().getCondition_event_bean().getenvEvent_rows().get(Condition_position).getUidSrc());

                            envEvent_rows.add(bean);
                            time_data.setDatType(29);
                            time_data.setDevUnitID(GlobalVars.getDevid());
                            time_data.setItemCnt(1);
                            time_data.setSubType1(0);
                            time_data.setSubType2(0);
                            time_data.setenvEvent_rows(envEvent_rows);
                            Gson gson = new Gson();
                            Log.i("保存触发器数据", gson.toJson(time_data));
                            MyApplication.mApplication.showLoadDialog(ConditionEventActivity_details.this);
                            MyApplication.mApplication.getUdpServer().send(gson.toJson(time_data));
                        } catch (Exception e) {
                            MyApplication.mApplication.dismissLoadDialog();
                            Log.e("保存触发器数据", "保存数据异常" + e);
                            ToastUtil.showText("保存数据异常,请检查数据是否合适");
                        }
                    }
                });
                builder.create().show();
                break;
            case R.id.add_dev_condition://点击添加设备按钮事件

                //添加页面的item点击，以及listview的初始化
                equipmentAdapter = new ConditionEventActivity_details.EquipmentAdapter(initGridViewData(home_position), this);
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
                                    Toast.makeText(ConditionEventActivity_details.this, "设备已存在！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        if (tag) {
                            if (common_dev.size() == 4) {
                                ToastUtil.showText("定时设备最多4个！");
                                return;
                            }
                            Condition_Event_Bean.EnvEventRowsBean.RunDevItemBean bean = new Condition_Event_Bean.EnvEventRowsBean.RunDevItemBean();
                            bean.setDevID(item.getDevId());
                            bean.setBOnOff(item.getbOnOff());
                            bean.setDevType(item.getType());
                            bean.setCanCpuID(item.getCanCpuId());
                            common_dev.add(bean);
                            if (GridViewAdapter_Condition != null)
                                GridViewAdapter_Condition.notifyDataSetChanged(common_dev);
                            else {
                                GridViewAdapter_Condition = new GridViewAdapter_Condition(common_dev);
                                gridView_condition.setAdapter(GridViewAdapter_Condition);
                            }
                        }
                    }
                });

                add_dev_Layout_lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                        if (common_dev.size() > 0) {
                            common_dev.remove(position);
                            if (GridViewAdapter_Condition != null)
                                GridViewAdapter_Condition.notifyDataSetChanged(common_dev);
                            else {
                                GridViewAdapter_Condition = new GridViewAdapter_Condition(common_dev);
                                gridView_condition.setAdapter(GridViewAdapter_Condition);
                            }
                        }
                        return true;
                    }
                });
                add_dev_Layout_ll.setVisibility(View.VISIBLE);
                break;
            case R.id.enabled: //启用开关
                List<String> Enabled = new ArrayList<>();
                Enabled.add("禁用");
                Enabled.add("启用");
                initRadioPopupWindow(tv_enabled, Enabled);
                popupWindow.showAsDropDown(v, 0, 0);
                break;
            case R.id.event_way://触发方式
                initRadioPopupWindow(event_way, Event_Way);
                popupWindow.showAsDropDown(v, 0, 0);
                break;
            case R.id.event_type://是否开开全网
                initRadioPopupWindow(event_type, Event_type);
                popupWindow.showAsDropDown(v, 0, 0);
                break;
            case R.id.tv_equipment_parlour://添加设备 选择房间
                initRadioPopupWindow(tv_text_parlour, home_text);
                popupWindow.showAsDropDown(v, 0, 0);
                break;
            case R.id.equipment_close: //关闭  添加设备界面
                add_dev_Layout_ll.setVisibility(View.GONE);
                break;
        }
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
                        equipmentAdapter = new ConditionEventActivity_details.EquipmentAdapter(dev, ConditionEventActivity_details.this);
                        gridView_condition.setAdapter(equipmentAdapter);
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
     * GridView Adapter
     */
    class GridViewAdapter_Condition extends BaseAdapter {

        private List<Condition_Event_Bean.EnvEventRowsBean.RunDevItemBean> env_list;

        GridViewAdapter_Condition(List<Condition_Event_Bean.EnvEventRowsBean.RunDevItemBean> list) {
            env_list = list;
        }

        public void notifyDataSetChanged(List<Condition_Event_Bean.EnvEventRowsBean.RunDevItemBean> list) {
            env_list = list;
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (env_list == null)
                return 0;
            return env_list.size();
        }

        @Override
        public Object getItem(int position) {
            if (env_list == null)
                return null;
            return env_list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(ConditionEventActivity_details.this).inflate(R.layout.gridview_item_user, null);
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView) convertView.findViewById(R.id.equip_name);
                viewHolder.type = (ImageView) convertView.findViewById(R.id.equip_type);
                viewHolder.state = (TextView) convertView.findViewById(R.id.equip_style);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            int type_dev = env_list.get(position).getDevType();
            if (type_dev == 0) {
                for (int j = 0; j < MyApplication.getWareData().getAirConds().size(); j++) {
                    WareAirCondDev AirCondDev = MyApplication.getWareData().getAirConds().get(j);
                    if (env_list.get(position).getDevID() == AirCondDev.getDev().getDevId() &&
                            env_list.get(position).getCanCpuID().endsWith(AirCondDev.getDev().getCanCpuId())) {
                        viewHolder.name.setText(AirCondDev.getDev().getDevName());
                        if (env_list.get(position).getBOnOff() == 0) {
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
                    if (env_list.get(position).getDevID() == tv.getDev().getDevId() &&
                            env_list.get(position).getCanCpuID().endsWith(tv.getDev().getCanCpuId())) {
                        viewHolder.name.setText(tv.getDev().getDevName());
                        if (env_list.get(position).getBOnOff() == 0) {
                            viewHolder.type.setImageResource(R.drawable.dianshi);
                            viewHolder.state.setText("关闭");
                        } else {
                            viewHolder.type.setImageResource(R.drawable.dianshi);
                            viewHolder.state.setText("打开");
                        }
                    }
                }
            } else if (type_dev == 2) {
                for (int j = 0; j < MyApplication.getWareData().getStbs().size(); j++) {
                    WareSetBox box = MyApplication.getWareData().getStbs().get(j);
                    if (env_list.get(position).getDevID() == box.getDev().getDevId() &&
                            env_list.get(position).getCanCpuID().endsWith(box.getDev().getCanCpuId())) {
                        viewHolder.name.setText(box.getDev().getDevName());
                        if (env_list.get(position).getBOnOff() == 0) {
                            viewHolder.type.setImageResource(R.drawable.jidinghe);
                            viewHolder.state.setText("关闭");
                        } else {
                            viewHolder.type.setImageResource(R.drawable.jidinghe1);
                            viewHolder.state.setText("打开");
                        }
                    }
                }
            } else if (type_dev == 3) {
                for (int j = 0; j < MyApplication.getWareData().getLights().size(); j++) {
                    WareLight Light = MyApplication.getWareData().getLights().get(j);
                    if (env_list.get(position).getDevID() == Light.getDev().getDevId() &&
                            env_list.get(position).getCanCpuID().endsWith(Light.getDev().getCanCpuId())) {
                        viewHolder.name.setText(Light.getDev().getDevName());
                        if (env_list.get(position).getBOnOff() == 0) {
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
                    if (env_list.get(position).getDevID() == Curtain.getDev().getDevId() &&
                            env_list.get(position).getCanCpuID().endsWith(Curtain.getDev().getCanCpuId())) {
                        viewHolder.name.setText(Curtain.getDev().getDevName());
                        if (env_list.get(position).getBOnOff() == 0) {
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
                    if (env_list.get(position).getDevID() == freshAir.getDev().getDevId() &&
                            env_list.get(position).getCanCpuID().endsWith(freshAir.getDev().getCanCpuId())) {
                        viewHolder.name.setText(freshAir.getDev().getDevName());
                        if (env_list.get(position).getBOnOff() == 0) {
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
                    if (env_list.get(position).getDevID() == floorHeat.getDev().getDevId() &&
                            env_list.get(position).getCanCpuID().endsWith(floorHeat.getDev().getCanCpuId())) {
                        viewHolder.name.setText(floorHeat.getDev().getDevName());
                        if (env_list.get(position).getBOnOff() == 0) {
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
            ConditionEventActivity_details.EquipmentAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.equipment_listview_item, null);
                viewHolder = new ConditionEventActivity_details.EquipmentAdapter.ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.equipment_tv);
                viewHolder.image = (ImageView) convertView.findViewById(R.id.equipment_iv);
                convertView.setTag(viewHolder);
            } else
                viewHolder = (ConditionEventActivity_details.EquipmentAdapter.ViewHolder) convertView.getTag();
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
