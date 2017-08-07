package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.UiUtils.ToastUtil;
import cn.etsoft.smarthomephone.adapter.PopupWindowAdapter2;
import cn.etsoft.smarthomephone.adapter.SafetyAdapter_home;
import cn.etsoft.smarthomephone.domain.Safety_Data;
import cn.etsoft.smarthomephone.pullmi.utils.Dtat_Cache;
import cn.etsoft.smarthomephone.view.Circle_Progress;
import cn.etsoft.smarthomephone.weidget.CustomDatePicker;

/**
 * Created by Say GoBay on 2017/8/7.
 */
public class SafetyActivity_home extends Activity implements View.OnClickListener {
    private TextView safety, screen, title;
    private CustomDatePicker time_start, time_end;
    private ListView listView;
    private ImageView back;
    private List<String> safetyName;
    private PopupWindow popupWindow;
    private int safety_position = 0;
    private SafetyAdapter_home safetyAdapter;
    private Safety_Data safety_Data;
    //所有报警信息
    private List<Safety_Data.Data_Data> data_data_all;
    //防区的报警信息
    private List<Safety_Data.Data_Data> data_data_safety;
    //时间区间内的防区的报警信息
    private List<Safety_Data.Data_Data> data_data;
    private List<String> getTime;
    private List<Long> Time;
    private Dialog mDialog;
    private long startTime = 0, endTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_home);
        initDialog("初始化数据中...");
        safety_Data = Dtat_Cache.readFile_safety(true);
        //初始化标题栏
        initTitleBar();
        //初始化控件
        initView();
        //加载数据
        initData();
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        title = (TextView) findViewById(R.id.title_bar_tv_title);
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        title.setTextColor(0xffffffff);
        title.setText("安防");
        back.setImageResource(R.drawable.return2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

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

    /**
     * 初始化控件
     */
    private void initView() {
        time_start = (CustomDatePicker) findViewById(R.id.time_start);
        time_end = (CustomDatePicker) findViewById(R.id.time_end);
        safety = (TextView) findViewById(R.id.safety);
        screen = (TextView) findViewById(R.id.screen);
        listView = (ListView) findViewById(R.id.listView);
        if (MyApplication.getWareData().getResult_safety() == null || MyApplication.getWareData().getResult_safety().getSec_info_rows().size() == 0)
            return;
        safetyName = new ArrayList<>();
        safetyName.add("全部");
        for (int i = 0; i < MyApplication.getWareData().getResult_safety().getSec_info_rows().size(); i++) {
            safetyName.add(MyApplication.getWareData().getResult_safety().getSec_info_rows().get(i).getSecName());
        }
        safety.setText(safetyName.get(safety_position));
        safety.setOnClickListener(this);
        screen.setOnClickListener(this);
        time_start.setDividerColor(0xff63C4E8);
        time_start.setPickerMargin(5);
        time_end.setDividerColor(0xff63C4E8);
        time_end.setPickerMargin(5);
    }

    /**
     * 加载数据
     */
    private void initData() {
        if (safety_Data == null) {
            ToastUtil.showToast(this, "没有检索到信息");
            mDialog.dismiss();
        } else {
            initData_safety(safety_position, safety_Data, startTime, endTime);
        }
    }

    String start = "", end = "";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.safety:
                initRadioPopupWindow(safety, safetyName);
                popupWindow.showAsDropDown(v, 0, 0);
                break;
            case R.id.screen:
                if ("".equals(time_start.getYear()) || "".equals(time_start.getMonth() + 1) || "".equals(time_start.getDayOfMonth()) || "".equals(time_end.getYear()) || "".equals(time_end.getMonth() + 1) || "".equals(time_end.getDayOfMonth())) {
                    ToastUtil.showToast(SafetyActivity_home.this, "请输入检索时间");
                    return;
                }
                if ((time_start.getMonth() + 1) < 10 && time_start.getDayOfMonth() > 10) {
                    start = String.valueOf(time_start.getYear() + "" + ("0" + (time_start.getMonth() + 1)) + "" + time_start.getDayOfMonth());
                }else if((time_start.getMonth() + 1) < 10 && time_start.getDayOfMonth() < 10){
                    start = String.valueOf(time_start.getYear() + "" + ("0" + (time_start.getMonth() + 1)) + "" + ("0" + (time_start.getDayOfMonth())) );
                } else if((time_start.getMonth() + 1) > 10 && time_start.getDayOfMonth() < 10){
                    start = String.valueOf(time_start.getYear() + "" +  (time_start.getMonth() + 1) + "" + ("0" + (time_start.getDayOfMonth())) );
                } else {
                    start = String.valueOf(time_start.getYear() + "" + (time_start.getMonth() + 1) + "" + time_start.getDayOfMonth());
                }

                startTime = Integer.parseInt(start);
                if ((time_end.getMonth() + 1) < 10 && time_end.getDayOfMonth() > 10) {
                    end = String.valueOf(time_end.getYear() + "" + ("0" + (time_end.getMonth() + 1)) + "" + time_end.getDayOfMonth());
                }else if((time_end.getMonth() + 1) < 10 && time_end.getDayOfMonth() < 10){
                    end = String.valueOf(time_end.getYear() + "" + ("0" + (time_end.getMonth() + 1)) + "" + ("0" + (time_end.getDayOfMonth())) );
                } else if((time_end.getMonth() + 1) > 10 && time_end.getDayOfMonth() < 10){
                    end = String.valueOf(time_end.getYear() + "" +  (time_end.getMonth() + 1) + "" + ("0" + (time_end.getDayOfMonth())) );
                } else {
                    end = String.valueOf(time_end.getYear() + "" + (time_end.getMonth() + 1) + "" + time_end.getDayOfMonth());
                }
                endTime = Integer.parseInt(end);
                if (startTime > endTime) {
                    ToastUtil.showToast(SafetyActivity_home.this, "截止日期不能小于起始日期");
                    return;
                }
                initData();
                break;
        }
    }

    /**
     * 检索报警信息
     *
     * @param safety_position
     * @param safety_Data
     * @param startTime
     * @param endTime
     */
    String month_get, day_get;

    private void initData_safety(int safety_position, Safety_Data safety_Data, long startTime, long endTime) {
        data_data_all = safety_Data.getDatas();
        if (data_data_all == null) {
            data_data_all = new ArrayList<>();
        } else {
            data_data_all = safety_Data.getDatas();
        }
        data_data_safety = new ArrayList<>();
        data_data = new ArrayList<>();

        if (startTime == 0 || endTime == 0) {
            data_data = data_data_all;
        } else {
            if (safety_position == 0) {//全部防区
                data_data_safety = data_data_all;
                //获取到的信息的时间
                getTime();
                //获取数据
                judge(startTime, endTime);
            } else {//各个防区
                if ((safety_position - 1) >= data_data_all.size()) {
                    ToastUtil.showToast(SafetyActivity_home.this, "没有检索到信息");
                    return;
                }
                for (int i = 0; i < data_data_all.size(); i++) {
                    if (safety_position == data_data_all.get(i).getId()) {
                        data_data_safety.add(data_data_all.get(i));
                    }
                }
                //获取到的信息的时间
                getTime();
                //获取数据
                judge(startTime, endTime);
            }
        }
        safetyAdapter = new SafetyAdapter_home(data_data, SafetyActivity_home.this);
        listView.setAdapter(safetyAdapter);
        mDialog.dismiss();
    }

    /**
     * 获取到的信息的时间
     *
     * @return
     */
    private List<Long> getTime() {
        getTime = new ArrayList<>();
        Time = new ArrayList<>();
        for (int i = 0; i < data_data_safety.size(); i++) {
            if (data_data_safety.get(i).getMonth() < 10) {
                month_get = 0 + "" + data_data_safety.get(i).getMonth();
            } else {
                month_get = "" + data_data_safety.get(i).getMonth();
            }
            if (data_data_safety.get(i).getDay() < 10) {
                day_get = 0 + "" + data_data_safety.get(i).getDay();
            } else {
                day_get = "" + data_data_safety.get(i).getDay();
            }
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            try {
//                Long a = simpleDateFormat.parse(data_data_safety.get(i).getYear() + "-" + month_get + "-" + day_get).getTime();
//                getTime.add(String.valueOf(a));
//            } catch (ParseException e) {
//            }
//            Time.add(Long.valueOf(getTime.get(i)));
            getTime.add(data_data_safety.get(i).getYear() + "" + month_get + "" + day_get);
            Time.add(Long.valueOf(getTime.get(i)));

        }
        return Time;
    }

    //获取数据
    private List<Safety_Data.Data_Data> judge(long startTime, long endTime) {
        boolean match = false;
        for (int i = 0; i < Time.size(); i++) {
            if (startTime <= Time.get(i) && Time.get(i) <= endTime) {
                data_data.add(data_data_safety.get(i));
                match = true;
            }
        }
        if (!match) {
            data_data = new ArrayList<>();
            ToastUtil.showToast(SafetyActivity_home.this, "没有检索到信息");
        }
        return data_data;
    }

    /**
     * 初始化自定义设备的状态以及设备PopupWindow
     */
    private void initRadioPopupWindow(final View view_parent, final List<String> text) {

        //获取自定义布局文件pop.xml的视图
        final View customView = view_parent.inflate(SafetyActivity_home.this, R.layout.popupwindow_equipment_listview, null);
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
        popupWindow = new PopupWindow(view_parent.findViewById(R.id.popupWindow_equipment_sv), view_parent.getWidth(), 200);
        popupWindow.setContentView(customView);
        ListView list_pop = (ListView) customView.findViewById(R.id.popupWindow_equipment_lv);
        PopupWindowAdapter2 adapter = new PopupWindowAdapter2(text, SafetyActivity_home.this);
        list_pop.setAdapter(adapter);
        list_pop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view_parent;
                tv.setText(text.get(position));
                safety_position = position;
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
}
