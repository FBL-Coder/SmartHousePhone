package cn.etsoft.smarthome.ui.Setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.PopupWindowAdapter;
import cn.etsoft.smarthome.domain.WareAirCondDev;
import cn.etsoft.smarthome.domain.WareBoardChnout;
import cn.etsoft.smarthome.domain.WareDev;
import cn.etsoft.smarthome.pullmi.common.CommonUtils;
import cn.etsoft.smarthome.pullmi.utils.LogUtils;
import cn.etsoft.smarthome.utils.ToastUtil;

/**
 * Created by fbl on 16-11-17.
 * 添加设备
 */
public class Add_Dev_Activity extends Activity implements View.OnClickListener {

    private TextView title, add_dev_type, add_dev_room, add_dev_board, add_dev_save, add_dev_way;
    private EditText add_dev_name, edit_dev_room;
    private ImageView back, edit_roomname;
    private PopupWindow popupWindow;
    private List<String> Board_text;
    private List<WareBoardChnout> list_board;
    private List<String> home_text;
    private List<String> type_text;
    private boolean IsSave = true;
    private int type_position = 0;
    private int board_position = 0;
    private List<String> message_save;
    private PopupWindowAdapter_channel popupWindowAdapter_channel;
    private TreeMap<Integer, Boolean> map = new TreeMap<>();// 存放已被选中的CheckBox


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dev);
        initView();
    }

    private void initView() {

        title = (TextView) findViewById(R.id.title_bar_tv_title);
        add_dev_type = (TextView) findViewById(R.id.add_dev_type);
        add_dev_room = (TextView) findViewById(R.id.add_dev_room);
        add_dev_save = (TextView) findViewById(R.id.add_dev_save);
        add_dev_board = (TextView) findViewById(R.id.add_dev_board);
        add_dev_name = (EditText) findViewById(R.id.add_dev_name);
        edit_dev_room = (EditText) findViewById(R.id.edit_dev_room);
        add_dev_way = (TextView) findViewById(R.id.add_dev_way);
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        edit_roomname = (ImageView) findViewById(R.id.edit_roomname);

        edit_roomname.setOnClickListener(this);
        back.setOnClickListener(this);
        add_dev_type.setOnClickListener(this);
        add_dev_room.setOnClickListener(this);
        add_dev_board.setOnClickListener(this);
        add_dev_save.setOnClickListener(this);
        add_dev_way.setOnClickListener(this);

        Board_text = new ArrayList<>();
        list_board = MyApplication.getWareData().getBoardChnouts();
        for (int i = 0; i < list_board.size(); i++) {
            Board_text.add(list_board.get(i).getBoardName());
        }

        // 创建PopupWindow实例
        home_text = new ArrayList<>();
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
        type_text = new ArrayList<>();
        type_text.add("空调");
        type_text.add("电视");
        type_text.add("机顶盒");
        type_text.add("灯光");
        type_text.add("窗帘");
        type_text.add("新风");
        type_text.add("地暖");
        title.setText("添加设备");
        if (type_text != null && type_text.size() != 0 &&
                Board_text != null && Board_text.size() != 0 &&
                home_text != null && home_text.size() != 0) {
            add_dev_type.setText(type_text.get(0));
            add_dev_board.setText(Board_text.get(0));
            add_dev_room.setText(home_text.get(0));
        } else {
            ToastUtil.showText("没有数据");
            return;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_bar_iv_back:
                finish();
                break;
            case R.id.edit_roomname:
                if (add_dev_room.getVisibility() == View.VISIBLE) {
                    edit_dev_room.setVisibility(View.VISIBLE);
                    add_dev_room.setVisibility(View.GONE);
                } else {
                    edit_dev_room.setVisibility(View.GONE);
                    add_dev_room.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.add_dev_type:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                } else {
                    initPopupWindow(type_text, 1);
                    popupWindow.showAsDropDown(v, 0, 0);
                }
                break;
            case R.id.add_dev_room:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                } else {
                    initPopupWindow(home_text, 2);
                    popupWindow.showAsDropDown(v, 0, 0);
                }
                break;
            case R.id.add_dev_board:
                if ("机顶盒".equals(add_dev_type.getText()) ||
                        "电视".equals(add_dev_type.getText())) {
                    add_dev_board.setText("此设备不需要输出板");
                    return;
                }
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                } else {
                    initPopupWindow(Board_text, 3);
                    popupWindow.showAsDropDown(v, 0, 0);
                }
                break;
            case R.id.add_dev_way:
                if (board_position == -1) {
                    ToastUtil.showText("请选择输出板");
                    return;
                }
                List<Integer> list_voard_cancpuid = new ArrayList<>();

                for (int z = 0; z < MyApplication.getWareData().getDevs().size(); z++) {
                    WareDev dev = MyApplication.getWareData().getDevs().get(z);
                    if (dev.getType() == 0) {
                        for (int i = 0; i < MyApplication.getWareData().getAirConds().size(); i++) {
                            WareAirCondDev airCondDev = MyApplication.getWareData().getAirConds().get(i);

                            if (list_board.get(board_position).getDevUnitID().equals(airCondDev.getDev().getCanCpuId())) {
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
                    } else if (dev.getType() == 3) {
                        for (int i = 0; i < MyApplication.getWareData().getLights().size(); i++) {

                            if (list_board.get(board_position).getDevUnitID().equals(
                                    MyApplication.getWareData().getLights().get(i).getDev().getCanCpuId())) {
                                int PowChn = MyApplication.getWareData().getLights().get(i).getPowChn() + 1;
                                list_voard_cancpuid.add(PowChn);
                            }
                        }
                    } else if (dev.getType() == 4) {
                        for (int i = 0; i < MyApplication.getWareData().getCurtains().size(); i++) {
                            if (list_board.get(board_position).getDevUnitID().equals(
                                    MyApplication.getWareData().getCurtains().get(i).getDev().getCanCpuId())) {
                                int PowChn = MyApplication.getWareData().getCurtains().get(i).getDev().getPowChn() + 1;
                                list_voard_cancpuid.add(PowChn);
                            }
                        }
                    } else if (dev.getType() == 7) {
                        for (int i = 0; i < MyApplication.getWareData().getFreshAirs().size(); i++) {

                            if (list_board.get(board_position).getDevUnitID().equals(
                                    MyApplication.getWareData().getFreshAirs().get(i).getDev().getCanCpuId())) {
                                list_voard_cancpuid.add(MyApplication.getWareData().getFreshAirs().get(i).getOnOffChn() + 1);
                                list_voard_cancpuid.add(MyApplication.getWareData().getFreshAirs().get(i).getSpdHighChn() + 1);
                                list_voard_cancpuid.add(MyApplication.getWareData().getFreshAirs().get(i).getSpdLowChn() + 1);
                                list_voard_cancpuid.add(MyApplication.getWareData().getFreshAirs().get(i).getSpdMidChn() + 1);
                            }
                        }
                    } else if (dev.getType() == 9) {
                        for (int i = 0; i < MyApplication.getWareData().getFloorHeat().size(); i++) {
                            if (list_board.get(board_position).getDevUnitID().equals(
                                    MyApplication.getWareData().getFloorHeat().get(i).getDev().getCanCpuId())) {
                                int PowChn = MyApplication.getWareData().getFloorHeat().get(i).getPowChn() + 1;
                                list_voard_cancpuid.add(PowChn);
                            }
                        }
                    }
                }
                list_voard_cancpuid.size();
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
                    initPopupWindow_channel((TextView) v, list_channel);
                    popupWindow.showAsDropDown(v,0,0);
                }
                break;
            case R.id.add_dev_save:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("是否保存设置？");
                builder.setPositiveButton("是的", new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        String Save_DevName;
                        String Save_Roomname;
                        //  保存修改
                        int Save_DevWay = 0;
                        String[] WayStr_ok = null;
                        //设备名数据处理
                        Save_DevName = add_dev_name.getText().toString();
                        if ("".equals(Save_DevName) || Save_DevName.length() > 6) {
                            ToastUtil.showText("设备名称过长");
                            return;
                        }
                        try {
                            Save_DevName = CommonUtils.bytesToHexString(Save_DevName.getBytes("GB2312"));
                        } catch (UnsupportedEncodingException e) {
                            ToastUtil.showText("设备名称不合适");
                            return;
                        }
                        //房间名数据处理
                        if (add_dev_room.getVisibility() == View.VISIBLE)
                            Save_Roomname = add_dev_room.getText().toString();
                        else {
                            Save_Roomname = edit_dev_room.getText().toString();
                            if (Save_Roomname.length() > 6) {
                                ToastUtil.showText("房间名过长");
                                return;
                            }
                        }
                        if ("".equals(Save_Roomname) || "点击选择房间".equals(Save_Roomname)) {
                            ToastUtil.showText("房间名为空");
                            return;
                        }
                        try {
                            Save_Roomname = CommonUtils.bytesToHexString(Save_Roomname.getBytes("GB2312"));
                        } catch (UnsupportedEncodingException e) {
                            ToastUtil.showText("房间名称不合适");
                            return;
                        }
                        if (type_position == 0) {
                            //设备通道 保存数据处理
                            String Way_Str = add_dev_way.getText().toString();
                            String[] WayStr_air = Way_Str.split("、");
                            if (WayStr_air.length == 0) {
                                ToastUtil.showText("请选择通道");
                                return;
                            } else {
                                if (WayStr_air.length != 5) {//135
                                    ToastUtil.showText("空调是5个通道");
                                    return;
                                }
                                String Way = "";
                                for (int j = 0; j < 12; j++) {
                                    boolean IsEnter = false;
                                    for (int k = 0; k < WayStr_air.length; k++) {
                                        if (j == Integer.parseInt(WayStr_air[k]) - 1) {
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
                        } else if (type_position == 2 || type_position == 1) {

                        } else if (type_position == 3) {
                            //设备通道 保存数据处理
                            String Way_Str = add_dev_way.getText().toString();
                            if (Way_Str.length() == 0) {
                                ToastUtil.showText("请选择通道");
                                return;
                            } else if (Way_Str.contains("、")) {
                                ToastUtil.showText("灯光只能有一个通道");
                                return;
                            }
                            Save_DevWay = Integer.parseInt(Way_Str) - 1;
                        } else if (type_position == 4) {
                            //设备通道 保存数据处理
                            String Way_Str = add_dev_way.getText().toString();
                            String[] WayStr_air = Way_Str.split("、");
                            if (WayStr_air.length == 0) {
                                ToastUtil.showText("请选择通道");
                                return;
                            } else {
                                if (WayStr_air.length != 2) {//135
                                    ToastUtil.showText("窗帘是2个通道");
                                    return;
                                }
                                String Way = "";
                                for (int j = 0; j < 12; j++) {
                                    boolean IsEnter = false;
                                    for (int k = 0; k < WayStr_air.length; k++) {
                                        if (j == Integer.parseInt(WayStr_air[k]) - 1) {
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
                        } else if (type_position == 7) {
                            //设备通道 保存数据处理
                            String Way_Str = add_dev_way.getText().toString();
                            WayStr_ok = Way_Str.split("、");
                            if (WayStr_ok.length < 4 || WayStr_ok.length > 4) {
                                ToastUtil.showText("新风是4个通道");
                                return;
                            }
                        } else if (type_position == 9) {
                            //设备通道 保存数据处理
                            String Way_Str = add_dev_way.getText().toString();
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
                        if (type_position == 7)
                            chn_str = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"," +
                                    "\"datType\":" + 5 + "," +
                                    "\"subType1\":0," +
                                    "\"subType2\":0," +
                                    "\"canCpuID\":\"" + list_board.get(board_position).getDevUnitID() + "\"," +
                                    "\"devType\":" + type_position + "," +
                                    "\"devName\":" + "\"" + Save_DevName + "\"," +
                                    "\"roomName\":" + "\"" + Save_Roomname + "\"," +
                                    "\"spdLowChn\":" + (Integer.parseInt(WayStr_ok[1]) - 1) + "," +
                                    "\"spdMidChn\":" + (Integer.parseInt(WayStr_ok[2]) - 1) + "," +
                                    "\"spdHighChn\":" + (Integer.parseInt(WayStr_ok[3]) - 1) + "," +
                                    "\"autoRun\":" + 0 + "," +
                                    "\"valPm10\":" + 0 + "," +
                                    "\"valPm25\":" + 0 + "," +
                                    "\"powChn\":" + (Integer.parseInt(WayStr_ok[0]) - 1) + "}";
                        else if (type_position == 9)
                            chn_str = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"," +
                                    "\"datType\":" + 5 + "," +
                                    "\"subType1\":0," +
                                    "\"subType2\":0," +
                                    "\"canCpuID\":\"" + list_board.get(board_position).getDevUnitID() + "\"," +
                                    "\"devType\":" + type_position + "," +
                                    "\"devName\":" + "\"" + Save_DevName + "\"," +
                                    "\"roomName\":" + "\"" + Save_Roomname + "\"," +
                                    "\"tempget\":" + 25 + "," +
                                    "\"bOnOff\":" + 0 + "," +
                                    "\"tempset\":" + 25 + "," +
                                    "\"autoRun\":" + 0 + "," +
                                    "\"powChn\":" + Save_DevWay + "}";
                        else
                            chn_str = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"," +
                                    "\"datType\":" + 5 + "," +
                                    "\"subType1\":0," +
                                    "\"subType2\":0," +
                                    "\"canCpuID\":\"" + list_board.get(board_position).getDevUnitID() + "\"," +
                                    "\"devType\":" + type_position + "," +
                                    "\"devName\":" + "\"" + Save_DevName + "\"," +
                                    "\"roomName\":" + "\"" + Save_Roomname + "\"," +
                                    "\"powChn\":" + Save_DevWay + "}";
                        MyApplication.mApplication.getUdpServer().send(chn_str,5);
                        MyApplication.mApplication.showLoadDialog(Add_Dev_Activity.this);
                        finish();
                    }
                });
                builder.setNegativeButton("不要", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
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


    /**
     * 初始化自定义设备的状态以及设备PopupWindow
     */
    private void initPopupWindow(final List<String> text, final int type) {
        //获取自定义布局文件pop.xml的视图
        final View customView = getLayoutInflater().from(this).inflate(R.layout.popupwindow_equipment_listview, null);
        customView.setBackgroundResource(R.drawable.selectbg);
        // 创建PopupWindow实例
        if (type == 1)
            popupWindow = new PopupWindow(findViewById(R.id.popupWindow_equipment_sv), 320, 250);
        else if (type == 2)
            popupWindow = new PopupWindow(findViewById(R.id.popupWindow_equipment_sv), 320, 300);
        else if (type == 3)
            popupWindow = new PopupWindow(findViewById(R.id.popupWindow_equipment_sv), 320, 250);
        popupWindow.setContentView(customView);
        ListView list_pop = (ListView) customView.findViewById(R.id.popupWindow_equipment_lv);
        PopupWindowAdapter adapter = new PopupWindowAdapter(text, this);
        list_pop.setAdapter(adapter);
        list_pop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (type == 1) {
                    add_dev_type.setText(text.get(position));
                    if (position == 0) {
                        type_position = 0;
                    } else if (position == 1) {
                        type_position = 1;
                    } else if (position == 2) {
                        type_position = 2;
                    } else if (position == 3) {
                        type_position = 3;
                    } else if (position == 4) {
                        for (int i = 0; i < MyApplication.getWareData().getCurtains().size(); i++) {
                            if (add_dev_room.getText().equals(MyApplication.getWareData().getCurtains().get(i).getDev().getRoomName())) {
                                ToastUtil.showText("此房间已有窗帘，换个房间");
                                IsSave = false;
                                popupWindow.dismiss();
                                type_position = 4;
                                return;
                            }
                        }
                        type_position = 4;
                    } else if (position == 5) {
                        type_position = 7;
                    } else if (position == 6) {
                        type_position = 9;
                    }
                } else if (type == 2) {
                    if ("窗帘".equals(add_dev_type.getText())) {
                        String roomName = text.get(position);
                        for (int i = 0; i < MyApplication.getWareData().getCurtains().size(); i++) {
                            if (roomName.equals(MyApplication.getWareData().getCurtains().get(i).getDev().getRoomName())) {
                                ToastUtil.showText("此房间已有窗帘，换个房间");
                                IsSave = false;
                                popupWindow.dismiss();
                                return;
                            }
                        }
                        if (!IsSave) {
                            popupWindow.dismiss();
                            add_dev_room.setText(roomName);
                        }
                    } else {
                        add_dev_room.setText(text.get(position));
                    }
                } else if (type == 3) {
                    add_dev_board.setText(text.get(position));
                    board_position = position;
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
     * 初始化自定义设备的状态以及设备PopupWindow
     */
    private void initPopupWindow_channel(final View textView, List<Integer> list_channel) {
        //获取自定义布局文件pop.xml的视图
        final View customView = getLayoutInflater().from(this).inflate(R.layout.popupwindow_equipment_listview2, null);
        customView.setBackgroundResource(R.drawable.selectbg);
        popupWindow = new PopupWindow(customView, textView.getWidth(), 400);
        ListView list_pop = (ListView) customView.findViewById(R.id.popupWindow_equipment_lv);
        Button time_ok = (Button) customView.findViewById(R.id.time_ok);
        Button time_cancel = (Button) customView.findViewById(R.id.time_cancel);
        popupWindowAdapter_channel = new PopupWindowAdapter_channel(this, list_channel);
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
                message_save = new ArrayList<>();
                for (int i = 0; i < map.keySet().toArray().length; i++) {
                    message += String.valueOf(map.keySet().toArray()[i]) + "、";
                    message_save.add(String.valueOf(map.keySet().toArray()[i]));
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

    public String Sutf2Sgbk(String string) {

        byte[] data = {0};
//        byte[] dataname = new byte[12];
        try {
            data = string.getBytes("GB2312");
//            System.arraycopy(dataname,0,data,0,data.length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String str_gb = CommonUtils.bytesToHexString(data);
        LogUtils.LOGE("情景模式名称:%s", str_gb);
        return str_gb;
    }

    @Override
    protected void onDestroy() {
        MyApplication.mApplication.dismissLoadDialog();
        super.onDestroy();
    }
}


