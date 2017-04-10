package cn.etsoft.smarthomephone.pullmi.entity;

import java.io.Serializable;

public class WareTv implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private WareDev dev;

    public WareDev getDev() {
        return dev;
    }

    public void setDev(WareDev dev) {
        this.dev = dev;
    }


    public byte getbOnOff() {
        return bOnOff;
    }

    public void setbOnOff(byte bOnOff) {
        this.bOnOff = bOnOff;
    }

    private byte bOnOff;


}
