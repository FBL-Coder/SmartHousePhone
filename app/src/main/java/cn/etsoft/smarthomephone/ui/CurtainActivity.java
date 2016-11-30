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

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.adapter.GridViewAdapter;
import cn.etsoft.smarthomephone.pullmi.app.GlobalVars;
import cn.etsoft.smarthomephone.pullmi.common.CommonUtils;
import cn.etsoft.smarthomephone.pullmi.entity.UdpProPkt;

/**
 * Created by Say GoBay on 2016/9/1.
 */
public class CurtainActivity extends Activity implements AdapterView.OnItemClickListener {
    private GridView gridView;
    private LinearLayout ll;
    private int[] image = {R.drawable.curtainfullopen, R.drawable.curtainhalfopen, R.drawable.curtainalloff};
    private String[] text = {"全开", "半开", "全关"};
    private ImageView back;
    private TextView title;

    private boolean IsCanClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lights);
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
        ll.setBackgroundResource(R.drawable.tu4);
    }

    /**
     * 初始化GridView
     */
    private void initGridView() {
        gridView = (GridView) findViewById(R.id.light_gv);
        gridView.setAdapter(new GridViewAdapter(image, text, this));
        gridView.setSelector(R.drawable.selector_gridview_item);
        gridView.setOnItemClickListener(this);

        if (MyApplication.getWareData().getCurtains() != null && MyApplication.getWareData().getCurtains().size() > 1) {
            IsCanClick = true;
        } else {
            Toast.makeText(this, "没有找到可控制窗帘", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (IsCanClick) {
            String str_Fixed = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
                    ",\"datType\":4" +
                    ",\"subType1\":0" +
                    ",\"subType2\":0" +
                    ",\"canCpuID\":" + MyApplication.getWareData().getCurtains().get(0).getDev().getCanCpuId() +
                    ".\"devType\":" + MyApplication.getWareData().getCurtains().get(0).getDev().getType() +
                    ".\"devID\":" + MyApplication.getWareData().getCurtains().get(0).getDev().getDevId();
            int Value = -1;
            switch (position) {
                case 0:
                    Value = UdpProPkt.E_CURT_CMD.e_curt_offOn.getValue();
                    break;
                case 1:
                    Value = UdpProPkt.E_CURT_CMD.e_curt_stop.getValue();
                    break;
                case 2:
                    Value = UdpProPkt.E_CURT_CMD.e_curt_offOff.getValue();
                    break;
            }
            if (Value != 1) {
                str_Fixed = str_Fixed +
                        ".\"cmd:" + Value + "}";
                CommonUtils.sendMsg(str_Fixed);
            }
        }
    }
}
