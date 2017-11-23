package cn.etsoft.smarthome.ui.Setting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.PopupWindowAdapter;
import cn.etsoft.smarthome.domain.UdpProPkt;
import cn.etsoft.smarthome.domain.WareAirCondDev;
import cn.etsoft.smarthome.domain.WareCurtain;
import cn.etsoft.smarthome.domain.WareDev;
import cn.etsoft.smarthome.domain.WareFloorHeat;
import cn.etsoft.smarthome.domain.WareFreshAir;
import cn.etsoft.smarthome.domain.WareLight;
import cn.etsoft.smarthome.pullmi.common.CommonUtils;
import cn.etsoft.smarthome.pullmi.utils.LogUtils;
import cn.etsoft.smarthome.utils.SendDataUtil;
import cn.etsoft.smarthome.utils.ToastUtil;

/**
 * Created by fbl on 16-11-17.
 * 设备详情页面
 */
public class Devs_Detail_Activity extends Activity implements View.OnClickListener {

    private TextView dev_room, dev_save, title, dev_way, dev_test, delete;
    private EditText dev_name;
    private ImageView back, dev_type;
    private WareDev dev;
    private int id, type;
    private String cpu;
    private PopupWindow popupWindow;
    private List<String> message_save;
    private PopupWindowAdapter_channel popupWindowAdapter_channel;
    // Hashtable.keySet()降序 TreeMap.keySet()升序 HashMap.keySet()乱序 LinkedHashMap.keySet()原序
    private TreeMap<Integer, Boolean> map = new TreeMap<>();// 存放已被选中的CheckBox


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dec_detail_activity);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.AppTheme_color));

        id = getIntent().getIntExtra("id", 0);
        type = getIntent().getIntExtra("type", 0);
        cpu = getIntent().getStringExtra("cpu");
        initView(true);
        MyApplication.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int datType, int subtype1, int subtype2) {
                if (datType == 3 || datType == 4 || datType == 35
                        || (datType == 3 && subtype2 == 7) || (datType == 3 && subtype2 == 9)) {
                    initView(false);
                }
                if (datType == 6) {
                    if (subtype2 == 1) {
                        ToastUtil.showText("操作成功");
                        MyApplication.mApplication.dismissLoadDialog();
                        finish();
                    } else {
                        ToastUtil.showText("操作失败");
                    }
                }
                if (datType == 7) {
                    if (subtype2 == 1) {
                        MyApplication.mApplication.dismissLoadDialog();
                        ToastUtil.showText("操作成功");
                        finish();
                    } else {
                        ToastUtil.showText("操作失败");
                    }
                }
            }
        });
    }

    /**
     * 初始化组件以及数据
     */
    int count = 0;

    private void initView(boolean isRef) {
        message_save = new ArrayList<>();
        title = (TextView) findViewById(R.id.dev_info_title);
        dev_type = (ImageView) findViewById(R.id.dev_type);
        back = (ImageView) findViewById(R.id.back);
        dev_room = (TextView) findViewById(R.id.dev_room);
        dev_save = (TextView) findViewById(R.id.dev_save);
        dev_name = (EditText) findViewById(R.id.dev_name);
        dev_way = (TextView) findViewById(R.id.dev_way);
        dev_test = (TextView) findViewById(R.id.dev_test);
        delete = (TextView) findViewById(R.id.dev_delete);

        for (int i = 0; i < MyApplication.getWareData().getDevs().size(); i++) {
            WareDev wareDev = MyApplication.getWareData().getDevs().get(i);
            if (wareDev.getCanCpuId().equals(cpu)
                    && wareDev.getType() == type
                    && wareDev.getDevId() == id) {
                dev = wareDev;
            }
        }
        if (dev == null) {
            ToastUtil.showText("数据错误");
            finish();
            return;
        }
        title.setText(dev.getDevName());
        if (isRef)
        dev_name.setText(dev.getDevName());
        dev_room.setText(dev.getRoomName());

        if (dev.getType() == 0) {
            for (int i = 0; i < MyApplication.getWareData().getAirConds().size(); i++) {
                WareAirCondDev airCondDev = MyApplication.getWareData().getAirConds().get(i);
                if (dev.getDevId() == airCondDev.getDev().getDevId()
                        && dev.getCanCpuId().equals(airCondDev.getDev().getCanCpuId())) {
                    if (airCondDev.getbOnOff() == 0) {
                        dev_type.setImageResource(R.drawable.kongtiao);
                    } else dev_type.setImageResource(R.drawable.kongtiao2);
                    //可视布局数据

                    int Way_num = airCondDev.getPowChn();
                    String Way_str = new StringBuffer(Integer.toBinaryString(Way_num)).reverse().toString();
                    String Way_ok = "";
                    for (int j = 0; j < Way_str.length(); j++) {
                        if (Way_str.charAt(j) == '1') {
                            Way_ok += j + 1 + "、";
                        }
                    }
                    if (!"".equals(Way_ok))
                        Way_ok = Way_ok.substring(0, Way_ok.lastIndexOf("、"));
                    dev_way.setText(Way_ok);

                    final WareAirCondDev finalair = airCondDev;
                    dev_test.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int cmdValue = 0;
                            if (finalair.getbOnOff() == 0) {
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_pwrOn.getValue();//打开空调
                            } else {
                                cmdValue = UdpProPkt.E_AIR_CMD.e_air_pwrOff.getValue();//关闭空调
                            }
                            int value = (0 << 5) | cmdValue;
                            SendDataUtil.controlDev(dev, value);
                        }
                    });
                }
            }
        } else if (dev.getType() == 1) {
            dev_type.setImageResource(R.drawable.tv1);
            dev_way.setText("此设备无通道");
            dev_way.setClickable(false);
        } else if (dev.getType() == 2) {
            dev_type.setImageResource(R.drawable.jidinghe1);
            dev_way.setText("此设备无通道");
            dev_way.setClickable(false);
        } else if (dev.getType() == 3) {
            for (int i = 0; i < MyApplication.getWareData().getLights().size(); i++) {
                WareLight light = MyApplication.getWareData().getLights().get(i);
                if (light.getDev().getDevId() == dev.getDevId()
                        && dev.getCanCpuId().equals(light.getDev().getCanCpuId())) {

                    int PowChn = light.getPowChn();
                    dev_way.setText((PowChn + 1) + "");
                    if (light.getbOnOff() == 0) {
                        dev_type.setImageResource(R.drawable.dengguang);
                    } else dev_type.setImageResource(R.drawable.light_icon);

                    final WareLight finallight = light;
                    dev_test.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (finallight.getbOnOff() == 0) {
                                SendDataUtil.controlDev(dev, 0);
                            } else {
                                SendDataUtil.controlDev(dev, 1);
                            }
                        }
                    });
                }
            }
        } else if (dev.getType() == 4) {
            dev_type.setImageResource(R.drawable.curtains_icon);
            for (int i = 0; i < MyApplication.getWareData().getCurtains().size(); i++) {
                WareCurtain curtain = MyApplication.getWareData().getCurtains().get(i);
                if (curtain.getDev().getDevId()
                        == dev.getDevId() && dev.getCanCpuId()
                        .equals(curtain.getDev().getCanCpuId())) {
                    int Way_num = curtain.getDev().getPowChn();
                    String Way_str = new StringBuffer(Integer.toBinaryString(Way_num)).reverse().toString();
                    String Way_ok = "";
                    for (int j = 0; j < Way_str.length(); j++) {
                        if (Way_str.charAt(j) == '1') {
                            Way_ok += j + 1 + "、";
                        }
                    }
                    if (!"".equals(Way_ok))
                        Way_ok = Way_ok.substring(0, Way_ok.lastIndexOf("、"));
                    dev_way.setText(Way_ok);
                    dev_test.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (count % 2 == 0) {
                                SendDataUtil.controlDev(dev, UdpProPkt.E_CURT_CMD.e_curt_offOn.getValue());
                            } else {
                                SendDataUtil.controlDev(dev, UdpProPkt.E_CURT_CMD.e_curt_offOff.getValue());
                            }
                            count++;
                        }
                    });
                }
            }
        } else if (dev.getType() == 7) {
            for (int i = 0; i < MyApplication.getWareData().getFreshAirs().size(); i++) {
                WareFreshAir freshAir = MyApplication.getWareData().getFreshAirs().get(i);
                if (freshAir.getDev().getDevId()
                        == dev.getDevId() && dev.getCanCpuId()
                        .equals(freshAir.getDev().getCanCpuId())) {
                    int PowChn = freshAir.getPowChn();
                    dev_way.setText((freshAir.getOnOffChn() + 1) + "、"
                            + (freshAir.getSpdLowChn() + 1) + "、" + (freshAir.getSpdMidChn() + 1)
                            + "、" + (freshAir.getSpdHighChn() + 1));
                    if (freshAir.getbOnOff() == 0) {
                        dev_type.setImageResource(R.drawable.freshair_close);
                    } else dev_type.setImageResource(R.drawable.freshair_icon);

                    final WareFreshAir finalfreshAir = freshAir;
                    dev_test.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (finalfreshAir.getbOnOff() == 1) {
                                SendDataUtil.controlDev(dev, UdpProPkt.E_FRESHAIR_CMD.e_freshair_close.getValue());
                            } else {
                                SendDataUtil.controlDev(dev, UdpProPkt.E_FRESHAIR_CMD.e_freshair_open.getValue());
                            }
                        }
                    });
                }
            }
        } else if (dev.getType() == 9) {
            for (int i = 0; i < MyApplication.getWareData().getFloorHeat().size(); i++) {
                WareFloorHeat floorHeat = MyApplication.getWareData().getFloorHeat().get(i);
                if (floorHeat.getDev().getDevId()
                        == dev.getDevId() && dev.getCanCpuId()
                        .equals(floorHeat.getDev().getCanCpuId())) {
                    int PowChn = floorHeat.getPowChn();
                    dev_way.setText((PowChn + 1) + "");
                    if (floorHeat.getbOnOff() == 0) {
                        dev_type.setImageResource(R.drawable.floorheat_close);
                    } else dev_type.setImageResource(R.drawable.floorheat_icon);


                    final WareFloorHeat finalfloorHeat = floorHeat;
                    dev_test.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (finalfloorHeat.getDev().getbOnOff() == 1) {
                                SendDataUtil.controlDev(dev, UdpProPkt.E_FLOOR_HEAT_CMD.e_floorHeat_close.getValue());
                            } else
                                SendDataUtil.controlDev(dev, UdpProPkt.E_FLOOR_HEAT_CMD.e_floorHeat_open.getValue());
                        }
                    });
                }
            }
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        dev_way.setOnClickListener(this);
        dev_save.setOnClickListener(this);
        dev_room.setOnClickListener(this);
        delete.setOnClickListener(this);
    }

    /**
     * 初始化自定义设备的状态以及设备PopupWindow
     */
    private void initPopupWindow(final View textView, final List<String> text) {
        //获取自定义布局文件pop.xml的视图
        final View customView = getLayoutInflater().from(this).inflate(R.layout.popupwindow_equipment_listview, null);
        customView.setBackgroundResource(R.drawable.selectbg);

        // 创建PopupWindow实例

        popupWindow = new PopupWindow(findViewById(R.id.popupWindow_equipment_sv), textView.getWidth(), 250);

        popupWindow.setContentView(customView);
        ListView list_pop = (ListView) customView.findViewById(R.id.popupWindow_equipment_lv);
        PopupWindowAdapter adapter = new PopupWindowAdapter(text, this);
        list_pop.setAdapter(adapter);
        list_pop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) textView;
                tv.setText(text.get(position));
                popupWindow.dismiss();
            }
        });
        //popupwindow页面之外可点
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.update();
        // 自定义view添加触摸事件
        customView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
                return false;
            }
        });
    }

    private List<Integer> list_channel;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dev_save:
                String Save_DevName;
                String Save_Roomname;
                String[] WayStr_ok = null;
                int Save_DevWay = 0;

                //设备名数据处理
                Save_DevName = dev_name.getText().toString();
                if ("".equals(Save_DevName))
                    Save_DevName = dev_name.getHint().toString();
                if (Save_DevName.length() > 6) {
                    ToastUtil.showText("设备名称过长");
                    return;
                }
                if ("".equals(Save_DevName)) {
                    ToastUtil.showText("请输入设备名称");
                    return;
                }
                try {
                    Save_DevName = CommonUtils.bytesToHexString(Save_DevName.getBytes("GB2312"));
                } catch (UnsupportedEncodingException e) {
                    ToastUtil.showText("设备名称不合适");
                    return;
                }
                //房间名数据处理
                Save_Roomname = dev_room.getText().toString();
                try {
                    Save_Roomname = CommonUtils.bytesToHexString(Save_Roomname.getBytes("GB2312"));
                } catch (UnsupportedEncodingException e) {
                    ToastUtil.showText("房间名称不合适");
                    return;
                }


                if (dev.getType() == 0) {
                    for (int i = 0; i < MyApplication.getWareData().getAirConds().size(); i++) {
                        //设备通道 保存数据处理
                        String Way_Str = dev_way.getText().toString();
                        String[] WayStr_ok_air = Way_Str.split("、");
                        if (WayStr_ok_air.length == 0) {
                            ToastUtil.showText("请选择通道");
                            return;
                        } else {
                            if (WayStr_ok_air.length != 5) {//135
                                ToastUtil.showText("空调是5个通道");
                                return;
                            }
                            String Way = "";
                            for (int j = 0; j < 12; j++) {
                                boolean IsEnter = false;
                                for (int k = 0; k < WayStr_ok_air.length; k++) {
                                    if (j == Integer.parseInt(WayStr_ok_air[k]) - 1) {
                                        Way += "1";
                                        IsEnter = true;
                                    }
                                }
                                if (!IsEnter) {
                                    Way += "0";
                                }
                            }
                            Save_DevWay = Integer.parseInt(new StringBuffer(Way).reverse().toString(), 2);
                        }
                    }
                } else if (dev.getType() == 3) {
                    //设备通道 保存数据处理
                    String Way_Str = dev_way.getText().toString();
                    if (Way_Str.length() == 0) {
                        ToastUtil.showText("请选择通道");
                        return;
                    } else if (Way_Str.contains("、")) {
                        ToastUtil.showText("灯光只能有一个通道");
                        return;
                    }
                    Save_DevWay = Integer.parseInt(Way_Str) - 1;
                } else if (dev.getType() == 4) {

                    //设备通道 保存数据处理
                    String Way_Str = dev_way.getText().toString();
                    String[] WayStr_ok_air = Way_Str.split("、");
                    if (WayStr_ok_air.length == 0) {
                        ToastUtil.showText("请选择通道");
                        return;
                    } else {
                        if (WayStr_ok_air.length != 2) {//135
                            ToastUtil.showText("窗帘是2个通道");
                            return;
                        }
                        String Way = "";
                        for (int j = 0; j < 12; j++) {
                            boolean IsEnter = false;
                            for (int k = 0; k < WayStr_ok_air.length; k++) {
                                if (j == Integer.parseInt(WayStr_ok_air[k]) - 1) {
                                    Way += "1";
                                    IsEnter = true;
                                }
                            }
                            if (!IsEnter) {
                                Way += "0";
                            }
                        }
                        Save_DevWay = Integer.parseInt(new StringBuffer(Way).reverse().toString(), 2);
                    }
                } else if (dev.getType() == 7) {
                    //设备通道 保存数据处理
                    String Way_Str = dev_way.getText().toString();
                    WayStr_ok = Way_Str.split("、");
                    if (WayStr_ok.length != 4) {
                        ToastUtil.showText("新风是4个通道");
                        return;
                    }
                } else if (dev.getType() == 9) {
                    //设备通道 保存数据处理
                    String Way_Str = dev_way.getText().toString();
                    if (Way_Str.length() == 0) {
                        ToastUtil.showText("请选择通道");
                        return;
                    } else if (Way_Str.contains("、")) {
                        ToastUtil.showText("地暖只能有一个通道");
                        return;
                    }
                    Save_DevWay = Integer.parseInt(Way_Str) - 1;
                }
                String chn_str = "";
                if (dev.getType() == 7) {
                    for (int i = 0; i < MyApplication.getWareData().getFreshAirs().size(); i++) {

                        WareFreshAir freshAir = MyApplication.getWareData().getFreshAirs().get(i);

                        if (dev.getCanCpuId().equals(freshAir.getDev().getCanCpuId())
                                && dev.getType() == freshAir.getDev().getType()
                                && dev.getDevId() == freshAir.getDev().getDevId()) {
                            chn_str = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"," +
                                    "\"datType\":" + 6 + "," +
                                    "\"subType1\":0," +
                                    "\"subType2\":0," +
                                    "\"canCpuID\":\"" + freshAir.getDev().getCanCpuId() + "\"," +
                                    "\"devType\":" + freshAir.getDev().getType() + "," +
                                    "\"devID\":" + freshAir.getDev().getDevId() + "," +
                                    "\"devName\":" + "\"" + Save_DevName + "\"," +
                                    "\"roomName\":" + "\"" + Save_Roomname + "\"," +
                                    "\"spdLowChn\":" + (Integer.parseInt(WayStr_ok[1]) - 1) + "," +
                                    "\"spdMidChn\":" + (Integer.parseInt(WayStr_ok[2]) - 1) + "," +
                                    "\"spdHighChn\":" + (Integer.parseInt(WayStr_ok[3]) - 1) + "," +
                                    "\"autoRun\":" + freshAir.getValPm10() + "," +
                                    "\"valPm10\":" + 0 + "," +
                                    "\"valPm25\":" + 0 + "," +
                                    "\"cmd\":" + 1 + "," +
                                    "\"powChn\":" + (Integer.parseInt(WayStr_ok[0]) - 1) + "}";

                        }
                    }
                } else if (dev.getType() == 9) {
                    for (int i = 0; i < MyApplication.getWareData().getFloorHeat().size(); i++) {
                        WareFloorHeat floorHeat = MyApplication.getWareData().getFloorHeat().get(i);
                        if (dev.getType() == floorHeat.getDev().getType()
                                && dev.getDevId() == floorHeat.getDev().getDevId()
                                && dev.getCanCpuId().equals(floorHeat.getDev().getCanCpuId()))
                            chn_str = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"," +
                                    "\"datType\":" + 6 + "," +
                                    "\"subType1\":0," +
                                    "\"subType2\":0," +
                                    "\"canCpuID\":\"" + dev.getCanCpuId() + "\"," +
                                    "\"devType\":" + dev.getType() + "," +
                                    "\"devID\":" + dev.getDevId() + "," +
                                    "\"devName\":" + "\"" + Save_DevName + "\"," +
                                    "\"roomName\":" + "\"" + Save_Roomname + "\"," +
                                    "\"bOnOff\":" + floorHeat.getDev().getbOnOff() + "," +
                                    "\"tempget\":" + floorHeat.getTempget() + "," +
                                    "\"tempset\":" + floorHeat.getTempset() + "," +
                                    "\"autoRun\":" + floorHeat.getAutoRun() + "," +
                                    "\"cmd\":" + 1 + "," +
                                    "\"powChn\":" + Save_DevWay + "}";
                    }
                } else {
                    chn_str = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"," +
                            "\"datType\":" + 6 + "," +
                            "\"subType1\":0," +
                            "\"subType2\":0," +
                            "\"canCpuID\":\"" + dev.getCanCpuId() + "\"," +
                            "\"devType\":" + dev.getType() + "," +
                            "\"devID\":" + dev.getDevId() + "," +
                            "\"devName\":" + "\"" + Save_DevName + "\"," +
                            "\"roomName\":" + "\"" + Save_Roomname + "\"," +
                            "\"powChn\":" + Save_DevWay + "," +
                            "\"cmd\":" + 1 + "}";
                }
                MyApplication.mApplication.showLoadDialog(Devs_Detail_Activity.this);
                MyApplication.mApplication.getUdpServer().send(chn_str, 6);
                break;

            case R.id.dev_room:
                final List<String> home_text = new ArrayList<>();
                List<WareDev> mWareDev_room = new ArrayList<>();

                for (int i = 0; i < MyApplication.getWareData().getDevs().size(); i++) {
                    mWareDev_room.add(MyApplication.getWareData().getDevs().get(i));
                }
                for (int i = 0; i < mWareDev_room.size() - 1; i++) {
                    for (int j = mWareDev_room.size() - 1; j > i; j--) {
                        if (mWareDev_room.get(i).getRoomName().equals(mWareDev_room.get(j).getRoomName())) {
                            mWareDev_room.remove(j);
                        }
                    }
                }
                for (int i = 0; i < mWareDev_room.size(); i++) {
                    home_text.add(mWareDev_room.get(i).getRoomName());
                }

                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                } else {
                    initPopupWindow(v, home_text);
                    popupWindow.showAsDropDown(v, 0, 0);
                }
                break;
            case R.id.dev_way:
                List<Integer> list_voard_cancpuid = new ArrayList<>();
                List<WareDev> boardDev = new ArrayList<>();
                for (int z = 0; z < MyApplication.getWareData().getDevs().size(); z++) {
                    if (dev.getCanCpuId().equals(MyApplication.getWareData().getDevs().get(z).getCanCpuId()))
                        boardDev.add(MyApplication.getWareData().getDevs().get(z));
                }
                for (int z = 0; z < boardDev.size(); z++) {
                    WareDev dev_inner = boardDev.get(z);
                    if (!(dev_inner.getType() == dev.getType()
                            && dev_inner.getDevId() == dev.getDevId()
                            && dev_inner.getCanCpuId().equals(dev.getCanCpuId()))) {
                        if (dev_inner.getType() == 0) {
                            for (int i = 0; i < MyApplication.getWareData().getAirConds().size(); i++) {
                                WareAirCondDev airCondDev = MyApplication.getWareData().getAirConds().get(i);
                                if (dev_inner.getDevId() == airCondDev.getDev().getDevId()
                                        && dev_inner.getCanCpuId().equals(airCondDev.getDev().getCanCpuId())) {
                                    int PowChn = airCondDev.getPowChn();
                                    String PowChnList = Integer.toBinaryString(PowChn);
                                    PowChnList = new StringBuffer(PowChnList).reverse().toString();
                                    List<Integer> index_list = new ArrayList<>();
                                    for (int j = 0; j < PowChnList.length(); j++) {
                                        if (PowChnList.charAt(j) == '1') {
                                            index_list.add(j + 1);
                                        }
                                    }
                                    list_voard_cancpuid.addAll(index_list);
                                }
                            }
                        } else if (dev_inner.getType() == 3) {
                            for (int i = 0; i < MyApplication.getWareData().getLights().size(); i++) {
                                WareLight light = MyApplication.getWareData().getLights().get(i);
                                if (dev_inner.getDevId() == light.getDev().getDevId()
                                        && dev_inner.getCanCpuId().equals(light.getDev().getCanCpuId())) {
                                    int PowChn = light.getPowChn() + 1;
                                    list_voard_cancpuid.add(PowChn);
                                }
                            }
                        } else if (dev_inner.getType() == 4) {
                            for (int i = 0; i < MyApplication.getWareData().getCurtains().size(); i++) {
                                WareCurtain curtain = MyApplication.getWareData().getCurtains().get(i);
                                if (dev_inner.getDevId() == curtain.getDev().getDevId()
                                        && dev_inner.getCanCpuId().equals(curtain.getDev().getCanCpuId())) {
                                    int PowChn = curtain.getPowChn();
                                    String PowChnList = Integer.toBinaryString(PowChn);
                                    PowChnList = new StringBuffer(PowChnList).reverse().toString();
                                    List<Integer> index_list = new ArrayList<>();
                                    for (int j = 0; j < PowChnList.length(); j++) {
                                        if (PowChnList.charAt(j) == '1') {
                                            index_list.add(j + 1);
                                        }
                                    }
                                    list_voard_cancpuid.addAll(index_list);
                                }
                            }
                        } else if (dev_inner.getType() == 7) {
                            for (int i = 0; i < MyApplication.getWareData().getFreshAirs().size(); i++) {
                                WareFreshAir freshAir = MyApplication.getWareData().getFreshAirs().get(i);
                                if (dev_inner.getDevId() == freshAir.getDev().getDevId()
                                        && dev_inner.getCanCpuId().equals(freshAir.getDev().getCanCpuId())) {
                                    list_voard_cancpuid.add(freshAir.getOnOffChn() + 1);
                                    list_voard_cancpuid.add(freshAir.getSpdHighChn() + 1);
                                    list_voard_cancpuid.add(freshAir.getSpdLowChn() + 1);
                                    list_voard_cancpuid.add(freshAir.getSpdMidChn() + 1);
                                }
                            }
                        } else if (dev_inner.getType() == 9) {
                            for (int i = 0; i < MyApplication.getWareData().getFloorHeat().size(); i++) {
                                WareFloorHeat floorHeat = MyApplication.getWareData().getFloorHeat().get(i);
                                if (dev_inner.getDevId() == floorHeat.getDev().getDevId()
                                        && dev_inner.getCanCpuId().equals(floorHeat.getDev().getCanCpuId())) {
                                    int PowChn = floorHeat.getPowChn() + 1;
                                    list_voard_cancpuid.add(PowChn);
                                }
                            }
                        }
                    }
                }

                List<Integer> list_channel = new ArrayList<>();
                for (int i = 1; i < 13; i++) {
                    list_channel.add(i);
                }

                for (int i = 0; i < list_voard_cancpuid.size(); i++) {
                    for (int j = 0; j < list_channel.size(); j++) {
                        if (list_channel.get(j) == list_voard_cancpuid.get(i)) {
                            list_channel.remove(j);
                        }
                    }
                }
                boolean[] isSelect = new boolean[list_channel.size()];
                if (list_channel.size() == 0) {
                    ToastUtil.showText("没有可用通道");
                } else {
//                        initPopupWindow_channel(v, list_channel);
//                        popupWindow.showAsDropDown(v, 0, 0);
                    initPopupWindow_channel(v, list_channel);
                    popupWindow.showAsDropDown(v, 0, 0);
                }
                break;
            case R.id.dev_delete:
                SendDataUtil.deleteDev(dev);
                MyApplication.mApplication.showLoadDialog(this);
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

    /**
     * 初始化自定义设备的状态以及设备PopupWindow
     */
    @SuppressLint("NewApi")
    private void initPopupWindow_channel(final View textView, List<Integer> list_channel) {
        //获取自定义布局文件pop.xml的视图
        final View customView = getLayoutInflater().from(this).inflate(R.layout.popupwindow_equipment_listview2, null);
        customView.setBackgroundResource(R.drawable.selectbg);
        customView.setFocusable(true);
        customView.setFocusableInTouchMode(true);
        customView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (popupWindow != null) {
                        popupWindow.dismiss();
                    }
                }
                return false;
            }
        });
        // 创建PopupWindow实例
        popupWindow = new PopupWindow(customView, textView.getWidth(), 400);
        ListView list_pop = (ListView) customView.findViewById(R.id.popupWindow_equipment_lv);
        Button time_ok = (Button) customView.findViewById(R.id.time_ok);
        Button time_cancel = (Button) customView.findViewById(R.id.time_cancel);
        popupWindowAdapter_channel = new PopupWindowAdapter_channel(Devs_Detail_Activity.this, list_channel);
        list_pop.setAdapter(popupWindowAdapter_channel);
        time_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] data = new int[12];
                String message = "";
                if (map.keySet().toArray().length == 0) {
                    ToastUtil.showText("请选择设备通道");
                    return;
                }
                for (int i = 0; i < 12; i++) {
                    for (int k = 0; k < map.keySet().toArray().length; k++) {
                        int key = (Integer) map.keySet().toArray()[k];
                        if (i == (key - 1)) {
                            data[i] = 1;
                            break;
                        } else data[i] = 0;
                    }
                }
                String data_str = "";
                for (int i = 0; i < data.length; i++) {
                    data_str += data[i];
                }
                for (int i = 0; i < map.keySet().toArray().length; i++) {
                    message += String.valueOf(map.keySet().toArray()[i]) + "、";

                    message_save.add(String.valueOf(map.keySet().toArray()[i]));
                    Log.i("测试", String.valueOf(message_save));
                }
                if (!"".equals(message)) {
                    message = message.substring(0, message.lastIndexOf("、"));
                }
                TextView tv = (TextView) textView;
                tv.setText(message);
                popupWindow.dismiss();
                map.clear();
            }
        });
        time_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (map != null) {
                    map.clear();
                }
                popupWindow.dismiss();
            }
        });
        //popupWindow页面之外可点
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.update();
        // 自定义view添加触摸事件
        customView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
                return false;
            }
        });
    }

    /**
     * 防区的适配器
     */
    private class ViewHolder {
        public TextView text;
        public CheckBox checkBox;
    }

    private class PopupWindowAdapter_channel extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mLayoutInflater;
        private List<Integer> list_channel;


        public PopupWindowAdapter_channel(Context context, List<Integer> list_channel) {
            mContext = context;
            this.list_channel = list_channel;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            if (null != list_channel) {
                return list_channel.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            return list_channel.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.popupwindow_listview_item2, null);
                viewHolder = new ViewHolder();
                viewHolder.text = (TextView) convertView.findViewById(R.id.popupWindow_equipment_tv);
                viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.popupWindow_equipment_cb);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.text.setText(String.valueOf(list_channel.get(position)));
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked == true) {
                        map.put(list_channel.get(position), true);
                    } else {
                        map.remove(list_channel.get(position));
                    }
                }
            });
            if (map != null && map.containsKey(list_channel.get(position))) {
                viewHolder.checkBox.setChecked(true);
            } else {
                viewHolder.checkBox.setChecked(false);
            }
            return convertView;
        }
    }

    /**
     * 得到字符串中的数字和
     *
     * @param str
     * @return
     */
    public int str2num(String str) {
        str = reverseString(str);
        return Integer.valueOf(str, 2);
    }

    /**
     * 倒置字符串
     *
     * @param str
     * @return
     */
    public static String reverseString(String str) {
        char[] arr = str.toCharArray();
        int middle = arr.length >> 1;//EQ length/2
        int limit = arr.length - 1;
        for (int i = 0; i < middle; i++) {
            char tmp = arr[i];
            arr[i] = arr[limit - i];
            arr[limit - i] = tmp;
        }
        return new String(arr);
    }
}
