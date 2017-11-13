package cn.etsoft.smarthome.adapter;

import android.app.Activity;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.domain.UdpProPkt;
import cn.etsoft.smarthome.domain.WareAirCondDev;
import cn.etsoft.smarthome.utils.SendDataUtil;
import cn.etsoft.smarthome.utils.ToastUtil;

/**
 * Author：FBL  Time： 2017/11/13.
 * 空调控制适配器
 */

public class AirConditionAdapter extends BaseAdapter {

    private List<WareAirCondDev> airCondDevs;
    private Activity activity;
    private int modelValue = 0, curValue = 0, cmdValue = 0;
    private PopupWindow popupWindow;
    private List<String> modes;


    public AirConditionAdapter(Activity activity, List<WareAirCondDev> airCondDevs) {
        this.airCondDevs = airCondDevs;
        this.activity = activity;
        modes = new ArrayList<>();
        modes.add("制冷");
        modes.add("制热");
        modes.add("除湿");
        modes.add("扫风");
    }


    public void notifyDataSetChanged(List<WareAirCondDev> airCondDevs) {
        this.airCondDevs = airCondDevs;
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return airCondDevs.size();
    }

    @Override
    public Object getItem(int i) {
        return airCondDevs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            view = LayoutInflater.from(activity).inflate(R.layout.air_list_item, null, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else viewHolder = (ViewHolder) view.getTag();
        viewHolder.mAirName.setText(airCondDevs.get(i).getDev().getDevName());
        viewHolder.mAirTempNow.setText(airCondDevs.get(i).getSelTemp() + "℃");
        viewHolder.mAirTempSet.setText(airCondDevs.get(i).getSelTemp() + "℃");

        if (airCondDevs.get(i).getbOnOff() == 0) {
            viewHolder.mAirState.setText("关闭");
            viewHolder.mAirSwitch.setImageResource(R.drawable.off);
        } else {
            viewHolder.mAirState.setText("打开");
            viewHolder.mAirSwitch.setImageResource(R.drawable.on);
        }
        if (airCondDevs.get(i).getSelMode() == 1) {
            viewHolder.mAirMode.setText("制冷");
        } else if (airCondDevs.get(i).getSelMode() == 2) {
            viewHolder.mAirMode.setText("制热");
        } else if (airCondDevs.get(i).getSelMode() == 3) {
            viewHolder.mAirMode.setText("除湿");
        } else if (airCondDevs.get(i).getSelMode() == 4) {
            viewHolder.mAirMode.setText("扫风");
        }
        if (airCondDevs.get(i).getSelSpd()
                == UdpProPkt.E_AIR_CMD.e_air_spdLow.getValue()) {
            viewHolder.mAirSpeed.setText("低风");
        } else if (airCondDevs.get(i).getSelSpd()
                == UdpProPkt.E_AIR_CMD.e_air_spdMid.getValue()) {
            viewHolder.mAirSpeed.setText("中风");
        } else if (airCondDevs.get(i).getSelSpd()
                == UdpProPkt.E_AIR_CMD.e_air_spdHigh.getValue()) {
            viewHolder.mAirSpeed.setText("高风");
        } else if (airCondDevs.get(i).getSelSpd()
                == UdpProPkt.E_AIR_CMD.e_air_spdAuto.getValue()) {
            viewHolder.mAirSpeed.setText("自动");
        }

        final ViewHolder finalViewHolder1 = viewHolder;
        viewHolder.mAirSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.mApplication.getSp().play(MyApplication.mApplication.getMusic(), 1, 1, 0, 0, 1);
                if (airCondDevs.get(i).getbOnOff() == 0) {
                    cmdValue = UdpProPkt.E_AIR_CMD.e_air_pwrOn.getValue();
                    int value = (modelValue << 5) | cmdValue;
                    SendDataUtil.controlDev(airCondDevs.get(i).getDev(), value);
                } else {
                    cmdValue = UdpProPkt.E_AIR_CMD.e_air_pwrOff.getValue();
                    int value = (modelValue << 5) | cmdValue;
                    SendDataUtil.controlDev(airCondDevs.get(i).getDev(), value);
                }
            }
        });
        viewHolder.mAirSetMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.mApplication.getSp().play(MyApplication.mApplication.getMusic(), 1, 1, 0, 0, 1);
                if (airCondDevs.get(i).getbOnOff() == 0) {
                    ToastUtil.showText("请先打开空调");
                    return;
                }
                initRadioPopupWindow(finalViewHolder1.mAirSetMode, i, modes);
                popupWindow.showAsDropDown(finalViewHolder1.mAirSetMode,0,0);

            }
        });
        viewHolder.mAirSetSpeedAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.mApplication.getSp().play(MyApplication.mApplication.getMusic(), 1, 1, 0, 0, 1);
                if (airCondDevs.get(i).getbOnOff() == 0) {
                    ToastUtil.showText("请先打开空调");
                    return;
                }
                if (airCondDevs.get(i).getSelSpd() == 5) {
                    ToastUtil.showText("不能再高了");
                    return;
                }
                cmdValue = airCondDevs.get(i).getSelSpd() + 1;
                int value = (modelValue << 5) | cmdValue;
                SendDataUtil.controlDev(airCondDevs.get(i).getDev(), value);
            }
        });
        viewHolder.mAirSetSpeedMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.mApplication.getSp().play(MyApplication.mApplication.getMusic(), 1, 1, 0, 0, 1);
                if (airCondDevs.get(i).getbOnOff() == 0) {
                    ToastUtil.showText("请先打开空调");
                    return;
                }
                if (airCondDevs.get(i).getSelSpd() == 2) {
                    ToastUtil.showText("不能再低了");
                    return;
                }
                cmdValue = airCondDevs.get(i).getSelSpd() - 1;
                int value = (modelValue << 5) | cmdValue;
                SendDataUtil.controlDev(airCondDevs.get(i).getDev(), value);
            }
        });

        viewHolder.mAirSetTempAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.mApplication.getSp().play(MyApplication.mApplication.getMusic(), 1, 1, 0, 0, 1);
                if (airCondDevs.get(i).getbOnOff() == 0) {
                    ToastUtil.showText("请先打开空调");
                    return;
                }
                if (airCondDevs.get(i).getSelTemp() == 30) {
                    ToastUtil.showText("不能再高了");
                    return;
                }

                cmdValue = airCondDevs.get(i).getSelTemp() + 1;
                int value = (modelValue << 5) | cmdValue;
                SendDataUtil.controlDev(airCondDevs.get(i).getDev(), value);
                finalViewHolder1.mAirTempSet.setText(cmdValue + "℃");
            }
        });
        viewHolder.mAirSetTempMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.mApplication.getSp().play(MyApplication.mApplication.getMusic(), 1, 1, 0, 0, 1);
                if (airCondDevs.get(i).getbOnOff() == 0) {
                    ToastUtil.showText("请先打开空调");
                    return;
                }
                if (airCondDevs.get(i).getSelTemp() == 16) {
                    ToastUtil.showText("不能再低了");
                    return;
                }
                MyApplication.mApplication.getSp().play(MyApplication.mApplication.getMusic(), 1, 1, 0, 0, 1);
                cmdValue = airCondDevs.get(i).getSelTemp() - 1;
                int value = (modelValue << 5) | cmdValue;
                SendDataUtil.controlDev(airCondDevs.get(i).getDev(), value);
                finalViewHolder1.mAirTempSet.setText(cmdValue + "℃");
            }
        });

        return view;
    }


    /**
     * 初始化自定义设备的状态以及设备PopupWindow
     */
    private void initRadioPopupWindow(final View view_parent, final int parentPosition, final List<String> text) {

        //获取自定义布局文件pop.xml的视图
        final View customView = view_parent.inflate(activity, R.layout.listview_popupwindow_equipment, null);
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
        popupWindow = new PopupWindow(customView, view_parent.getWidth(), 200);
        popupWindow.setContentView(customView);
        ListView list_pop = (ListView) customView.findViewById(R.id.popupWindow_equipment_lv);
        PopupWindowAdapter2 adapter = new PopupWindowAdapter2(text, activity);
        list_pop.setAdapter(adapter);
        list_pop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1://制冷
                        modelValue = UdpProPkt.E_AIR_MODE.e_air_cool.getValue();
                        break;
                    case 0://制热
                        modelValue = UdpProPkt.E_AIR_MODE.e_air_hot.getValue();
                        break;
                    case 2://除湿
                        modelValue = UdpProPkt.E_AIR_MODE.e_air_dry.getValue();
                        break;
                    case 3://扫风
                        modelValue = UdpProPkt.E_AIR_MODE.e_air_wind.getValue();
                        break;
                }
                int value = (modelValue << 5) | cmdValue;
                SendDataUtil.controlDev(airCondDevs.get(parentPosition).getDev(), value);
                popupWindow.dismiss();
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


    static class ViewHolder {
        View view;
        TextView mAirName;
        TextView mAirState;
        TextView mAirMode;
        ImageView mAirSwitch;
        TextView mAirSpeed;
        TextView mAirTempNow;
        TextView mAirTempSet;
        TextView mAirSetMode;
        TextView mAirSetSpeedAdd;
        TextView mAirSetSpeedMin;
        TextView mAirSetTempAdd;
        TextView mAirSetTempMin;

        ViewHolder(View view) {
            this.view = view;
            this.mAirName = (TextView) view.findViewById(R.id.air_name);
            this.mAirState = (TextView) view.findViewById(R.id.air_state);
            this.mAirMode = (TextView) view.findViewById(R.id.air_mode);
            this.mAirSwitch = (ImageView) view.findViewById(R.id.air_switch);
            this.mAirSpeed = (TextView) view.findViewById(R.id.air_speed);
            this.mAirTempNow = (TextView) view.findViewById(R.id.air_temp_now);
            this.mAirTempSet = (TextView) view.findViewById(R.id.air_temp_set);
            this.mAirSetMode = (TextView) view.findViewById(R.id.air_set_mode);
            this.mAirSetSpeedAdd = (TextView) view.findViewById(R.id.air_set_speed_add);
            this.mAirSetSpeedMin = (TextView) view.findViewById(R.id.air_set_speed_min);
            this.mAirSetTempAdd = (TextView) view.findViewById(R.id.air_set_temp_add);
            this.mAirSetTempMin = (TextView) view.findViewById(R.id.air_set_temp_min);
        }
    }
}
