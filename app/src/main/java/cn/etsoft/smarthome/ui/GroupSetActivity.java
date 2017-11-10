package cn.etsoft.smarthome.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.adapter.CroupSetAdapter;
import cn.etsoft.smarthome.ui.Setting.GroupSetActivity_details;
import cn.etsoft.smarthome.utils.SendDataUtil;
import cn.etsoft.smarthome.utils.ToastUtil;

/**
 * Created by Say GoBay on 2017/6/1.
 * 系统设置--组合设置
 */
public class GroupSetActivity extends Activity implements AdapterView.OnItemClickListener {
    private ImageView back;
    private TextView title;
    private ListView lv;
    private CroupSetAdapter groupSetAdapter;
    //触发器所在列表位置
    private int GroupSet_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sceneset_listview2);

        //初始化标题栏
        initTitleBar();
        MyApplication.mApplication.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int datType, int subtype1, int subtype2) {
                if (datType == 66) {
                    MyApplication.mApplication.dismissLoadDialog();
                    //初始化ListView
                    initListView();
//                    if (GroupSet_position != 0) {
//                        ToastUtil.showToast(GroupSetActivity.this, "保存成功");
//                    }
                }
            }
        });
        if (!"".equals(GlobalVars.getDevid())) {
            SendDataUtil.getGroupSetInfo();
            MyApplication.mApplication.showLoadDialog(this);
        }
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
        if (MyApplication.getWareData().getmGroupSet_Data() == null || MyApplication.getWareData().getmGroupSet_Data().getSecs_trigger_rows().size() == 0) {
            return;
        }
        initListView();
    }

    /**
     * 初始化组合器名称
     */
    private void initListView() {
        lv = (ListView) findViewById(R.id.sceneSet_lv);
        MyApplication.mApplication.dismissLoadDialog();
        if (MyApplication.getWareData().getmGroupSet_Data() == null || MyApplication.getWareData().getmGroupSet_Data().getSecs_trigger_rows().size() == 0) {
            ToastUtil.showText("没有收到组合器信息");
            return;
        }
        groupSetAdapter = new CroupSetAdapter(this, MyApplication.getWareData().getmGroupSet_Data());
        lv.setAdapter(groupSetAdapter);
        lv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(this, GroupSetActivity_details.class);
        GroupSet_position = position;
        Bundle bundle = new Bundle();
        bundle.putInt("GroupSet_position", GroupSet_position);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
