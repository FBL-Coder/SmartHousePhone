package cn.etsoft.smarthome.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.IClick_PZ;
import cn.etsoft.smarthome.adapter.PopupWindowAdapter;
import cn.etsoft.smarthome.adapter.SwipeAdapter;
import cn.etsoft.smarthome.domain.Iclick_Tag;
import cn.etsoft.smarthome.domain.Save_Quipment;
import cn.etsoft.smarthome.domain.WareDev;
import cn.etsoft.smarthome.domain.WareKeyOpItem;
import cn.etsoft.smarthome.utils.SendDataUtil;
import cn.etsoft.smarthome.view.Circle_Progress;
import cn.etsoft.smarthome.weidget.CustomDialog_comment;
import cn.etsoft.smarthome.weidget.SwipeListView;

/**
 * Created by Say GoBay on 2016/8/29.
 * 组合设置--输入板配置设备页面
 */
public class AddEquipmentControlActivity extends Activity implements View.OnClickListener {
    private TextView mTitle, equipment_close, tv_equipment_parlour, ref_equipment, del_equipment, save_equipment;
    private ImageView back, input_out_iv_noData;
    private RelativeLayout add_equipment_btn;
    private SwipeListView lv;
    private ListView add_equipment_Layout_lv;
    private PopupWindow popupWindow;
    private LinearLayout add_equipment_Layout_ll;
    private int index;
    private String uid;
    private List<WareKeyOpItem> keyOpItems;
    private SwipeAdapter adapter = null;
    private List<WareDev> dev;
    private List<String> home_text;
    private List<WareDev> mWareDev_room;
    private List<WareDev> mWareDev;
    private EquipmentAdapter equipmentAdapter;
    private int DATTYPE_SET = 12, DATTYPE_DEL = 13, POP_TYPE_DOWNUP = 1, POP_TYPE_STATE = 0, POP_TYPE_ROOM = 2, DEL_ALL = 110;
    private Dialog mDialog;
    private int del_Position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipmentdeploy_listview);
        //初始化标题栏
        initTitleBar();
//        //初始化listView
//        initListView();
        initData();
        MyApplication.mApplication.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int datType, int subtype1, int subtype2) {

                if (datType == 11 || datType == 12 || datType == 13) {
                    if (mDialog != null)
                        mDialog.dismiss();
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
                //删除某一条
                if (del_Position != DEL_ALL && MyApplication.getWareData().getResult() != null
                        && MyApplication.getWareData().getResult().getResult() == 1) {
                    Toast.makeText(AddEquipmentControlActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    MyApplication.getWareData().setResult(null);
                    keyOpItems.remove(del_Position);
                    del_Position = 0;
                    if (adapter != null) {
                        adapter.notifyDataSetChanged(keyOpItems);
                    } else {
                        adapter = new SwipeAdapter(AddEquipmentControlActivity.this, keyOpItems, mListener);
                        lv.setAdapter(adapter);
                    }
                }
                //全部删除
                if (del_Position == DEL_ALL && MyApplication.getWareData().getResult() != null
                        && MyApplication.getWareData().getResult().getResult() == 1) {
                    Toast.makeText(AddEquipmentControlActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    MyApplication.getWareData().setResult(null);
                    del_Position = 0;
                    keyOpItems.clear();
                    if (adapter != null)
                        adapter.notifyDataSetChanged(keyOpItems);
                    else {
                        adapter = new SwipeAdapter(AddEquipmentControlActivity.this, keyOpItems, mListener, input_out_iv_noData);
                        lv.setAdapter(adapter);
                    }
                }
            }
        });
        //初始化控件
        initView();
    }

    //自定义加载进度条
    private void initDialog(String str) {
        Circle_Progress.setText(str);
        mDialog = Circle_Progress.createLoadingDialog(AddEquipmentControlActivity.this);
        mDialog.setCancelable(true);//允许返回
        mDialog.show();//显示
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mDialog.dismiss();
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3500);
                    if (mDialog.isShowing()) {
                        handler.sendMessage(handler.obtainMessage());
                    }
                } catch (Exception e) {
                    System.out.println(e + "");
                }
            }
        }).start();
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
        System.out.println("加载数据");
        dev = new ArrayList<>();
        home_text = new ArrayList<>();
        mWareDev_room = new ArrayList<>();
        //所有设备
        mWareDev = MyApplication.getWareData().getDevs();

        for (int i = 0; i < MyApplication.getWareData().getDevs().size(); i++) {
            mWareDev_room.add(MyApplication.getWareData().getDevs().get(i));
        }
        //去重
        for (int i = 0; i < mWareDev_room.size() - 1; i++) {
            for (int j = mWareDev_room.size() - 1; j > i; j--) {
                if (mWareDev_room.get(i).getRoomName().equals(mWareDev_room.get(j).getRoomName())) {
                    mWareDev_room.remove(j);
                }
            }
        }
        //设备的房间名集合
        for (int i = 0; i < mWareDev_room.size(); i++) {
            home_text.add(mWareDev_room.get(i).getRoomName());
        }

        for (int i = 0; i < mWareDev.size(); i++) {
            if (mWareDev.get(i).getRoomName().equals(home_text.get(0)))
                dev.add(mWareDev.get(i));
        }
    }

    public void initData() {
        keyOpItems = new ArrayList<>();
        SendDataUtil.getKeyItemInfo(index, uid);
        initDialog("正在加载...");
    }

    /**
     * 初始化控件
     */
    private void initView() {
        input_out_iv_noData = (ImageView) findViewById(R.id.input_out_iv_nodata);
        add_equipment_btn = (RelativeLayout) findViewById(R.id.equipment_out_rl);
        equipment_close = (TextView) findViewById(R.id.equipment_close);
        tv_equipment_parlour = (TextView) findViewById(R.id.tv_equipment_parlour);
        ref_equipment = (TextView) findViewById(R.id.ref_equipment);
        del_equipment = (TextView) findViewById(R.id.del_equipment);
        save_equipment = (TextView) findViewById(R.id.save_equipment);

        add_equipment_Layout_ll = (LinearLayout) findViewById(R.id.add_equipment_Layout_ll);
        add_equipment_Layout_lv = (ListView) findViewById(R.id.equipment_loyout_lv);
        lv = (SwipeListView) findViewById(R.id.equipment_out_lv);
        ref_equipment.setOnClickListener(this);
        del_equipment.setOnClickListener(this);
        save_equipment.setOnClickListener(this);
        add_equipment_btn.setOnClickListener(this);
        equipment_close.setOnClickListener(this);
        tv_equipment_parlour.setOnClickListener(this);
        back.setOnClickListener(this);
        tv_equipment_parlour.setText(home_text.get(0));
    }

    /**
     * 初始化listView
     */
    private void initListView() {
        if (MyApplication.getWareData().getKeyOpItems() == null || MyApplication.getWareData().getKeyOpItems().size() == 0) {
            input_out_iv_noData.setVisibility(View.VISIBLE);
            if (adapter != null) {
                adapter.notifyDataSetChanged(keyOpItems);
            } else {
                adapter = new SwipeAdapter(this, keyOpItems, mListener);
                lv.setAdapter(adapter);
            }
            return;
        }
        input_out_iv_noData.setVisibility(View.GONE);
        for (int i = 0; i < MyApplication.getWareData().getKeyOpItems().size(); i++) {
            keyOpItems.add(MyApplication.getWareData().getKeyOpItems().get(i));
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged(keyOpItems);
        } else {
            adapter = new SwipeAdapter(this, keyOpItems, mListener);
            lv.setAdapter(adapter);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_bar_iv_back:
                finish();
                break;
            case R.id.equipment_out_rl:
                //添加页面的item点击，以及listView的初始化
                equipmentAdapter = new EquipmentAdapter(dev, this);
                add_equipment_Layout_lv.setAdapter(equipmentAdapter);

                add_equipment_Layout_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        WareKeyOpItem item = new WareKeyOpItem();
                        item.setDevId((byte) dev.get(position).getDevId());
                        item.setDevType((byte) dev.get(position).getType());
                        item.setOut_cpuCanID(dev.get(position).getCanCpuId());
                        item.setKeyOpCmd((byte) 0);
                        item.setKeyOp((byte) 3);

                        boolean tag = true;
                        for (int i = 0; i < keyOpItems.size(); i++) {
                            if (keyOpItems.get(i).getDevType() == item.getDevType()
                                    && keyOpItems.get(i).getDevId() == item.getDevId()
                                    && keyOpItems.get(i).getOut_cpuCanID().equals(item.getOut_cpuCanID())) {
                                tag = false;
                                Toast.makeText(AddEquipmentControlActivity.this, "设备已存在！", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (tag) {
                            keyOpItems.add(item);
                            input_out_iv_noData.setVisibility(View.GONE);
                            if (adapter != null) {
                                adapter.notifyDataSetChanged(keyOpItems);
                            } else {
                                adapter = new SwipeAdapter(AddEquipmentControlActivity.this, keyOpItems, mListener, input_out_iv_noData);
                                lv.setAdapter(adapter);
                            }
                        }
                    }
                });
                add_equipment_Layout_ll.setVisibility(View.VISIBLE);
                add_equipment_btn.setVisibility(View.GONE);
                break;
            case R.id.equipment_close:
                //添加页面的关闭按钮
                add_equipment_Layout_ll.setVisibility(View.GONE);
                add_equipment_btn.setVisibility(View.VISIBLE);
                break;
            case R.id.ref_equipment:
                //刷新
                input_out_iv_noData.setVisibility(View.GONE);
                initData();
                break;
            case R.id.del_equipment:

                //删除所有设备
                CustomDialog_comment.Builder builder = new CustomDialog_comment.Builder(AddEquipmentControlActivity.this);
                builder.setTitle("提示 :");
                builder.setMessage("您确定要删除所有设备吗？");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        del_Position = DEL_ALL;
                        Save_Quipment save_quipment = new Save_Quipment();
                        if (keyOpItems.size() == 0) {
                            dialog.dismiss();
                            return;
                        }
                        List<Save_Quipment.key_Opitem_Rows> list_kor = new ArrayList<>();
                        for (int i = 0; i < keyOpItems.size(); i++) {
                            Save_Quipment.key_Opitem_Rows key_opitem_rows = save_quipment.new key_Opitem_Rows();
                            key_opitem_rows.setOut_cpuCanID(keyOpItems.get(i).getOut_cpuCanID());
                            key_opitem_rows.setDevID(keyOpItems.get(i).getDevId());
                            key_opitem_rows.setDevType(keyOpItems.get(i).getDevType());
                            key_opitem_rows.setKeyOp(keyOpItems.get(i).getKeyOp());
                            key_opitem_rows.setKeyOpCmd(keyOpItems.get(i).getKeyOpCmd());
                            list_kor.add(key_opitem_rows);
                        }

                        save_quipment.setDevUnitID(GlobalVars.getDevid());
                        save_quipment.setDatType(DATTYPE_DEL);
                        save_quipment.setKey_cpuCanID(uid);
                        save_quipment.setKey_opitem(keyOpItems.size());
                        save_quipment.setKey_index(index);
                        save_quipment.setSubType1(0);
                        save_quipment.setSubType2(0);
                        save_quipment.setKey_opitem_rows(list_kor);
                        dialog.dismiss();
                        Gson gson = new Gson();
                        System.out.println(gson.toJson(save_quipment));
                        MyApplication.mApplication.getUdpServer().send(gson.toJson(save_quipment).toString());
                        initDialog("正在删除...");
                    }
                });
                builder.create().show();
                break;
            case R.id.save_equipment:
                if (keyOpItems.size() == 0)
                    return;
                //保存
                for (int i = 0; i < keyOpItems.size(); i++) {
                    if (keyOpItems.get(i).getKeyOp() == 2 || keyOpItems.get(i).getKeyOpCmd() == 0) {
                        Toast.makeText(AddEquipmentControlActivity.this, "存在未设置，请设置完", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Save_Quipment save_quipment = new Save_Quipment();

                List<Save_Quipment.key_Opitem_Rows> list_kor = new ArrayList<>();
                for (int i = 0; i < keyOpItems.size(); i++) {
                    Save_Quipment.key_Opitem_Rows key_opitem_rows = save_quipment.new key_Opitem_Rows();
                    key_opitem_rows.setOut_cpuCanID(keyOpItems.get(i).getOut_cpuCanID());
                    key_opitem_rows.setDevID(keyOpItems.get(i).getDevId());
                    key_opitem_rows.setDevType(keyOpItems.get(i).getDevType());
                    key_opitem_rows.setKeyOp(keyOpItems.get(i).getKeyOp());
                    key_opitem_rows.setKeyOpCmd(keyOpItems.get(i).getKeyOpCmd());
                    list_kor.add(key_opitem_rows);
                }
                save_quipment.setDevUnitID(GlobalVars.getDevid());
                save_quipment.setDatType(DATTYPE_SET);
                save_quipment.setKey_cpuCanID(uid);
                save_quipment.setKey_opitem(keyOpItems.size());
                save_quipment.setKey_index(index);
                save_quipment.setSubType1(0);
                save_quipment.setSubType2(0);
                save_quipment.setKey_opitem_rows(list_kor);
                Gson gson = new Gson();
                System.out.println(gson.toJson(save_quipment));
                MyApplication.mApplication.getUdpServer().send(gson.toJson(save_quipment).toString());
                initDialog("正在保存...");
                break;
            case R.id.tv_equipment_parlour:
                //添加设备页的弹出框
                int widthOff = getWindow().getWindowManager().getDefaultDisplay().getWidth() / 500;
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                } else {
                    initPopupWindow(v, -1, home_text, POP_TYPE_ROOM);
                    popupWindow.showAsDropDown(v, 0, 0);
                }
                break;
        }
    }

    /**
     * 初始化自定义设备的状态以及设备PopupWindow
     */
    private void initPopupWindow(final View view_parent, final int parent_position, final List<String> text, final int type) {
        //获取自定义布局文件pop.xml的视图
        final View customView = getLayoutInflater().from(this).inflate(R.layout.popupwindow_equipment_listview, null);
        customView.setBackgroundResource(R.drawable.selectbg);

        // 创建PopupWindow实例

        if (type == 1)
            popupWindow = new PopupWindow(findViewById(R.id.popupWindow_equipment_sv), 140, 160);
        else if (type == 0)
            popupWindow = new PopupWindow(findViewById(R.id.popupWindow_equipment_sv), 140, 300);
        else
            popupWindow = new PopupWindow(findViewById(R.id.popupWindow_equipment_sv), 240, 120);

        popupWindow.setContentView(customView);
        ListView list_pop = (ListView) customView.findViewById(R.id.popupWindow_equipment_lv);
        PopupWindowAdapter adapter = new PopupWindowAdapter(text, this);
        list_pop.setAdapter(adapter);
        list_pop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view_parent;
                tv.setText(text.get(position));
                if (type == POP_TYPE_STATE)
                    keyOpItems.get(parent_position).setKeyOpCmd((byte) position);
                else if (type == POP_TYPE_DOWNUP)
                    keyOpItems.get(parent_position).setKeyOp((byte) position);
                else {
                    dev.clear();
                    for (int i = 0; i < mWareDev.size(); i++) {
                        if (mWareDev.get(i).getRoomName().equals(home_text.get(position)))
                            dev.add(mWareDev.get(i));

                    }
                    if (equipmentAdapter != null)
                        equipmentAdapter.notifyDataSetChanged();
                    else {
                        equipmentAdapter = new EquipmentAdapter(dev, AddEquipmentControlActivity.this);
                        add_equipment_Layout_lv.setAdapter(equipmentAdapter);
                    }
                }
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

    /**
     * 实现类，响应按钮点击事件
     */
    private IClick_PZ mListener = new IClick_PZ() {
        @Override
        public void listViewItemClick(final int position, View v) {
            int widthOff = getWindow().getWindowManager().getDefaultDisplay().getWidth() / 500;
            List<String> list_text = new ArrayList<>();
            Iclick_Tag tag = (Iclick_Tag) v.getTag();
            System.out.println("设备类型:" + tag.getType());
            for (int i = 0; i < tag.getText().length; i++) {
                list_text.add(tag.getText()[i]);
            }

            switch (v.getId()) {

                case R.id.deploy_choose:
                    if (tag.getType() == 0) {
                        if (popupWindow != null && popupWindow.isShowing()) {
                            popupWindow.dismiss();
                            popupWindow = null;
                        } else {
                            initPopupWindow(v, position, list_text, POP_TYPE_STATE);
                            popupWindow.showAsDropDown(v, -widthOff, 0);
                        }
                    } else if (tag.getType() == 3) {
                        if (popupWindow != null && popupWindow.isShowing()) {
                            popupWindow.dismiss();
                            popupWindow = null;
                        } else {
                            initPopupWindow(v, position, list_text, POP_TYPE_STATE);
                            popupWindow.showAsDropDown(v, -widthOff, 0);
                        }
                    } else if (tag.getType() == 4) {
                        if (popupWindow != null && popupWindow.isShowing()) {
                            popupWindow.dismiss();
                            popupWindow = null;
                        } else {
                            initPopupWindow(v, position, list_text, POP_TYPE_STATE);
                            popupWindow.showAsDropDown(v, -widthOff, 0);
                        }
                    }

                    break;
                case R.id.deploy_choose1:
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        popupWindow = null;
                    } else {
                        List<String> list = new ArrayList<>();
                        list.add("按下");
                        list.add("弹起");
                        list.add("未设置");
                        initPopupWindow(v, position, list, POP_TYPE_DOWNUP);
                        popupWindow.showAsDropDown(v, -widthOff, 0);
                    }
                    break;
                case R.id.deploy_delete:
                    CustomDialog_comment.Builder builder = new CustomDialog_comment.Builder(AddEquipmentControlActivity.this);
                    builder.setTitle("提示 :");
                    builder.setMessage("您确定要删除此条目吗？");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            del_Position = position;
                            Save_Quipment save_quipment = new Save_Quipment();
                            List<Save_Quipment.key_Opitem_Rows> list_kor = new ArrayList<>();
                            Save_Quipment.key_Opitem_Rows key_opitem_rows = save_quipment.new key_Opitem_Rows();
                            key_opitem_rows.setOut_cpuCanID(keyOpItems.get(position).getOut_cpuCanID());
                            key_opitem_rows.setDevID(keyOpItems.get(position).getDevId());
                            key_opitem_rows.setDevType(keyOpItems.get(position).getDevType());
                            key_opitem_rows.setKeyOp(keyOpItems.get(position).getKeyOp());
                            key_opitem_rows.setKeyOpCmd(keyOpItems.get(position).getKeyOpCmd());
                            list_kor.add(key_opitem_rows);
                            save_quipment.setDevUnitID(GlobalVars.getDevid());
                            save_quipment.setDatType(DATTYPE_DEL);
                            save_quipment.setKey_cpuCanID(uid);
                            save_quipment.setKey_opitem(1);
                            save_quipment.setKey_index(index);
                            save_quipment.setSubType1(0);
                            save_quipment.setSubType2(0);
                            save_quipment.setKey_opitem_rows(list_kor);
                            dialog.dismiss();
                            Gson gson = new Gson();
                            System.out.println(gson.toJson(save_quipment));

                            MyApplication.mApplication.getUdpServer().send(gson.toJson(save_quipment).toString());
                            initDialog("正在删除...");

                        }
                    });
                    builder.create().show();
                    break;
            }
        }
    };

    /**
     * 设备适配器；
     */

    private int[] image = new int[]{R.drawable.kongtiao, R.drawable.tv_0, R.drawable.jidinghe, R.drawable.dengguang, R.drawable.chuanglian};

    class EquipmentAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private List<WareDev> listViewItems;

        public EquipmentAdapter(List<WareDev> title, Context context) {
            super();
            listViewItems = title;
            mInflater = LayoutInflater.from(context);

        }

        @Override
        public int getCount() {
            if (null != listViewItems)
                return listViewItems.size();
            else
                return 0;

        }

        @Override
        public Object getItem(int position) {
            return listViewItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.equipment_listview_item, null);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.equipment_tv);
                viewHolder.image = (ImageView) convertView.findViewById(R.id.equipment_iv);
                convertView.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) convertView.getTag();

            viewHolder.title.setText(listViewItems.get(position).getDevName());

            if (listViewItems.get(position).getType() == 0)
                viewHolder.image.setImageResource(image[0]);
            else if (listViewItems.get(position).getType() == 1)
                viewHolder.image.setImageResource(image[1]);
            else if (listViewItems.get(position).getType() == 2)
                viewHolder.image.setImageResource(image[2]);
            else if (listViewItems.get(position).getType() == 3)
                viewHolder.image.setImageResource(image[3]);
            else if (listViewItems.get(position).getType() == 4)
                viewHolder.image.setImageResource(image[4]);
            else if (listViewItems.get(position).getType() == 7)
                viewHolder.image.setImageResource(R.mipmap.ic_launcher);
            else if (listViewItems.get(position).getType() == 9)
                viewHolder.image.setImageResource(R.mipmap.ic_launcher);
            else
                viewHolder.image.setImageResource(image[4]);

            return convertView;
        }

        public class ViewHolder {
            public TextView title;
            public ImageView image;
        }
    }

}
