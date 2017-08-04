package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.UiUtils.ToastUtil;
import cn.etsoft.smarthomephone.adapter.PopupWindowAdapter;
import cn.etsoft.smarthomephone.pullmi.app.GlobalVars;
import cn.etsoft.smarthomephone.pullmi.common.CommonUtils;
import cn.etsoft.smarthomephone.pullmi.entity.WareDev;
import cn.etsoft.smarthomephone.pullmi.utils.LogUtils;
import cn.etsoft.smarthomephone.view.Circle_Progress;

/**
 * Created by fbl on 16-11-17.
 * 设备详情页面
 */
public class Devs_Detail_Activity extends Activity implements View.OnClickListener {

    private TextView dev_type, dev_room, dev_save, title,dev_way;
    private EditText dev_name;
    private ImageView back;
    private WareDev dev;
    private int id;
    private PopupWindow popupWindow;
    private Dialog mDialog;

    //自定义加载进度条
    private void initDialog(String str) {
        Circle_Progress.setText(str);
        mDialog = Circle_Progress.createLoadingDialog(this);
        //允许返回
        mDialog.setCancelable(true);
        //显示
        mDialog.show();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mDialog.dismiss();
            }
        };
        //加载数据进度条，5秒数据没加载出来自动消失
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    if (mDialog.isShowing()) {
                        handler.sendMessage(handler.obtainMessage());
                    }
                } catch (Exception e) {
                    System.out.println(e + "");
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dec_detail_activity);
        initView();
    }
    private List<String> message_save;
    private List<String> message_get;
    /**
     * 初始化组件以及数据
     */
    private void initView() {
        id = getIntent().getIntExtra("id", 0);
        dev = MyApplication.getWareData().getDevs().get(id);

        title = (TextView) findViewById(R.id.title_bar_tv_title);
        dev_type = (TextView) findViewById(R.id.dev_type);
        dev_room = (TextView) findViewById(R.id.dev_room);
        dev_save = (TextView) findViewById(R.id.dev_save);
        dev_name = (EditText) findViewById(R.id.dev_name);
        dev_way = (TextView) findViewById(R.id.dev_way);
        back = (ImageView) findViewById(R.id.title_bar_iv_back);


        title.setText(dev.getDevName());
        dev_name.setText(dev.getDevName());
        dev_room.setText(dev.getRoomName());

        if (dev.getType() == 0) {
            dev_type.setText("空调");
            for (int i = 0; i < MyApplication.getWareData().getAirConds().size(); i++) {
                if (MyApplication.getWareData().getAirConds().get(i).getDev().getCanCpuId() == dev.getCanCpuId()) {
                    String message1 = "";
                    int PowChn = MyApplication.getWareData().getAirConds().get(i).getPowChn();
                    String PowChnList = Integer.toBinaryString(PowChn);
                    List<Integer> index_list = new ArrayList<>();
                    message_get = new ArrayList<>();
                    for (int j = 0; j < PowChnList.length(); j++) {
                        if (PowChnList.charAt(PowChnList.length() - j - 1) == '1') {
                            index_list.add(PowChnList.length() - j - 1);
                            message1 += j + 1 + ".";
                            message_get.add(String.valueOf(j + 1));
                        }
                    }
                    if (message_get.size() > 5) {
                        message1 = "";
                        for (int k = 0; k < 5; k++) {
                            message1 += message_get.get(k) + ".";
                        }
                    }
                    if (!"".equals(message1)) {
                        message1 = message1.substring(0, message1.lastIndexOf("."));
                    }
                    dev_way.setText(message1);
                }
            }
        } else if (dev.getType() == 1) {
            dev_type.setText("电视");
            dev_way.setText("无");
            dev_way.setClickable(false);
        } else if (dev.getType() == 2) {
            dev_type.setText("机顶盒");
            dev_way.setText("无");
            dev_way.setClickable(false);
        } else if (dev.getType() == 3) {
            dev_type.setText("灯光");
            for (int i = 0; i < MyApplication.getWareData().getLights().size(); i++) {
                if (MyApplication.getWareData().getLights().get(i).getDev().getDevId() == dev.getDevId()) {
                    int PowChn = MyApplication.getWareData().getLights().get(i).getPowChn();
                    dev_way.setText(PowChn + "");
                }
            }
        } else if (dev.getType() == 4) {
            dev_type.setText("窗帘");
            for (int i = 0; i < MyApplication.getWareData().getCurtains().size(); i++) {
                if (MyApplication.getWareData().getCurtains().get(i).getDev().getDevId() == dev.getDevId()) {
                    String message1 = "";
                    int PowChn = MyApplication.getWareData().getCurtains().get(i).getPowChn();

                    String PowChnList = Integer.toBinaryString(PowChn);
                    List<Integer> index_list = new ArrayList<>();
                    message_get = new ArrayList<>();
                    for (int j = 0; j < PowChnList.length(); j++) {
                        if (PowChnList.charAt(PowChnList.length() - j - 1) == '1') {
                            index_list.add(PowChnList.length() - j - 1);
                            message1 += j + 1 + ".";
                            message_get.add(String.valueOf(j + 1));
                        }
                    }
                    if (message_get.size() > 3) {
                        message1 = "";
                        for (int k = 0; k < 3; k++) {
                            message1 += message_get.get(k) + ".";
                        }
                    }
                    if (!"".equals(message1)) {
                        message1 = message1.substring(0, message1.lastIndexOf("."));
                    }
                    dev_way.setText(message1);
                }
            }
        }
        dev_way.setOnClickListener(this);
        dev_save.setOnClickListener(this);
        back.setOnClickListener(this);
        dev_room.setOnClickListener(this);
    }


    /**
     * 初始化自定义设备的状态以及设备PopupWindow
     */
    private void initPopupWindow(final View textView, final List<String> text) {
        //获取自定义布局文件pop.xml的视图
        final View customView = getLayoutInflater().from(this).inflate(R.layout.popupwindow_equipment_listview, null);
        customView.setBackgroundResource(R.drawable.selectbg);

        // 创建PopupWindow实例

        popupWindow = new PopupWindow(findViewById(R.id.popupWindow_equipment_sv), 320, 120);

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
                if (dev.getType() == 0) {
                    for (int i = 0; i < MyApplication.getWareData().getAirConds().size(); i++) {
                        if (MyApplication.getWareData().getAirConds().get(i).getDev().getDevId() == dev.getDevId()) {
                            String message = "";
                            int PowChn = MyApplication.getWareData().getAirConds().get(i).getPowChn();
                            String PowChnList = Integer.toBinaryString(PowChn);
                            List<Integer> index_list = new ArrayList<>();
                            for (int j = 0; j < PowChnList.length(); j++) {
                                if (PowChnList.charAt(PowChnList.length() - j - 1) == '1') {
                                    index_list.add(PowChnList.length() - j - 1);
                                    message += j + 1 + ".";
                                }
                            }
                            if (!"".equals(message)) {
                                message = message.substring(0, message.lastIndexOf("."));
                            }
                            if (message.equals(dev_way.getText().toString())) {
                                data_save = PowChn;
                            } else {
                                if (message_save.size() > 5) {
                                    ToastUtil.showToast(Devs_Detail_Activity.this, "空调通道不能超过5个");
                                    return;
                                }
                            }
                            MyApplication.getWareData().getAirConds().get(i).setPowChn(data_save);
                        }
                    }
                } else if (dev.getType() == 3) {
                    for (int i = 0; i < MyApplication.getWareData().getLights().size(); i++) {
                        if (MyApplication.getWareData().getLights().get(i).getDev().getDevId() == dev.getDevId()) {
                            int PowChn = MyApplication.getWareData().getLights().get(i).getPowChn();
                            if ((PowChn + "").equals(dev_way.getText().toString())) {
                                data_save = PowChn;
                            } else {
                                if (message_save.size() > 1) {
                                    ToastUtil.showToast(Devs_Detail_Activity.this, "灯光通道不能超过1个");
                                    return;
                                }
                            }
                            MyApplication.getWareData().getLights().get(i).setPowChn((byte) data_save);
                        }
                    }
                } else if (dev.getType() == 4) {
                    for (int i = 0; i < MyApplication.getWareData().getCurtains().size(); i++) {
                        if (MyApplication.getWareData().getCurtains().get(i).getDev().getDevId() == dev.getDevId()) {
                            String message = "";
                            int PowChn = MyApplication.getWareData().getCurtains().get(i).getPowChn();
                            String PowChnList = Integer.toBinaryString(PowChn);
                            List<Integer> index_list = new ArrayList<>();
                            for (int j = 0; j < PowChnList.length(); j++) {
                                if (PowChnList.charAt(PowChnList.length() - j - 1) == '1') {
                                    index_list.add(PowChnList.length() - j - 1);
                                    message += j + 1 + ".";
                                }
                            }
                            if (!"".equals(message)) {
                                message = message.substring(0, message.lastIndexOf("."));
                            }
                            if (message.equals(dev_way.getText().toString())) {
                                data_save = PowChn;
                            } else {
                                if (message_save.size() > 3) {
                                    ToastUtil.showToast(Devs_Detail_Activity.this, "窗帘通道不能超过3个");
                                    return;
                                }
                            }
                            MyApplication.getWareData().getCurtains().get(i).setPowChn(data_save);
                        }
                    }
                }
                dev.setDevName(dev_name.getText().toString());
                dev.setRoomName(dev_room.getText().toString());
////                发送：
//            {
//                "devUnitID": "37ffdb05424e323416702443",
//                    "datType": 6,
//                    "subType1": 0,
//                    "subType2": 0,
//                    "canCpuID": "31ffdf054257313827502543",
//                    "devType": 3,
//                    "devID": 6,
//                    "devName": "b5c636360000000000000000",
//                    "roomName": "ceb4b6a8d2e5000000000000",
//                    "powChn":	6，
//                "cmd": 1
//            }

                final String chn_str = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"," +
                        "\"datType\":" + 6 + "," +
                        "\"subType1\":0," +
                        "\"subType2\":0," +
                        "\"canCpuID\":\"" + dev.getCanCpuId() + "\"," +
                        "\"devType\":" + dev.getType() + "," +
                        "\"devID\":" + +dev.getDevId() + "," +
                        "\"devName\":" + "\"" + Sutf2Sgbk(dev_name.getText().toString()) + "\"," +
                        "\"roomName\":" + "\"" + Sutf2Sgbk(dev_room.getText().toString()) + "\"," +
                        "\"powChn\":" + data_save + "," +
                        "\"cmd\":" + 1 + "}";
                MyApplication.sendMsg(chn_str);
                initDialog("正在保存...");
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
                    initPopupWindow(v,home_text);
                    popupWindow.showAsDropDown(v, 0, 0);
                }
                break;
            case R.id.dev_way:
                List<Integer> list_voard_cancpuid = new ArrayList<>();
                if (dev.getType() == 0) {
                    for (int i = 0; i < MyApplication.getWareData().getAirConds().size(); i++) {
                        if (dev.getCanCpuId()
                                .equals(MyApplication.getWareData().getAirConds().get(i).getDev().getCanCpuId())
                                && dev.getDevId() != MyApplication.getWareData().getAirConds().get(i).getDev().getDevId()) {
                            //TODO  3968
                            int PowChn = MyApplication.getWareData().getAirConds().get(i).getPowChn();
                            String PowChnList = Integer.toBinaryString(PowChn);
                            PowChnList = reverseString(PowChnList);
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
                        if (dev.getCanCpuId()
                                .equals(MyApplication.getWareData().getLights().get(i).getDev().getCanCpuId())
                                && dev.getDevId() != MyApplication.getWareData().getLights().get(i).getDev().getDevId()) {
                            //TODO  3968
                            int PowChn = MyApplication.getWareData().getLights().get(i).getPowChn();
                            list_voard_cancpuid.add(PowChn);
                        }
                    }
                } else if (dev.getType() == 4) {
                    for (int i = 0; i < MyApplication.getWareData().getCurtains().size(); i++) {
                        if (dev.getCanCpuId()
                                .equals(MyApplication.getWareData().getCurtains().get(i).getDev().getCanCpuId())
                                && dev.getDevId() != MyApplication.getWareData().getCurtains().get(i).getDev().getDevId()) {
                            //TODO  3968
                            int PowChn = MyApplication.getWareData().getCurtains().get(i).getPowChn();
                            String PowChnList = Integer.toBinaryString(PowChn);
                            PowChnList = reverseString(PowChnList);
                            List<Integer> index_list = new ArrayList<>();
                            for (int j = 0; j < PowChnList.length(); j++) {
                                if (PowChnList.charAt(j) == '1') {
                                    index_list.add(j + 1);
                                }
                            }
                            list_voard_cancpuid.addAll(index_list);
                        }
                    }
                }
                list_channel = new ArrayList<>();
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
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                } else {
                    if (list_channel.size() == 0) {
                        ToastUtil.showToast(Devs_Detail_Activity.this, "没有可用通道");
                    } else {
                        initPopupWindow_channel(v, list_channel);
                        popupWindow.showAsDropDown(v, 0, 0);
                    }
                }
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
    private PopupWindowAdapter_channel popupWindowAdapter_channel;
    // Hashtable.keySet()降序 TreeMap.keySet()升序 HashMap.keySet()乱序 LinkedHashMap.keySet()原序
    private TreeMap<Integer, Boolean> map = new TreeMap<>();// 存放已被选中的CheckBox
    private int data_save;
    /**
     * 初始化自定义设备的状态以及设备PopupWindow
     */
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
        popupWindow = new PopupWindow(customView, textView.getWidth(), 300);
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
                    ToastUtil.showToast(Devs_Detail_Activity.this, "请选择设备通道");
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
                    message += String.valueOf(map.keySet().toArray()[i]) + ".";

                    message_save.add(String.valueOf(map.keySet().toArray()[i]));
                    Log.i("测试", String.valueOf(message_save));
                }
                if (!"".equals(message)) {
                    message = message.substring(0, message.lastIndexOf("."));
                }
                data_save = str2num(data_str);
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
     * @param str
     * @return
     */
    public int str2num(String str) {
        str = reverseString(str);
        return Integer.valueOf(str, 2);
    }
    /**
     * 倒置字符串
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
