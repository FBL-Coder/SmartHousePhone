package cn.etsoft.smarthome.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.KeyAdapter_keyScene;
import cn.etsoft.smarthome.domain.ChnOpItem_scene;
import cn.etsoft.smarthome.utils.SendDataUtil;
import cn.etsoft.smarthome.utils.ToastUtil;
import cn.etsoft.smarthome.view.Circle_Progress;
import cn.etsoft.smarthome.weidget.CustomDialog_comment;

/**
 * Created by Say GoBay on 2017/5/31.
 * 组合设置--按键情景--详情
 */
public class KeySceneActivity_dev extends Activity implements View.OnClickListener {
    private TextView mTitle, save;
    private ImageView back;
    private int sceneId = 0;
    private int keyInput_position = 0;
    private GridView gridView;
    private boolean IsClose = false;
    private KeyAdapter_keyScene keyAdapter_keyscene;
    private Dialog mDialog;
    private ChnOpItem_scene listData_all;
    private boolean IsHaveData = false;

    //自定义加载进度条
    private void initDialog(String str) {
        Circle_Progress.setText(str);
        mDialog = Circle_Progress.createLoadingDialog(this);
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
        setContentView(R.layout.activity_key);
        //初始化标题栏
        initTitleBar();
        //发送消息
        SendDataUtil.getScene_KeysData();
        //通知数据更新
        MyApplication.mApplication.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int datType, int subtype1, int subtype2) {
                if (mDialog != null)
                    mDialog.dismiss();

                if (datType == 58 && MyApplication.getWareData().getChnOpItem_scene().getSubType1() == 1) {
                    IsHaveData = true;
//                    WareDataHliper.initCopyWareData().startCopyScene_KeysData();
                    listData_all = MyApplication.getWareData().getChnOpItem_scene();
                    MyApplication.getWareData().setChnOpItem_scene(listData_all);
                    onGetKeySceneDataListener.getKeySceneData();
                }

                if (datType == 59 && MyApplication.getWareData().getResult() != null && MyApplication.getWareData().getResult().getSubType1() == 1) {
                    Toast.makeText(KeySceneActivity_dev.this, "保存成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
        initDialog("正在加载...");
        KeySceneActivity_dev.setOnGetKeySceneDataListener(new KeySceneActivity_dev.OnGetKeySceneDataListener() {
            @Override
            public void getKeySceneData() {
                initData();
            }
        });
        KeySceneActivity_dev.setOnGetIsChooseListener(new KeySceneActivity_dev.OnGetIsChooseListener() {
            @Override
            public void getOutChoose(boolean isClose) {
                IsClose = isClose;
                initData();
            }
        });
        initGridView();
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        mTitle = (TextView) findViewById(R.id.title_bar_tv_title);
        mTitle.setText(getIntent().getStringExtra("title"));
        mTitle.setTextColor(0xffffffff);
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        back.setImageResource(R.drawable.return2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        save = (TextView) findViewById(R.id.title_bar_tv_room);
        save.setVisibility(View.VISIBLE);
        save.setText("保存");
        save.setTextColor(0xffffffff);
        save.setOnClickListener(this);
        sceneId = getIntent().getExtras().getInt("sceneId");
        keyInput_position = getIntent().getExtras().getInt("keyInput_position");

    }

    /**
     * 初始化控件
     *
     * @param
     */
    private void initGridView() {
        gridView = (GridView) findViewById(R.id.gridView_light);
//        input_choose = (ImageView) findViewById(R.id.input_choose);
//        input_choose.setOnClickListener(this);
    }

    private void initData() {
        keyAdapter_keyscene = new KeyAdapter_keyScene(this, sceneId, keyInput_position, IsClose);
        gridView.setAdapter(keyAdapter_keyscene);
    }

    @Override
    public void onClick(View view) {
        if (!IsHaveData) {
            ToastUtil.showText("获取数据异常，请稍后在试");
            return;
        }
        switch (view.getId()) {
//            case R.id.input_choose:
//                if (IsClose) {
//                    IsClose = false;
//                    input_choose.setImageResource(R.drawable.off);
//                } else {
//                    IsClose = true;
//                    input_choose.setImageResource(R.drawable.on);
//                }
//                onGetIsChooseListener.getOutChoose(IsClose);
//                break;
            case R.id.title_bar_tv_room:
                if (MyApplication.getWareData().getChnOpItem_scene() == null) {
                    ToastUtil.showText("没有按键信息，不能保存");
                    return;
                }
                CustomDialog_comment.Builder builder = new CustomDialog_comment.Builder(this);
                builder.setTitle("提示 :");
                builder.setMessage("您要保存这些设置吗？");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ChnOpItem_scene chnOpItem_scene = null;
                        String data_str = "";
                        String div;
                        String more_data = "";
                        div = ",";
                        chnOpItem_scene = MyApplication.getWareData().getChnOpItem_scene();
                        if (chnOpItem_scene.getKey2scene_item().size() > 12) {
                            ToastUtil.showText( "最多只能添加12个按键");
                            return;
                        } else {
                            for (int j = 0; j < chnOpItem_scene.getKey2scene_item().size(); j++) {
                                data_str = "{" +
                                        //不确定正确
                                        "\"canCpuID\":\"" + chnOpItem_scene.getKey2scene_item().get(j).getKeyUId() + "\"," +
                                        "\"keyIndex\":" + chnOpItem_scene.getKey2scene_item().get(j).getKeyIndex() + "," +
                                        "\"eventId\":" + chnOpItem_scene.getKey2scene_item().get(j).getEventId()
                                        + "}" + div;
                                more_data += data_str;
                            }
                        }
                        try {
                            more_data = more_data.substring(0, more_data.lastIndexOf(","));
                        } catch (Exception e) {
                            System.out.println(e + "");
                        }
                        initDialog("正在保存...");
                        //这就是要上传的字符串:data_hoad
                        String data_hoad = "{" +
                                "\"devUnitID\":\"" + GlobalVars.getDevid() + "\"," +
                                "\"datType\":59" + "," +
                                "\"subType1\":0" + "," +
                                "\"subType2\":0" + "," +
                                "\"itemCnt\":" + chnOpItem_scene.getKey2scene_item().size() + "," +
                                "\"key2scene_item\":[" + more_data + "]}";
                        Log.e("情景模式测试:", data_hoad);
                        MyApplication.mApplication.getUdpServer().send(data_hoad);
                    }
                });
                builder.create().show();
                break;
        }
    }

    //数据刷新后提供数据接口
    private static OnGetKeySceneDataListener onGetKeySceneDataListener;
    private static OnGetIsChooseListener onGetIsChooseListener;

    public static void setOnGetKeySceneDataListener(OnGetKeySceneDataListener ongetKeySceneDataListener) {
        onGetKeySceneDataListener = ongetKeySceneDataListener;
    }

    public static void setOnGetIsChooseListener(OnGetIsChooseListener ongetIsChooseListener) {
        onGetIsChooseListener = ongetIsChooseListener;
    }

    interface OnGetKeySceneDataListener {
        void getKeySceneData();
    }

    interface OnGetIsChooseListener {
        void getOutChoose(boolean isClose);
    }
}
