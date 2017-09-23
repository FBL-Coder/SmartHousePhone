package cn.etsoft.smarthome.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthome.Helper.WareDataHliper;
import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.SwipeAdapter;
import cn.etsoft.smarthome.domain.Save_Quipment;
import cn.etsoft.smarthome.domain.WareDev;
import cn.etsoft.smarthome.domain.WareKeyOpItem;
import cn.etsoft.smarthome.utils.SendDataUtil;
import cn.etsoft.smarthome.utils.ToastUtil;

/**
 * Created by Say GoBay on 2016/8/29.
 * 组合设置--输入板配置设备页面
 */
public class AddEquipmentControlActivity extends Activity implements View.OnClickListener {
    private TextView mTitle, ref_equipment, save_equipment;
    private ImageView back, input_out_iv_noData;
    private ListView lv;
    private int index;
    private String uid;
    private List<WareKeyOpItem> keyOpItems;
    private SwipeAdapter adapter = null;
    private List<WareDev> mWareDev;
    private List<WareDev> mWareDev_ok;
    private int DATTYPE_SET = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipmentdeploy_listview);
        //初始化标题栏
        initTitleBar();
        initView();
        initData();
        initListView();
        MyApplication.mApplication.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int datType, int subtype1, int subtype2) {

                if (datType == 11 || datType == 12 || datType == 13) {
                    MyApplication.mApplication.dismissLoadDialog();
                }
                if (datType == 11) {
                    keyOpItems.clear();
                    initListView();
                }
                if (datType == 12 && MyApplication.getWareData().getResult() != null
                        && MyApplication.getWareData().getResult().getResult() == 1) {
                    Toast.makeText(AddEquipmentControlActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    MyApplication.getWareData().setResult(null);
                }
            }
        });
    }
    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        mTitle = (TextView) findViewById(R.id.title_bar_tv_title);
        mTitle.setText(getIntent().getStringExtra("title"));
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        index = getIntent().getExtras().getInt("key_index");
        uid = getIntent().getExtras().getString("uid");
    }

    public void initData() {
        keyOpItems = new ArrayList<>();
        SendDataUtil.getKeyItemInfo(index, uid);
        MyApplication.mApplication.showLoadDialog(this);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        input_out_iv_noData = (ImageView) findViewById(R.id.input_out_iv_nodata);
        ref_equipment = (TextView) findViewById(R.id.ref_equipment);
        save_equipment = (TextView) findViewById(R.id.save_equipment);

        lv = (ListView) findViewById(R.id.equipment_out_lv);
        ref_equipment.setOnClickListener(this);
        save_equipment.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    /**
     * 初始化listView
     */
    private void initListView() {
        if (WareDataHliper.initCopyWareData().getCopyDevs().size() == 0) {
            input_out_iv_noData.setVisibility(View.VISIBLE);
            return;
        }
        input_out_iv_noData.setVisibility(View.GONE);

        mWareDev = WareDataHliper.initCopyWareData().getCopyDevs();
        for (int j = 0; j < mWareDev.size(); j++) {
            WareDev dev = mWareDev.get(j);
            for (int i = 0; i < MyApplication.getWareData().getKeyOpItems().size(); i++) {
                if (dev.getDevId() == MyApplication.getWareData().getKeyOpItems().get(i).getDevId()
                        && dev.getType() == MyApplication.getWareData().getKeyOpItems().get(i).getDevType()
                        && dev.getCanCpuId().equals(MyApplication.getWareData().getKeyOpItems().get(i).getOut_cpuCanID())) {
                    dev.setSelect(true);
                    dev.setCmd(MyApplication.getWareData().getKeyOpItems().get(i).getKeyOpCmd());
                }
            }
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged(mWareDev);
        } else {
            adapter = new SwipeAdapter(this, mWareDev);
            lv.setAdapter(adapter);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_bar_iv_back:
                finish();
                break;
            case R.id.ref_equipment:
                //刷新
                input_out_iv_noData.setVisibility(View.GONE);
                initData();
                break;
            case R.id.save_equipment:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示 :");
                builder.setMessage("您要保存这些设置吗？");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("是的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mWareDev.size() == 0) {
                            dialog.dismiss();
                            ToastUtil.showText("请添加绑定设备");
                            return;
                        }
                        mWareDev_ok = new ArrayList<>();
                        for (int i = 0; i < mWareDev.size(); i++) {
                            if (mWareDev.get(i).isSelect()) {
                                mWareDev_ok.add(mWareDev.get(i));
                            }
                        }
                        Save_Quipment save_quipment = new Save_Quipment();
                        List<Save_Quipment.key_Opitem_Rows> list_kor = new ArrayList<>();
                        //保存
                        for (int i = 0; i < mWareDev_ok.size(); i++) {
                            Save_Quipment.key_Opitem_Rows key_opitem_rows = save_quipment.new key_Opitem_Rows();
                            key_opitem_rows.setOut_cpuCanID(mWareDev_ok.get(i).getCanCpuId());
                            key_opitem_rows.setDevID(mWareDev_ok.get(i).getDevId());
                            key_opitem_rows.setDevType(mWareDev_ok.get(i).getType());
                            key_opitem_rows.setKeyOp(1);
                            key_opitem_rows.setKeyOpCmd(mWareDev_ok.get(i).getCmd());
                            list_kor.add(key_opitem_rows);
                        }
                        save_quipment.setDevUnitID(GlobalVars.getDevid());
                        save_quipment.setDatType(DATTYPE_SET);
                        save_quipment.setKey_cpuCanID(uid);
                        save_quipment.setKey_opitem(mWareDev_ok.size());
                        save_quipment.setKey_index(index);
                        save_quipment.setSubType1(0);
                        save_quipment.setSubType2(0);
                        save_quipment.setKey_opitem_rows(list_kor);
                        Gson gson = new Gson();
                        System.out.println(gson.toJson(save_quipment));
                        MyApplication.mApplication.getUdpServer().send(gson.toJson(save_quipment).toString());
                        MyApplication.mApplication.showLoadDialog(AddEquipmentControlActivity.this);
                    }
                });
                builder.create().show();
                break;
        }
    }
}
