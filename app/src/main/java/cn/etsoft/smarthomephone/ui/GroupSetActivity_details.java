package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import java.util.TreeMap;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.adapter.PopupWindowAdapter2;
import cn.etsoft.smarthomephone.domain.GroupSet_Data;
import cn.etsoft.smarthomephone.pullmi.app.GlobalVars;
import cn.etsoft.smarthomephone.pullmi.common.CommonUtils;
import cn.etsoft.smarthomephone.pullmi.entity.WareAirCondDev;
import cn.etsoft.smarthomephone.pullmi.entity.WareCurtain;
import cn.etsoft.smarthomephone.pullmi.entity.WareDev;
import cn.etsoft.smarthomephone.pullmi.entity.WareLight;
import cn.etsoft.smarthomephone.pullmi.entity.WareSetBox;
import cn.etsoft.smarthomephone.pullmi.entity.WareTv;
import cn.etsoft.smarthomephone.utils.ToastUtil;
import cn.etsoft.smarthomephone.view.Circle_Progress;
import cn.etsoft.smarthomephone.weidget.CustomDialog_comment;

/**
 * Created by Say GoBay on 2017/6/2.
 * 系统设置--组合设置--详情
 */
public class GroupSetActivity_details extends Activity implements View.OnClickListener {
    private ImageView back;
    private TextView title, save, tv_enabled, event_way, add_dev_groupSet, add_dev_Layout_close, tv_text_parlour, safety;
    private Dialog mDialog;
    private EditText et_name;
    private GridView gridView_groupSet;
    private LinearLayout add_dev_Layout_ll;
    private ListView add_dev_Layout_lv;
    private List<String> home_text;
    //添加设备房间position；
    private List<WareDev> dev;
    private List<WareDev> mWareDev;
    //组合器位置position
    private int GroupSet_position = 0;
    //    private GridViewAdapter_Safety mGridViewAdapter_Safety;
    private int[] image = new int[]{R.drawable.kongtiao, R.drawable.tv_0, R.drawable.jidinghe, R.drawable.dengguang, R.drawable.chuanglian};
    private GroupSetActivity_details.EquipmentAdapter equipmentAdapter;
    private GroupSetActivity_details.GridViewAdapter_groupSet gridViewAdapter_groupSet;
    private PopupWindow popupWindow;
    private List<GroupSet_Data.SecsTriggerRowsBean.RunDevItemBean> common_dev;
    //添加设备房间position；
    private int home_position;
    private List<String> safety_list;

    //自定义加载进度条
    private void initDialog(String str) {
        Circle_Progress.setText(str);
        mDialog = Circle_Progress.createLoadingDialog(this);
        mDialog.setCancelable(true);//允许返回
        mDialog.show();//显示
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mDialog.dismiss();
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    if (mDialog.isShowing()) {
                        handler.sendMessage(handler.obtainMessage());
                    }
                } catch (Exception e) {
                    System.out.println(e + "");
                }
            }
        }).start();
    }

    String ctlStr = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
            ",\"datType\":66" +
            ",\"subType1\":0" +
            ",\"subType2\":255" +
            "}";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //解决弹出键盘压缩布局的问题
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_groupset);
        //初始化标题栏
        initTitleBar();
        //初始化组件
        initView();
        initGridView(GroupSet_position);
        initData(GroupSet_position);
        MyApplication.mInstance.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int what) {
                if (what == 66) {
                    if (mDialog != null)
                        mDialog.dismiss();
                    initGridView(GroupSet_position);
                    initData(GroupSet_position);
//                    if (GroupSet_position != 0) {
//                        MyApplication.sendMsg(ctlStr);
                    ToastUtil.showToast(GroupSetActivity_details.this, "保存成功");
//                    }
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
        GroupSet_position = getIntent().getExtras().getInt("GroupSet_position");
        title.setTextColor(0xffffffff);
        title.setText(MyApplication.getWareData().getGroupSet_Data().getSecs_trigger_rows().get(GroupSet_position).getTriggerName());
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
        event_way = (TextView) findViewById(R.id.event_way);
        add_dev_groupSet = (TextView) findViewById(R.id.add_dev_groupSet);
        et_name = (EditText) findViewById(R.id.name);
        gridView_groupSet = (GridView) findViewById(R.id.gridView_groupSet);
        safety = (TextView) findViewById(R.id.safety);

        //添加设备布局
        add_dev_Layout_lv = (ListView) findViewById(R.id.equipment_loyout_lv);
        add_dev_Layout_close = (TextView) findViewById(R.id.equipment_close);
        tv_text_parlour = (TextView) findViewById(R.id.tv_equipment_parlour);
        add_dev_Layout_ll = (LinearLayout) findViewById(R.id.add_dev_Layout_ll);
        add_dev_Layout_close.setOnClickListener(this);
        tv_text_parlour.setOnClickListener(this);

        event_way.setOnClickListener(this);
        tv_enabled.setOnClickListener(this);
        add_dev_groupSet.setOnClickListener(this);
        et_name.setOnClickListener(this);
        safety.setOnClickListener(this);

    }

    int position_delete;

    /**
     * 初始化GridView
     */
    public void initGridView(int GroupSet_position) {
        if (MyApplication.getWareData().getGroupSet_Data() == null || MyApplication.getWareData().getGroupSet_Data().getSecs_trigger_rows().size() == 0)
            return;
        common_dev = new ArrayList<>();
        for (int i = 0; i < MyApplication.getWareData().getGroupSet_Data().getSecs_trigger_rows().get(GroupSet_position).getRun_dev_item().size(); i++) {
            common_dev.add(MyApplication.getWareData().getGroupSet_Data().getSecs_trigger_rows().get(GroupSet_position).getRun_dev_item().get(i));
        }
//        common_dev = MyApplication.getWareData().getGroupSet_Data().getSecs_trigger_rows().get(GroupSet_position).getRun_dev_item();
        gridViewAdapter_groupSet = new GridViewAdapter_groupSet(common_dev);
        gridView_groupSet.setAdapter(gridViewAdapter_groupSet);

        gridView_groupSet.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                position_delete = position;
                CustomDialog_comment.Builder builder = new CustomDialog_comment.Builder(GroupSetActivity_details.this);
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
//                        initDialog("正在删除...");
                        common_dev.remove(position_delete);
                        gridViewAdapter_groupSet.notifyDataSetChanged(common_dev);
                        ToastUtil.showToast(GroupSetActivity_details.this, "删除成功");
                    }
                });
                builder.create().show();
                return true;
            }
        });
        gridView_groupSet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (common_dev.get(position).getBOnOff() == 0)
                    common_dev.get(position).setBOnOff(1);
                else
                    common_dev.get(position).setBOnOff(0);
                gridViewAdapter_groupSet.notifyDataSetChanged(common_dev);
            }
        });
    }

    /**
     * 初始化数据
     *
     * @param groupSet_position
     */
    public void initData(int groupSet_position) {
        home_text = MyApplication.getRoom_list();
        if (MyApplication.getWareData().getGroupSet_Data().getSecs_trigger_rows().size() == 0)
            return;
        et_name.setText("");
        et_name.setHint(MyApplication.getWareData().getGroupSet_Data().getSecs_trigger_rows().get(groupSet_position).getTriggerName());
        if (MyApplication.getWareData().getGroupSet_Data().getSecs_trigger_rows().get(groupSet_position).getRun_dev_item() == null
                || MyApplication.getWareData().getGroupSet_Data().getSecs_trigger_rows().get(groupSet_position).getRun_dev_item().size() == 0) {
            tv_enabled.setText("禁用");
        } else {
            if (MyApplication.getWareData().getGroupSet_Data().getSecs_trigger_rows().get(groupSet_position).getValid() == 1)
                tv_enabled.setText("启用");
            else tv_enabled.setText("禁用");

            if (MyApplication.getWareData().getGroupSet_Data().getSecs_trigger_rows().get(groupSet_position).getReportServ() == 1)
                event_way.setText("是");
            else event_way.setText("否");
        }
        safety_list = new ArrayList<>();
        for (int i = 0; i < MyApplication.getWareData().getResult_safety().getSec_info_rows().size(); i++) {
            safety_list.add(MyApplication.getWareData().getResult_safety().getSec_info_rows().get(i).getSecName());
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        common_dev.clear();
    }

    /**
     * 初始化 添加设备 数据
     *
     * @param home_position
     * @return
     */
    public List<WareDev> initGridViewAddData(int home_position) {
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
                CustomDialog_comment.Builder builder = new CustomDialog_comment.Builder(GroupSetActivity_details.this);
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
                            GroupSet_Data time_data = new GroupSet_Data();
                            List<GroupSet_Data.SecsTriggerRowsBean> envEvent_rows = new ArrayList<>();
                            GroupSet_Data.SecsTriggerRowsBean bean = new GroupSet_Data.SecsTriggerRowsBean();
                            //  "devCnt": 1,
                            bean.setDevCnt(common_dev.size());
                            //"eventId":	0,
                            bean.setTriggerId(MyApplication.getWareData().getGroupSet_Data().getSecs_trigger_rows().get(GroupSet_position).getTriggerId());
                            // "run_dev_item":
                            bean.setRun_dev_item(common_dev);

                            if ("".equals(et_name.getText().toString())) {
                                bean.setTriggerName(CommonUtils.bytesToHexString(MyApplication.getWareData().getGroupSet_Data().getSecs_trigger_rows().get(GroupSet_position).getTriggerName().getBytes("GB2312")));
                            } else {
                                try {
                                    //触发器名称
                                    bean.setTriggerName(CommonUtils.bytesToHexString(et_name.getText().toString().getBytes("GB2312")));
                                } catch (UnsupportedEncodingException e) {
                                    ToastUtil.showToast(GroupSetActivity_details.this, "触发器名称不合适");
                                    return;
                                }
                            }
                            if (et_name.getText().toString().length() > 24) {
                                ToastUtil.showToast(GroupSetActivity_details.this, "触发器名称不能过长");
                                return;
                            }
                            //组合触发器是否启用   "valid":
                            if ("启用".equals(tv_enabled.getText().toString()))
                                bean.setValid(1);
                            else
                                bean.setValid(0);

                            //是否上报服务器
                            if ("是".equals(event_way.getText().toString()))
                                bean.setReportServ(1);
                            else bean.setReportServ(0);

                            envEvent_rows.add(bean);
                            time_data.setDatType(66);
                            time_data.setDevUnitID(GlobalVars.getDevid());
                            time_data.setItemCnt(1);
                            time_data.setSubType1(2);
                            time_data.setSubType2(0);
                            time_data.setSecs_trigger_rows(envEvent_rows);
                            Gson gson = new Gson();
                            Log.i("保存触发器数据", gson.toJson(time_data));
                            initDialog("保存数据中...");
                            MyApplication.sendMsg(gson.toJson(time_data));
                        } catch (Exception e) {
                            if (mDialog != null)
                                mDialog.dismiss();
                            Log.e("保存触发器数据", "保存数据异常" + e);
                            ToastUtil.showToast(GroupSetActivity_details.this, "保存数据异常,请检查数据是否合适");
                        }
                        //TODO   保存名字不合适。
                    }
                });
                builder.create().show();
                break;
            case R.id.add_dev_groupSet://点击添加设备按钮事件

                //添加页面的item点击，以及listview的初始化
                equipmentAdapter = new GroupSetActivity_details.EquipmentAdapter(initGridViewAddData(home_position), this);
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
                                    Toast.makeText(GroupSetActivity_details.this, "设备已存在！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        if (tag) {
                            if (common_dev.size() == 4) {
                                ToastUtil.showToast(GroupSetActivity_details.this, "定时设备最多4个！");
                                return;
                            }
                            GroupSet_Data.SecsTriggerRowsBean.RunDevItemBean bean = new GroupSet_Data.SecsTriggerRowsBean.RunDevItemBean();
                            bean.setDevID(item.getDevId());
                            bean.setBOnOff(item.getbOnOff());
                            bean.setDevType(item.getType());
                            bean.setCanCpuID(item.getCanCpuId());
                            common_dev.add(bean);
                            if (gridViewAdapter_groupSet != null)
                                gridViewAdapter_groupSet.notifyDataSetChanged(common_dev);
                            else {
                                gridViewAdapter_groupSet = new GridViewAdapter_groupSet(common_dev);
                                gridView_groupSet.setAdapter(gridViewAdapter_groupSet);
                            }
                        }
                    }
                });

                add_dev_Layout_lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        if (common_dev.size() > 0) {
                            common_dev.remove(position);
                            if (gridViewAdapter_groupSet != null)
                                gridViewAdapter_groupSet.notifyDataSetChanged(common_dev);
                            else {
                                gridViewAdapter_groupSet = new GridViewAdapter_groupSet(common_dev);
                                gridView_groupSet.setAdapter(gridViewAdapter_groupSet);
                            }
                        }
                        return true;
                    }
                });
                add_dev_Layout_ll.setVisibility(View.VISIBLE);
                break;
            case R.id.safety: //关联防区
                initPopupWindow_channel(safety, safety_list);
                popupWindow.showAsDropDown(v, 0, 0);
                break;
            case R.id.enabled: //启用开关
                List<String> Enabled = new ArrayList<>();
                Enabled.add("禁用");
                Enabled.add("启用");
                initRadioPopupWindow(tv_enabled, Enabled);
                popupWindow.showAsDropDown(v, 0, 0);
                break;
            case R.id.event_way://是否上传服务器
                List<String> Event_Way = new ArrayList<>();
                Event_Way.add("否");
                Event_Way.add("是");
                initRadioPopupWindow(event_way, Event_Way);
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
                        equipmentAdapter = new GroupSetActivity_details.EquipmentAdapter(dev, GroupSetActivity_details.this);
                        gridView_groupSet.setAdapter(equipmentAdapter);
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

    private PopupWindowAdapter_channel popupWindowAdapter_channel;
    private TreeMap<Integer, Boolean> map = new TreeMap<>();// 存放已被选中的CheckBox
    private List<String> message_save;
    private int data_save;

    /**
     * 初始化自定义设备的状态以及设备PopupWindow
     */
    private void initPopupWindow_channel(final View textView, List<String> list_channel) {
        //获取自定义布局文件pop.xml的视图
        final View customView = getLayoutInflater().from(this).inflate(R.layout.popupwindow_equipment_listview2, null);
        customView.setBackgroundResource(R.drawable.selectbg);
        popupWindow = new PopupWindow(customView, textView.getWidth(), 300);
        ListView list_pop = (ListView) customView.findViewById(R.id.popupWindow_equipment_lv);
        Button time_ok = (Button) customView.findViewById(R.id.time_ok);
        Button time_cancel = (Button) customView.findViewById(R.id.time_cancel);
        popupWindowAdapter_channel = new PopupWindowAdapter_channel(this, list_channel);
        list_pop.setAdapter(popupWindowAdapter_channel);
        time_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int length = MyApplication.getWareData().getResult_safety().getSec_info_rows().size();
                int[] data = new int[length];
                String message = "";
                if (map.keySet().toArray().length == 0) {
                    ToastUtil.showToast(GroupSetActivity_details.this, "请选择防区");
                    return;
                }
                for (int i = 0; i < length; i++) {
                    for (int k = 0; k < map.keySet().toArray().length; k++) {
                        int key = (Integer) map.keySet().toArray()[k];
                        if (i == (key - 1)) {
                            data[i] = 1;
                            break;
                        } else data[i] = 0;
                    }
                }
                String data_str = "";
                for (int i = 0; i < data.length; i++) {
                    data_str += data[i];
                }
                message_save = new ArrayList<>();
                for (int i = 0; i < map.keySet().toArray().length; i++) {
                    message += String.valueOf(map.keySet().toArray()[i]) + ".";
                    message_save.add(String.valueOf(map.keySet().toArray()[i]));
                }

                if (!"".equals(message)) {
                    message = message.substring(0, message.lastIndexOf("."));
                }
                data_save = str2num(data_str);
                TextView tv = (TextView) textView;
                tv.setText(message);
                popupWindow.dismiss();
                map.clear();
            }
        });
        time_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (map != null) {
                    map.clear();
                }
                popupWindow.dismiss();
            }
        });
        //popupWindow页面之外可点
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
     * 得到字符串中的数字和
     *
     * @param str
     * @return
     */
    public int str2num(String str) {
        str = reverseString(str);
        return Integer.valueOf(str, 2);
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
     * 防区的适配器
     */
    private class ViewHolder {
        public TextView text;
        public CheckBox checkBox;
    }

    private class PopupWindowAdapter_channel extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mLayoutInflater;
        private List<String> list_channel;


        public PopupWindowAdapter_channel(Context context, List<String> list_channel) {
            mContext = context;
            this.list_channel = list_channel;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            if (null != list_channel) {
                return list_channel.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            return list_channel.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.popupwindow_listview_item2, null);
                viewHolder = new ViewHolder();
                viewHolder.text = (TextView) convertView.findViewById(R.id.popupWindow_equipment_tv);
                viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.popupWindow_equipment_cb);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.text.setText(String.valueOf(list_channel.get(position)));
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked == true) {
                        map.put(position, true);
                    } else {
                        map.remove(list_channel.get(position));
                    }
                }
            });

            if (map != null && map.containsKey(list_channel.get(position))) {
                viewHolder.checkBox.setChecked(true);
            } else {
                viewHolder.checkBox.setChecked(false);
            }
            return convertView;
        }
    }

    /**
     * GridView Adapter
     */
    class GridViewAdapter_groupSet extends BaseAdapter {

        private List<GroupSet_Data.SecsTriggerRowsBean.RunDevItemBean> env_list;

        GridViewAdapter_groupSet(List<GroupSet_Data.SecsTriggerRowsBean.RunDevItemBean> list) {
            env_list = list;
        }

        public void notifyDataSetChanged(List<GroupSet_Data.SecsTriggerRowsBean.RunDevItemBean> list) {
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
                convertView = LayoutInflater.from(GroupSetActivity_details.this).inflate(R.layout.gridview_item_user, null);
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
                    if (env_list.get(position).getDevID() == box.getDev().getDevId() &&
                            env_list.get(position).getCanCpuID().endsWith(box.getDev().getCanCpuId())) {
                        viewHolder.name.setText(box.getDev().getDevName());
                        if (env_list.get(position).getBOnOff() == 0) {
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
            GroupSetActivity_details.EquipmentAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.equipment_listview_item, null);
                viewHolder = new GroupSetActivity_details.EquipmentAdapter.ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.equipment_tv);
                viewHolder.image = (ImageView) convertView.findViewById(R.id.equipment_iv);
                convertView.setTag(viewHolder);
            } else
                viewHolder = (GroupSetActivity_details.EquipmentAdapter.ViewHolder) convertView.getTag();
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
