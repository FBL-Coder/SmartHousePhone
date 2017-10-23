package cn.etsoft.smarthome.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.domain.GroupList_BoardDevData;
import cn.etsoft.smarthome.domain.GroupList_OutToInputData;
import cn.etsoft.smarthome.domain.PrintCmd;
import cn.etsoft.smarthome.domain.WareBoardKeyInput;
import cn.etsoft.smarthome.domain.WareDev;
import cn.etsoft.smarthome.utils.ToastUtil;

/**
 * Author：FBL  Time： 2017/10/20.
 * 问卷的显示适配器；
 * 这个适配器是类似QQ好友分组的数据适配；
 */
public class GroupList_OutToInputAdapter extends BaseExpandableListAdapter {

    private Activity mContext;
    private List<GroupList_OutToInputData> ListDatas;
    private PopupWindow popupWindow;
    private List<String> cmd_name = null;
    private List<PrintCmd> listData;

    public GroupList_OutToInputAdapter(Activity context, List<GroupList_OutToInputData> GroupListDatas) {
        mContext = context;
        ListDatas = GroupListDatas;
    }

    public void notifyDataSetChanged(List<GroupList_OutToInputData> GroupListDatas) {
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
        return ListDatas.get(groupPosition).getPrintCmds().size();
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
        if (ListDatas.get(groupPosition).getPrintCmds().get(childPosition) != null)
            return ListDatas.get(groupPosition).getPrintCmds().get(childPosition);
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
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder_Child viewHolder_child;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.equipmentdeploy_listview_item, null);
            viewHolder_child = new ViewHolder_Child(convertView);
            convertView.setTag(viewHolder_child);
        } else {
            viewHolder_child = (ViewHolder_Child) convertView.getTag();
        }

        listData = ListDatas.get(groupPosition).getPrintCmds();
        if (listData.get(childPosition).isSelect()) {
            viewHolder_child.mDevSelectIv.setImageResource(R.drawable.select_ok);
        } else viewHolder_child.mDevSelectIv.setImageResource(R.drawable.select_no);

        if (listData.get(childPosition).getDevType() == 0) {
            cmd_name = new ArrayList<>();
            cmd_name.add("未设置");
            cmd_name.add("开关");
            cmd_name.add("模式");
            cmd_name.add("风速");
            cmd_name.add("温度+");
            cmd_name.add("温度-");
        } else if (listData.get(childPosition).getDevType() == 3) {
            cmd_name = new ArrayList<>();
            cmd_name.add("未设置");
            cmd_name.add("打开");
            cmd_name.add("关闭");
            cmd_name.add("开关");
            cmd_name.add("变暗");
            cmd_name.add("变亮");
        } else if (listData.get(childPosition).getDevType() == 4) {
            cmd_name = new ArrayList<>();
            cmd_name.add("未设置");
            cmd_name.add("打开");
            cmd_name.add("关闭");
            cmd_name.add("停止");
            cmd_name.add("开关停");
        } else if (listData.get(childPosition).getDevType() == 7) {
            cmd_name = new ArrayList<>();
            cmd_name.add("未设置");
            cmd_name.add("打开");
            cmd_name.add("低风");
            cmd_name.add("中风");
            cmd_name.add("高风");
            cmd_name.add("自动");
            cmd_name.add("关闭");
        } else if (listData.get(childPosition).getDevType() == 9) {
            cmd_name = new ArrayList<>();
            cmd_name.add("未设置");
            cmd_name.add("打开");
            cmd_name.add("自动");
            cmd_name.add("关闭");
        } else {
            cmd_name = new ArrayList<>();
            cmd_name.add("未知");
        }
        viewHolder_child.mDevTvName.setText(listData.get(childPosition).getKeyname());

        for (int i = 0; i < MyApplication.getWareData().getKeyInputs().size(); i++) {
            if (MyApplication.getWareData().getKeyInputs().get(i).getCanCpuID()
                    .equals(listData.get(childPosition).getKeyboardid())){
                viewHolder_child.mCupName.setText(MyApplication.getWareData().getKeyInputs().get(i).getBoardName());
            }
        }

        try {
            viewHolder_child.mDevTvCMD.setText(cmd_name.get(listData.get(childPosition).getKey_cmd()));
        } catch (Exception e) {
        }
        viewHolder_child.mDevTvCMD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!listData.get(childPosition).isSelect()) {
                    ToastUtil.showText("请先选中按键");
                    return;
                }

                initPopupWindow(view, childPosition, cmd_name);
                popupWindow.showAsDropDown(view, 0, 0);
            }
        });

        viewHolder_child.mDevSelectIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listData.get(childPosition).isSelect()) {
                    listData.get(childPosition).setSelect(false);
                } else {
                    listData.get(childPosition).setSelect(true);
                }
                notifyDataSetChanged(ListDatas);
            }
        });
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
