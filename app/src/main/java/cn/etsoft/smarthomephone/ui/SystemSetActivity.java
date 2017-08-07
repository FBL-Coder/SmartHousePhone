package cn.etsoft.smarthomephone.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.etsoft.smarthomephone.MyApplication;
import cn.etsoft.smarthomephone.R;
import cn.etsoft.smarthomephone.UiUtils.ToastUtil;
import cn.etsoft.smarthomephone.adapter.GridViewAdapter;
import cn.etsoft.smarthomephone.pullmi.app.GlobalVars;
import cn.etsoft.smarthomephone.weidget.CustomDialog_comment;

/**
 * Created by Say GoBay on 2016/9/2.
 * 系统设置页面
 */
public class SystemSetActivity extends Activity implements AdapterView.OnItemClickListener {
    private GridView gridView;
    private LinearLayout ll;
    private String[] text = {"联网模块", "远程IP", "模块详情", "用户界面", "控制设置", "设备信息", "情景设置", "安防设置", "定时设置", "环境事件", "组合设置"};
    private int[] image = {R.drawable.systemsetup2, R.drawable.systemsetup3, R.drawable.systemsetup4, R.drawable.systemsetup5, R.drawable.equipmentcontrol,R.drawable.systemsetup2, R.drawable.systemsetup3, R.drawable.systemsetup4, R.drawable.systemsetup5, R.drawable.equipmentcontrol,R.drawable.systemsetup2};
    private ImageView back, title_bar_iv_or;
    private TextView title, title_bar_tv_room, name_cur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene);
        //初始化标题栏
        initTitleBar();
        //初始化控件
        initView();
        //初始化GridView
        initGridView();
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        back = (ImageView) findViewById(R.id.title_bar_iv_back);
        title = (TextView) findViewById(R.id.title_bar_tv_title);
        title.setText(getIntent().getStringExtra("title"));
        title.setTextColor(0xffffffff);
        name_cur = (TextView) findViewById(R.id.name_cur);
        name_cur.setVisibility(View.GONE);
        title_bar_tv_room = (TextView) findViewById(R.id.title_bar_tv_room);
        title_bar_tv_room.setVisibility(View.VISIBLE);
        title_bar_tv_room.setText("退出登录");
        title_bar_tv_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CustomDialog_comment.Builder builder = new CustomDialog_comment.Builder(SystemSetActivity.this);
                builder.setMessage("您确定要退出登录？");
                builder.setTitle("提示");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences sharedPreferences = getSharedPreferences("profile",
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("list", "");
                        editor.putString("user", "");
                        editor.commit();
                        String a = sharedPreferences.getString("list", "");
                        String b = sharedPreferences.getString("user", "");
                        GlobalVars.setDevid("");
                        GlobalVars.setDevpass("");

                        if (MyApplication.getmHomeActivity() != null)
                            MyApplication.getmHomeActivity().finish();
                        startActivity(new Intent(SystemSetActivity.this, cn.semtec.community2.activity.LoginActivity.class));
                        dialogInterface.dismiss();
                        finish();
                    }
                });
                builder.create().show();
            }
        });
        back.setImageResource(R.drawable.return2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        })
        ;
    }

    /**
     * 初始化控件
     */
    private void initView() {
        ll = (LinearLayout) findViewById(R.id.ll);
        ll.setBackgroundResource(R.drawable.tu7);
    }

    /**
     * 初始化GridView
     */
    private void initGridView() {
        gridView = (GridView) findViewById(R.id.light_gv);
        gridView.setAdapter(new GridViewAdapter(image, text, this));
        gridView.setSelector(R.drawable.selector_gridview_item);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        switch (position) {
            case 0:
                intent = new Intent(SystemSetActivity.this, NetWorkActivity.class);
                MyApplication.getActivities().add(SystemSetActivity.this);
                intent.putExtra("title", text[0]);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(SystemSetActivity.this, IpActivity.class);
                intent.putExtra("title", text[1]);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(SystemSetActivity.this, ModuleDetailActivity.class);
                intent.putExtra("title", text[2]);
                startActivity(intent);
                break;
            case 3:
                if (MyApplication.mInstance.isSkip()) {
                    ToastUtil.showToast(this, "跳过登录不能体验此功能");
                    return;
                }
                intent = new Intent(SystemSetActivity.this, UserActivity.class);
                intent.putExtra("title", text[3]);
                intent.putExtra("tag","user");
                startActivity(intent);
                break;
            case 4:
                intent = new Intent(SystemSetActivity.this, ControlActivity.class);
                intent.putExtra("title", text[4]);
                startActivity(intent);
                break;
            case 5:
                intent = new Intent(SystemSetActivity.this, Equipment_control.class);
                intent.putExtra("title", text[5]);
                startActivity(intent);
                break;
            case 6:
                intent = new Intent(SystemSetActivity.this, SceneSetActivity.class);
                intent.putExtra("title", text[6]);
                startActivity(intent);
                break;
            case 7:
                intent = new Intent(SystemSetActivity.this, SafetyActivity.class);
                intent.putExtra("title", text[7]);
                startActivity(intent);
                break;
            case 8:
                intent = new Intent(SystemSetActivity.this, TimerActivity.class);
                intent.putExtra("title", text[8]);
                startActivity(intent);
                break;
            case 9:
                intent = new Intent(SystemSetActivity.this, ConditionEventActivity.class);
                intent.putExtra("title", text[9]);
                startActivity(intent);
                break;
            case 10:
                intent = new Intent(SystemSetActivity.this, GroupSetActivity.class);
                intent.putExtra("title", text[10]);
                startActivity(intent);
                break;

        }
    }
}
