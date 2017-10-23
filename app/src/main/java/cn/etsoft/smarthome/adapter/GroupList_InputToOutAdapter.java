package cn.etsoft.smarthome.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.domain.GroupList_BoardDevData;
import cn.etsoft.smarthome.domain.WareDev;
import cn.etsoft.smarthome.ui.Setting.Add_Dev_Activity;
import cn.etsoft.smarthome.ui.Setting.EquipmentDeployActivity;
import cn.etsoft.smarthome.utils.ToastUtil;
import cn.etsoft.smarthome.weidget.CustomDialog;

/**
 * Author：FBL  Time： 2017/10/20.
 * 按键配设备  双层适配器
 */
public class GroupList_InputToOutAdapter extends BaseExpandableListAdapter {

    private Activity mContext;
    private List<GroupList_BoardDevData> ListDatas;
    private PopupWindow popupWindow;
    private List<String> text_0, text_1, text_2, text_3, text_4, text_7, text_9;
    private int[] image = new int[]{R.drawable.kongtiao, R.drawable.tv_0, R.drawable.jidinghe,
            R.drawable.dengguang, R.drawable.chuanglian, R.drawable.freshair_close, R.drawable.floorheat_close};

    public GroupList_InputToOutAdapter(Activity context, List<GroupList_BoardDevData> GroupListDatas) {
        mContext = context;
        ListDatas = GroupListDatas;
    }

    public void notifyDataSetChanged(List<GroupList_BoardDevData> GroupListDatas) {
        ListDatas = GroupListDatas;
        super.notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        //父条目个数
        return ListDatas.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        //子条目个数
        return ListDatas.get(groupPosition).getDevs().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        //父条目对象
        if (ListDatas.get(groupPosition) != null)
            return ListDatas.get(groupPosition);
        else return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        //子条目对象
        if (ListDatas.get(groupPosition).getDevs().get(childPosition) != null)
            return ListDatas.get(groupPosition).getDevs().get(childPosition);
        else return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        //父条目ID
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        //子条目ID
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_grouplist_father_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mGrouplistIvIcon.setImageResource(R.drawable.outboard);
        viewHolder.mGrouplistIvIcon.setPadding(10, 10, 10, 10);
        viewHolder.mGrouplistTvTitle.setText(ListDatas.get(groupPosition).getBoardName());
        viewHolder.mGrouplistTvTest.setVisibility(View.INVISIBLE);
        viewHolder.mGrouplistIvEditname.setVisibility(View.INVISIBLE);
        viewHolder.mGrouplistIvSet.setVisibility(View.INVISIBLE);
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder_Child viewHolder_child;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.equipmentdeploy_listview_item, null);
            viewHolder_child = new ViewHolder_Child(convertView);
            convertView.setTag(viewHolder_child);
        } else {
            viewHolder_child = (ViewHolder_Child) convertView.getTag();
        }

        if (ListDatas.get(groupPosition).getDevs().get(childPosition).isSelect()) {
            viewHolder_child.mDevSelectIv.setImageResource(R.drawable.select_ok);
        } else viewHolder_child.mDevSelectIv.setImageResource(R.drawable.select_no);
        if (ListDatas.get(groupPosition).getDevs().get(childPosition).getType() == 0) {//空调
            text_0 = new ArrayList<>();
            text_0.add("未设置");
            text_0.add("开关");
            text_0.add("模式");
            text_0.add("风速");
            text_0.add("温度+");
            text_0.add("温度-");
            viewHolder_child.mDevTvCMD.setText(text_0.get(ListDatas.get(groupPosition).getDevs().get(childPosition).getCmd()));
            viewHolder_child.mDevIv.setImageResource(image[0]);
            viewHolder_child.mDevTvName.setText(ListDatas.get(groupPosition).getDevs().get(childPosition).getDevName());
        } else if (ListDatas.get(groupPosition).getDevs().get(childPosition).getType() == 1) {//电视
            viewHolder_child.mDevIv.setImageResource(image[1]);
            viewHolder_child.mDevTvName.setText(ListDatas.get(groupPosition).getDevs().get(childPosition).getDevName());
            viewHolder_child.mDevTvCMD.setText("无操作");
            text_1 = new ArrayList<>();
            text_1.add("无操作");
        } else if (ListDatas.get(groupPosition).getDevs().get(childPosition).getType() == 2) {//机顶盒
            viewHolder_child.mDevIv.setImageResource(image[2]);
            viewHolder_child.mDevTvName.setText(ListDatas.get(groupPosition).getDevs().get(childPosition).getDevName());
            viewHolder_child.mDevTvCMD.setText("无操作");
            text_2 = new ArrayList<>();
            text_2.add("无操作");
        } else if (ListDatas.get(groupPosition).getDevs().get(childPosition).getType() == 3) {//灯光
            text_3 = new ArrayList<>();
            text_3.add("未设置");
            text_3.add("打开");
            text_3.add("关闭");
            text_3.add("开关");
            text_3.add("变暗");
            text_3.add("变亮");
            viewHolder_child.mDevTvCMD.setText(text_3.get(ListDatas.get(groupPosition).getDevs().get(childPosition).getCmd()));
            viewHolder_child.mDevIv.setImageResource(image[3]);
            viewHolder_child.mDevTvName.setText(ListDatas.get(groupPosition).getDevs().get(childPosition).getDevName());
        } else if (ListDatas.get(groupPosition).getDevs().get(childPosition).getType() == 4) {//窗帘
            text_4 = new ArrayList<>();
            text_4.add("未设置");
            text_4.add("打开");
            text_4.add("关闭");
            text_4.add("停止");
            text_4.add("开关停");
            viewHolder_child.mDevTvCMD.setText(text_4.get(ListDatas.get(groupPosition).getDevs().get(childPosition).getCmd()));
            viewHolder_child.mDevIv.setImageResource(image[4]);
            viewHolder_child.mDevTvName.setText(ListDatas.get(groupPosition).getDevs().get(childPosition).getDevName());
        } else if (ListDatas.get(groupPosition).getDevs().get(childPosition).getType() == 7) {//新风
            text_7 = new ArrayList<>();
            text_7.add("未设置");
            text_7.add("打开");
            text_7.add("高风");
            text_7.add("中风");
            text_7.add("自动");
            text_7.add("关闭");
            viewHolder_child.mDevTvCMD.setText(text_7.get(ListDatas.get(groupPosition).getDevs().get(childPosition).getCmd()));
            viewHolder_child.mDevIv.setImageResource(image[5]);
            viewHolder_child.mDevTvName.setText(ListDatas.get(groupPosition).getDevs().get(childPosition).getDevName());
        } else if (ListDatas.get(groupPosition).getDevs().get(childPosition).getType() == 9) {//地暖
            text_9 = new ArrayList<>();
            text_9.add("未设置");
            text_9.add("打开");
            text_9.add("自动");
            text_9.add("关闭");
            viewHolder_child.mDevTvCMD.setText(text_9.get(ListDatas.get(groupPosition).getDevs().get(childPosition).getCmd()));
            viewHolder_child.mDevIv.setImageResource(image[6]);
            viewHolder_child.mDevTvName.setText(ListDatas.get(groupPosition).getDevs().get(childPosition).getDevName());
        }
        viewHolder_child.mDevSelectIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ListDatas.get(groupPosition).getDevs().get(childPosition).isSelect()) {
                    ListDatas.get(groupPosition).getDevs().get(childPosition).setSelect(false);
                } else {
                    ListDatas.get(groupPosition).getDevs().get(childPosition).setSelect(true);
                }
                notifyDataSetChanged(ListDatas);
            }
        });
        viewHolder_child.mDevTvCMD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ListDatas.get(groupPosition).getDevs().get(childPosition).isSelect()) {
                    if (ListDatas.get(groupPosition).getDevs().get(childPosition).getType() == 0)
                        initPopupWindow(view, ListDatas.get(groupPosition).getDevs().get(childPosition), text_0);
                    if (ListDatas.get(groupPosition).getDevs().get(childPosition).getType() == 1)
                        initPopupWindow(view, ListDatas.get(groupPosition).getDevs().get(childPosition), text_1);
                    if (ListDatas.get(groupPosition).getDevs().get(childPosition).getType() == 2)
                        initPopupWindow(view, ListDatas.get(groupPosition).getDevs().get(childPosition), text_2);
                    if (ListDatas.get(groupPosition).getDevs().get(childPosition).getType() == 3)
                        initPopupWindow(view, ListDatas.get(groupPosition).getDevs().get(childPosition), text_3);
                    if (ListDatas.get(groupPosition).getDevs().get(childPosition).getType() == 4)
                        initPopupWindow(view, ListDatas.get(groupPosition).getDevs().get(childPosition), text_4);
                    if (ListDatas.get(groupPosition).getDevs().get(childPosition).getType() == 7)
                        initPopupWindow(view, ListDatas.get(groupPosition).getDevs().get(childPosition), text_7);
                    if (ListDatas.get(groupPosition).getDevs().get(childPosition).getType() == 9)
                        initPopupWindow(view, ListDatas.get(groupPosition).getDevs().get(childPosition), text_9);
                    popupWindow.showAsDropDown(view, 0, 0);
                } else ToastUtil.showText("请先选中设备");
            }
        });
        viewHolder_child.mCupName.setText(ListDatas.get(groupPosition).getDevs().get(childPosition).getRoomName());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * 初始化自定义设备的状态以及设备PopupWindow
     */
    private void initPopupWindow(final View view_parent,
                                 final WareDev dev,
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
                dev.setCmd(position);
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
        ImageView mGrouplistIvIcon;
        ImageView mGrouplistIvSet;
        TextView mGrouplistTvTitle;
        TextView mGrouplistTvTest;
        ImageView mGrouplistIvEditname;

        ViewHolder(View view) {
            this.view = view;
            this.mGrouplistIvIcon = (ImageView) view.findViewById(R.id.grouplist_iv_icon);
            this.mGrouplistIvSet = (ImageView) view.findViewById(R.id.grouplist_iv_set);
            this.mGrouplistTvTitle = (TextView) view.findViewById(R.id.grouplist_tv_title);
            this.mGrouplistTvTest = (TextView) view.findViewById(R.id.grouplist_tv_test);
            this.mGrouplistIvEditname = (ImageView) view.findViewById(R.id.grouplist_iv_editname);
        }
    }

    class ViewHolder_Child {
        View view;
        ImageView mDevSelectIv;
        ImageView mDevIv;
        TextView mCupName;
        TextView mDevTvName;
        TextView mDevTvCMD;

        ViewHolder_Child(View view) {
            this.view = view;
            this.mDevSelectIv = (ImageView) view.findViewById(R.id.Dev_select_iv);
            this.mDevIv = (ImageView) view.findViewById(R.id.Dev_iv);
            this.mCupName = (TextView) view.findViewById(R.id.CupName);
            this.mDevTvName = (TextView) view.findViewById(R.id.Dev_tv_Name);
            this.mDevTvCMD = (TextView) view.findViewById(R.id.Dev_tv_CMD);
        }
    }
}
