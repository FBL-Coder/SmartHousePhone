package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.adapter.LightAdapter;
import cn.etsoft.smarthomephone.pullmi.app.GlobalVars;
import cn.etsoft.smarthomephone.pullmi.common.CommonUtils;
import cn.etsoft.smarthomephone.pullmi.entity.UdpProPkt;
import cn.etsoft.smarthomephone.pullmi.entity.WareLight;

/**
 * Created by Say GoBay on 2016/9/1.
 */
public class LightActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private GridView gridView;
    private LinearLayout ll;
    private ImageView back;
    private TextView title;

    private List<WareLight> wareLight;
    private ImageView allOpen, allClose;
    private boolean IsCanClick = false;
    private LightAdapter lightAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);
        //初始化标题栏
        initTitleBar();
        //初始化控件
        intView();
        //初始化GridView
        initGridView();
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        title = (TextView) findViewById(R.id.title_bar_tv_title);
        title.setText(getIntent().getStringExtra("title") + "控制");
        title.setTextColor(0xffffffff);
        back.setImageResource(R.drawable.return2);
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
    private void intView() {
        ll = (LinearLayout) findViewById(R.id.ll);
        ll.setBackgroundResource(R.drawable.tu3);
    }

    /**
     * 初始化GridView
     */
    private void initGridView() {
        gridView = (GridView) findViewById(R.id.light_gv);
        gridView.setSelector(R.drawable.selector_gridview_item);
        gridView.setOnItemClickListener(this);
        allOpen = (ImageView) findViewById(R.id.light_open_all);
        allClose = (ImageView) findViewById(R.id.light_close_all);

        if (MyApplication.getWareData().getLights() != null && MyApplication.getWareData().getLights().size() > 1) {
            initEvent();
            upData();
            IsCanClick = true;
        } else {
            Toast.makeText(this, "没有找到可控制灯具", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (IsCanClick) {
            if (wareLight.get(position).getbTuneEn() == 0) {
                String ctlStr;
                if (wareLight.get(position).getbOnOff() == 0) {

                    ctlStr = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
                            ",\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue() +
                            ",\"subType1\":0" +
                            ",\"subType2\":0" +
                            ",\"canCpuID\":\"" + MyApplication.getWareData().getLights().get(position).getDev().getCanCpuId() +
                            "\",\"devType\":" + MyApplication.getWareData().getLights().get(position).getDev().getType() +
                            ",\"devID\":" + MyApplication.getWareData().getLights().get(position).getDev().getDevId() +
                            ",\"cmd\":0" +
                            "" +
                            "}";
                    CommonUtils.sendMsg(ctlStr);
                } else {

                    ctlStr = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
                            ",\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrlDev.getValue() +
                            ",\"subType1\":0" +
                            ",\"subType2\":0" +
                            ",\"canCpuID\":\"" + MyApplication.getWareData().getLights().get(position).getDev().getCanCpuId() +
                            "\",\"devType\":" + MyApplication.getWareData().getLights().get(position).getDev().getType() +
                            ",\"devID\":" + MyApplication.getWareData().getLights().get(position).getDev().getDevId() +
                            ",\"cmd\":1" +
                            "}";

                    CommonUtils.sendMsg(ctlStr);

                }

                System.out.println(ctlStr);
            }
        }
    }

    private void initEvent() {
        MyApplication.mInstance.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int what) {
                //更新界面
                lightAdapter.notifyDataSetChanged();
                //更新数据
                wareLight = MyApplication.getWareData().getLights();
            }
        });
    }

    private void upData() {
        wareLight = MyApplication.getWareData().getLights();
        lightAdapter = new LightAdapter(MyApplication.getWareData(), this);
        gridView.setAdapter(lightAdapter);
        gridView.setSelector(R.drawable.selector_gridview_item);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (IsCanClick) {
            switch (v.getId()) {
                case R.id.light_open_all:
                    String open_str = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
                            ",\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrl_allDevs.getValue() +
                            ",\"subType1\":0" +
                            ",\"subType2\":0" +
                            ",\"canCpuID\":0\"" +
                            "\",\"devType\":" + UdpProPkt.E_WARE_TYPE.e_ware_light +
                            ",\"devID\":0" +
                            ",\"cmd\": 1" +
                            "}";
                    CommonUtils.sendMsg(open_str);
                    break;
                case R.id.light_close_all:
                    String close_str = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
                            ",\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_ctrl_allDevs.getValue() +
                            ",\"subType1\":0" +
                            ",\"subType2\":0" +
                            ",\"canCpuID\":0\"" +
                            "\",\"devType\":" + UdpProPkt.E_WARE_TYPE.e_ware_light +
                            ",\"devID\":0" +
                            ",\"cmd\": 0" +
                            "}";
                    CommonUtils.sendMsg(close_str);
                    break;
            }
        }
    }
}
