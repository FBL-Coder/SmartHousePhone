package cn.etsoft.smarthome.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.linphone.mediastream.Log;

import java.io.UnsupportedEncodingException;
import java.util.List;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.domain.UdpProPkt;
import cn.etsoft.smarthome.domain.WareBoardKeyInput;
import cn.etsoft.smarthome.pullmi.common.CommonUtils;
import cn.etsoft.smarthome.ui.Setting.AddEquipmentControlActivity;
import cn.etsoft.smarthome.utils.ToastUtil;
import cn.etsoft.smarthome.weidget.CustomDialog;

/**
 * Author：FBL  Time： 2017/10/20.
 * 问卷的显示适配器；
 * 这个适配器是类似QQ好友分组的数据适配；
 */
public class GroupList_InputAdapter extends BaseExpandableListAdapter {

    private Activity mContext;
    private List<WareBoardKeyInput> ListDatas;
    private CustomDialog dialog;
    private EditText mEtBoardName, mEtBoardKeycut;
    private Button mBtnSure, mBtnCancel;
    private TextView mDialogTitle, mDialogTitleName, mDialogSmallTitleName;
    private LinearLayout mLinearLayout;

    public GroupList_InputAdapter(Activity context, List<WareBoardKeyInput> GroupListDatas) {
        mContext = context;
        ListDatas = GroupListDatas;
    }

    @Override
    public int getGroupCount() {
        //父条目个数
        return ListDatas.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        //子条目个数
        return ListDatas.get(groupPosition).getKeyCnt() < ListDatas.get(groupPosition).getKeyName().length ?
                ListDatas.get(groupPosition).getKeyCnt() : ListDatas.get(groupPosition).getKeyName().length;

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
        if (ListDatas.get(groupPosition).getKeyName()[childPosition] != null)
            return ListDatas.get(groupPosition).getKeyName()[childPosition];
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
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_grouplist_father_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mGrouplistIvIcon.setImageResource(R.drawable.inputborad);
        viewHolder.mGrouplistIvIcon.setPadding(10, 10, 10, 10);
        viewHolder.mGrouplistTvTitle.setText(ListDatas.get(groupPosition).getBoardName());
        viewHolder.mGrouplistTvTest.setText("呼叫");
        viewHolder.mGrouplistIvEditname.setImageResource(R.drawable.edit_roomname);
        viewHolder.mGrouplistIvSet.setVisibility(View.GONE);
        viewHolder.mGrouplistIvEditname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //编辑输出板名称点击事件
                GroupDialogView(groupPosition);
            }
        });
        viewHolder.mGrouplistTvTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 点击发送呼叫指令
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_grouplist_child_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mGrouplistTvTest.setVisibility(View.VISIBLE);
        viewHolder.mGrouplistTvTest.setText("修改键名");
        viewHolder.mGrouplistTvTest.setTextSize(12);
        viewHolder.mGrouplistTvTest.setBackgroundColor(Color.TRANSPARENT);
        viewHolder.mGrouplistTvTest.setTextColor(Color.RED);

        viewHolder.mGrouplistTvTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //修改键名
                ChlidDialogView(groupPosition, childPosition);
            }
        });

        viewHolder.mGrouplistTvTitle.setText(ListDatas.get(groupPosition).getKeyName()[childPosition]);
        viewHolder.mGrouplistIvEditname.setImageResource(R.drawable.config_icon);
        viewHolder.mGrouplistIvIcon.setImageResource(R.drawable.key);

        final int fatherPosition = groupPosition;
        final int childerPosition = childPosition;
        viewHolder.mGrouplistIvEditname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //设备点击进入设备配按键
                Intent intent = new Intent(mContext, AddEquipmentControlActivity.class);
                intent.putExtra("key_index", childerPosition);
                intent.putExtra("title", ListDatas.get(fatherPosition).getKeyName()[childerPosition]);
                intent.putExtra("uid", ListDatas.get(fatherPosition).getCanCpuID());
                intent.putExtras(intent);
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
    private void GroupDialogView(final int groupposition) {
        dialog = new CustomDialog(mContext, R.style.customDialog, R.layout.dialog_inputboard);
        dialog.show();
        mDialogTitle = (TextView) dialog.findViewById(R.id.Dialog_title);
        mDialogTitle.setText("修改输入板名称");
        mDialogTitleName = (TextView) dialog.findViewById(R.id.Dialog_title_name);
        mDialogTitleName.setText("输入板名称：");
        mEtBoardName = (EditText) dialog.findViewById(R.id.edit_title_name);
        mEtBoardName.setHint("请输入新名称");
        mDialogSmallTitleName = (TextView) dialog.findViewById(R.id.Dialog_sTitle);
        mDialogSmallTitleName.setText("有效按键");
        mEtBoardKeycut = (EditText) dialog.findViewById(R.id.stitle_name);
        mEtBoardKeycut.setInputType(InputType.TYPE_CLASS_NUMBER);
        mEtBoardKeycut.setMaxEms(1);
        mEtBoardKeycut.setHint("请输入有效按键数");
        mEtBoardKeycut.setText(ListDatas.get(groupposition).getKeyCnt() + "");
        mEtBoardName.setText(ListDatas.get(groupposition).getBoardName());
        mBtnSure = (Button) dialog.findViewById(R.id.btn_sure);
        mBtnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        mBtnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if ("".equals(mEtBoardName.getText().toString()) || mEtBoardName.getText().toString().length() > 6) {
                    ToastUtil.showText("输入名称最多6个字符");
                    return;
                } else {
                    //修改按键板名称逻辑
                    String name = mEtBoardName.getText().toString();
                    WareBoardKeyInput keyInput = new WareBoardKeyInput();

                    if ("".equals(mEtBoardKeycut.getText().toString()) || Integer.valueOf(mEtBoardKeycut.getText().toString()) > 8) {
                        ToastUtil.showText("有效按键个数不能为空或者大于8");
                        return;
                    }
                    try {
                        keyInput.setBoardName(CommonUtils.bytesToHexString(name.getBytes("GB2312")));
                        keyInput.setBoardType(ListDatas.get(groupposition).getBoardType());
                        keyInput.setbResetKey(ListDatas.get(groupposition).getbResetKey());
                        keyInput.setCanCpuID(ListDatas.get(groupposition).getCanCpuID());
                        keyInput.setKeyAllCtrlType(ListDatas.get(groupposition).getKeyAllCtrlType());

                        int keyCut = Integer.valueOf(mEtBoardKeycut.getText().toString());
                        String[] nameKey = new String[keyCut];

                        for (int i = 0; i < keyCut; i++) {
                            if (i < ListDatas.get(groupposition).getKeyName().length) {
                                if ("".equals(ListDatas.get(groupposition).getKeyName()[i])) {
                                    nameKey[i] = CommonUtils.bytesToHexString("未命名".getBytes("GB2312"));
                                } else
                                    nameKey[i] = CommonUtils.bytesToHexString(ListDatas.get(groupposition).getKeyName()[i].getBytes("GB2312"));
                            } else {
                                nameKey[i] = CommonUtils.bytesToHexString("未命名".getBytes("GB2312"));
                            }
                        }
                        keyInput.setKeyCnt(keyCut);
                        keyInput.setKeyName(nameKey);
                        keyInput.setLedBkType(ListDatas.get(groupposition).getLedBkType());

                    Gson gson = new Gson();
                    String data = gson.toJson(keyInput);
                    String str = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
                            ",\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_editBoards.getValue() +
                            ",\"subType1\":0" +
                            ",\"subType2\":1" +
                            ",\"keyinput_rows\":[" + data + "]" +
                            ",\"keyinput\":1}";
                    Log.i("修改输入板名称    ", str);
                    MyApplication.mApplication.showLoadDialog(mContext);
                    MyApplication.mApplication.getUdpServer().send(str, 9);
                    } catch (Exception e) {
                        Log.i("转码Name", "onClick: " + e);
                    }

                }
            }
        });
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 修改按键名称Dialog
     */
    private void ChlidDialogView(final int groupposition, final int chlidposition) {
        dialog = new CustomDialog(mContext, R.style.customDialog, R.layout.dialog_inputboard);
        dialog.show();
        mDialogTitle = (TextView) dialog.findViewById(R.id.Dialog_title);
        mDialogTitle.setText("修改按键名称");
        mDialogTitleName = (TextView) dialog.findViewById(R.id.Dialog_title_name);
        mDialogTitleName.setText("按键名称：");
        mEtBoardName = (EditText) dialog.findViewById(R.id.edit_title_name);
        mEtBoardName.setHint("请输入新名称");
        mLinearLayout = (LinearLayout) dialog.findViewById(R.id.ll_dialog_input);
        mLinearLayout.setVisibility(View.GONE);
        mBtnSure = (Button) dialog.findViewById(R.id.btn_sure);
        mBtnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        mBtnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if ("".equals(mEtBoardName.getText().toString()) || mEtBoardName.getText().toString().length() > 6) {
                    ToastUtil.showText("输入名称最多6个字符");
                    return;
                } else {
                    //修改按键板名称逻辑

                    String name = mEtBoardName.getText().toString();
                    WareBoardKeyInput keyInput = new WareBoardKeyInput();
                    try {
                        keyInput.setBoardName(CommonUtils.bytesToHexString(ListDatas.get(groupposition).getBoardName().getBytes("GB2312")));
                        keyInput.setBoardType(ListDatas.get(groupposition).getBoardType());
                        keyInput.setbResetKey(ListDatas.get(groupposition).getbResetKey());
                        keyInput.setCanCpuID(ListDatas.get(groupposition).getCanCpuID());
                        keyInput.setKeyAllCtrlType(ListDatas.get(groupposition).getKeyAllCtrlType());
                        keyInput.setKeyCnt(ListDatas.get(groupposition).getKeyCnt());
                        String[] nameKey = new String[ListDatas.get(groupposition).getKeyName().length];
                        ListDatas.get(groupposition).getKeyName()[chlidposition] = name;
                        for (int i = 0; i < ListDatas.get(groupposition).getKeyName().length; i++) {
                            nameKey[i] = CommonUtils.bytesToHexString(ListDatas.get(groupposition).getKeyName()[i].getBytes("GB2312"));
                        }
                        keyInput.setKeyName(nameKey);
                        keyInput.setLedBkType(ListDatas.get(groupposition).getLedBkType());
                    } catch (UnsupportedEncodingException e) {
                        Log.i("转码Name", "onClick: " + e);
                    }
                    Gson gson = new Gson();
                    String data = gson.toJson(keyInput);
                    String str = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
                            ",\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_editBoards.getValue() +
                            ",\"subType1\":0" +
                            ",\"subType2\":1" +
                            ",\"keyinput_rows\":[" + data + "]" +
                            ",\"keyinput\":1}";
                    Log.i("修改输入按键名称字符串   ", str);
                    MyApplication.mApplication.showLoadDialog(mContext);
                    MyApplication.mApplication.getUdpServer().send(str, 9);

                }
            }
        });
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
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
