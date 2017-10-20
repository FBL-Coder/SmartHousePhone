package cn.etsoft.smarthome.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.GridViewAdapter;
import cn.etsoft.smarthome.ui.ConditionEventActivity;
import cn.etsoft.smarthome.ui.Setting.ConfigPassActivity;
import cn.etsoft.smarthome.ui.ControlActivity;
import cn.etsoft.smarthome.ui.Setting.EditDevActivity;
import cn.etsoft.smarthome.ui.GroupSetActivity;
import cn.etsoft.smarthome.ui.Setting.NewWorkSetActivity;
import cn.etsoft.smarthome.ui.SafetyActivity;
import cn.etsoft.smarthome.ui.SceneSetActivity;
import cn.etsoft.smarthome.ui.TimerActivity;
import cn.etsoft.smarthome.utils.AppSharePreferenceMgr;
import cn.etsoft.smarthome.utils.SendDataUtil;
import cn.etsoft.smarthome.utils.ToastUtil;
import cn.etsoft.smarthome.weidget.CustomDialog;

/**
 * Created by Say GoBay on 2016/9/1.
 * 设置碎片
 */
public class SettingFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private GridView gridView;
    private Activity mActivity;
    private String[] text = {"联网模块", "控制设置",
            "设备信息", "情景设置", "安防设置", "定时设置", "环境事件", "组合设置"};
    private int[] image = {R.drawable.net_set,
            R.drawable.control_set, R.drawable.equip_set,
            R.drawable.scene_set, R.drawable.safety_set, R.drawable.time_set,
            R.drawable.env_set, R.drawable.group_set};
    private CustomDialog dialog;
    private EditText mSceneEtName;
    private Button mSceneBtnSure, mSceneBtnCancel;
    private TextView mDialogTitle, mDialogTitleName, mDialoHelp;
    private LinearLayout mLayout;
    private boolean isPassCorrect;

    public SettingFragment(Activity activity) {
        mActivity = activity;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set, container, false);
        //初始化GridView
        initGridView(view);
        isPassCorrect = false;
        getDialog();
        return view;
    }

    public void getDialog() {
        dialog = new CustomDialog(mActivity, R.style.customDialog, R.layout.dialog_sceneset);
        dialog.show();
        initDialogView(dialog);
    }

    private void initDialogView(Dialog view) {
        mDialoHelp = (TextView) view.findViewById(R.id.Dialog_help);
        mDialoHelp.setVisibility(View.VISIBLE);
        mDialogTitle = (TextView) view.findViewById(R.id.Dialog_title);
        mDialogTitle.setText("配置密码");
        mDialogTitleName = (TextView) view.findViewById(R.id.Dialog_title_name);
        mDialogTitleName.setText("配置密码：");
        mSceneEtName = (EditText) view.findViewById(R.id.scene_et_name);
        mSceneEtName.setHint("情输入配置密码");
        mSceneBtnSure = (Button) view.findViewById(R.id.scene_btn_sure);
        mSceneBtnSure.setOnClickListener(this);
        mSceneBtnCancel = (Button) view.findViewById(R.id.scene_btn_cancel);
        mSceneBtnCancel.setOnClickListener(this);
        mLayout = (LinearLayout) view.findViewById(R.id.layout);
        mDialoHelp.setOnClickListener(this);
    }

    /**
     * 初始化GridView
     *
     * @param view
     */
    private void initGridView(View view) {
        gridView = (GridView) view.findViewById(R.id.home_gv);
        gridView.setAdapter(new GridViewAdapter(image, text, mActivity));
        gridView.setSelector(R.drawable.selector_gridview_item);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!isPassCorrect) {
            ToastUtil.showText("没有输入密码，不可操作");
            return;
        }
        Intent intent;
        switch (position) {
            case 0:
                intent = new Intent(mActivity, NewWorkSetActivity.class);
                intent.putExtra("title", text[0]);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(mActivity, ControlActivity.class);
                intent.putExtra("title", text[1]);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(mActivity, EditDevActivity.class);
                intent.putExtra("title", text[2]);
                startActivity(intent);
                break;
            case 3:
                if (MyApplication.getWareData().getSceneEvents().size() == 0)
                    SendDataUtil.getSceneInfo();
                intent = new Intent(mActivity, SceneSetActivity.class);
                intent.putExtra("title", text[3]);
                startActivity(intent);
                break;
            case 4:
                intent = new Intent(mActivity, SafetyActivity.class);
                intent.putExtra("title", text[4]);
                startActivity(intent);
                break;
            case 5:
                intent = new Intent(mActivity, TimerActivity.class);
                intent.putExtra("title", text[5]);
                startActivity(intent);
                break;
            case 6:
                intent = new Intent(mActivity, ConditionEventActivity.class);
                intent.putExtra("title", text[6]);
                startActivity(intent);
                break;
            case 7:
                intent = new Intent(mActivity, GroupSetActivity.class);
                intent.putExtra("title", text[7]);
                startActivity(intent);
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scene_btn_sure:
                if (mSceneEtName.getText().toString().equals(
                        AppSharePreferenceMgr.get(GlobalVars.CONFIG_PASS_SHAREPREFERENCE, ""))) {
                    isPassCorrect = true;
                    dialog.dismiss();
                } else {
                    ToastUtil.showText("输入密码正确，请重新输入！");
                    mSceneEtName.setText("");
                }
                break;
            case R.id.scene_btn_cancel:
                dialog.dismiss();
                break;
            case R.id.Dialog_help:
                startActivity(new Intent(getActivity(), ConfigPassActivity.class));
                break;
        }
    }
}
