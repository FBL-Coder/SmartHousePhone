package cn.etsoft.smarthome.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
    private EditText mSceneEtName;
    private Button mSceneBtnSure, mSceneBtnCancel;
    private TextView mDialogTitle, mDialogTitleName;

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
        return ListDatas.get(groupPosition).getKeyCnt();
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
                initDialogView(groupPosition);
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
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_grouplist_child_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
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
    private void initDialogView(final int groupposition) {
        dialog = new CustomDialog(mContext, R.style.customDialog, R.layout.dialog_sceneset);
        dialog.show();
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
//修改按键板名称逻辑
                    String name = mSceneEtName.getText().toString();
                    WareBoardKeyInput keyInput = new WareBoardKeyInput();
                    try {
                        keyInput.setBoardName(CommonUtils.bytesToHexString(name.getBytes("GB2312")));
                        keyInput.setBoardType(ListDatas.get(groupposition).getBoardType());
                        keyInput.setbResetKey(ListDatas.get(groupposition).getbResetKey());
                        keyInput.setCanCpuID(ListDatas.get(groupposition).getCanCpuID());
                        keyInput.setKeyAllCtrlType(ListDatas.get(groupposition).getKeyAllCtrlType());
                        keyInput.setKeyCnt(ListDatas.get(groupposition).getKeyCnt());
                        String[] nameKey = new String[ListDatas.get(groupposition).getKeyName().length];
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
                    Log.i("修改输入板名称    ", str);
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
