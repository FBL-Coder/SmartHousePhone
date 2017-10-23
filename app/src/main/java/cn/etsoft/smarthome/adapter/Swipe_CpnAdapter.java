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
import cn.etsoft.smarthome.domain.PrintCmd;
import cn.etsoft.smarthome.utils.ToastUtil;

/**
 * Created by Say GoBay on 2016/8/30.
 * 输出设置  适配器
 */
public class Swipe_CpnAdapter extends BaseAdapter {
    private Context mContext = null;
    private PopupWindow popupWindow;
    private List<PrintCmd> listData;
    private List<String> cmd_name = null;

    public Swipe_CpnAdapter(Context context, List<PrintCmd> listData) {
        this.mContext = context;
        this.listData = listData;
    }

    @Override
    public int getCount() {
        if (listData != null)
            return listData.size();
        else
            return 0;
    }

    public void notifyDataSetChanged(List<PrintCmd> listData) {
        this.listData = listData;
        super.notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        if (listData != null)
            return listData.get(position);
        else
            return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View contentView, ViewGroup arg2) {
        ViewHolder viewHolder;
        if (contentView == null) {
            contentView = LayoutInflater.from(mContext).inflate(R.layout.equipmentdeploy_listview_item, null);
            viewHolder = new ViewHolder(contentView);
            contentView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) contentView.getTag();
        }

        if (listData.get(position).isSelect()) {
            viewHolder.mDevSelectIv.setImageResource(R.drawable.select_ok);
        } else viewHolder.mDevSelectIv.setImageResource(R.drawable.select_no);

        if (listData.get(position).getDevType() == 0) {
            cmd_name = new ArrayList<>();
            cmd_name.add("未设置");
            cmd_name.add("开关");
            cmd_name.add("模式");
            cmd_name.add("风速");
            cmd_name.add("温度+");
            cmd_name.add("温度-");
        } else if (listData.get(position).getDevType() == 3) {
            cmd_name = new ArrayList<>();
            cmd_name.add("未设置");
            cmd_name.add("打开");
            cmd_name.add("关闭");
            cmd_name.add("开关");
            cmd_name.add("变暗");
            cmd_name.add("变亮");
        } else if (listData.get(position).getDevType() == 4) {
            cmd_name = new ArrayList<>();
            cmd_name.add("未设置");
            cmd_name.add("打开");
            cmd_name.add("关闭");
            cmd_name.add("停止");
            cmd_name.add("开关停");
        } else if (listData.get(position).getDevType() == 7) {
            cmd_name = new ArrayList<>();
            cmd_name.add("未设置");
            cmd_name.add("打开");
            cmd_name.add("低风");
            cmd_name.add("中风");
            cmd_name.add("高风");
            cmd_name.add("自动");
            cmd_name.add("关闭");
        } else if (listData.get(position).getDevType() == 9) {
            cmd_name = new ArrayList<>();
            cmd_name.add("未设置");
            cmd_name.add("打开");
            cmd_name.add("自动");
            cmd_name.add("关闭");
        } else {
            cmd_name = new ArrayList<>();
            cmd_name.add("未知");
        }
        viewHolder.mDevTvName.setText(listData.get(position).getKeyname());

        for (int i = 0; i < MyApplication.getWareData().getKeyInputs().size(); i++) {
            if (MyApplication.getWareData().getKeyInputs().get(i).getCanCpuID()
                    .equals(listData.get(position).getKeyboardid())){
                viewHolder.mCupName.setText(MyApplication.getWareData().getKeyInputs().get(i).getBoardName());
            }
        }


        try {
            viewHolder.mDevTvCMD.setText(cmd_name.get(listData.get(position).getKey_cmd()));
        } catch (Exception e) {
        }
        viewHolder.mDevTvCMD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!listData.get(position).isSelect()) {
                    ToastUtil.showText("请先选中按键");
                    return;
                }

                initPopupWindow(view, position, cmd_name);
                popupWindow.showAsDropDown(view, 0, 0);
            }
        });

        viewHolder.mDevSelectIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listData.get(position).isSelect()) {
                    listData.get(position).setSelect(false);
                } else {
                    listData.get(position).setSelect(true);
                }
                notifyDataSetChanged(listData);
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
                listData.get(position_parent).setKey_cmd(position);
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
        TextView mCupName;
        TextView mDevTvName;
        TextView mDevTvCMD;

        ViewHolder(View view) {
            this.view = view;
            this.mDevSelectIv = (ImageView) view.findViewById(R.id.Dev_select_iv);
            this.mDevIv = (ImageView) view.findViewById(R.id.Dev_iv);
            this.mDevTvName = (TextView) view.findViewById(R.id.Dev_tv_Name);
            this.mCupName = (TextView) view.findViewById(R.id.CupName);
            this.mDevTvCMD = (TextView) view.findViewById(R.id.Dev_tv_CMD);
        }
    }
}

