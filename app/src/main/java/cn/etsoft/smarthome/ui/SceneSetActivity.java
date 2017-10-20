package cn.etsoft.smarthome.ui;

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

import cn.etsoft.smarthome.Helper.WareDataHliper;
import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.IClick;
import cn.etsoft.smarthome.adapter.SystemAdapter;
import cn.etsoft.smarthome.domain.UdpProPkt;
import cn.etsoft.smarthome.domain.WareSceneEvent;
import cn.etsoft.smarthome.pullmi.common.CommonUtils;
import cn.etsoft.smarthome.pullmi.utils.LogUtils;
import cn.etsoft.smarthome.utils.SendDataUtil;
import cn.etsoft.smarthome.utils.ToastUtil;
import cn.etsoft.smarthome.view.Circle_Progress;
import cn.etsoft.smarthome.weidget.CustomDialog;
import cn.etsoft.smarthome.weidget.CustomDialog_comment;
import cn.etsoft.smarthome.weidget.SwipeListView;

/**
 * Created by Say GoBay on 2016/9/2.
 * 情景设置页面
 */
public class SceneSetActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ImageView iv_cancel, back,ref_IV;
    private EditText name;
    private Button sure, cancel;
    private TextView title;
    private SwipeListView lv;

    private SystemAdapter systemAdapter;
    private List<WareSceneEvent> event;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sceneset_listview2);
        if (MyApplication.getWareData().getSceneEvents().size() == 0) {
            SendDataUtil.getSceneInfo();
            MyApplication.mApplication.showLoadDialog(this);
        } else {
            WareDataHliper.initCopyWareData().startCopySceneData();
        }
        //初始化标题栏
        initTitleBar();
        //初始化ListView
        initListView();
        MyApplication.mApplication.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int datType, int subtype1, int subtype2) {
                if (datType == 22) {
                    MyApplication.mApplication.dismissLoadDialog();
                    WareDataHliper.initCopyWareData().startCopySceneData();
                    initListView();
                }
                if (datType == 23) {
                    MyApplication.mApplication.dismissLoadDialog();
                    //初始化情景的listView
                    initListView();
                    ToastUtil.showText("添加成功");
                }
                if (datType == 25) {
                    MyApplication.mApplication.dismissLoadDialog();
                    initListView();
                    ToastUtil.showText("删除成功");
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
        ref_IV = (ImageView) findViewById(R.id.title_bar_iv_or);
        ref_IV.setVisibility(View.VISIBLE);
        ref_IV.setImageResource(R.drawable.refrush_1);
        title.setText(getIntent().getStringExtra("title"));
        back.setOnClickListener(this);
        ref_IV.setOnClickListener(this);
        event = new ArrayList<>();
    }

    /**
     * 初始化ListView
     */
    private void initListView() {
        event = WareDataHliper.initCopyWareData().getCopyScenes();
        lv = (SwipeListView) findViewById(R.id.sceneSet_lv);
        if (MyApplication.getWareData().getSceneEvents().size() > 0) {
            systemAdapter = new SystemAdapter(this, event, mListener);
            lv.setAdapter(systemAdapter);
            lv.setOnItemClickListener(this);
        }
    }

    /**
     * 初始化自定义dialog
     */
    CustomDialog dialog;

    public void getDialog() {
        dialog = new CustomDialog(this, R.style.customDialog, R.layout.dialog_sceneset);
        dialog.show();
        name = (EditText) dialog.findViewById(R.id.scene_et_name);
        sure = (Button) dialog.findViewById(R.id.scene_btn_sure);
        cancel = (Button) dialog.findViewById(R.id.scene_btn_cancel);
        sure.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int listSize = event.size();
        if (listSize > 0) {
            if (position < listSize) {
                Intent intent = new Intent(SceneSetActivity.this, SceneSettingActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("eventId", event.get(position).getEventId());
                bundle.putString("sceneName", event.get(position).getSceneName());
                intent.putExtra("bundle", bundle);
                startActivity(intent);
            } else {
                if (MyApplication.getWareData().getSceneEvents().size() == 6) {
                    ToastUtil.showText("自定义情景最多6个！");
                    return;
                }
                getDialog();
            }
        } else {
            ToastUtil.showText("数据异常");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_bar_iv_or:
                SendDataUtil.getSceneInfo();
                MyApplication.mApplication.showLoadDialog(this);
                break;
            case R.id.scene_btn_sure:
                dialog.dismiss();
                String data = name.getText().toString();
                if (!"".equals(data)) {
                    //新增情景模式
                    List<Integer> Scene_int = new ArrayList<>();
                    for (int i = 0; i < MyApplication.getWareData().getSceneEvents().size(); i++) {
                        Scene_int.add((int) MyApplication.getWareData().getSceneEvents().get(i).getEventId());
                    }
                    List<Integer> Scene_id = new ArrayList<>();
                    for (int i = 2; i < 8; i++) {
                        Scene_id.add(i);
                    }
                    List<Integer> ID = new ArrayList<>();
                    for (int i = 0; i < Scene_id.size(); i++) {
                        if (!Scene_int.contains(Scene_id.get(i))) {
                            ID.add(Scene_id.get(i));
                        }
                    }
                    try {
                        add_scene(ID.get(0), data);
                    } catch (Exception e) {
                        ToastUtil.showText("数据异常，请重试");
                        return;
                    }
                    initListView();
                } else {
                    dialog.dismiss();
                    ToastUtil.showText("请填写情景名称");
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
     *
     * @param eventID
     * @param name
     */
    private void add_scene(int eventID, String name) {
        SendDataUtil.addscene(eventID, name);
        MyApplication.mApplication.showLoadDialog(this);
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
                            //删除情景模式
                            try {
                                SendDataUtil.deleteScene(MyApplication.getWareData().getSceneEvents().get(position));
                            }catch (Exception e){
                                ToastUtil.showText("数据请求异常，请刷新情景数据");
                                return;
                            }
                            MyApplication.mApplication.showLoadDialog(SceneSetActivity.this);
                        }
                    });
                    builder.create().show();
                    break;
            }
        }
    };
}
