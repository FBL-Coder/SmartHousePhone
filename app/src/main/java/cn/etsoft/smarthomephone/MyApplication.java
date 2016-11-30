package cn.etsoft.smarthomephone;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import cn.etsoft.smarthomephone.jniUtils;
import cn.etsoft.smarthomephone.pullmi.app.GlobalVars;
import cn.etsoft.smarthomephone.pullmi.common.CommonUtils;
import cn.etsoft.smarthomephone.pullmi.entity.UdpProPkt;
import cn.etsoft.smarthomephone.pullmi.entity.WareData;
import cn.etsoft.smarthomephone.udpService;
import cn.etsoft.smarthomephone.ui.WelcomeActivity;

/**
 * 作者：FBL  时间： 2016/8/30.
 */
public class MyApplication extends Application implements udpService.Callback {
    private MyServiceConn conn;
    public static MyApplication mInstance;
    private static WareData wareData;
    private Handler handler = null;
    private Handler mhandler = null;
    private udpService service;
    private static Context context;
    private WelcomeActivity activity;
    private DatagramSocket socket = null;
    public static int local_server_flag = -1; //0 local 1 server
    final static String local_ip = "127.0.0.1";


    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = MyApplication.this;
        /**
         * 初始化上下文；
         */
        MyApplication.context = getApplicationContext();

        GlobalVars.init(this.getApplicationContext());

        CommonUtils.CommonUtils_init();

        try {
            socket = new DatagramSocket(8080);
        } catch (SocketException e) {
//            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                jniUtils.udpServer();
            }
        }).start();

    }


    public DatagramSocket getSocket() {
        return socket;
    }


    public WelcomeActivity getActivity() {
        return activity;
    }

    public void setActivity(WelcomeActivity activity) {
        this.activity = activity;
    }

    public static Context getContext() {
        return MyApplication.context;
    }

    @Override
    public void getWareData(int what,WareData wareData) {
        MyApplication.wareData = wareData;
        onGetWareDataListener.upDataWareData(what);
    }

    public static void setRcuDevIDtoLocal() {

        final String str = "{" +
                "\"devUnitID\":\"" + GlobalVars.getDevid() + "\"," +
                "\"devPass\":\"" + GlobalVars.getDevpass() + "\"," +
                "\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_boardCast.getValue() + "," +
                "\"subType1\":0," +
                "\"subType2\":0}";
        sendMsg(str);
    }

    public static void getRcuInfo() {

        final String rcu_str = "{" +
                "\"devUnitID\":\"" + GlobalVars.getDevid() + "\"," +
                "\"devPass\":\"" + GlobalVars.getDevpass() + "\"," +
                "\"datType\":" + UdpProPkt.E_UDP_RPO_DAT.e_udpPro_getRcuInfo.getValue() + "," +
                "\"subType1\":0," +
                "\"subType2\":0}";

        sendMsg(rcu_str);
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


    public static void sendMsg(final String msg) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramPacket packet = null;
                try {
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

    public static WareData getWareData() {
        return wareData;
    }

    public Handler getAllHandler() {
        return handler;
    }

    public void setAllHandler(Handler handler) {
        this.handler = handler;

    }

    public void setHandler(Handler handler) {
        this.mhandler = handler;
        conn = new MyServiceConn();
        bindService(new Intent(this, udpService.class), conn, BIND_AUTO_CREATE);
    }

    public final class MyServiceConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            // TODO Auto-generated method stub
            service = ((udpService.LocalBinder) binder).getService();
            service.runUdpServer(mhandler);
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

    public interface OnGetWareDataListener {
        void upDataWareData(int what);
    }
}
