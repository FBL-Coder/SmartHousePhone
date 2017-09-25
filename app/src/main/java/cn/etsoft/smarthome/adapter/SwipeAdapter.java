package cn.etsoft.smarthome.adapter;

import android.content.Context;
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
import cn.etsoft.smarthome.domain.Iclick_Tag;
import cn.etsoft.smarthome.domain.WareAirCondDev;
import cn.etsoft.smarthome.domain.WareCurtain;
import cn.etsoft.smarthome.domain.WareDev;
import cn.etsoft.smarthome.domain.WareFloorHeat;
import cn.etsoft.smarthome.domain.WareFreshAir;
import cn.etsoft.smarthome.domain.WareKeyOpItem;
import cn.etsoft.smarthome.domain.WareLight;
import cn.etsoft.smarthome.domain.WareSetBox;
import cn.etsoft.smarthome.domain.WareTv;
import cn.etsoft.smarthome.utils.ToastUtil;

/**
 * Created by Say GoBay on 2016/8/30.
 * 按键配置中的详情列表 适配器
 */
public class SwipeAdapter extends BaseAdapter {
    private Context mContext = null;
    private List<WareDev> devs;
    private PopupWindow popupWindow;
    private List<String> text_0, text_1, text_2, text_3, text_4, text_7, text_9;
    private int[] image = new int[]{R.drawable.kongtiao, R.drawable.tv_0, R.drawable.jidinghe,
            R.drawable.dengguang, R.drawable.chuanglian, R.drawable.freshair_close, R.drawable.floorheat_close};


    public SwipeAdapter(Context context, List<WareDev> devs) {
        this.mContext = context;
        this.devs = devs;
    }

    @Override
    public int getCount() {
        if (devs != null)
            return devs.size();
        else
            return 0;
    }

    public void notifyDataSetChanged(List<WareDev> devs) {
        this.devs = devs;
        super.notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        if (devs != null)
            return devs.get(position);
        else
            return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View contentView, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        if (contentView == null) {
            contentView = LayoutInflater.from(mContext).inflate(R.layout.equipmentdeploy_listview_item, null);
            viewHolder = new ViewHolder(contentView);
            contentView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) contentView.getTag();
        }
        if (devs.get(position).isSelect()) {
            viewHolder.mDevSelectIv.setImageResource(R.drawable.select_ok);
        } else viewHolder.mDevSelectIv.setImageResource(R.drawable.select_no);
        if (devs.get(position).getType() == 0) {//空调
            text_0 = new ArrayList<>();
            text_0.add("未设置");
            text_0.add("开关");
            text_0.add("模式");
            text_0.add("风速");
            text_0.add("温度+");
            text_0.add("温度-");
            viewHolder.mDevTvCMD.setText(text_0.get(devs.get(position).getCmd()));
            viewHolder.mDevIv.setImageResource(image[0]);
            viewHolder.mDevTvName.setText(devs.get(position).getDevName());
        } else if (devs.get(position).getType() == 1) {//电视
            viewHolder.mDevIv.setImageResource(image[1]);
            viewHolder.mDevTvName.setText(devs.get(position).getDevName());
            viewHolder.mDevTvCMD.setText("无操作");
            text_1 = new ArrayList<>();
            text_1.add("无操作");
        } else if (devs.get(position).getType() == 2) {//机顶盒
            viewHolder.mDevIv.setImageResource(image[2]);
            viewHolder.mDevTvName.setText(devs.get(position).getDevName());
            viewHolder.mDevTvCMD.setText("无操作");
            text_2 = new ArrayList<>();
            text_2.add("无操作");
        } else if (devs.get(position).getType() == 3) {//灯光
            text_3 = new ArrayList<>();
            text_3.add("未设置");
            text_3.add("打开");
            text_3.add("关闭");
            text_3.add("开关");
            text_3.add("变暗");
            text_3.add("变亮");
            viewHolder.mDevTvCMD.setText(text_3.get(devs.get(position).getCmd()));
            viewHolder.mDevIv.setImageResource(image[3]);
            viewHolder.mDevTvName.setText(devs.get(position).getDevName());
        } else if (devs.get(position).getType() == 4) {//窗帘
            text_4 = new ArrayList<>();
            text_4.add("未设置");
            text_4.add("打开");
            text_4.add("关闭");
            text_4.add("停止");
            text_4.add("开关停");
            viewHolder.mDevTvCMD.setText(text_4.get(devs.get(position).getCmd()));
            viewHolder.mDevIv.setImageResource(image[4]);
            viewHolder.mDevTvName.setText(devs.get(position).getDevName());
        } else if (devs.get(position).getType() == 7) {//新风
            text_7 = new ArrayList<>();
            text_7.add("未设置");
            text_7.add("打开");
            text_7.add("高风");
            text_7.add("中风");
            text_7.add("自动");
            text_7.add("关闭");
            viewHolder.mDevTvCMD.setText(text_7.get(devs.get(position).getCmd()));
            viewHolder.mDevIv.setImageResource(image[5]);
            viewHolder.mDevTvName.setText(devs.get(position).getDevName());
        } else if (devs.get(position).getType() == 9) {//地暖
            text_9 = new ArrayList<>();
            text_9.add("未设置");
            text_9.add("打开");
            text_9.add("自动");
            text_9.add("关闭");
            viewHolder.mDevTvCMD.setText(text_9.get(devs.get(position).getCmd()));
            viewHolder.mDevIv.setImageResource(image[6]);
            viewHolder.mDevTvName.setText(devs.get(position).getDevName());
        }
        viewHolder.mDevSelectIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (devs.get(position).isSelect()) {
                    devs.get(position).setSelect(false);
                } else {
                    devs.get(position).setSelect(true);
                }
                notifyDataSetChanged(devs);
            }
        });
        viewHolder.mDevTvCMD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (devs.get(position).isSelect()) {
                    if (devs.get(position).getType() == 0)
                        initPopupWindow(view, position, text_0);
                    if (devs.get(position).getType() == 1)
                        initPopupWindow(view, position, text_1);
                    if (devs.get(position).getType() == 2)
                        initPopupWindow(view, position, text_2);
                    if (devs.get(position).getType() == 3)
                        initPopupWindow(view, position, text_3);
                    if (devs.get(position).getType() == 4)
                        initPopupWindow(view, position, text_4);
                    if (devs.get(position).getType() == 7)
                        initPopupWindow(view, position, text_7);
                    if (devs.get(position).getType() == 9)
                        initPopupWindow(view, position, text_9);
                    popupWindow.showAsDropDown(view, 0, 0);
                } else ToastUtil.showText("请先选中设备");
            }
        });
        return contentView;
    }


    /**
     * 初始化自定义设备的状态以及设备PopupWindow
     */
    private void initPopupWindow(final View view_parent,
                                 final int position_parent,
                                 final List<String> text) {
        //获取自定义布局文件pop.xml的视图
        final View customView = LayoutInflater.from(mContext).inflate(R.layout.popupwindow_equipment_listview, null);
        customView.setBackgroundResource(R.drawable.selectbg);

        // 创建PopupWindow实例
        popupWindow = new PopupWindow(customView.findViewById(R.id.popupWindow_equipment_sv),
                view_parent.getWidth(), 300);

        popupWindow.setContentView(customView);
        ListView list_pop = (ListView) customView.findViewById(R.id.popupWindow_equipment_lv);
        PopupWindowAdapter adapter = new PopupWindowAdapter(text, mContext);
        list_pop.setAdapter(adapter);
        list_pop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view_parent;
                tv.setText(text.get(position));
                devs.get(position_parent).setCmd(position);
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

    class ViewHolder {
        View view;
        ImageView mDevSelectIv;
        ImageView mDevIv;
        TextView mDevTvName;
        TextView mDevTvCMD;

        ViewHolder(View view) {
            this.view = view;
            this.mDevSelectIv = (ImageView) view.findViewById(R.id.Dev_select_iv);
            this.mDevIv = (ImageView) view.findViewById(R.id.Dev_iv);
            this.mDevTvName = (TextView) view.findViewById(R.id.Dev_tv_Name);
            this.mDevTvCMD = (TextView) view.findViewById(R.id.Dev_tv_CMD);
        }
    }
}

