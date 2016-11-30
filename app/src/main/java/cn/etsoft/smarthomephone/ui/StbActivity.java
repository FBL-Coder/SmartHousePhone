package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.adapter.StbAdapter;
import cn.etsoft.smarthomephone.pullmi.app.GlobalVars;
import cn.etsoft.smarthomephone.pullmi.common.CommonUtils;
import cn.etsoft.smarthomephone.pullmi.entity.UdpProPkt;
import cn.etsoft.smarthomephone.pullmi.entity.WareSetBox;

/**
 * Created by Say GoBay on 2016/9/1.
 */
public class StbActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private GridView gridView;
    private int[] image = {R.drawable.television4, R.drawable.television5, R.drawable.television6, R.drawable.television7, R.drawable.television8, R.drawable.television9, R.drawable.television10, R.drawable.television11, R.drawable.television12, R.drawable.television13, R.drawable.television14, R.drawable.television15};
    private ImageView back, choose, choose1, add, subtract, up, down;
    private TextView title;

    private WareSetBox wareSetBox;
    private byte[] devBuff;
    private boolean IsCanClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stb);
        //初始化标题栏
        initTitleBar();
        //初始化控件
        initView();
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
        back.setOnClickListener(this);
    }

    /**
     * 初始化控件
     */
    private void initView() {

        if (MyApplication.getWareData().getStbs() != null && MyApplication.getWareData().getStbs().size() > 0) {
            wareSetBox = MyApplication.getWareData().getStbs().get(0);
            IsCanClick = true;
        } else {
            Toast.makeText(this, "没有找到可控机顶盒", Toast.LENGTH_SHORT).show();
        }

        choose = (ImageView) findViewById(R.id.stb_choose);
        choose1 = (ImageView) findViewById(R.id.stb_choose1);
        add = (ImageView) findViewById(R.id.stb_add);
        subtract = (ImageView) findViewById(R.id.stb_subtract);
        up = (ImageView) findViewById(R.id.stb_up);
        down = (ImageView) findViewById(R.id.stb_down);
        choose.setOnClickListener(this);
        choose1.setOnClickListener(this);
        add.setOnClickListener(this);
        subtract.setOnClickListener(this);
        up.setOnClickListener(this);
        down.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.title_bar_iv_back)//返回
            finish();
        if (IsCanClick) {
            String str_Fixed = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
                    ",\"datType\":4" +
                    ",\"subType1\":0" +
                    ",\"subType2\":0" +
                    ",\"canCpuID\":" + MyApplication.getWareData().getStbs().get(0).getDev().getCanCpuId() +
                    ".\"devType\":" + MyApplication.getWareData().getStbs().get(0).getDev().getType() +
                    ".\"devID\":" + MyApplication.getWareData().getStbs().get(0).getDev().getDevId();
            int Value = -1;
            switch (v.getId()) {
                //开关
                case R.id.stb_choose:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_offOn.getValue();
                    break;
                //开关
                case R.id.stb_choose1:
                    Value = UdpProPkt.E_TV_CMD.e_tv_offOn.getValue();
                    break;
                //加
                case R.id.stb_add:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_numVInc.getValue();
                    break;
                //减
                case R.id.stb_subtract:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_numLf.getValue();
                    break;
                //上
                case R.id.stb_up:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_numUp.getValue();
                    break;
                //下
                case R.id.stb_down:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_numDn.getValue();
                    break;
            }
            if (Value != -1) {
                str_Fixed = str_Fixed +
                        ".\"cmd:" + Value + "}";
                CommonUtils.sendMsg(str_Fixed);
            }
        }
    }

    /**
     * 初始化GridView
     */
    private void initGridView() {
        gridView = (GridView) findViewById(R.id.stb_gv);
        gridView.setAdapter(new StbAdapter(image, this));
        gridView.setSelector(R.drawable.selector_gridview_item);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (IsCanClick) {
            String str_Fixed = "{\"devUnitID\":\"37ffdb05424e323416702443\"" +
                    ",\"datType\":4" +
                    ",\"subType1\":0" +
                    ",\"subType2\":0" +
                    ",\"canCpuID\":" + MyApplication.getWareData().getStbs().get(0).getDev().getCanCpuId() +
                    ".\"devType\":" + MyApplication.getWareData().getStbs().get(0).getDev().getType() +
                    ".\"devID\":" + MyApplication.getWareData().getStbs().get(0).getDev().getDevId();
            int Value = -1;
            switch (position) {
                case 0:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_num1.getValue();
                    break;
                case 1:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_num2.getValue();
                    break;
                case 2:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_num3.getValue();
                    break;
                case 3:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_num4.getValue();
                    break;
                case 4:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_num5.getValue();
                    break;
                case 5:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_num6.getValue();
                    break;
                case 6:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_num7.getValue();
                    break;
                case 7:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_num8.getValue();
                    break;
                case 8:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_num9.getValue();
                    break;
                case 9:
                    break;
                case 10:
                    Value = UdpProPkt.E_TVUP_CMD.e_tvUP_num0.getValue();
                    break;
                case 11:
                    break;
            }
            if (Value != -1) {
                str_Fixed = str_Fixed +
                        ".\"cmd:" + Value + "}";
                CommonUtils.sendMsg(str_Fixed);
            }
        }
    }
}
