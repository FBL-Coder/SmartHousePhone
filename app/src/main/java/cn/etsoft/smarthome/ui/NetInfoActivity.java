package cn.etsoft.smarthome.ui;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Pattern;

import cn.etsoft.smarthome.Helper.Net_AddorDel_Helper;
import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.NetWork_Adapter;
import cn.etsoft.smarthome.domain.RcuInfo;
import cn.etsoft.smarthome.pullmi.common.CommonUtils;
import cn.etsoft.smarthome.utils.AppSharePreferenceMgr;
import cn.etsoft.smarthome.utils.SendDataUtil;
import cn.etsoft.smarthome.utils.ToastUtil;

/**
 * Author：FBL  Time： 2017/7/24.
 * 模块详情
 */

public class NetInfoActivity extends Activity {
    private static final Pattern IPV4_PATTERN =
            Pattern.compile(
                    "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
    private ImageView mSeekNetBack;
    private TextView net_ID, net_Pass, save;
    private LinearLayout Net_Pass_LL;
    private TextView IsOnLine;
    private EditText name, IP, Ip_mask, GetWay, Server;
    private RadioGroup stateIP;
    private RadioButton stateIP_yes, stateIP_no;
    private int IpState = 0;
    private int FLAG = 0;
    private int position = 0;
    private RcuInfo info;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netindo);
        initView();
        initData();
    }

    public void initView() {
        mSeekNetBack = (ImageView) findViewById(R.id.SeekNet_Back);
        net_ID = (TextView) findViewById(R.id.NetWork_ID);
        net_Pass = (TextView) findViewById(R.id.NetWork_Pass);
        save = (TextView) findViewById(R.id.save);
        name = (EditText) findViewById(R.id.NetWork_Name);
        IP = (EditText) findViewById(R.id.NetWork_Ip);
        Ip_mask = (EditText) findViewById(R.id.NetWork_Ip_Mask);
        GetWay = (EditText) findViewById(R.id.NetWork_GetWay);
        Server = (EditText) findViewById(R.id.NetWork_Server);
        IsOnLine = (TextView) findViewById(R.id.NetWork_IsOnLine);
        stateIP = (RadioGroup) findViewById(R.id.stateIP);
        stateIP_yes = (RadioButton) findViewById(R.id.stateIP_yes);
        stateIP_no = (RadioButton) findViewById(R.id.stateIP_no);
        Net_Pass_LL = (LinearLayout) findViewById(R.id.Net_Pass_ll);
    }

    @Override
    protected void onResume() {
        MyApplication.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int datType, int subtype1, int subtype2) {
                if (datType == 1 && subtype1 == 1) {
                    MyApplication.mApplication.dismissLoadDialog();
                    if (subtype2 == 1) {
                        ToastUtil.showText("修改成功");
                        setResult(5);
                        finish();
                    }
                    else ToastUtil.showText("修改失败");
                }
            }
        });
        super.onResume();
    }

    public void initData() {
        FLAG = getIntent().getBundleExtra("BUNDLE").getInt("FLAG", 0);
        position = getIntent().getBundleExtra("BUNDLE").getInt("POSITION", 0);
        mSeekNetBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (FLAG == NetWork_Adapter.LOGIN) {
            info = MyApplication.mApplication.getRcuInfoList().get(position);
            name.setText(info.getCanCpuName());
            if (info.isOnline()) IsOnLine.setText("在线");
            else IsOnLine.setText("不在线");
        } else {
            info = MyApplication.mApplication.getSeekRcuInfos().get(position);
            name.setText(info.getName());
            IsOnLine.setText("不在线");
        }
        net_ID.setText(info.getDevUnitID());
        net_Pass.setText(info.getDevUnitPass());
        IP.setText(info.getIpAddr());
        Ip_mask.setText(info.getSubMask());
        GetWay.setText(info.getGateWay());
        Server.setText(info.getCenterServ());
        if (info.getbDhcp() == 1) {
            stateIP_yes.setChecked(true);
        } else if (info.getbDhcp() == 0) {
            stateIP_no.setChecked(true);
        }
        if (FLAG == NetWork_Adapter.SEEK) {
            net_Pass.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
//            save.setVisibility(View.GONE);
//            Net_Pass_LL.setVisibility(View.GONE);
//            name.setEnabled(false);
//            IP.setEnabled(false);
//            Ip_mask.setEnabled(false);
//            GetWay.setEnabled(false);
//            Server.setEnabled(false);
//            stateIP_yes.setClickable(false);
//            stateIP_no.setClickable(false);
        }

        stateIP.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.stateIP_yes:
                        IpState = 1;
                        break;
                    case R.id.stateIP_no:
                        IpState = 0;
                        break;
                }
            }
        });

        mSeekNetBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<RcuInfo> list = MyApplication.mApplication.getRcuInfoList();

                if (FLAG == NetWork_Adapter.SEEK) {
                    for (int i = 0; i < list.size(); i++) {
                        if (info.getDevUnitID().equals(list.get(i).getDevUnitID())) {
                            ToastUtil.showText("请在已有联网模块列表中修改模块信息", 5000);
                            return;
                        }
                    }
                }

                if (!info.getDevUnitID().
                        equals(AppSharePreferenceMgr.get(GlobalVars.RCUINFOID_SHAREPREFERENCE, ""))) {
                    ToastUtil.showText("这个联网模快没有使用中，不可修改信息");
                    return;
                }
                if (!GlobalVars.isIsLAN()) {
                    ToastUtil.showText("修改联网模块信息必须是局域网操作");
                    return;
                }
                String name_str = name.getText().toString();
                String IP_str = IP.getText().toString();
                String Ip_mask_str = Ip_mask.getText().toString();
                String GetWay_str = GetWay.getText().toString();
                String Server_str = Server.getText().toString();
                if (!IPV4_PATTERN.matcher(IP_str).matches() ||
                        !IPV4_PATTERN.matcher(Ip_mask_str).matches() ||
                        !IPV4_PATTERN.matcher(GetWay_str).matches() ||
                        !IPV4_PATTERN.matcher(Server_str).matches()) {
                    ToastUtil.showText("IP格式不正确，请确认后再保存");
                } else if ("".equals(name_str) || name_str.length() > 6) {
                    ToastUtil.showText("名称为空或者大于6个字符");
                } else {
                    byte[] data = {0};
                    try {
                        data = name_str.getBytes("GB2312");
                    } catch (UnsupportedEncodingException e) {
                        ToastUtil.showText("名称不合适！");
                        return;
                    }
                    MyApplication.mApplication.showLoadDialog(NetInfoActivity.this);
                    String name_str_gb = CommonUtils.bytesToHexString(data);
                    SendDataUtil.changeNetInfo(name_str_gb, info.getDevUnitPass(), IP_str, Ip_mask_str
                            , GetWay_str, Server_str, info.getMacAddr(), IpState);

                    MyHandler handler = new MyHandler();
                    if (FLAG == NetWork_Adapter.LOGIN) {
                        Net_AddorDel_Helper.editNew(handler, MyApplication.mApplication.getRcuInfoList(),
                                position, NetInfoActivity.this, name,
                                MyApplication.mApplication.getRcuInfoList().get(position).getDevUnitID(),
                                MyApplication.mApplication.getRcuInfoList().get(position).getDevUnitPass());
                    }
                }
            }
        });
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            initData();
        }
    }
}
