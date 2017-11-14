package cn.etsoft.smarthome.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.List;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.domain.GroupList_BoardDevData;
import cn.etsoft.smarthome.domain.UdpProPkt;
import cn.etsoft.smarthome.domain.WareBoardChnout;
import cn.etsoft.smarthome.domain.WareDev;
import cn.etsoft.smarthome.pullmi.common.CommonUtils;
import cn.etsoft.smarthome.ui.Setting.Add_Dev_Activity;
import cn.etsoft.smarthome.ui.Setting.EquipmentDeployActivity;
import cn.etsoft.smarthome.utils.ToastUtil;
import cn.etsoft.smarthome.weidget.CustomDialog;

/**
 * Author：FBL  Time： 2017/10/20.
 * 问卷的显示适配器；
 * 这个适配器是类似QQ好友分组的数据适配；
 */
public class GroupList_OutAdapter extends BaseExpandableListAdapter {

    private Activity mContext;
    private List<GroupList_BoardDevData> ListDatas;
    private CustomDialog dialog;
    private EditText mSceneEtName;
    private Button mSceneBtnSure, mSceneBtnCancel;
    private TextView mDialogTitle, mDialogTitleName, mDialoHelp;

    public GroupList_OutAdapter(Activity context, List<GroupList_BoardDevData> GroupListDatas) {
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
        viewHolder.mGrouplistTvTest.setText("呼叫");
        viewHolder.mGrouplistIvEditname.setImageResource(R.drawable.edit_roomname);
        final int group = groupPosition;
        viewHolder.mGrouplistIvEditname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //编辑输出板名称点击事件
                initDialogView(group);
            }
        });
        viewHolder.mGrouplistTvTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 点击发送呼叫指令
            }
        });

        viewHolder.mGrouplistIvSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击添加设备
                Intent intent = new Intent(mContext, Add_Dev_Activity.class);
                intent.putExtra("CancpuID",ListDatas.get(group).getDevUnitID());
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_grouplist_child_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final WareDev dev = ListDatas.get(groupPosition).getDevs().get(childPosition);
        viewHolder.mGrouplistTvTitle.setText(dev.getDevName());
        viewHolder.mGrouplistIvEditname.setImageResource(R.drawable.config_icon);


        if (dev.getType() == 0) {
            viewHolder.mGrouplistIvIcon.setImageResource(R.drawable.aircood_icon);
        } else if (dev.getType() == 1) {
            viewHolder.mGrouplistIvIcon.setImageResource(R.drawable.tv_icon);
        } else if (dev.getType() == 2) {
            viewHolder.mGrouplistIvIcon.setImageResource(R.drawable.stb_icon);
        } else if (dev.getType() == 3) {
            viewHolder.mGrouplistIvIcon.setImageResource(R.drawable.light_icon);
        } else if (dev.getType() == 4) {
            viewHolder.mGrouplistIvIcon.setImageResource(R.drawable.curtains_icon);
        } else if (dev.getType() == 5) {
            viewHolder.mGrouplistIvIcon.setImageResource(R.drawable.aircood_icon);
        } else if (dev.getType() == 6) {
            viewHolder.mGrouplistIvIcon.setImageResource(R.drawable.aircood_icon);
        } else if (dev.getType() == 7) {
            viewHolder.mGrouplistIvIcon.setImageResource(R.drawable.freshair_icon);
        } else if (dev.getType() == 8) {
            viewHolder.mGrouplistIvIcon.setImageResource(R.drawable.aircood_icon);
        } else if (dev.getType() == 9) {
            viewHolder.mGrouplistIvIcon.setImageResource(R.drawable.floorheat_icon);
        }
        viewHolder.mGrouplistTvTest.setVisibility(View.VISIBLE);
        viewHolder.mGrouplistTvTest.setText(ListDatas
                .get(groupPosition).getDevs().get(childPosition).getRoomName());
        viewHolder.mGrouplistTvTest.setBackgroundColor(Color.TRANSPARENT);
        viewHolder.mGrouplistTvTest.setTextColor(Color.BLACK);

        viewHolder.mGrouplistIvEditname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //设备点击进入设备配按键
                Intent intent = new Intent(mContext, EquipmentDeployActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", dev.getDevName());
                bundle.putString("uid", dev.getCanCpuId());
                bundle.putInt("devType", dev.getType());
                bundle.putInt("devID", dev.getDevId());
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * 修改输出版名称Dialog
     */
    private void initDialogView(final int groupPosition) {
        dialog = new CustomDialog(mContext, R.style.customDialog, R.layout.dialog_sceneset);
        dialog.show();
        mDialoHelp = (TextView) dialog.findViewById(R.id.Dialog_help);
        mDialogTitle = (TextView) dialog.findViewById(R.id.Dialog_title);
        mDialogTitle.setText("修改名称");
        mDialogTitleName = (TextView) dialog.findViewById(R.id.Dialog_title_name);
        mDialogTitleName.setText("名称：");
        mSceneEtName = (EditText) dialog.findViewById(R.id.scene_et_name);
        mSceneEtName.setHint("请输入新名称");
        mSceneBtnSure = (Button) dialog.findViewById(R.id.scene_btn_sure);
        mSceneBtnCancel = (Button) dialog.findViewById(R.id.scene_btn_cancel);
        mSceneBtnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if ("".equals(mSceneEtName.getText().toString()) || mSceneEtName.getText().toString().length() > 6) {
                    ToastUtil.showText("输入名称最多6个字符");
                    return;
                } else {
                    //修改按键板名称逻辑
                    String name = mSceneEtName.getText().toString();
                    WareBoardChnout boardChnout = new WareBoardChnout();
                    for (int i = 0; i < MyApplication.getWareData().getBoardChnouts().size(); i++) {
                        if (ListDatas.get(groupPosition).getDevUnitID()
                                .equals(MyApplication.getWareData().getBoardChnouts().get(i).getCanCpuID())) {
                            try {
                                boardChnout.setBoardName(CommonUtils.bytesToHexString(name.getBytes("GB2312")));
                                boardChnout.setBoardType(MyApplication.getWareData().getBoardChnouts().get(i).getBoardType());
                                boardChnout.setbOnline(MyApplication.getWareData().getBoardChnouts().get(i).getbOnline());
                                boardChnout.setChnCnt(MyApplication.getWareData().getBoardChnouts().get(i).getChnCnt());
                                boardChnout.setChnName(MyApplication.getWareData().getBoardChnouts().get(i).getChnName());
                                boardChnout.setCanCpuID(MyApplication.getWareData().getBoardChnouts().get(i).getCanCpuID());
                            } catch (UnsupportedEncodingException e) {
                                Log.i("转码Name", "onClick: " + e);
                            }
                        }
                    }
                    if (boardChnout == null)
                        return;
                    Gson gson = new Gson();
                    String data = gson.toJson(boardChnout);
                    String str = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
                            ",\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_editBoards.getValue() +
                            ",\"subType1\":0" +
                            ",\"subType2\":0" +
                            ",\"chnout_rows\":[" + data + "]" +
                            ",\"board\":1}";

                    Log.i("修改输出板名称    ", str);
                    MyApplication.mApplication.showLoadDialog(mContext);
                    MyApplication.mApplication.getUdpServer().send(str, 9);
                }
            }
        });
        mSceneBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        mDialoHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    class ViewHolder {
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
}
