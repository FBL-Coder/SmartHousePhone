package cn.etsoft.smarthome.ui.Setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthome.Helper.WareDataHliper;
import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.GroupList_InputToOutAdapter;
import cn.etsoft.smarthome.domain.GroupList_BoardDevData;
import cn.etsoft.smarthome.domain.Save_Quipment;
import cn.etsoft.smarthome.domain.WareBoardChnout;
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
    private ExpandableListView lv;
    private int index;
    private String uid;
    private List<WareKeyOpItem> keyOpItems;
    private GroupList_InputToOutAdapter adapter = null;
    private List<WareDev> mWareDev;
    private List<GroupList_BoardDevData> BoardDevDatas;
    private List<WareDev> mWareDev_ok;
    private int DATTYPE_SET = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipmentdeploy_listview);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.AppTheme_color));

        //初始化标题栏
        initTitleBar();
        initView();
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
        initData();
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        mTitle = (TextView) findViewById(R.id.title_bar_tv_title);
        mTitle.setText("按键配设备 · " + getIntent().getStringExtra("title"));
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        index = getIntent().getExtras().getInt("key_index");
        uid = getIntent().getExtras().getString("uid");
    }

    public void initData() {
        MyApplication.getWareData().setKeyOpItems(new ArrayList<WareKeyOpItem>());
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

        lv = (ExpandableListView) findViewById(R.id.equipment_out_lv);
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
        keyOpItems = MyApplication.getWareData().getKeyOpItems();
        input_out_iv_noData.setVisibility(View.GONE);
        mWareDev = WareDataHliper.initCopyWareData().getCopyDevs();
        for (int j = 0; j < mWareDev.size(); j++) {
            WareDev dev = mWareDev.get(j);
            for (int i = 0; i < keyOpItems.size(); i++) {
                if (dev.getDevId() == keyOpItems.get(i).getDevId()
                        && dev.getType() == keyOpItems.get(i).getDevType()
                        && dev.getCanCpuId().equals(keyOpItems.get(i).getOut_cpuCanID())) {
                    dev.setSelect(true);
                    dev.setCmd(MyApplication.getWareData().getKeyOpItems().get(i).getKeyOpCmd());
                }
            }
        }

        if (MyApplication.getWareData().getBoardChnouts() == null || MyApplication.getWareData().getBoardChnouts().size() == 0) {
            ToastUtil.showText("没有输出板信息,请在主页刷新数据");
            return;
        }
        List<WareBoardChnout> boardChnouts = MyApplication.getWareData().getBoardChnouts();
        BoardDevDatas = new ArrayList<>();
        for (int i = 0; i < boardChnouts.size(); i++) {
            GroupList_BoardDevData boardDevData = new GroupList_BoardDevData();
            boardDevData.setBoardName(boardChnouts.get(i).getBoardName());
            boardDevData.setBoardType(boardChnouts.get(i).getBoardType());
            boardDevData.setbOnline(boardChnouts.get(i).getbOnline());
            boardDevData.setChnCnt(boardChnouts.get(i).getChnCnt());
            boardDevData.setDevUnitID(boardChnouts.get(i).getCanCpuID());
            boardDevData.setRev2(boardChnouts.get(i).getRev2());
            List<WareDev> devList = new ArrayList<>();
            for (int j = 0; j < mWareDev.size(); j++) {
                if (mWareDev.get(j).getCanCpuId().equals(boardDevData.getDevUnitID())) {
                    devList.add(mWareDev.get(j));
                }
            }
            boardDevData.setDevs(devList);
            BoardDevDatas.add(boardDevData);
        }


        if (adapter != null) {
            adapter.notifyDataSetChanged(BoardDevDatas);
        } else {
            adapter = new GroupList_InputToOutAdapter(this, BoardDevDatas);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(this,
                        android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
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
                                if (mWareDev.get(i).getCmd() == 0) {
                                    ToastUtil.showText("存在未设置，请设置指令");
                                    return;
                                }
                                mWareDev_ok.add(mWareDev.get(i));
                            }
                        }
                        Save_Quipment save_quipment = new Save_Quipment();
                        List<Save_Quipment.key_Opitem_Rows> list_kor = new ArrayList<>();
                        //保存
                        if (mWareDev_ok.size() == 0){
                            ToastUtil.showText("不能少于一个配置项");
                            return;
                        }
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
                        MyApplication.mApplication.getUdpServer().send(gson.toJson(save_quipment).toString(), 12);
                        MyApplication.mApplication.showLoadDialog(AddEquipmentControlActivity.this);
                    }
                });
                builder.create().show();
                break;
        }
    }
}
