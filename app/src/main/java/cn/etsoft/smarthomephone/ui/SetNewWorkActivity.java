package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.pullmi.app.GlobalVars;
import cn.etsoft.smarthomephone.pullmi.common.CommonUtils;
import cn.etsoft.smarthomephone.pullmi.entity.RcuInfo;
import cn.etsoft.smarthomephone.pullmi.utils.LogUtils;

/**
 * Created by fbl on 16-11-17.
 */
public class SetNewWorkActivity extends Activity implements View.OnClickListener {

    TextView Title, devUnitID, roomNum, macAddr, tvback, tvsave;
    private int id;
    private EditText name, devUnitPass, IpAddr, SubMask, GateWay, centerServ;
    private RcuInfo rcuinfo;

    private ImageView back;
    private RadioGroup group;
    private RadioButton yes, no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_set_activity);
        initView();
    }

    int bDhcp = -1;

    private void initView() {
        id = getIntent().getIntExtra("id", 0);
        SharedPreferences sharedPreferences = getSharedPreferences("profile", Context.MODE_PRIVATE);
        String json_rcuinfo_list = sharedPreferences.getString("list", "");
        List<RcuInfo> json_list;
        Log.i("JSON", json_rcuinfo_list);

        json_list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json_rcuinfo_list);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                RcuInfo info = new RcuInfo();
                info.setDevUnitID(object.getString("devUnitID"));
                info.setDevUnitPass(object.getString("devUnitPass"));
                info.setName(object.getString("name"));
                info.setSoftVersion(object.getString("SoftVersion"));
                info.setRoomNum(object.getString("roomNum"));
                info.setMacAddr(object.getString("macAddr"));
                info.setbDhcp(object.getInt("bDhcp"));
                info.setCenterServ(object.getString("centerServ"));
                info.setGateWay(object.getString("GateWay"));
                info.setHwVversion(object.getString("HwVversion"));
                info.setSubMask(object.getString("SubMask"));
                info.setIpAddr(object.getString("IpAddr"));
                json_list.add(info);
            }

        } catch (JSONException e) {
//                e.printStackTrace();
            Log.i("NetWorkActivity", e + "");
        }
        rcuinfo = json_list.get(id);
        Title = (TextView) findViewById(R.id.title_bar_tv_title);
        Title.setText(rcuinfo.getName());
        bDhcp = rcuinfo.getbDhcp();
        back = (ImageView) findViewById(R.id.title_bar_iv_back);

        devUnitID = (TextView) findViewById(R.id.work_devUnitID);
        devUnitPass = (EditText) findViewById(R.id.work_devUnitPass);
        name = (EditText) findViewById(R.id.work_name);
        IpAddr = (EditText) findViewById(R.id.work_IpAddr);
        SubMask = (EditText) findViewById(R.id.work_SubMask);
        GateWay = (EditText) findViewById(R.id.work_GateWay);
        centerServ = (EditText) findViewById(R.id.work_centerServ);

        roomNum = (TextView) findViewById(R.id.work_roomNum);
        macAddr = (TextView) findViewById(R.id.work_macAddr);
        tvback = (TextView) findViewById(R.id.work_back);
        tvsave = (TextView) findViewById(R.id.work_save);

        group = (RadioGroup) findViewById(R.id.y_n_Group);
        yes = (RadioButton) findViewById(R.id.work_yes);
        no = (RadioButton) findViewById(R.id.work_no);

        back.setOnClickListener(this);
        tvback.setOnClickListener(this);
        tvsave.setOnClickListener(this);

        devUnitID.setText(rcuinfo.getDevUnitID());
        devUnitPass.setText(rcuinfo.getDevUnitPass());
        name.setText(rcuinfo.getName());
        IpAddr.setText(rcuinfo.getIpAddr());
        SubMask.setText(rcuinfo.getSubMask());
        GateWay.setText(rcuinfo.getGateWay());
        centerServ.setText(rcuinfo.getCenterServ());
        roomNum.setText(rcuinfo.getRoomNum());
        macAddr.setText(rcuinfo.getMacAddr());

        if (rcuinfo.getbDhcp() == 0) {
            no.setChecked(true);
        } else {
            yes.setChecked(true);
        }
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
            case R.id.title_bar_iv_back:
                finish();
                break;
            case R.id.work_back:
                finish();
                break;
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

                final String chn_str = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
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
                finish();
                break;
        }
    }
    public String Sutf2Sgbk (String string){

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
