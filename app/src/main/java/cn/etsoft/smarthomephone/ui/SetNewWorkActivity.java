package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.pullmi.entity.RcuInfo;

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

    private void initView() {
        id = getIntent().getIntExtra("id", 0);
        rcuinfo = MyApplication.getWareData().getRcuInfos().get(id);
        Title = (TextView) findViewById(R.id.title_bar_tv_title);
        Title.setText(rcuinfo.getName());

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

        if (rcuinfo.getbDhcp() == 0)
            no.setChecked(true);
        else
            yes.setChecked(true);

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.work_yes:
                        break;
                    case R.id.work_no:
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
                MyApplication.getWareData().getRcuInfos().set(id, rcuinfo);

                finish();
                break;
        }
    }
}
