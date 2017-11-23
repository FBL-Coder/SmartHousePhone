package cn.etsoft.smarthome.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.ConditionAdapter;
import cn.etsoft.smarthome.ui.Setting.ConditionEventActivity_details;
import cn.etsoft.smarthome.utils.SendDataUtil;
import cn.etsoft.smarthome.utils.ToastUtil;

/**
 * Created by Say GoBay on 2017/6/1.
 * 系统设置--环境事件
 */
public class ConditionEventActivity extends Activity implements AdapterView.OnItemClickListener {
    private ImageView back;
    private TextView title;
    private ListView lv;
    private ConditionAdapter conditionAdapter;
    //触发器所在列表位置
    private int Condition_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sceneset_listview2);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.AppTheme_color));

        if (!"".equals(GlobalVars.getDevid())) {
            if (MyApplication.getWareData().getCondition_event_bean().getenvEvent_rows().size() == 0) {
                MyApplication.mApplication.showLoadDialog(this);
                SendDataUtil.getConditionInfo();
            } else initListView();
        }
        //初始化标题栏
        initTitleBar();
        MyApplication.mApplication.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int datType, int subtype1, int subtype2) {
                if (datType == 27) {
                    MyApplication.mApplication.dismissLoadDialog();
                    //初始化ListView
                    initListView();
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
        MyApplication.mApplication.dismissLoadDialog();
        if (MyApplication.getWareData().getCondition_event_bean() == null || MyApplication.getWareData().getCondition_event_bean().getenvEvent_rows().size() == 0) {
            ToastUtil.showText("没有收到触发器信息");
            return;
        }
        conditionAdapter = new ConditionAdapter(this, MyApplication.getWareData().getCondition_event_bean());
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
