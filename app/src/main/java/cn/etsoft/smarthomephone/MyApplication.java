package cn.etsoft.smarthomephone;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.etsoft.smarthomephone.domain.City;
import cn.etsoft.smarthomephone.domain.Weather_All_Bean;
import cn.etsoft.smarthomephone.domain.Weather_Bean;
import cn.etsoft.smarthomephone.pullmi.app.GlobalVars;
import cn.etsoft.smarthomephone.pullmi.common.CommonUtils;
import cn.etsoft.smarthomephone.pullmi.entity.RcuInfo;
import cn.etsoft.smarthomephone.pullmi.entity.UdpProPkt;
import cn.etsoft.smarthomephone.pullmi.entity.WareData;
import cn.etsoft.smarthomephone.ui.HomeActivity;
import cn.etsoft.smarthomephone.ui.WelcomeActivity;
import cn.etsoft.smarthomephone.utils.CityDB;


/**
 * 作者：FBL  时间： 2016/10/30.
 */
public class MyApplication extends Application implements udpService.Callback, NetBroadcastReceiver.NetEvevt {

    /**
     * 网络监听
     */
    public static NetBroadcastReceiver.NetEvevt evevt;
    /**
     * 服务对象
     */
    private MyServiceConn conn;
    /**
     * Application 对象
     */
    public static MyApplication mInstance;
    /**
     * 全局数据
     */
    private static WareData wareData;
    /**
     * 数据变更handler
     */
    private Handler handler = null;
    /**
     * 启动服务Handler
     */
    private Handler mhandler = null;
    /**
     * udpService 对象
     */
    private udpService service;
    /**
     * 全局Context
     */
    private static Context context;
    /**
     * 欢迎页面   这里只是为了销毁
     */
    private WelcomeActivity activity;

    /**
     * Activity的集合，这里是为了切换联网模块后的跳转以及销毁之前的所有页面
     */
    private static List<Activity> activities;

    public static List<Activity> getActivities() {
        if(activities == null){
            activities = new ArrayList<>();
        }
        return activities;
    }

    public static void setActivities(List<Activity> activities) {
        MyApplication.activities = activities;
    }

    private DatagramSocket socket = null;
    public static int local_server_flag = -1; //0 local 1 server
    /**
     * 数据中，设备房间的集合
     */
    private static List<String> room_list;

    /**
     * 主页的Activity对象
     */
    private static HomeActivity mHomeActivity;
    /**
     * 联网模块大于一个的时候，保存最近使用的联网模块ID；
     */
    private String devUnitID;
    /**
     * 本地IP地址
     */
    public static String LOCAL_IP = "127.0.0.1";

    private Weather_All_Bean results;

    private List<Weather_Bean> weathers_list;
    private CityDB mCityDB;
    private List<City> mCityList;
    // 首字母集
    private List<String> mSections;
    // 根据首字母存放数据
    private Map<String, List<City>> mMap;
    // 首字母位置集
    private List<Integer> mPositions;
    // 首字母对应的位置
    private Map<String, Integer> mIndexer;
    private static final String FORMAT = "^[a-z,A-Z].*$";
    private static SharedPreferences sharedPreferences;

    private RcuInfo rcuInfo;

    public void setRcuInfo(RcuInfo rcuInfo) {
        this.rcuInfo = rcuInfo;
    }

    public RcuInfo getRcuInfo() {
        return rcuInfo;
    }

    private SoundPool sp;//声明一个SoundPool
    private int music;//定义一个整型用load（）；来设置suondID

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 腾讯 bugly
         */
        CrashReport.initCrashReport(getApplicationContext(), "f623f31b48", false);
        /**
         * 初始化网络状态监听
         */
        evevt = MyApplication.this;
        /**
         * 初始化Application
         */
        mInstance = MyApplication.this;
        /**
         * 初始化上下文；
         */
        MyApplication.context = getApplicationContext();

        /**
         * GlobalVars 这也是全局变量，这里只是给全局Context
         */
        GlobalVars.init(this.getApplicationContext());

        /**
         * 初始化工具类
         */
        CommonUtils.CommonUtils_init();

        //初始化城市
        initCityList();
//        mLocationClient = new LocationClient(getApplicationContext());
        /**始化Socket端口
         * 初
         */
        try {
            socket = new DatagramSocket(8080);
        } catch (SocketException e) {
//            e.printStackTrace();
        }

        /**
         * 启动底层C库 服务
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                jniUtils.udpServer();
            }
        }).start();

        /**
         * 天气对应的图标id
         */
        weathers_list = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 43; i++) {
                    Weather_Bean bean = new Weather_Bean();
                    bean.setCode(i);
                    weathers_list.add(bean);
                }
            }
        }).start();

        sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
        music = sp.load(this, R.raw.key_sound, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
    }

    public SoundPool getSp() {
        if (sp == null) {
            sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        }
        return sp;
    }

    public int getMusic() {
        if (music == 0) {
            music = sp.load(getActivity(), R.raw.key_sound, 1);
        }
        return music;
    }


    private void initCityList() {
        mCityList = new ArrayList<City>();
        mSections = new ArrayList<String>();
        mMap = new HashMap<String, List<City>>();
        mPositions = new ArrayList<Integer>();
        mIndexer = new HashMap<String, Integer>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                mCityDB = openCityDB();// 这个必须最先复制完,所以我放在单线程中处理
                prepareCityList();
            }
        }).start();
    }

    private CityDB openCityDB() {
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + MyApplication.getContext().getPackageName() + File.separator
                + CityDB.CITY_DB_NAME;
        File db = new File(path);
        if (!db.exists()) {
            // L.i("db is not exists");
            try {
                InputStream is = getAssets().open("city.db");
                FileOutputStream fos = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        return new CityDB(this, path);
    }

    private boolean prepareCityList() {
        mCityList = mCityDB.getAllCity();// 获取数据库中所有城市
        for (City city : mCityList) {
            String firstName = city.getFirstPY();// 第一个字拼音的第一个字母
            if (firstName.matches(FORMAT)) {
                if (mSections.contains(firstName)) {
                    mMap.get(firstName).add(city);
                } else {
                    mSections.add(firstName);
                    List<City> list = new ArrayList<City>();
                    list.add(city);
                    mMap.put(firstName, list);
                }
            } else {
                if (mSections.contains("#")) {
                    mMap.get("#").add(city);
                } else {
                    mSections.add("#");
                    List<City> list = new ArrayList<City>();
                    list.add(city);
                    mMap.put("#", list);
                }
            }
        }
        Collections.sort(mSections);// 按照字母重新排序
        int position = 0;
        for (int i = 0; i < mSections.size(); i++) {
            mIndexer.put(mSections.get(i), position);// 存入map中，key为首字母字符串，value为首字母在listview中位置
            mPositions.add(position);// 首字母在listview中位置，存入list中
            position += mMap.get(mSections.get(i)).size();// 计算下一个首字母在listview的位置
        }
        return true;
    }

    public synchronized CityDB getCityDB() {
        if (mCityDB == null)
            mCityDB = openCityDB();
        return mCityDB;
    }

    public String getDevUnitID() {
        return devUnitID;
    }

    public void setDevUnitID(String devUnitID) {
        this.devUnitID = devUnitID;
    }

    /**
     * 获取Socket对象
     *
     * @return
     */
    public DatagramSocket getSocket() {
        return socket;
    }

    /**
     * 获取欢迎页面的对象
     *
     * @return
     */
    public WelcomeActivity getActivity() {
        return activity;
    }

    /**
     * 赋值欢迎页面
     *
     * @param activity
     */
    public void setActivity(WelcomeActivity activity) {
        this.activity = activity;
    }

    /**
     * 获取全局Context
     *
     * @return
     */
    public static Context getContext() {
        return MyApplication.context;
    }

    /**
     * 数据服务的回调，接收到数据后触发此方法；
     *
     * @param what     根据what判断数据  What== datType
     * @param wareData 数据本身
     */



    public void getWareData(int what, WareData wareData) {
        MyApplication.wareData = wareData;
        onGetWareDataListener.upDataWareData(what);
    }

    /**
     * 获取主页对象
     * @return
     */
    public static HomeActivity getmHomeActivity() {
        return mHomeActivity;
    }

    /**
     * 赋值主页对象
     * @param mHomeActivity
     */
    public static void setmHomeActivity(HomeActivity mHomeActivity) {
        MyApplication.mHomeActivity = mHomeActivity;
    }

    public Weather_All_Bean getResults() {
        return results;
    }

    public void setResults(Weather_All_Bean results) {
        this.results = results;
    }

    public List<Weather_Bean> getWeathers_list() {
        return weathers_list;
    }


    /**
     * 用户注册发送数据
     *
     * @param id  user id
     * @param pos user password
     */
    public static void addUserData(String id, String pos) {
        final String str = "{" +
                "\"userName\":\"" + id + "\"," +
                "\"passwd\":\"" + pos + "\"," +
                "\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_add_user.getValue() + "," +
                "\"subType1\":0," +
                "\"subType2\":0}";
        sendMsg(str);
    }

    /**
     * 用户登录发送数据
     *
     * @param id  user id
     * @param pos user password
     */
    public static void sendUserData(String id, String pos) {
        final String str = "{" +
                "\"userName\":\"" + id + "\"," +
                "\"passwd\":\"" + pos + "\"," +
                "\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_login.getValue() + "," +
                "\"subType1\":0," +
                "\"subType2\":0}";
        sendMsg(str);
    }

    /**
     * 启动应用并且启动服务后  发送的数据包；
     */
    public static void setRcuDevIDtoLocal() {
        sharedPreferences = context.getSharedPreferences("profile", Context.MODE_PRIVATE);
        String appid = sharedPreferences.getString("appid", "");
        final String str = "{" +
                "\"devUnitID\":\"" + GlobalVars.getDevid() + "\"," +
                "\"devPass\":\"" + GlobalVars.getDevpass() + "\"," +
                "\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_boardCast.getValue() + "," +
                "\"uuid\":\"" + appid + "\"," +
                "\"subType1\":0," +
                "\"subType2\":0}";
        if ("".equals(GlobalVars.getDevid()) || "".equals(GlobalVars.getDevpass()))
            return;
        sendMsg(str);
    }

    public static void getSceneInfo() {
        final String scene_str = "{" +
                "\"devUnitID\":\"" + GlobalVars.getDevid() + "\"," +
                "\"devPass\":\"" + GlobalVars.getDevpass() + "\"," +
                "\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_getSceneEvents.getValue() + "," +
                "\"subType1\":0," +
                "\"subType2\":0}";
        sendMsg(scene_str);
    }

    /**
     * 获取输入板对应设备的数据包
     *
     * @param key_index
     * @param uid
     */
    public static void getKeyItemInfo(int key_index, String uid) {

        final String key_str = "{" +
                "\"devUnitID\":\"" + GlobalVars.getDevid() + "\"," +
                "\"devPass\":\"" + GlobalVars.getDevpass() + "\"," +
                "\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_getKeyOpItems.getValue() + "," +
                "\"uid\":" + "\"" + uid + "\"," +
                "\"subType1\":0," +
                "\"subType2\":0," +
                "\"key_index\":" + key_index + "}";

        sendMsg(key_str);
    }

    /**
     * 获取设备对应的输出板数据包
     *
     * @param uid
     * @param devType
     * @param devID
     */
    public static void getChnItemInfo(String uid, int devType, int devID) {
        final String chn_str = "{" +
                "\"devUnitID\":\"" + GlobalVars.getDevid() + "\"," +
                "\"devPass\":\"" + GlobalVars.getDevpass() + "\"," +
                "\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_getChnOpItems.getValue() + "," +
                "\"uid\":" + "\"" + uid + "\"," +
                "\"subType1\":0," +
                "\"subType2\":0," +
                "\"devID\":" + devID + "," +
                "\"devType\":" + devType + "}";

        sendMsg(chn_str);
    }


    /**
     * 发送消息的方法；
     *
     * @param msg
     */

    static int count = 0;

    public static void sendMsg(final String msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramPacket packet = null;
                try {
                    Log.i("IPAdd", "命令的IP地址 : " + GlobalVars.getDstip() + "----发送的消息 : " + msg);
                    packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length,
                            InetAddress.getByName(GlobalVars.getDstip()), 8400);
                    if (packet != null)
                        MyApplication.mInstance.getSocket().send(packet);
                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    public static void setWareData(WareData wareData) {
        MyApplication.wareData = wareData;
    }

    public static List<String> getRoom_list() {
        if (room_list == null) {
            return new ArrayList<>();
        }
        return room_list;
    }

    public static void setRoom_list(List<String> room_list) {
        MyApplication.room_list = room_list;
    }


    public static WareData getWareData() {
        if (wareData == null) {
            return new WareData();
        }
        return wareData;
    }

    public Handler getAllHandler() {
        return handler;
    }

    public void setAllHandler(Handler handler) {
        this.handler = handler;
    }
    /**
     * 启动服务；
     *
     * @param
     */
    public void startSer() {
        conn = new MyServiceConn();
        bindService(new Intent(this, udpService.class), conn, BIND_AUTO_CREATE);
    }

    @Override
    public void onNetChange(int netMobile) {
        if (isAppRunning(this))
            Log.i("NETChange", "网络状态已改变！");
    }

    /**
     * 判断当前程序是否还在运行
     *
     * @param context
     * @return
     */
    private boolean isAppRunning(Context context) {
        String packageName = context.getPackageName();
        String topActivityClassName = getTopActivityName(context);

        if (packageName != null && topActivityClassName != null && topActivityClassName.startsWith(packageName)) {
            return true;
        } else {
            return false;
        }
    }

    public String getTopActivityName(Context context) {
        String topActivityClassName = null;
        ActivityManager activityManager =
                (ActivityManager) (context.getSystemService(android.content.Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName f = runningTaskInfos.get(0).topActivity;
            topActivityClassName = f.getClassName();
        }
        return topActivityClassName;
    }

    /**
     * 网络状态该变监听
     */
    public final class MyServiceConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            // TODO Auto-generated method stub
            service = ((udpService.LocalBinder) binder).getService();
            service.runUdpServer();
            // 将当前activity添加到接口集合中
            service.addCallback(MyApplication.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            service = null;
        }
    }

    private static OnGetWareDataListener onGetWareDataListener;

    public void setOnGetWareDataListener(OnGetWareDataListener Listener) {
        onGetWareDataListener = Listener;
    }

    /**
     * 实现通知数据更新的接口；
     */
    public interface OnGetWareDataListener {
        void upDataWareData(int what);
    }
}
