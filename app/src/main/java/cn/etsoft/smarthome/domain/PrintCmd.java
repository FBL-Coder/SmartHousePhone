package cn.etsoft.smarthome.domain;

import java.io.Serializable;

import cn.etsoft.smarthome.adapter.IClick_PZ;


/**
 * Created by fbl on 16-11-8.
 */
public class PrintCmd implements Serializable {
    String DevUnitID;
    String keyboardid;
    int index;
    int devType;
    int keyAct_num;
    int key_cmd;
    boolean isSelect = false;
    String keyname;

    public String getDevUnitID() {
        return DevUnitID;
    }

    public void setDevUnitID(String devUnitID) {
        DevUnitID = devUnitID;
    }


    public String getKeyboardid() {
        return keyboardid;
    }

    public void setKeyboardid(String keyboardid) {
        this.keyboardid = keyboardid;
    }

    public int getKey_cmd() {
        return key_cmd;
    }

    public void setKey_cmd(int key_cmd) {
        this.key_cmd = key_cmd;
    }

    public int getKeyAct_num() {
        return keyAct_num;
    }

    public void setKeyAct_num(int keyAct_num) {
        this.keyAct_num = keyAct_num;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getDevType() {
        return devType;
    }

    public void setDevType(int devType) {
        this.devType = devType;
    }

    public String getKeyname() {
        return keyname;
    }

    public void setKeyname(String keyname) {
        this.keyname = keyname;
    }

}
