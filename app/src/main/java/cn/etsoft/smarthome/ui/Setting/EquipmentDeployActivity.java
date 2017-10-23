package cn.etsoft.smarthome.ui.Setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.GroupList_OutToInputAdapter;
import cn.etsoft.smarthome.adapter.Swipe_CpnAdapter;
import cn.etsoft.smarthome.domain.GroupList_OutToInputData;
import cn.etsoft.smarthome.domain.PrintCmd;
import cn.etsoft.smarthome.domain.UpBoardKeyData;
import cn.etsoft.smarthome.domain.WareBoardKeyInput;
import cn.etsoft.smarthome.domain.WareChnOpItem;
import cn.etsoft.smarthome.pullmi.utils.Sixteen2Two;
import cn.etsoft.smarthome.utils.SendDataUtil;
import cn.etsoft.smarthome.utils.ToastUtil;

/**
 * Created by Say GoBay on 2016/8/29.
 * 组合设置--输出板设备编辑页面
 */
public class EquipmentDeployActivity extends Activity implements View.OnClickListener {
    private TextView mTitle, ref_equipment, save_equipment;
    private ImageView back, input_out_iv_noData;
    private ExpandableListView lv;
    private int devType;
    private int devId;
    private String uid;
    private List<WareChnOpItem> ChnOpItem;
    private GroupList_OutToInputAdapter adapter = null;
    private List<String> Board_text;
    private int KEY_ACTION_DOWN = 0, KEY_ACTION_UP = 1;
    private int BOARD_UP = 15, BOARD_DEL = 16;

    private List<List<PrintCmd>> listData_double;
    private List<PrintCmd> list_Data_single;
    private List<PrintCmd> list_Data;
    private List<WareBoardKeyInput> list_board;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipmentdeploy_listview);
        //初始化标题栏
        initTitleBar();
        //初始化控件
        initView();
        //初始化listView
        initListView();
        initData();
        MyApplication.mApplication.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int datType, int subtype1, int subtype2) {
                if (datType == 14 || datType == 15 || datType == 16) {
                    MyApplication.mApplication.dismissLoadDialog();
                }
                if (datType == 14) {
                    ChnOpItem.clear();
                    listData_double.clear();
                    initListView();
                }
                if (datType == 15 && MyApplication.getWareData().getResult() != null && MyApplication.getWareData().getResult().getResult() == 1) {
                    Toast.makeText(EquipmentDeployActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
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
        mTitle.setText("设备配按键 · " + getIntent().getStringExtra("title"));
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        devType = getIntent().getIntExtra("devType", -1);
        devId = getIntent().getIntExtra("devID", -1);
        uid = getIntent().getExtras().getString("uid");
        System.out.println("加载数据");
        Board_text = new ArrayList<>();
        //按键名
        list_board = MyApplication.getWareData().getKeyInputs();
        for (int i = 0; i < list_board.size(); i++) {
            Board_text.add(list_board.get(i).getBoardName());
        }
    }

    public void initData() {
        SendDataUtil.getChnItemInfo(uid, devType, devId);
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

    }

    /**
     * 初始化listView
     */
    private void initListView() {
        if (MyApplication.getWareData().getKeyInputs().size() == 0) {
            input_out_iv_noData.setVisibility(View.VISIBLE);
            MyApplication.mApplication.showLoadDialog(this);
            return;
        }
        listData_double = new ArrayList<>();
        ChnOpItem = new ArrayList<>();
        input_out_iv_noData.setVisibility(View.GONE);
        for (int i = 0; i < MyApplication.getWareData().getChnOpItems().size(); i++) {
            ChnOpItem.add(MyApplication.getWareData().getChnOpItems().get(i));
        }

        for (int z = 0; z < ChnOpItem.size(); z++) {
            for (int i = 0; i < MyApplication.getWareData().getKeyInputs().size(); i++) {
                int keyCnt = MyApplication.getWareData().getKeyInputs().get(i).getKeyCnt();
                list_Data_single = new ArrayList<>();
                for (int j = 0; j < keyCnt; j++) {
                    PrintCmd cmd = new PrintCmd();
                    cmd.setSelect(false);
                    cmd.setDevType(devType);
                    cmd.setIndex(j);
                    cmd.setKeyboardid(MyApplication.getWareData().getKeyInputs().get(i).getCanCpuID());
                    if (MyApplication.getWareData().getKeyInputs().get(i).getKeyName().length >= keyCnt) {
                        cmd.setKeyname(MyApplication.getWareData().getKeyInputs().get(i).getKeyName()[j]);
                        list_Data_single.add(cmd);
                    } else {
                        cmd.setKeyname("按键" + j);
                        list_Data_single.add(cmd);
                    }
                }
                if (ChnOpItem.get(z).getCancupid().equals(MyApplication.getWareData().getKeyInputs().get(i).getCanCpuID()))
                    listData_double.add(list_Data_single);
            }
        }


        for (int i = 0; i < MyApplication.getWareData().getKeyInputs().size(); i++) {
            int keyCnt = MyApplication.getWareData().getKeyInputs().get(i).getKeyCnt();
            list_Data_single = new ArrayList<>();
            for (int j = 0; j < keyCnt; j++) {
                PrintCmd cmd = new PrintCmd();
                cmd.setSelect(false);
                cmd.setDevType(devType);
                cmd.setKeyboardid(MyApplication.getWareData().getKeyInputs().get(i).getCanCpuID());
                cmd.setIndex(j);
                if (MyApplication.getWareData().getKeyInputs().get(i).getKeyName().length >= keyCnt) {
                    cmd.setKeyname(MyApplication.getWareData().getKeyInputs().get(i).getKeyName()[j]);
                    list_Data_single.add(cmd);
                } else {
                    cmd.setKeyname("按键" + j);
                    list_Data_single.add(cmd);
                }
            }
            boolean isContain = false;
            for (int z = 0; z < ChnOpItem.size(); z++) {
                if (ChnOpItem.get(z).getCancupid().equals(MyApplication.getWareData().getKeyInputs().get(i).getCanCpuID())) {
                    isContain = true;
                }
            }
            if (!isContain && !(listData_double.size() > MyApplication.getWareData().getKeyInputs().size())) {
                listData_double.add(list_Data_single);
            }
        }

        if (ChnOpItem != null || ChnOpItem.size() > 0)
            for (int k = 0; k < ChnOpItem.size(); k++) {
                List<Integer> list_Key_up = Sixteen2Two.decimal2Binary(ChnOpItem.get(k).getKeyUpValid());
                for (int i = 0; i < list_Key_up.size(); i++) {
                    for (int j = 0; j < listData_double.get(k).size(); j++) {
                        if (list_Key_up.get(i) == j) {
                            listData_double.get(k).get(j).setDevUnitID(ChnOpItem.get(k).getCancupid());
                            listData_double.get(k).get(j).setIndex(list_Key_up.get(i));
                            listData_double.get(k).get(j).setDevType(devType);
                            listData_double.get(k).get(j).setKey_cmd(ChnOpItem.get(k).getKeyUpCmd()[list_Key_up.get(i)]);
                            listData_double.get(k).get(j).setKeyAct_num(1);
                            listData_double.get(k).get(j).setSelect(true);
                        }
                    }
                }
            }
        list_Data_single = new ArrayList<>();
        for (int i = 0; i < listData_double.size(); i++) {
            for (int j = 0; j < listData_double.get(i).size(); j++) {
                list_Data_single.add(listData_double.get(i).get(j));
            }
        }
        List<GroupList_OutToInputData> outToInputData = new ArrayList<>();
        for (int i = 0; i < MyApplication.getWareData().getKeyInputs().size(); i++) {
            WareBoardKeyInput input = MyApplication.getWareData().getKeyInputs().get(i);
            GroupList_OutToInputData data = new GroupList_OutToInputData();
            data.setBoardName(input.getBoardName());
            data.setBoardType(input.getBoardType());
            data.setbResetKey(input.getbResetKey());
            data.setCanCpuID(input.getCanCpuID());
            data.setKeyAllCtrlType(input.getKeyAllCtrlType());
            data.setKeyCnt(input.getKeyCnt());
            data.setKeyIsSelect(input.getKeyIsSelect());
            data.setKeyName_rows(input.getKeyName());
            data.setLedBkType(input.getLedBkType());
            List<PrintCmd> PrintCmds = new ArrayList<>();
            for (int j = 0; j < list_Data_single.size(); j++) {
                if (list_Data_single.get(j).getKeyboardid().equals(input.getCanCpuID())) {
                    PrintCmds.add(list_Data_single.get(j));
                }
            }
            data.setPrintCmds(PrintCmds);
            outToInputData.add(data);
        }

        if (adapter != null)
            adapter.notifyDataSetChanged(outToInputData);
        else {
            adapter = new GroupList_OutToInputAdapter(this, outToInputData);
            lv.setAdapter(adapter);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ref_equipment:
                //刷新
                input_out_iv_noData.setVisibility(View.GONE);
                initData();
                break;
            case R.id.save_equipment:
                //保存
                //判断设备中有没有未设置的，有终止保存。无，继续保存。
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
                        if (list_Data_single.size() == 0)
                            return;
                        list_Data = new ArrayList<>();
                        for (int i = 0; i < list_Data_single.size(); i++) {
                            if (list_Data_single.get(i).isSelect()) {
                                if (list_Data_single.get(i).getKey_cmd() == 0) {
                                    ToastUtil.showText("存在未设置，请设置指令");
                                    return;
                                }
                                list_Data.add(list_Data_single.get(i));
                            }
                        }

                        //根据以上注释掉的数据结构，将已有数据已此格式寄存；

                        String key_cpuCanID = "";//按键板的id；
                        UpBoardKeyData data = new UpBoardKeyData();//上传数据实体；
                        List<UpBoardKeyData.ChnOpitemRowsBean> bean_list = new ArrayList<>();//按键板实体集合；

                        if (list_Data != null && list_Data.size() > 0) {
                            do {
                                List<PrintCmd> listData_id = new ArrayList<>();//实例化一个新的数据集合，存放相同按键板的按键；
                                listData_id.add(list_Data.get(0));//添加一个按键实体；
                                list_Data.remove(0);//在所有数据中移除此实体；
                                for (int m = list_Data.size() - 1; m >= 0; m--) {
                                    if (listData_id.get(0).getKeyboardid().equals(list_Data.get(m).getKeyboardid())) {
                                        listData_id.add(list_Data.get(m));//将所有数据中和第一个添加的按键作比较，相同的话加入新的实体集合，
//                            listData.remove(m);//在所有数据集合中移除；
                                    }
                                }
                                UpBoardKeyData.ChnOpitemRowsBean bean = data.new ChnOpitemRowsBean();//按键实体
                                byte[] Valid_down = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};//存放按下键的相应位置；
                                byte[] Valid_up = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};//存放弹起键相应位置；
                                byte[] Cmd_down = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};//存放按下键相应的命令
                                byte[] Cmd_up = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};//存放弹起键相应的命令；
                                for (int j = 0; j < listData_id.size(); j++) {//循环新的/相同的按键板集合；
                                    key_cpuCanID = listData_id.get(j).getKeyboardid();//为按键板id赋值；
                                    Valid_up[listData_id.get(j).getIndex()] = 1;
                                    Cmd_up[listData_id.get(j).getIndex()] = (byte) listData_id.get(j).getKey_cmd();
                                }

                                //因为数据传递时，高位、低位和现实中相反，so循环赋值；
                                String down_v = "";
                                for (int j = 0; j < Valid_down.length; j++) {
                                    down_v += Valid_down[Valid_down.length - 1 - j];
                                }
                                //将改好的2#字符串转成10#；
                                BigInteger bi_down = new BigInteger(down_v, 2);  //转换成BigInteger类型
                                int v_down = Integer.parseInt(bi_down.toString(10)); //参数2指定的是转化成X进制，默认10进制

                                String up_v = "";
                                for (int j = 0; j < Valid_up.length; j++) {
                                    up_v += Valid_up[Valid_down.length - 1 - j];
                                }
                                BigInteger bi_up = new BigInteger(up_v, 2);  //转换成BigInteger类型
                                int v_up = Integer.parseInt(bi_up.toString(10)); //参数2指定的是转化成X进制，默认10进制

                                //将相应的数据加入到上传按键实体中；
                                bean.setKey_cpuCanID(key_cpuCanID);
                                bean.setKeyDownValid(v_down);
                                bean.setKeyUpValid(v_up);
                                bean.setKeyDownCmd(Cmd_down);
                                bean.setKeyUpCmd(Cmd_up);
                                //将每个上传按键实体加入实体集合中；
                                bean_list.add(bean);

                            } while (list_Data.size() > 0);
                        }else {
                            ToastUtil.showText("不能少于一个配置项");
                            return;
                        }
                        //将以上数据加入到上传实体中；
                        data.setDevUnitID(GlobalVars.getDevid());
                        data.setChn_opitem_rows(bean_list);
                        data.setDatType(BOARD_UP);
                        data.setSubType1(0);
                        data.setSubType2(0);
                        data.setDevType(devType);
                        data.setDevID(devId);
                        data.setOut_cpuCanID(uid);
                        data.setChn_opitem(bean_list.size());

                        Gson gson = new Gson();
                        System.out.println(gson.toJson(data));
                        MyApplication.mApplication.getUdpServer().send(gson.toJson(data), 15);
                        MyApplication.mApplication.showLoadDialog(EquipmentDeployActivity.this);
                    }
                });
                builder.create().show();
                break;
        }
    }
}
