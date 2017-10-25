package cn.etsoft.smarthome.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

import cn.etsoft.smarthome.Fragment.HomeFragment;
import cn.etsoft.smarthome.Fragment.SettingFragment;
import cn.etsoft.smarthome.Fragment.UserInterface;
import cn.etsoft.smarthome.Helper.LogoutHelper;
import cn.etsoft.smarthome.MyApplication;
import cn.etsoft.smarthome.NetMessage.GlobalVars;
import cn.etsoft.smarthome.NetWorkListener.AppNetworkMgr;
import cn.etsoft.smarthome.NetWorkListener.NetBroadcastReceiver;
import cn.etsoft.smarthome.R;
import cn.etsoft.smarthome.domain.City;
import cn.etsoft.smarthome.domain.RcuInfo;
import cn.etsoft.smarthome.domain.WareBoardChnout;
import cn.etsoft.smarthome.domain.WareData;
import cn.etsoft.smarthome.domain.WareDev;
import cn.etsoft.smarthome.domain.Weather_All_Bean;
import cn.etsoft.smarthome.pullmi.utils.Data_Cache;
import cn.etsoft.smarthome.ui.Setting.NewWorkSetActivity;
import cn.etsoft.smarthome.utils.AppSharePreferenceMgr;
import cn.etsoft.smarthome.utils.CityDB;
import cn.etsoft.smarthome.utils.NetUtil;
import cn.etsoft.smarthome.utils.SendDataUtil;
import cn.etsoft.smarthome.utils.ToastUtil;
import cn.etsoft.smarthome.view.DepthPageTransformer;
import cn.etsoft.smarthome.view.ViewPagerCompat;
import cn.etsoft.smarthome.weidget.CustomDialog;

/**
 * 主页界面
 */

public class HomeActivity extends FragmentActivity implements View.OnClickListener {
    private RadioGroup homeRadioGroup;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private Fragment homeFragment, settingFragment, Userinterface;
    public static final String PM2D5_BASE_URL = "http://jisutianqi.market.alicloudapi.com/weather/query?citycode=";
    private static final String 天气key = "APPCODE 500a3b58be714c519f83e8aa9a23810e";
    private CityDB mCityDB;
    private City mCurCity;
    private Button city_sure, city_cancel;
    private EditText city_name;
    private LinearLayout ll_loaction;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    private List<String> text_room;
    private List<WareDev> mWareDev_room;
    //ViewPager
    //图片标题
    private TextView textView_banner, loaction_text, temp_text,
            hum_text, pm_25, breath_text, weather_text, net_now;
    private ImageView ref_home, home_isConnect, home_logout;
    private ViewPagerCompat mViewPager;
    private List<Integer> mImgIds_img = new ArrayList<>();
    private int[] mImgIds = new int[]{R.drawable.tu5};
    private List<ImageView> mImageViews = new ArrayList<ImageView>();
    private LinearLayout ll_home_dots;
    private int RoomPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.mApplication.showLoadDialog(this, false);
        setContentView(R.layout.activity_home);
        //初始化控件
        initView();
        initFragment();
        //初始化数据
        upData();
        MyApplication.mApplication.setmHomeActivity(this);
        NetBroadcastReceiver.setEvevt(new NetBroadcastReceiver.NetEvevtChangListener() {
            @Override
            public void onNetChange(int netMobile) {
                getIp();
                GlobalVars.setIsLAN(true);
                SendDataUtil.getNetWorkInfo();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.mApplication.setOnGetWareDataListener(new MyApplication.OnGetWareDataListener() {
            @Override
            public void upDataWareData(int datType, int subtype1, int subtype2) {
                if (datType == 3) {
                    //更新数据
                    upData();
                }
                if (homeDataUpDataListener != null)
                    homeDataUpDataListener.getupData(datType, subtype1, subtype2);
            }
        });
        upData();
    }

    public void getIp() {
        int NETWORK = AppNetworkMgr.getNetworkState(MyApplication.mApplication);
        if (NETWORK >= 10) {
            //获取wifi服务
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            //判断wifi是否开启
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            String ip = intToIp(ipAddress);
            if (!ip.equals(GlobalVars.WIFI_IP)) {
                GlobalVars.WIFI_IP = ip;
            }
        }
    }

    private String intToIp(int i) {
        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mViewPager = (ViewPagerCompat) findViewById(R.id.home_fragemnt_vp);
        ll_home_dots = (LinearLayout) findViewById(R.id.ll_home_dots);
        ll_loaction = (LinearLayout) findViewById(R.id.ll_loaction);
        ref_home = (ImageView) findViewById(R.id.home_tv_ref);
        home_isConnect = (ImageView) findViewById(R.id.home_isConnect);
        home_logout = (ImageView) findViewById(R.id.home_logout);
        temp_text = (TextView) findViewById(R.id.temp_text);
        hum_text = (TextView) findViewById(R.id.hum_text);
        pm_25 = (TextView) findViewById(R.id.pm_25);
        breath_text = (TextView) findViewById(R.id.breath_text);
        weather_text = (TextView) findViewById(R.id.weather_text);
        loaction_text = (TextView) findViewById(R.id.loaction_text);
        net_now = (TextView) findViewById(R.id.net_now);
        ll_loaction.setOnClickListener(this);
        home_logout.setOnClickListener(this);
        ref_home.setOnClickListener(this);
        net_now.setOnClickListener(this);

        fragmentManager = getSupportFragmentManager();
        homeRadioGroup = (RadioGroup) findViewById(R.id.rg_home);
        ((RadioButton) homeRadioGroup.findViewById(R.id.rb_home_home)).setChecked(true);
        transaction = fragmentManager.beginTransaction();
        //定位
        mCityDB = MyApplication.mApplication.getmCityDB();
        initLocation();
    }

    /**
     * 定位回调监听
     */
    private void getLocationClientOption() {
        //初始化AMapLocationClientOption对象
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(30000);
        mLocationClient.setLocationOption(mLocationOption);
    }

    Handler weather_handler;

    private void getSimpleWeatherInfo(boolean isRefresh) {
        String url = PM2D5_BASE_URL + mCurCity.getNumber();
        // L.i("weather url: " + url);
        Log.i("DATA", "请求网址  " + url);
        // 请求服务器，获取返回数据
        String weatherResult = connServerForResult(url);
        Log.i("DATA", "返回结果" + weatherResult);
        Gson gson = new Gson();
        if (!"".equals(weatherResult)) {
            try {
                Weather_All_Bean results = gson.fromJson(weatherResult, Weather_All_Bean.class);
                Message message = weather_handler.obtainMessage();
                message.obj = results;
                weather_handler.sendMessage(message);
            } catch (Exception e) {
                Log.e("Exception", "" + e);
            }
        } else {
            Looper.prepare();
            ToastUtil.showText("获取天气信息失败");
            Looper.loop();
        }
    }

    /**
     * 请求服务器，获取返回数据
     */
    private String connServerForResult(String url) {
        HttpGet httpRequest = new HttpGet(url);
        //天气key
        httpRequest.setHeader("Authorization", 天气key);
        String strResult = "";
        if (NetUtil.getNetworkState(HomeActivity.this) != NetUtil.NETWORN_NONE) {
            try {
                // HttpClient对象
                HttpClient httpClient = new DefaultHttpClient();
                // 获得HttpResponse对象
                HttpResponse httpResponse = httpClient.execute(httpRequest);
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
                    // 取得返回的数据
                    strResult = EntityUtils.toString(httpResponse.getEntity());
            } catch (Exception e) {
            }
        }
        return strResult; // 返回结果
    }

    /**
     * 定位
     */
    private void initLocation() {

        mLocationClient = new AMapLocationClient(MyApplication.getContext());
        //设置定位回调监听
        getLocationClientOption();
        //声明定位回调监听器
        AMapLocationListener mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        //可在其中解析amapLocation获取相应内容。
                        loaction_text.setText(aMapLocation.getCity());
                        mCurCity = mCityDB.getCity(aMapLocation.getCity());
                        Log.i("LOCATION", mCurCity.getCity() + "-----------" + mCurCity.getNumber());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                getSimpleWeatherInfo(true);
                            }
                        }).start();
                    } else {
                        ToastUtil.showText("定位失败");
                        loaction_text.setText("北京市");
                        mCurCity = mCityDB.getCity("北京市");
                        Log.i("LOCATION", mCurCity.getCity() + "-----------" + mCurCity.getNumber());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                getSimpleWeatherInfo(true);
                            }
                        }).start();
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        Log.e("Location", "location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                }
            }
        };
        mLocationClient.setLocationListener(mLocationListener);
        if (NetUtil.getNetworkState(HomeActivity.this) != NetUtil.NETWORN_NONE) {
            ToastUtil.showText("正在定位...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mLocationClient.startLocation();
                    Log.i("Location", "定位开始");
                }
            }).start();
        } else {
            ToastUtil.showText("没有网络...");
        }

        weather_handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Weather_All_Bean bean = (Weather_All_Bean) msg.obj;
                if (bean == null)
                    return;

                int code = Integer.parseInt(bean.getResult().getImg());
                String text = bean.getResult().getWeather();
                String temp = bean.getResult().getTemp();
                String shidu = bean.getResult().getHumidity() + "%";

                String qujian = bean.getResult().getTemplow() + "℃ ~ " + bean.getResult().getTemphigh() + "℃";
                String pm = bean.getResult().getAqi().getPm2_5();
                String zhiliang = bean.getResult().getAqi().getQuality();
                hum_text.setText(shidu);
                pm_25.setText(pm);
//                temp_text.setText(qujian);
                breath_text.setText(zhiliang);
                temp_text.setText(temp + " ℃");
                weather_text.setText(text);
            }
        };
    }

    /**
     * 填充ViewPager页面的适配器
     */
    private class MyAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            try {
                container.addView(mImageViews.get(position));
                return mImageViews.get(position);
            } catch (Exception e) {
                System.out.println("" + e);
            }
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            try {
                container.removeView(mImageViews.get(position));
            } catch (Exception e) {
                System.out.println("" + e);
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            try {
                return view == object;
            } catch (Exception e) {
                System.out.println("" + e);
            }
            return false;
        }

        @Override
        public int getCount() {
            try {
                return mImageViews.size();
            } catch (Exception e) {
                System.out.println("" + e);
            }
            return 0;
        }
    }

    /**
     * 当ViewPager中页面的状态发生改变时调用
     */
    List<ImageView> dots_iv;

    private class MyPageChangeListener implements ViewPagerCompat.OnPageChangeListener {
        private int oldPosition = 0;

        //页面状态改变的时候调用
        public void onPageSelected(int position) {
            RoomPosition = position;
            onGetViewPageNum.getViewPageNum(position);
            textView_banner.setText(text_room.get(position));
            dots_iv.get(oldPosition).setBackgroundResource(R.drawable.point_unfocused);
            dots_iv.get(position).setBackgroundResource(R.drawable.point_focused);
            oldPosition = position;
        }

        public void onPageScrollStateChanged(int arg0) {

        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }
    }

    private void initViewPager() {
        dots_iv = new ArrayList<>();
        ll_home_dots.removeAllViews();
        mImageViews.clear();
        mImgIds_img.clear();
        initData();
        for (int i = 0; i < text_room.size(); i++) {
            ImageView iv = new ImageView(HomeActivity.this);
            iv.setMaxWidth(8);
            iv.setMaxHeight(8);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(12, 12);
            lp.setMargins(1, 0, 1, 0);
            iv.setLayoutParams(lp);
            if (i == 0)
                iv.setBackgroundResource(R.drawable.point_focused);
            else
                iv.setBackgroundResource(R.drawable.point_unfocused);
            ll_home_dots.addView(iv);
            dots_iv.add(iv);
        }
        textView_banner = (TextView) findViewById(R.id.textView_banner);

        //将字体文件保存在assets/fonts/目录下，创建Typeface对象
        Typeface typeface = Typeface.createFromAsset(HomeActivity.this.getAssets(), "fonnts/hua.ttf");
        //使用字体成楷体
        textView_banner.setTypeface(typeface);
        if (text_room.size() == 0) {
            textView_banner.setText("没有数据");
        } else
            textView_banner.setText(text_room.get(0));
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        // 设置填充ViewPager页面的适配器
        mViewPager.setAdapter(new MyAdapter());
        // 设置一个监听器，当ViewPage中的页面改变时调用
        mViewPager.setOnPageChangeListener(new MyPageChangeListener());
        mViewPager.setCurrentItem(RoomPosition);
    }

    private void initData() {
        if (text_room.size() == 0) {
            mImgIds_img.add(mImgIds[0]);
        }
        for (int i = 0; i < text_room.size(); i++) {
            mImgIds_img.add(mImgIds[0]);
        }
        for (int imgId : mImgIds_img) {
            ImageView imageView = new ImageView(HomeActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(imgId);
            mImageViews.add(imageView);
        }
    }

    private void upData() {
        if (!"".equals(AppSharePreferenceMgr.get(GlobalVars.RCUINFOID_SHAREPREFERENCE, ""))) {
            List<RcuInfo> list = MyApplication.mApplication.getRcuInfoList();
            for (int i = 0; i < list.size(); i++) {
                if (AppSharePreferenceMgr.get(GlobalVars.RCUINFOID_SHAREPREFERENCE, "")
                        .equals(list.get(i).getDevUnitID())) {
                    net_now.setText(list.get(i).getCanCpuName());
                }
            }
        }
        text_room = new ArrayList<>();
        mWareDev_room = new ArrayList<>();
        for (int i = 0; i < MyApplication.getWareData().getDevs().size(); i++) {
            mWareDev_room.add(MyApplication.getWareData().getDevs().get(i));
        }
        for (int i = 0; i < mWareDev_room.size(); i++) {
            for (int j = mWareDev_room.size() - 1; j > i; j--) {
                if (mWareDev_room.get(i).getRoomName().equals(mWareDev_room.get(j).getRoomName())) {
                    mWareDev_room.remove(j);
                }
            }
        }
        if (GlobalVars.isIsLAN()) {
            home_isConnect.setImageResource(R.drawable.online);
        } else home_isConnect.setImageResource(R.drawable.noonline);
        text_room = MyApplication.getWareData().getRooms();
//        if (text_room.size() < 1) {
//            return;
//        }
        initViewPager();
    }

    /**
     * 初始化数据
     */
    private void initFragment() {
        homeFragment = new HomeFragment(this);
        settingFragment = new SettingFragment(this);
        Userinterface = new UserInterface(this);

        transaction.replace(R.id.home, homeFragment).commit();
        homeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                transaction = fragmentManager.beginTransaction();
                switch (checkedId) {
                    case R.id.rb_home_user:
                        if (MyApplication.mApplication.isVisitor()) {
                            ToastUtil.showText("这里您不可以操作哦~");
                            return;
                        }
                        transaction.replace(R.id.home, Userinterface);
                        ref_home.setImageResource(R.drawable.selector_ref);
                        break;
                    case R.id.rb_home_home:
                        transaction.replace(R.id.home, homeFragment);
                        ref_home.setImageResource(R.drawable.selector_ref);
                        break;
                    case R.id.rb_home_setting:
                        if (MyApplication.mApplication.isVisitor()) {
                            ToastUtil.showText("这里您不可以操作哦~");
                            return;
                        }
                        if (Condition()) return;
                        transaction.replace(R.id.home, settingFragment);
                        MyApplication.mApplication.setActivities(HomeActivity.this);
                        break;
                }
                transaction.commit();
            }
        });
    }

    /**
     * 判断是否处于局域网
     *
     * @return
     */
    private boolean Condition() {
        if (!GlobalVars.isIsLAN()) {
            ToastUtil.showText("设置只能在局域网下进行");
            return true;
        }
        return false;
    }

    private long TimeExit = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (MyApplication.mApplication.isVisitor()) {
            finish();
        } else {
            try {
                if (keyCode != KeyEvent.KEYCODE_BACK)
                    return false;
                if (System.currentTimeMillis() - TimeExit < 1500) {
                    MyApplication.getWareData().setBoardChnouts(new ArrayList<WareBoardChnout>());
                    Data_Cache.writeFile(GlobalVars.getDevid(), MyApplication.getWareData());
                    for (int i = 0; i < MyApplication.mApplication.getActivities().size(); i++) {
                        MyApplication.mApplication.getActivities().get(i).finish();
                    }
                    System.exit(0);
                } else {
                    Toast.makeText(HomeActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                    TimeExit = System.currentTimeMillis();
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    CustomDialog dialog_add_loaction;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_loaction:
                dialog_add_loaction = new CustomDialog(HomeActivity.this,
                        R.style.customDialog, R.layout.dialog_add_loaction);
                dialog_add_loaction.show();
                city_name = (EditText) dialog_add_loaction.findViewById(R.id.city_et_name);
                city_sure = (Button) dialog_add_loaction.findViewById(R.id.city_btn_sure);
                city_cancel = (Button) dialog_add_loaction.findViewById(R.id.city_btn_cancel);
                city_sure.setOnClickListener(this);
                city_cancel.setOnClickListener(this);
                break;
            case R.id.home_tv_ref:
                GlobalVars.setIsLAN(true);
                SendDataUtil.getNetWorkInfo();
                MyApplication.mApplication.showLoadDialog(HomeActivity.this, false);
                break;
            case R.id.city_btn_sure:
                String cityName = city_name.getText().toString();
                try {
                    mCurCity = mCityDB.getCity(cityName);
                    Log.i("LOCATION", mCurCity.getCity() + "-----------" + mCurCity.getNumber());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getSimpleWeatherInfo(true);
                        }
                    }).start();

                    if (city_name.getText().toString().contains("市"))
                        loaction_text.setText(city_name.getText().toString());
                    else
                        loaction_text.setText(city_name.getText().toString() + "市");
                } catch (Exception e) {
                    ToastUtil.showText("请输入正确的城市名！");
                }
                dialog_add_loaction.dismiss();
                break;
            case R.id.city_btn_cancel:
                dialog_add_loaction.dismiss();
                break;
            case R.id.home_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示");
                builder.setMessage("您是否要退出登录？");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        LogoutHelper.logout(HomeActivity.this);
                    }
                });
                builder.create().show();
                break;
            case R.id.net_now:
                startActivity(new Intent(HomeActivity.this, NewWorkSetActivity.class));
                break;
        }
    }

    OnGetViewPageNum onGetViewPageNum;

    public void setOnGetViewPageNum(OnGetViewPageNum onGetViewPageNum) {
        this.onGetViewPageNum = onGetViewPageNum;
    }

    public interface OnGetViewPageNum {
        void getViewPageNum(int position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MyApplication.mApplication.isVisitor()) {
            Data_Cache.writeFile(GlobalVars.getDevid(), new WareData());
            GlobalVars.setDevid("");
            GlobalVars.setDevpass("");
            GlobalVars.setUserid("");
            AppSharePreferenceMgr.put(GlobalVars.RCUINFOID_SHAREPREFERENCE, "");
            AppSharePreferenceMgr.put(GlobalVars.SAFETY_TYPE_SHAREPREFERENCE, 0);
            AppSharePreferenceMgr.put(GlobalVars.RCUINFOLIST_SHAREPREFERENCE, "");
        }
    }


    public static HomeDataUpDataListener homeDataUpDataListener;

    public static void setHomeDataUpDataListener(HomeDataUpDataListener homeDataUpDataListener) {
        HomeActivity.homeDataUpDataListener = homeDataUpDataListener;
    }

    public static interface HomeDataUpDataListener {
        void getupData(int datType, int subtype1, int subtype2);
    }
}
