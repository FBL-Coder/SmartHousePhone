package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.adapter.IClick;
import cn.etsoft.smarthomephone.adapter.SystemAdapter;
import cn.etsoft.smarthomephone.pullmi.app.GlobalVars;
import cn.etsoft.smarthomephone.pullmi.common.CommonUtils;
import cn.etsoft.smarthomephone.pullmi.entity.UdpProPkt;
import cn.etsoft.smarthomephone.pullmi.entity.WareSceneEvent;
import cn.etsoft.smarthomephone.pullmi.utils.LogUtils;
import cn.etsoft.smarthomephone.utils.ToastUtil;
import cn.etsoft.smarthomephone.view.Circle_Progress;
import cn.etsoft.smarthomephone.weidget.CustomDialog;
import cn.etsoft.smarthomephone.weidget.CustomDialog_comment;
import cn.etsoft.smarthomephone.weidget.SwipeListView;

/**
 * Created by Say GoBay on 2016/9/2.
 * 情景设置页面
 */
public class SceneSetActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ImageView iv_cancel, back;
    private EditText name;
    private Button sure, cancel;
    private TextView title;
    private SwipeListView lv;

    private SystemAdapter systemAdapter;
    private List<WareSceneEvent> event;
    private Dialog mDialog;
    private Handler mHandler;

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
        setContentView(R.layout.activity_sceneset_listview2);
        //初始化标题栏
        initTitleBar();
        //初始化ListView
        initListView();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (mDialog != null)
                    mDialog.dismiss();
                //初始化ListView
                initListView();
                super.handleMessage(msg);
            }
        };
//        MyApplication.mInstance.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
//            @Override
//            public void upDataWareData(int what) {
//                if (what == UdpProPkt.E_UDP_RPO_DAT.e_udpPro_addSceneEvents.getValue()
//                        || what == UdpProPkt.E_UDP_RPO_DAT.e_udpPro_delSceneEvents.getValue()) {
//                    Message message = mHandler.obtainMessage();
//                    mHandler.sendMessage(message);
//                }
//            }
//        });

        MyApplication.mInstance.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int what) {
                if (mDialog != null)
                    mDialog.dismiss();
                if (what == 23) {
                    //初始化情景的listView
                    initListView();
                    ToastUtil.showToast(SceneSetActivity.this, "添加成功");
                }
                if (what == 25) {
                    initListView();
                    ToastUtil.showToast(SceneSetActivity.this, "删除成功");
                }
            }
        });
    }


    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        title = (TextView) findViewById(R.id.title_bar_tv_title);
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        title.setText(getIntent().getStringExtra("title"));
        back.setOnClickListener(this);
        event = new ArrayList<>();
    }

    /**
     * 初始化ListView
     */
    private void initListView() {

        event.clear();
        for (int i = 0; i < MyApplication.getWareData().getSceneEvents().size(); i++) {
            event.add(MyApplication.getWareData().getSceneEvents().get(i));
        }
        lv = (SwipeListView) findViewById(R.id.sceneSet_lv);
        systemAdapter = new SystemAdapter(this, event, mListener);
        lv.setAdapter(systemAdapter);
        lv.setOnItemClickListener(this);
    }

    /**
     * 初始化自定义dialog
     */
    CustomDialog dialog;

    public void getDialog() {
        dialog = new CustomDialog(this, R.style.customDialog, R.layout.dialog_sceneset);
        dialog.show();
        iv_cancel = (ImageView) dialog.findViewById(R.id.scene_iv_cancel);
        name = (EditText) dialog.findViewById(R.id.scene_et_name);
        sure = (Button) dialog.findViewById(R.id.scene_btn_sure);
        cancel = (Button) dialog.findViewById(R.id.scene_btn_cancel);
        iv_cancel.setOnClickListener(this);
        sure.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        int listSize = event.size();
        if (listSize > 0) {
            if (position < 2) {
                ToastUtil.showToast(SceneSetActivity.this, "全开、全关模式不可操作");
                return;
            } else if (position < listSize ) {
                Intent intent = new Intent(SceneSetActivity.this, SceneSettingActivity.class);
                Bundle bundle = new Bundle();
                bundle.putByte("eventId", event.get(position).getEventld());
                bundle.putString("sceneName", event.get(position).getSceneName());
                intent.putExtra("bundle",bundle);
                startActivity(intent);
            }else {
                if (MyApplication.getWareData().getSceneEvents().size() == 8) {
                    ToastUtil.showToast(SceneSetActivity.this, "最多添加8个情景模式");
                    return;
                  }
                getDialog();
            }
        } else {
            ToastUtil.showToast(this, "数据异常");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scene_iv_cancel:
                dialog.dismiss();
                break;
            case R.id.scene_btn_sure:
                dialog.dismiss();
                initDialog("正在添加...");
                String data = name.getText().toString();
                if (!"".equals(data)) {
                    //新增情景模式
                    List<Integer> Scene_int = new ArrayList<>();
                    for (int i = 0; i < MyApplication.getWareData().getSceneEvents().size(); i++) {
                        Scene_int.add((int) MyApplication.getWareData().getSceneEvents().get(i).getEventld());
                    }
                    List<Integer> Scene_id = new ArrayList<>();
                    for (int i = 0; i < 8; i++) {
                        Scene_id.add(i);
                    }
                    List<Integer> ID = new ArrayList<>();
                    for (int i = 0; i < Scene_id.size(); i++) {
                        if (!Scene_int.contains(Scene_id.get(i))) {
                            ID.add(Scene_id.get(i));
                        }
                    }
//                    for (int i = 0; i < ID.size(); i++) {
//                        Log.e("ID",ID.get(i)+"");
//                    }
                    add_scene((byte) (int) ID.get(0), data);
                    //新增情景模式
//                    add_scene(event.size(), data);
//
                    initListView();
                } else {
                    dialog.dismiss();
                    ToastUtil.showToast(SceneSetActivity.this, "请填写情景名称");
                }
                break;
            case R.id.scene_btn_cancel:
                dialog.dismiss();
                break;
            case R.id.title_bar_iv_back:
                finish();
                break;
        }
    }

    /**
     * 新增情景模式
     * @param eventID
     * @param name
     */
    private void add_scene(int eventID, String name) {
        byte[] data = {0};
        try {
            data = name.getBytes("GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String str_gb = CommonUtils.bytesToHexString(data);
        LogUtils.LOGE("情景模式名称:%s", str_gb);

        String ctlStr = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
                ",\"sceneName\":\"" + str_gb + "\"" +
                ",\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_addSceneEvents.getValue() +
                ",\"subType1\":0" +
                ",\"subType2\":0" +
                ",\"eventId\":" + eventID +
                ",\"devCnt\":" + 0 +
                ",\"itemAry\":[{" +
                "\"uid\":\"\"" +
                ",\"devType\":" + 0 +
                ",\"devID\":" + 0 +
                ",\"bOnOff\":" + 0 +
                ",\"lmVal\":0" +
                ",\"param1\":0" +
                ",\"param2\":0" +
                "}]}";
        MyApplication.sendMsg(ctlStr);
    }

    /**
     * 删除情景模式
     * @param eventID
     * @param name
     */
    private void del_scene(int eventID, String name) {
        byte[] data = {0};
        try {
            data = name.getBytes("GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String str_gb = CommonUtils.bytesToHexString(data);
        LogUtils.LOGE("情景模式名称:%s", str_gb);

        String ctlStr = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
                ",\"sceneName\":\"" + str_gb + "\"" +
                ",\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_delSceneEvents.getValue() +
                ",\"subType1\":0" +
                ",\"subType2\":0" +
                ",\"eventId\":" + eventID +
                ",\"devCnt\":" + 0 +
                ",\"itemAry\":[{" +
                "\"uid\":\"\"" +
                ",\"devType\":" + 0 +
                ",\"devID\":" + 0 +
                ",\"bOnOff\":" + 0 +
                ",\"lmVal\":0" +
                ",\"param1\":0" +
                ",\"param2\":0" +
                "}]}";
        LogUtils.LOGE("情景模式测试数据:", ctlStr);
        MyApplication.sendMsg(ctlStr);
    }

    /**
     * 实现类，响应按钮点击事件
     */
    private IClick mListener = new IClick() {
        @Override
        public void listViewItemClick(final int position, View v) {
            switch (v.getId()) {
                case R.id.deploy_delete:
                    CustomDialog_comment.Builder builder = new CustomDialog_comment.Builder(SceneSetActivity.this);
                    builder.setTitle("提示 :");
                    builder.setMessage("您确定删除此模式?");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                            initDialog("正在删除...");
                            byte sceneId = MyApplication.getWareData().getSceneEvents().get(position).getEventld();
                            String name = MyApplication.getWareData().getSceneEvents().get(position).getSceneName();
                            //删除情景模式
                            del_scene(sceneId, name);
                        }
                    });
                    builder.create().show();
                    break;
            }
        }
    };
}
