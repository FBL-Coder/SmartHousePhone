package cn.etsoft.smarthomephone.ui;

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

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.adapter.ConditionAdapter;
import cn.etsoft.smarthomephone.pullmi.app.GlobalVars;
import cn.etsoft.smarthomephone.utils.ToastUtil;
import cn.etsoft.smarthomephone.view.Circle_Progress;

/**
 * Created by Say GoBay on 2017/6/1.
 * 系统设置--环境事件
 */
public class ConditionEventActivity extends Activity implements AdapterView.OnItemClickListener {
    private ImageView back;
    private TextView title;
    private ListView lv;
    private ConditionAdapter conditionAdapter;
    private Dialog mDialog;
    //触发器所在列表位置
    private int Condition_position;
    String ctlStr = "{\"devUnitID\":\"" + GlobalVars.getDevid() + "\"" +
            ",\"datType\":27" +
            ",\"subType1\":0" +
            ",\"subType2\":0" +
            "}";

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
        MyApplication.mInstance.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int what) {
                if (mDialog != null)
                    mDialog.dismiss();
                if (what == 27) {
                    mDialog.dismiss();
                    //初始化ListView
                    initListView();
                }
            }
        });
        MyApplication.sendMsg(ctlStr);

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
        if (MyApplication.getWareData().getCondition_event_bean() == null || MyApplication.getWareData().getCondition_event_bean().getenvEvent_rows().size() == 0) {
            return;
        }
        initListView();
    }
    /**
     * 初始化触发器名称
     */
    private void initListView() {
        lv = (ListView) findViewById(R.id.sceneSet_lv);
        mDialog.dismiss();
        if (MyApplication.getWareData().getCondition_event_bean() == null || MyApplication.getWareData().getCondition_event_bean().getenvEvent_rows().size() == 0) {
            ToastUtil.showToast(this, "没有收到触发器信息");
            return;
        }
        conditionAdapter = new ConditionAdapter(this,MyApplication.getWareData().getCondition_event_bean());
        lv.setAdapter(conditionAdapter);
        lv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(this, ConditionEventActivity_details.class);
        Condition_position = position;
        Bundle bundle = new Bundle();
        bundle.putInt("Condition_position", Condition_position);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
