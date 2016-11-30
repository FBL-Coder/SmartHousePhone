package cn.etsoft.smarthomephone;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

//import cn.etsoft.smarthomephone.domain.SetEquipmentResult;
import cn.etsoft.smarthomephone.domain.DevControl_Result;
import cn.etsoft.smarthomephone.pullmi.app.GlobalVars;
import cn.etsoft.smarthomephone.pullmi.common.CommonUtils;
import cn.etsoft.smarthomephone.pullmi.entity.RcuInfo;
import cn.etsoft.smarthomephone.pullmi.entity.WareAirCondDev;
import cn.etsoft.smarthomephone.pullmi.entity.WareBoardChnout;
import cn.etsoft.smarthomephone.pullmi.entity.WareBoardKeyInput;
import cn.etsoft.smarthomephone.pullmi.entity.WareChnOpItem;
import cn.etsoft.smarthomephone.pullmi.entity.WareCurtain;
import cn.etsoft.smarthomephone.pullmi.entity.WareData;
import cn.etsoft.smarthomephone.pullmi.entity.WareDev;
import cn.etsoft.smarthomephone.pullmi.entity.WareKeyOpItem;
import cn.etsoft.smarthomephone.pullmi.entity.WareLight;
import cn.etsoft.smarthomephone.pullmi.entity.WareSceneDevItem;
import cn.etsoft.smarthomephone.pullmi.entity.WareSceneEvent;
import cn.etsoft.smarthomephone.pullmi.entity.WareSetBox;
import cn.etsoft.smarthomephone.pullmi.entity.WareTv;
import cn.etsoft.smarthomephone.pullmi.utils.Dtat_Cache;

/**
 * The type Udp service.
 */
public class udpService extends Service {

    private List<Callback> list;

    private WareData wareData;
    private String TAG = this.getClass().getName();

    private boolean isFreshData = false;
    private static final int OUTTIME_INITUID = 1000;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        list = new ArrayList<Callback>();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return new LocalBinder();
    }

    public final class LocalBinder extends Binder {

        public udpService getService() {
            return udpService.this;
        }
    }

    public void addCallback(Callback callback) {
        list.add(callback);
    }

    public interface Callback {
        //获取数据接口
        void getWareData(int what, WareData wareData);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            // 遍历集合，通知所有的实现类，即activity
            for (int i = 0; i < list.size(); i++) {
                list.get(i).getWareData(msg.what,wareData);
            }
        }
    };

    public void runUdpServer(final Handler handler) {

        GlobalVars.setDstip(MyApplication.local_ip);
        Handler handler1 = MyApplication.mInstance.getAllHandler();
        Message message = handler1.obtainMessage(OUTTIME_INITUID);
        handler1.sendMessage(message);

        new Thread(new Runnable() {//执行接收数据接口，有数据，则执行；
            @Override
            public void run() {
                wareData = new WareData();
                MyApplication.setWareData(wareData);

                byte[] lMsg = new byte[1024 * 5];
                DatagramPacket packet = new DatagramPacket(lMsg, lMsg.length);
                try {
                    while (true) {
                        MyApplication.mInstance.getSocket().receive(packet);
                        // 处理消息
                        String info = new String(packet.getData(), 0, packet.getLength());
                        Log.i("接收信息：", info);
                        extractData(info, handler);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (Error a) {
                    a.printStackTrace();
                } finally {
                    if (MyApplication.mInstance.getSocket() != null) {
                        MyApplication.mInstance.getSocket().close();
                    }
                }
            }
        }).start();
    }


    public void extractData(String info, Handler mhandler) {
        // TODO Auto-generated method stub
        int datType = 0;
        int subType2 = 0;
        int subType1 = 0;
        try {
            JSONObject jsonObject = new JSONObject(info);
            datType = jsonObject.getInt("datType");
            subType1 = jsonObject.getInt("subType1");
            subType2 = jsonObject.getInt("subType2");
        } catch (JSONException e) {
            System.out.println(e.toString());
        }

        switch (datType) {
            case 0:// e_udpPro_getRcuinfo
                if (subType2 == 1) {
                    setRcuInfo(info);
                }
                break;
            case 3: // getDevsInfo
                if (subType1 == 1) {
                    getDevsInfo(info);
                    isFreshData = true;
                    //删除重复的设备
                    List<WareDev> devs = removeDuplicateDevs(MyApplication.getWareData().getDevs());
                    MyApplication.getWareData().setDevs(devs);

                    Message msg = mhandler.obtainMessage();
                    if (mhandler != null) {
                        mhandler.sendMessage(msg);
                    }
                    Log.i(TAG, "灯数:" + MyApplication.getWareData().getLights().size() + "");
                    Log.i(TAG, "空调:" + MyApplication.getWareData().getAirConds().size() + "");
                }
                break;
            case 4: // ctrlDev
                if (subType1 == 1) {
                    refreshDevData(info);
                    isFreshData = true;
                }
                break;
            case 5: // ctrlDev
                if (subType1 == 1) {
                    deldev_result(info);
                    isFreshData = true;
                }
                break;
            case 6: // ctrlDev
                if (subType1 == 1) {
                    deldev_result(info);
                    isFreshData = true;
                }
                break;
            case 7: // delDev
                if (subType1 == 1) {
                    deldev_result(info);
                    isFreshData = true;
                }
                break;
            case 8:
                if (subType2 == 0) {
                    getkeyOutBoard(info);
                    isFreshData = true;
                }
                if (subType2 == 1) {
                    getKyeInputBoard(info);
                    isFreshData = true;
                }
                break;
            case 11: // e_udpPro_getKeyOpItems
                if (subType1 == 1) {
                    getKeyOpItem(info);
                    isFreshData = true;
                }
                break;
            case 12: // e_udpPro_setKeyOpItems
                if (subType1 == 1) {
                    setKeyOpItem_result(info);
                    isFreshData = true;
                }
                break;
            case 13: // e_udpPro_delKeyOpItems
                if (subType1 == 1) {
                    delKeyOpItem_result(info);
                    isFreshData = true;
                }
                break;
            case 14: // e_udpPro_getChnOpitems
                if (subType1 == 1) {
                    isFreshData = true;
                    getChnOpItem(info);

                }
                break;
            case 15: // e_udpPro_setChnOpitems
                if (subType1 == 1) {
                    setKeyOpItem_result(info);
                    isFreshData = true;
                /*if (getChnOpItemReply(udpProData))
                    myApp.getHandler().sendEmptyMessage(MSG_SETCHNOPITEM_INFO);*/
                }
                break;
            case 16: // e_udpPro_setChnOpitems
                if (subType1 == 1) {
                    delChnOpItem_result(info);
                    isFreshData = true;
                /*if (getChnOpItemReply(udpProData))
                    myApp.getHandler().sendEmptyMessage(MSG_SETCHNOPITEM_INFO);*/
                }
                break;
            case 22: // e_udpPro_getSceneEvents
                if (subType2 == 1) {
                    getSceneEvents(info);
                    isFreshData = true;
                }
                break;
            case 23: // e_udpPro_addSceneEvents
                if (subType2 == 1) {
                    getSceneEvents(info);
                    //添加提示信息
                    isFreshData = true;
                }
                break;
            case 24: // e_udpPro_editSceneEvents
                if (subType2 == 1) {
                    getSceneEvents(info);
                    //添加提示信息
                    isFreshData = true;
                }
                break;
            case 25: // e_udpPro_delSceneEvents
                if (subType2 == 1) {
                    getSceneEvents(info);
                    isFreshData = true;
                }
                break;
            case 26: // e_udpPro_exeSceneEvents
                if (subType2 == 1) {
                    // setSceneEvents(udpProData);
                    isFreshData = true;
                }
                break;
            case 35:// e_udpPro_chns_status
                ctrlDevReply(info);
                isFreshData = true;
                break;
        }

        if (isFreshData && datType != 2) {
            handler.sendMessage(handler.obtainMessage(datType, wareData));
            Message message = new Message();
            message.what = datType;
            MyApplication.mInstance.getAllHandler().sendMessage(message);
            Dtat_Cache.writeFile(MyApplication.getWareData());
            isFreshData = false;
        }
    }

    public void setRcuInfo(String info) {

//        获取联网模块的返回数据类型；
//        rcu_return
//        {
//            "devUnitID": "37ffdb05424e323416702443",
//                "datType": 0,
//                "subType1": 0,
//                "subType2": 1,
//                "rcu_rows": [
//            {
//                "canCpuID": "37ffdb05424e323416702443",
//                    "devUnitPass": "160724436666",
//                    "name": "6666",
//                    "IpAddr": "192.168.0.102",
//                    "SubMask": "255.255.255.0",
//                    "Gateway": "192.168.0.1",
//                    "centerServ": "192.168.1.114",
//                    "roomNum": "0000",
//                    "macAddr": "00502a040248",
//                    "SoftVersion": "",
//                    "HwVersion": "",
//                    "bDhcp": 0
//            }
//            ]
//        }
        List<RcuInfo> list = new ArrayList<>();
        RcuInfo info1 = new RcuInfo();
        try {
            JSONObject jsonObject = new JSONObject(info);
            info1.setDevUnitID(jsonObject.getString("devUnitID"));

            JSONArray jsonArray = jsonObject.getJSONArray("rcu_rows");
            JSONObject jsonObject1 = jsonArray.getJSONObject(0);

            info1.setIpAddr(jsonObject1.getString("IpAddr"));
            info1.setGateWay(jsonObject1.getString("Gateway"));
            info1.setDevUnitPass(jsonObject1.getString("devUnitPass"));
            info1.setCenterServ(jsonObject1.getString("centerServ"));
            info1.setMacAddr(jsonObject1.getString("macAddr"));
            info1.setName(jsonObject1.getString("name"));
            info1.setRoomNum(jsonObject1.getString("roomNum"));
            info1.setSoftVersion(jsonObject1.getString("SoftVersion"));
            info1.setSubMask(jsonObject1.getString("SubMask"));
            info1.setHwVversion(jsonObject1.getString("HwVersion"));

        } catch (JSONException e) {
            System.out.println(e.toString());
        }
        list.add(info1);
        MyApplication.getWareData().setRcuInfos(list);
    }

    public void getDevsInfo(String info) {

//        所有设备 返回数据类型；
//        dev_return
//        {
//            "devUnitID":	"37ffdb05424e323416702443",
//                "datType":	3,
//                "subType1":	0,
//                "subType2":	0,
//                "aircond_rows":	[{
//            "canCpuID":	"31ffdf054257313827502543",
//                    "devName":	"6b7400000000000000000000",
//                    "roomName":	"ceb4b6a8d2e5000000000000",
//                    "devType":	0,
//                    "devID":	0,
//                    "bOnOff":	1,
//                    "selMode":	0,
//                    "selTemp":	24,
//                    "selSpd":	5,
//                    "selDirect":	28,
//                    "rev1":	24,
//                    "powChn":	31
//        }],
//            "aircond":	1,
//                "light_rows":	[{
//            "canCpuID":	"31ffdf054257313827502543",
//                    "devName":	"b5c635350000000000000000",
//                    "roomName":	"ceb4b6a8d2e5000000000000",
//                    "devType":	3,
//                    "devID":	5,
//                    "bOnOff":	1,
//                    "bTuneEn":	0,
//                    "lmVal":	0,
//                    "powChn":	5
//        }, {
//            "canCpuID":	"31ffdf054257313827502543",
//                    "devName":	"b5c636360000000000000000",
//                    "roomName":	"ceb4b6a8d2e5000000000000",
//                    "devType":	3,
//                    "devID":	6,
//                    "bOnOff":	1,
//                    "bTuneEn":	0,
//                    "lmVal":	0,
//                    "powChn":	6
//        }, {
//            "canCpuID":	"31ffdf054257313827502543",
//                    "devName":	"b5c637000000000000000000",
//                    "roomName":	"ceb4b6a8d2e5000000000000",
//                    "devType":	3,
//                    "devID":	7,
//                    "bOnOff":	1,
//                    "bTuneEn":	0,
//                    "lmVal":	0,
//                    "powChn":	7
//        }, {
//            "canCpuID":	"31ffdf054257313827502543",
//                    "devName":	"b5c638000000000000000000",
//                    "roomName":	"ceb4b6a8d2e5000000000000",
//                    "devType":	3,
//                    "devID":	8,
//                    "bOnOff":	1,
//                    "bTuneEn":	0,
//                    "lmVal":	0,
//                    "powChn":	8
//        }, {
//            "canCpuID":	"31ffdf054257313827502543",
//                    "devName":	"b5c639000000000000000000",
//                    "roomName":	"ceb4b6a8d2e5000000000000",
//                    "devType":	3,
//                    "devID":	9,
//                    "bOnOff":	1,
//                    "bTuneEn":	0,
//                    "lmVal":	0,
//                    "powChn":	9
//        }, {
//            "canCpuID":	"31ffdf054257313827502543",
//                    "devName":	"b5c631300000000000000000",
//                    "roomName":	"ceb4b6a8d2e5000000000000",
//                    "devType":	3,
//                    "devID":	10,
//                    "bOnOff":	1,
//                    "bTuneEn":	0,
//                    "lmVal":	0,
//                    "powChn":	10
//        }, {
//            "canCpuID":	"31ffdf054257313827502543",
//                    "devName":	"b5c631310000000000000000",
//                    "roomName":	"ceb4b6a8d2e5000000000000",
//                    "devType":	3,
//                    "devID":	11,
//                    "bOnOff":	1,
//                    "bTuneEn":	0,
//                    "lmVal":	0,
//                    "powChn":	11
//        }],
//            "light":	7,
//                "curtain_rows":	[],
//            "curtain":	0
//        }

        int devnum_air = 0;
        int devnum_cur = 0;
        int devnum_box = 0;
        int devnum_tv = 0;
        int devnum_light = 0;
        try {
            JSONObject jsonObject = new JSONObject(info);
            devnum_air = jsonObject.getInt("aircond");
            devnum_cur = jsonObject.getInt("curtain");
//            devnum_box = jsonObject.getInt("setbox");
//            devnum_tv = jsonObject.getInt("tv");
            devnum_light = jsonObject.getInt("light");

            if (devnum_air > 0) {
                List<WareAirCondDev> list = new ArrayList<>();

                WareAirCondDev airCondDev = new WareAirCondDev();
                JSONArray jsonArray = jsonObject.getJSONArray("aircond_rows");
                for (int i = 0; i < devnum_air; i++) {

                    JSONObject jsonobj = jsonArray.getJSONObject(i);
                    WareDev dev = new WareDev();
                    dev.setCanCpuId(jsonobj.getString("canCpuID"));
                    dev.setDevName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(jsonobj.getString("devName"))));
                    dev.setRoomName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(jsonobj.getString("roomName"))));
                    dev.setDevId((byte) jsonobj.getInt("devID"));
                    dev.setType((byte) jsonobj.getInt("devType"));
                    airCondDev.setDev(dev);
                    //add dev to devlist
                    MyApplication.getWareData().getDevs().add(dev);

                    airCondDev.setbOnOff((byte) jsonobj.getInt("bOnOff"));
                    airCondDev.setPowChn(jsonobj.getInt("powChn"));
                    airCondDev.setRev1((byte) jsonobj.getInt("rev1"));
                    airCondDev.setSelDirect((byte) jsonobj.getInt("selDirect"));
                    airCondDev.setSelMode((byte) jsonobj.getInt("selMode"));
                    airCondDev.setSelSpd((byte) jsonobj.getInt("selSpd"));
                    airCondDev.setSelTemp((byte) jsonobj.getInt("selTemp"));

                    list.add(airCondDev);
                }
                MyApplication.getWareData().setAirConds(list);
            }

            if (devnum_light > 0) {

                List<WareLight> lights = new ArrayList<>();
                JSONArray jsonArray = jsonObject.getJSONArray("light_rows");
                for (int i = 0; i < devnum_light; i++) {

                    WareLight light = new WareLight();

                    JSONObject jsonobj = jsonArray.getJSONObject(i);
                    WareDev dev = new WareDev();
                    dev.setCanCpuId(jsonobj.getString("canCpuID"));
                    dev.setDevName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(jsonobj.getString("devName"))));
                    dev.setRoomName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(jsonobj.getString("roomName"))));
                    dev.setDevId((byte) jsonobj.getInt("devID"));
                    dev.setType((byte) jsonobj.getInt("devType"));
                    light.setDev(dev);

                    MyApplication.getWareData().getDevs().add(dev);

                    light.setbOnOff((byte) jsonobj.getInt("bOnOff"));
                    light.setPowChn((byte) jsonobj.getInt("powChn"));
                    light.setLmVal((byte) jsonobj.getInt("lmVal"));
                    light.setbTuneEn((byte) jsonobj.getInt("bTuneEn"));

                    lights.add(light);
                }
                MyApplication.getWareData().setLights(lights);
            }
            if (devnum_tv > 0) {

                List<WareTv> list = new ArrayList<>();

                JSONArray jsonArray = jsonObject.getJSONArray("代填");
                for (int i = 0; i < devnum_tv; i++) {
                    WareTv tv = new WareTv();

                    JSONObject jsonobj = jsonArray.getJSONObject(i);
                    WareDev dev = new WareDev();
                    dev.setCanCpuId(jsonobj.getString("canCpuID"));
                    dev.setDevName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(jsonobj.getString("devName"))));
                    dev.setRoomName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(jsonobj.getString("roomName"))));
                    dev.setDevId((byte) jsonobj.getInt("devID"));
                    dev.setType((byte) jsonobj.getInt("devType"));
                    tv.setDev(dev);

                    MyApplication.getWareData().getDevs().add(dev);

                    list.add(tv);
                }

                MyApplication.getWareData().setTvs(list);
            }
            if (devnum_box > 0) {

                List<WareSetBox> list = new ArrayList<>();
                JSONArray jsonArray = jsonObject.getJSONArray("代填");
                for (int i = 0; i < devnum_box; i++) {
                    WareSetBox box = new WareSetBox();

                    JSONObject jsonobj = jsonArray.getJSONObject(i);
                    WareDev dev = new WareDev();
                    dev.setCanCpuId(jsonobj.getString("canCpuID"));
                    dev.setDevName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(jsonobj.getString("devName"))));
                    dev.setRoomName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(jsonobj.getString("roomName"))));
                    dev.setDevId((byte) jsonobj.getInt("devID"));
                    dev.setType((byte) jsonobj.getInt("devType"));
                    box.setDev(dev);

                    MyApplication.getWareData().getDevs().add(dev);

                    list.add(box);
                }

                MyApplication.getWareData().setStbs(list);
            }
            if (devnum_cur > 0) {

                List<WareCurtain> list = new ArrayList<>();
                JSONArray jsonArray = jsonObject.getJSONArray("curtain_rows");
                for (int i = 0; i < devnum_cur; i++) {

                    WareCurtain curtain = new WareCurtain();

                    JSONObject jsonobj = jsonArray.getJSONObject(i);
                    WareDev dev = new WareDev();
                    dev.setCanCpuId(jsonobj.getString("canCpuID"));
                    dev.setDevName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(jsonobj.getString("devName"))));
                    dev.setRoomName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(jsonobj.getString("roomName"))));
                    dev.setDevId((byte) jsonobj.getInt("devID"));
                    dev.setType((byte) jsonobj.getInt("devType"));

                    MyApplication.getWareData().getDevs().add(dev);

                    curtain.setDev(dev);
                    curtain.setbOnOff((byte) jsonobj.getInt("bOnOff"));
                    curtain.setPowChn(jsonobj.getInt("powChn"));


                    list.add(curtain);
                }
                MyApplication.getWareData().setCurtains(list);
            }
        } catch (JSONException e) {
            System.out.println(e.toString());
        }
    }

    public void refreshDevData(String info) {

//        控制模块都得数据类型；
//        {
//            "devUnitID":	"37ffdb05424e323416702443",
//                "datType":	4,
//                "subType1":	1,
//                "subType2":	1,
//                "dev_rows":	[{
//            "canCpuID":	"31ffdf054257313827502543",
//                    "devName":	"6b7400000000000000000000",
//                    "roomName":	"ceb4b6a8d2e5000000000000",
//                    "devType":	0,
//                    "devID":	0,
//                    "bOnOff":	0,
//                    "selMode":	0,
//                    "selTemp":	24,
//                    "selSpd":	5,
//                    "selDirect":	0,
//                    "rev1":	24,
//                    "powChn":	31
//        }]
//        }

        int devType = -1;

        try {
            JSONObject jsonObject = new JSONObject(info);

            JSONArray array = jsonObject.getJSONArray("dev_rows");

            devType = array.getJSONObject(0).getInt("devType");

            if (devType == 4) {
                WareCurtain curtain = new WareCurtain();
                JSONObject jsonobj = array.getJSONObject(0);
                WareDev dev = new WareDev();
                dev.setCanCpuId(jsonobj.getString("canCpuID"));
                dev.setDevName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(jsonobj.getString("devName"))));
                dev.setRoomName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(jsonobj.getString("roomName"))));
                dev.setDevId((byte) jsonobj.getInt("devID"));
                dev.setType((byte) jsonobj.getInt("devType"));
                curtain.setDev(dev);
                curtain.setbOnOff((byte) jsonobj.getInt("bOnOff"));
                curtain.setPowChn(jsonobj.getInt("powChn"));
                MyApplication.getWareData().getCurtains().add(curtain);

            } else if (devType == 0) {

                WareAirCondDev airCondDev = new WareAirCondDev();

                JSONObject jsonobj = array.getJSONObject(0);
                WareDev dev = new WareDev();
                dev.setCanCpuId(jsonobj.getString("canCpuID"));
                dev.setDevName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(jsonobj.getString("devName"))));
                dev.setRoomName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(jsonobj.getString("roomName"))));
                dev.setDevId((byte) jsonobj.getInt("devID"));
                dev.setType((byte) jsonobj.getInt("devType"));
                airCondDev.setDev(dev);
                airCondDev.setbOnOff((byte) jsonobj.getInt("bOnOff"));
                airCondDev.setPowChn(jsonobj.getInt("powChn"));
                airCondDev.setRev1((byte) jsonobj.getInt("rev1"));
                airCondDev.setSelDirect((byte) jsonobj.getInt("selDirect"));
                airCondDev.setSelMode((byte) jsonobj.getInt("selMode"));
                airCondDev.setSelSpd((byte) jsonobj.getInt("selSpd"));
                airCondDev.setSelTemp((byte) jsonobj.getInt("selTemp"));

                for (int i = 0; i < MyApplication.getWareData().getAirConds().size(); i++) {
                    WareAirCondDev air = MyApplication.getWareData().getAirConds().get(i);
                    if (air.getDev().getCanCpuId().equals(airCondDev.getDev().getCanCpuId())
                            && air.getDev().getDevId() == airCondDev.getDev().getDevId()) {
                        MyApplication.getWareData().getAirConds().remove(i);
                    }
                }
                MyApplication.getWareData().getAirConds().add(airCondDev);
            }
        } catch (JSONException e) {
            System.out.println(e.toString());
        }
    }

    public void deldev_result(String info) {
//        {
//            "devUnitID":	"37ffdb05424e323416702443",
//                "datType":	7,
//                "subType1":	1,
//                "subType2":	1,
//                "dev_rows":	[{
//            "canCpuID":	"31ffdf054257313827502543",
//                    "devName":	"b5c636360000000000000000",
//                    "roomName":	"ceb4b6a8d2e5000000000000",
//                    "devType":	3,
//                    "devID":	6,
//                    "bOnOff":	0,
//                    "bTuneEn":	0,
//                    "lmVal":	0,
//                    "powChn":	6
//        }]
//        }
        Gson gson = new Gson();
        DevControl_Result result = gson.fromJson(info, DevControl_Result.class);
        MyApplication.getWareData().setDev_result(result);
    }


    public void getkeyOutBoard(String info) {

//        返回数据类型；
//        {
//            "devUnitID": "37ffdb05424e323416702443",
//                "datType": 8,
//                "subType1": 1,
//                "subType2": 0,
//                "chnout_rows": [
//            {
//                "canCpuID": "31ffdf054257313827502543",
//                    "boardName": "3132cda8b5c0303031000",
//                    "boardType": 0,
//                    "chnCnt": 12,
//                    "bOnline": 0,
//                    "chnName_rows": [
//                "cda8b5c0300000000",
//                        "cda8b5c0310000000",
//                        "cda8b5c0320000000",
//                        "cda8b5c0330000000",
//                        "cda8b5c0340000000",
//                        "cda8b5c0350000000",
//                        "cda8b5c0360000000",
//                        "cda8b5c0370000000",
//                        "cda8b5c0380000000",
//                        "cda8b5c0390000000",
//                        "cda8b5c03130000000",
//                        "cda8b5c03131000000"
//                ]
//            }
//            ],
//            "board": 1
//        }
        Log.i(TAG, "keyOutBoard: " + info);
        try {
            JSONObject jsonObject = new JSONObject(info);

            WareBoardChnout chnout = new WareBoardChnout();

            JSONArray array = jsonObject.getJSONArray("chnout_rows");

            for (int i = 0; i < array.length(); i++) {

                JSONObject object = array.getJSONObject(i);

                chnout.setDevUnitID(object.getString("canCpuID"));
                chnout.setBoardName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(object.getString("boardName"))));
                chnout.setBoardType((byte) object.getInt("boardType"));
                chnout.setbOnline((byte) object.getInt("bOnline"));
                chnout.setChnCnt((byte) object.getInt("chnCnt"));

                JSONArray array1 = object.getJSONArray("chnName_rows");
                String[] name = new String[array1.length()];
                for (int j = 0; j < array1.length(); j++) {
                    name[j] = array1.getString(j);
                }
                chnout.setChnName(name);

                MyApplication.getWareData().getBoardChnouts().add(chnout);
            }


        } catch (JSONException e) {
            System.out.println(e.toString());
        }
    }

    public void getKyeInputBoard(String info) {

//        返回数据类型；
//        {
//        "devUnitID": "37ffdb05424e323416702443",
//                "datType": 8,
//                "subType1": 1,
//                "subType2": 1,
//                "keyinput_rows": [
//        {
//            "canCpuID": "50ff6c067184515640421267",
//                "boardName": "b0b4bcfcb0e53132363700",
//                "boardType": 1,
//                "keyCnt": 6,
//                "ledBkType": 0,
//                "keyName_rows": [
//                    "b0b4bcfc310000000",
//                    "b0b4bcfc320000000",
//                    "b0b4bcfc330000000",
//                    "b0b4bcfc340000000",
//                    "b0b4bcfc350000000",
//                    "b0b4bcfc360000000",
//            ],
//            "keyAllCtrlType_rows": []
//        }
//        ],
//        "keyinput": 1
//    }
        Log.i(TAG, "keyinput: " + info);
        try {
            JSONObject jsonObject = new JSONObject(info);

            WareBoardKeyInput input = new WareBoardKeyInput();

            JSONArray array = jsonObject.getJSONArray("keyinput_rows");
            for (int i = 0; i < array.length(); i++) {

                JSONObject object = array.getJSONObject(i);
                input.setDevUnitID(object.getString("canCpuID"));
                input.setBoardName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(object.getString("boardName"))));
                input.setBoardType((byte) object.getInt("boardType"));
                input.setKeyCnt((byte) object.getInt("keyCnt"));
                input.setLedBkType((byte) object.getInt("ledBkType"));

                JSONArray array1 = object.getJSONArray("keyName_rows");
                String[] name = new String[array1.length()];
                for (int j = 0; j < array1.length(); j++) {
                    name[j] = CommonUtils.getGBstr(CommonUtils.hexStringToBytes(array1.getString(j)));
                }
                input.setKeyName(name);
            }

            MyApplication.getWareData().getKeyInputs().add(input);

        } catch (JSONException e) {
            System.out.println(e.toString());
        }
    }


    public void getKeyOpItem(String info) {

//        返回数据类型；
//        {
//            "devUnitID": "37ffdb05424e323416702443",
//                "datType": 11,
//                "subType1": 1,
//                "subType2": 0,
//                "key_opitem_rows": [
//            {
//                "key_cpuCanID": "50ff6c067184515640421267",
//                    "out_cpuCanID": "31ffdf054257313827502543",
//                    "devType": 3,
//                    "devID": 5,
//                    "keyOpCmd": 3,
//                    "keyOp": 1
//            }
//            ],
//            "key_opitem": 1
//        }
        MyApplication.getWareData().getKeyOpItems().clear();
        try {
            JSONObject jsonObject = new JSONObject(info);
            if (jsonObject.getInt("key_opitem") == 0) {
                isFreshData = false;
                return;
            }
            JSONArray array = jsonObject.getJSONArray("key_opitem_rows");
            for (int i = 0; i < array.length(); i++) {
                WareKeyOpItem opItem = new WareKeyOpItem();
                JSONObject object = array.getJSONObject(i);
                opItem.setDevUnitID(object.getString("out_cpuCanID"));
                opItem.setDevType((byte) object.getInt("devType"));
                opItem.setDevId((byte) object.getInt("devID"));
                opItem.setKeyOpCmd((byte) object.getInt("keyOpCmd"));
                opItem.setKeyOp((byte) object.getInt("keyOp"));
                MyApplication.getWareData().getKeyOpItems().add(opItem);
            }

        } catch (JSONException e) {
            System.out.println(e.toString());
        }
    }

    public void delKeyOpItem_result(String info) {
//        {
//            "devUnitID":	"37ffdb05424e323416702443",
//                "datType":	12,
//                "subType1":	1,
//                "subType2":	0,
//                "canCpuID":	"50ff6c067184515640421267",
//                "result":	1
//        }
        MyApplication.getWareData().getKeyOpItems().clear();
        Gson gson = new Gson();
//        SetEquipmentResult result = gson.fromJson(info, SetEquipmentResult.class);
//        MyApplication.getWareData().setResult(result);
    }

    public void delChnOpItem_result(String info) {
//        {
//            "devUnitID":	"37ffdb05424e323416702443",
//                "datType":	12,
//                "subType1":	1,
//                "subType2":	0,
//                "canCpuID":	"50ff6c067184515640421267",
//                "result":	1
//        }
        MyApplication.getWareData().getChnOpItems().clear();
        Gson gson = new Gson();
//        SetEquipmentResult result = gson.fromJson(info, SetEquipmentResult.class);
//        MyApplication.getWareData().setResult(result);
    }

    public void setKeyOpItem_result(String info) {
//        {
//            "devUnitID":	"37ffdb05424e323416702443",
//                "datType":	12,
//                "subType1":	1,
//                "subType2":	0,
//                "canCpuID":	"50ff6c067184515640421267",
//                "result":	1
//        }
        Gson gson = new Gson();
//        SetEquipmentResult result = gson.fromJson(info, SetEquipmentResult.class);
//        MyApplication.getWareData().setResult(result);
    }


    public void getChnOpItem(String info) {
//        {
//            "chn_opitem_rows":	[{
//            "key_cpuCanID":	"50ff6c067184515640421267",
//                    "keyDownValid":	1,
//                    "keyUpValid":	0,

//                    "keyUpCmd":	[0, 0, 0, 0, 163, 772],
//            "keyDownCmd":	[4, 0, 0, 0, 0, 0]
//        }],
//            "devUnitID":	"37ffdb05424e323416702443",
//                "datType":	14,
//                "subType1":	1,
//                "subType2":	1,
//                "chn_opitem":	1
//        }
        List<WareChnOpItem> inputs = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(info);

            if (object.getInt("chn_opitem") == 0) {
                isFreshData = false;
                return;
            }
            JSONArray array = object.getJSONArray("chn_opitem_rows");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object_rows = array.getJSONObject(i);
                WareChnOpItem item = new WareChnOpItem();
                item.setDevUnitID(object_rows.getString("key_cpuCanID"));
                item.setKeyDownValid((byte) object_rows.getInt("keyDownValid"));
                item.setKeyUpValid((byte) object_rows.getInt("keyUpValid"));

                JSONArray array_up_cmd = object_rows.getJSONArray("keyUpCmd");
                byte[] up_cmd = new byte[6];
                for (int j = 0; j < array_up_cmd.length(); j++) {
                    up_cmd[j] = (byte) array_up_cmd.getInt(j);
                }
                item.setKeyUpCmd(up_cmd);

                JSONArray array_down_cmd = object_rows.getJSONArray("keyDownCmd");
                byte[] down_cmd = new byte[6];
                for (int j = 0; j < array_down_cmd.length(); j++) {
                    down_cmd[j] = (byte) array_down_cmd.getInt(j);
                }
                item.setKeyDownCmd(down_cmd);
                inputs.add(item);
                MyApplication.getWareData().setChnOpItems(inputs);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void getSceneEvents(String info) {

//        情景模式返回数据类型；
//        scene_return
//        {
//            "devUnitID": "37ffdb05424e323416702443",
//                "datType": 22,
//                "subType1": 1,
//                "subType2": 0,
//                "scene_rows": [
//            {
//                "sceneName": "c8abbfaac4a3cabd00000000",
//                    "devCnt": 0,
//                    "eventID": 0
//            },
//            {
//                "sceneName": "c8abb9d8c4a3cabd00000000",
//                    "devCnt": 0,
//                    "eventID": 1
//            }
//            ],
//            "scene": 2
//        }

        try {
            JSONObject jsonObject = new JSONObject(info);

            JSONArray array = jsonObject.getJSONArray("scene_rows");

            List<WareSceneEvent> list = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {

                WareSceneEvent event = new WareSceneEvent();
                JSONObject objcet = array.getJSONObject(i);

                event.setEventld((byte) objcet.getInt("eventID"));
                event.setDevCnt((byte) objcet.getInt("devCnt"));
                event.setSceneName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(objcet.getString("sceneName"))));
                int devCnt = objcet.getInt("devCnt");
                if (devCnt > 0) {
                    event.setItemAry(new ArrayList<WareSceneDevItem>());
                    JSONArray itemAry = objcet.getJSONArray("itemAry");
                    for (int j = 0; j < devCnt; j++) {
                        JSONObject object2 = itemAry.getJSONObject(j);
                        WareSceneDevItem item = new WareSceneDevItem();
                        item.setbOnOff((byte) object2.getInt("bOnOff"));
                        item.setDevID((byte) object2.getInt("devID"));
                        item.setDevType((byte) object2.getInt("devType"));
                        item.setUid(object2.getString("canCpuID"));
                        event.getItemAry().add(item);
                    }
                }
                list.add(event);
            }
            MyApplication.getWareData().setSceneEvents(list);
        } catch (JSONException e) {
            System.out.println(e.toString());
        }
    }

    public void ctrlDevReply(String info) {

//        灯返回数据类型；
//        light:
//        {
//            "devUnitID":	"37ffdb05424e323416702443",
//                "datType":	35,
//                "subType1":	1,
//                "subType2":	1,
//                "light_rows":	[{
//            "canCpuID":	"31ffdf054257313827502543",
//                    "devName":	"b5c635350000000000000000",
//                    "roomName":	"ceb4b6a8d2e5000000000000",
//                    "devType":	3,
//                    "devID":	5,
//                    "bOnOff":	1,
//                    "bTuneEn":	0,
//                    "lmVal":	0,
//                    "powChn":	5
//        }, {
//            "canCpuID":	"31ffdf054257313827502543",
//                    "devName":	"b5c636360000000000000000",
//                    "roomName":	"ceb4b6a8d2e5000000000000",
//                    "devType":	3,
//                    "devID":	6,
//                    "bOnOff":	0,
//                    "bTuneEn":	0,
//                    "lmVal":	0,
//                    "powChn":	6
//        }, {
//            "canCpuID":	"31ffdf054257313827502543",
//                    "devName":	"b5c637000000000000000000",
//                    "roomName":	"ceb4b6a8d2e5000000000000",
//                    "devType":	3,
//                    "devID":	7,
//                    "bOnOff":	1,
//                    "bTuneEn":	0,
//                    "lmVal":	0,
//                    "powChn":	7
//        }, {
//            "canCpuID":	"31ffdf054257313827502543",
//                    "devName":	"b5c638000000000000000000",
//                    "roomName":	"ceb4b6a8d2e5000000000000",
//                    "devType":	3,
//                    "devID":	8,
//                    "bOnOff":	1,
//                    "bTuneEn":	0,
//                    "lmVal":	0,
//                    "powChn":	8
//        }, {
//            "canCpuID":	"31ffdf054257313827502543",
//                    "devName":	"b5c639000000000000000000",
//                    "roomName":	"ceb4b6a8d2e5000000000000",
//                    "devType":	3,
//                    "devID":	9,
//                    "bOnOff":	1,
//                    "bTuneEn":	0,
//                    "lmVal":	0,
//                    "powChn":	9
//        }, {
//            "canCpuID":	"31ffdf054257313827502543",
//                    "devName":	"b5c631300000000000000000",
//                    "roomName":	"ceb4b6a8d2e5000000000000",
//                    "devType":	3,
//                    "devID":	10,
//                    "bOnOff":	1,
//                    "bTuneEn":	0,
//                    "lmVal":	0,
//                    "powChn":	10
//        }, {
//            "canCpuID":	"31ffdf054257313827502543",
//                    "devName":	"b5c631310000000000000000",
//                    "roomName":	"ceb4b6a8d2e5000000000000",
//                    "devType":	3,
//                    "devID":	11,
//                    "bOnOff":	1,
//                    "bTuneEn":	0,
//                    "lmVal":	0,
//                    "powChn":	11
//        }],
//            "light":	7
//        }

        try {
            JSONObject jsonObject = new JSONObject(info);

            JSONArray array = jsonObject.getJSONArray("light_rows");
            int num = jsonObject.getInt("light");

            List<WareLight> lights = new ArrayList<>();

            for (int i = 0; i < num; i++) {

                WareLight light = new WareLight();

                JSONObject jsonobj = array.getJSONObject(i);
                WareDev dev = new WareDev();
                dev.setCanCpuId(jsonobj.getString("canCpuID"));
                dev.setDevName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(jsonobj.getString("devName"))));
                dev.setRoomName(CommonUtils.getGBstr(CommonUtils.hexStringToBytes(jsonobj.getString("roomName"))));
                dev.setDevId((byte) jsonobj.getInt("devID"));
                dev.setType((byte) jsonobj.getInt("devType"));
                light.setDev(dev);
                light.setbOnOff((byte) jsonobj.getInt("bOnOff"));
                light.setPowChn((byte) jsonobj.getInt("powChn"));
                light.setLmVal((byte) jsonobj.getInt("lmVal"));
                light.setbTuneEn((byte) jsonobj.getInt("bTuneEn"));
                lights.add(light);

            }

            MyApplication.getWareData().setLights(lights);

        } catch (JSONException e) {
            System.out.println(e.toString());
        }
    }

    private static List<WareDev> removeDuplicateDevs(List<WareDev> list) {
        List<WareDev> devs = new ArrayList<WareDev>();

        for (int i = 0; i < list.size(); i++) {
            devs.add(list.get(i));
        }

        for (int i = 0; i < devs.size() - 1; i++) {
            for (int j = devs.size() - 1; j > i; j--) {
                if (devs.get(i).getCanCpuId().equals(devs.get(j).getCanCpuId())
                        && devs.get(i).getDevId() == devs.get(j).getDevId()
                        && devs.get(i).getType() == devs.get(j).getType()) {
                    devs.remove(j);
                }
            }
        }

        return devs;
    }
}