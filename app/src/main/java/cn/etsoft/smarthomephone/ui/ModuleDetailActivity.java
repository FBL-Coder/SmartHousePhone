package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.UiUtils.ToastUtil;
import cn.etsoft.smarthomephone.pullmi.app.GlobalVars;
import cn.etsoft.smarthomephone.pullmi.common.CommonUtils;
import cn.etsoft.smarthomephone.pullmi.entity.RcuInfo;
import cn.etsoft.smarthomephone.pullmi.utils.LogUtils;

/**
 * Created by Say GoBay on 2017/8/3.
 */
public class ModuleDetailActivity extends Activity implements View.OnClickListener{
    private TextView devUnitID, roomNum, macAddr, tvsave;
    private int id;
    private EditText name, devUnitPass, IpAddr, SubMask, GateWay, centerServ;
    private RcuInfo rcuinfo;

    private RadioGroup group;
    private RadioButton yes, no;
    int bDhcp = -1;
    private ImageView back;
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module);
        //初始化标题栏
        initTitleBar();
        //初始化控件
        initView();
    }
    /**
     * 初始化标题栏
     */
    private void initTitleBar(){
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        title = (TextView) findViewById(R.id.title_bar_tv_title);
        title.setText(getIntent().getStringExtra("title"));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initView() {

//        if(MyApplication.getWareData().getRcuInfos() == null || MyApplication.getWareData().getRcuInfos().size() == 0){
//            ToastUtil.showToast(mActivity,"没有数据！");
//            return;
//        }
        if(MyApplication.getWareData().getRcuInfos() == null ){
            ToastUtil.showToast(ModuleDetailActivity.this,"没有数据！");
            return;
        }
        rcuinfo =  MyApplication.mInstance.getRcuInfo();
        bDhcp = rcuinfo.getbDhcp();

        devUnitID = (TextView) findViewById(R.id.work_devUnitID);
        devUnitPass = (EditText) findViewById(R.id.work_devUnitPass);
        name = (EditText) findViewById(R.id.work_name);
        IpAddr = (EditText) findViewById(R.id.work_IpAddr);
        SubMask = (EditText) findViewById(R.id.work_SubMask);
        GateWay = (EditText) findViewById(R.id.work_GateWay);
        centerServ = (EditText) findViewById(R.id.work_centerServ);

        roomNum = (TextView) findViewById(R.id.work_roomNum);
        macAddr = (TextView) findViewById(R.id.work_macAddr);
        tvsave = (TextView) findViewById(R.id.work_save);

        group = (RadioGroup) findViewById(R.id.y_n_Group);
        yes = (RadioButton) findViewById(R.id.work_yes);
        no = (RadioButton) findViewById(R.id.work_no);

        tvsave.setOnClickListener(this);
        try {
            devUnitID.setText(rcuinfo.getDevUnitID());
        }catch (Exception e){}
        try {
            devUnitPass.setText(rcuinfo.getDevUnitPass().substring(0,8));
        }catch (Exception e){}

        try {
            if (rcuinfo.getName().equals("")&& !rcuinfo.getCanCpuName().equals("")){
//                if (Pattern.compile("[\u4e00-\u9fa5]+").matcher(rcuinfo.getCanCpuName()).matches()) {
//                    name.setText(rcuinfo.getCanCpuName());
//                } else {
//                    name.setText(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(rcuinfo.getCanCpuName())));
//                }
                name.setText(rcuinfo.getCanCpuName());
            }else {
//                if (Pattern.compile("[\u4e00-\u9fa5]+").matcher(rcuinfo.getName()).matches()) {
//                    name.setText(rcuinfo.getName());
//                } else {
//                    name.setText(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(rcuinfo.getName())));
//                }
                name.setText(rcuinfo.getName());
            }
        }catch (Exception e){}
        try {
            IpAddr.setText(rcuinfo.getIpAddr());
        }catch (Exception e){}
        try {
            SubMask.setText(rcuinfo.getSubMask());
        }catch (Exception e){}
        try {
            GateWay.setText(rcuinfo.getGateWay());
        }catch (Exception e){}
        try {
            centerServ.setText(rcuinfo.getCenterServ());
        }catch (Exception e){}
        try {
            roomNum.setText(rcuinfo.getRoomNum());
        }catch (Exception e){}
        try {
            macAddr.setText(rcuinfo.getMacAddr());
        }catch (Exception e){}
        try {
            if (rcuinfo.getbDhcp() == 0) {
                no.setChecked(true);
            } else {
                yes.setChecked(true);
            }
        }catch (Exception e){}
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.work_yes:
                        bDhcp = 1;
                        break;
                    case R.id.work_no:
                        bDhcp = 0;
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.work_save:

//            {
//                "devUnitID": "37ffdb05424e323416702443",
//                    "datType": 1,
//                    "subType1": 0,
//                    "subType2": 0,
//                    "canCpuID": "37ffdb05424e323416702443",
//                    "devUnitPass": "16072443",
//                    "name": "6666",
//                    "IpAddr": "192.168.0.102",
//                    "SubMask": "255.255.255.0",
//                    "Gateway": "192.168.0.1",
//                    "centerServ": "192.168.1.114",
//                    "roomNum": "0000",
//                    "macAddr": "00502a040248",
//                    "SoftVersion": 0,
//                    "HwVersion": 0,
//                    "bDhcp": 0
//            }
                String newname = name.getText().toString();
                String newpass = devUnitPass.getText().toString();
                String newip = IpAddr.getText().toString();
                String newSubmask = SubMask.getText().toString();
                String newGateway = GateWay.getText().toString();
                String newcenterServ = centerServ.getText().toString();

                rcuinfo.setName(newname);
                rcuinfo.setDevUnitPass(newpass);
                rcuinfo.setIpAddr(newip);
                rcuinfo.setSubMask(newSubmask);
                rcuinfo.setGateWay(newGateway);
                rcuinfo.setCenterServ(newcenterServ);
                rcuinfo.setbDhcp(bDhcp);

                MyApplication.getWareData().getRcuInfos().set(id, rcuinfo);

                final String chn_str = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"," +
                        "\"datType\":" + 1 + "," +
                        "\"subType1\":0," +
                        "\"subType2\":0," +
                        "\"canCpuID\":\"" + rcuinfo.getDevUnitID() + "\"," +
                        "\"devUnitPass\":\"" + newpass + "\"," +
                        "\"name\":" + "\"" + Sutf2Sgbk(newname) + "\"," +
                        "\"IpAddr\":" + "\"" + newip + "\"," +
                        "\"SubMask\":" + "\"" + newSubmask + "\"," +
                        "\"Gateway\":" + "\"" + newGateway + "\"," +
                        "\"centerServ\":" + "\"" + newcenterServ + "\"," +
                        "\"roomNum\":" + "\"" + rcuinfo.getRoomNum() + "\"," +
                        "\"macAddr\":" + "\"" + rcuinfo.getMacAddr() + "\"," +
                        "\"SoftVersion\":" + 0 + "," +
                        "\"HwVersion\":" + 0 + "," +
                        "\"bDhcp\":" + bDhcp + "}";

                MyApplication.sendMsg(chn_str);
                break;
        }
    }

    public String Sutf2Sgbk(String string) {

        byte[] data = {0};
        try {
            data = string.getBytes("GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String str_gb = CommonUtils.bytesToHexString(data);
        LogUtils.LOGE("情景模式名称:%s", str_gb);
        return str_gb;
    }
}
