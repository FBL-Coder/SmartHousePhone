package cn.etsoft.smarthome.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.SafetyAdapter;
import cn.etsoft.smarthome.utils.SendDataUtil;
import cn.etsoft.smarthome.utils.ToastUtil;
import cn.etsoft.smarthome.view.Circle_Progress;

/**
 * Created by Say GoBay on 2017/6/1.
 * 系统设置--安防设置
 */
public class SafetyActivity extends Activity implements AdapterView.OnItemClickListener {
    private ImageView back;
    private TextView title;
    private ListView lv;
    private SafetyAdapter safetyAdapter;
    private Dialog mDialog;
    //安防位置position
    private int Safety_position = 0;

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
        setContentView(R.layout.activity_sceneset_listview2);
        initDialog("初始化数据中...");
        //初始化标题栏
        initTitleBar();
        //初始化ListView
        initListView();
        MyApplication.mApplication.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int datType, int subtype1, int subtype2) {
                if (mDialog != null)
                    mDialog.dismiss();
                if (datType == 32) {
                    if (MyApplication.getWareData().getResult_safety() != null && MyApplication.getWareData().getResult_safety().getSubType2() == 255
                            && MyApplication.getWareData().getResult_safety().getSubType1() == 4) {
                        //初始化ListView
                        initListView();
                    }
                }
            }
        });
        SendDataUtil.getSafetyInfo();
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        title = (TextView) findViewById(R.id.title_bar_tv_title);
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        title.setText(getIntent().getStringExtra("title"));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //修改名称之后返回页面刷新
    @Override
    protected void onRestart() {
        super.onRestart();
        if (MyApplication.getWareData().getResult_safety() == null || MyApplication.getWareData().getResult_safety().getSec_info_rows().size() == 0) {
            return;
        }
        initListView();
    }

    /**
     * 初始化防区名称
     */
    private void initListView() {
        lv = (ListView) findViewById(R.id.sceneSet_lv);
        mDialog.dismiss();
        if (MyApplication.getWareData().getResult_safety() == null || MyApplication.getWareData().getResult_safety().getSec_info_rows().size() == 0) {
            ToastUtil.showText("没有收到防区信息");
            return;
        }
        safetyAdapter = new SafetyAdapter(this,MyApplication.getWareData().getResult_safety());
        lv.setAdapter(safetyAdapter);
        lv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(this, SafetyActivity_details.class);
        Safety_position = position;
        Bundle bundle = new Bundle();
//        bundle.putString("title", MyApplication.getWareData().getResult_safety().getSec_info_rows().get(position).getSecName());
        bundle.putInt("Safety_position", Safety_position);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
