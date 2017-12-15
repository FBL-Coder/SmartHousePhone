package cn.semtec.community2.entity;

import java.util.List;

/**
 * Author：FBL  Time： 2017/12/5.
 */

public class RecordEntity {


    /**
     * returnCode : 0
     * msg :
     * args : http://yitong-ss1.weedoor.com:8040/sipManager/
     * object : [{"photoUrl":"files/201744/upload/openDoorLog/ad45f46781ab4cd4b7e6777c830e77ab.jpg","time":"2017-11-01 05:32:47","userName":"0600000000000000","openType":1,"lockName":"上海1016"},{"photoUrl":"files/201744/upload/openDoorLog/12809fc7b9da4e528c7c86d7bd8df1f4.jpg","time":"2017-11-01 05:32:49","userName":"0600000000000000","openType":1,"lockName":"上海1016"},{"photoUrl":"files/201744/upload/openDoorLog/1e5ef869791f4487a7cd44fd8bb26c8d.jpg","time":"2017-11-01 05:33:03","userName":"0600000000000000","openType":1,"lockName":"上海1016"},{"photoUrl":"files/201744/upload/openDoorLog/71b280f0b3c549358a9b131078f1f794.jpg","time":"2017-11-01 05:34:35","userName":"0D00000000000000","openType":1,"lockName":"上海1016"},{"photoUrl":"files/201744/upload/openDoorLog/1d3ef3c0b77d4b2a9313ac9d206d377f.jpg","time":"2017-11-01 05:34:41","userName":"0600000000000000","openType":1,"lockName":"上海1016"},{"photoUrl":"files/201744/upload/openDoorLog/9f123ac4eff34bd1a25a6b2730eef2c1.jpg","time":"2017-11-01 05:34:55","userName":"0500000000000000","openType":1,"lockName":"上海1016"},{"photoUrl":"files/201744/upload/openDoorLog/7e01ab434c5b4cb2baf378b104f152f0.jpg","time":"2017-11-03 00:39:39","userName":"0600000000000000","openType":1,"lockName":"上海1016"},{"photoUrl":"files/201744/upload/openDoorLog/19701366023c431e860b1e6a8eddc790.jpg","time":"2017-11-03 00:48:35","userName":"0400000000000000","openType":1,"lockName":"上海1016"},{"photoUrl":"files/201744/upload/openDoorLog/ca71adf13fad4a29a0e04ff576ebce08.jpg","time":"2017-11-03 03:06:46","userName":"0400000000000000","openType":1,"lockName":"上海1016"},{"photoUrl":"files/201744/upload/openDoorLog/16f989493ea84676bf0105019542665f.jpg","time":"2017-11-03 07:01:34","userName":"0300000000000000","openType":1,"lockName":"上海1016"},{"photoUrl":"files/201744/upload/openDoorLog/ecced403588f4ff183e4ccdb592f592e.jpg","time":"2017-11-03 07:01:42","userName":"0400000000000000","openType":1,"lockName":"上海1016"},{"photoUrl":"20171208/7238044dcff6fa0054c020052530dc0b.jpg","time":null,"userName":"我是开门人","openType":"","lockName":""}]
     */

    private int returnCode;
    private String msg;
    private String args;
    private List<ObjectBean> object;

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public List<ObjectBean> getObject() {
        return object;
    }

    public void setObject(List<ObjectBean> object) {
        this.object = object;
    }

    public static class ObjectBean {
        /**
         * photoUrl : files/201744/upload/openDoorLog/ad45f46781ab4cd4b7e6777c830e77ab.jpg
         * time : 2017-11-01 05:32:47
         * userName : 0600000000000000
         * openType : 1
         * lockName : 上海1016
         */

        private String photoUrl;
        private String time;
        private String userName;
        private int openType;
        private String lockName;

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public int getOpenType() {
            return openType;
        }

        public void setOpenType(int openType) {
            this.openType = openType;
        }

        public String getLockName() {
            return lockName;
        }

        public void setLockName(String lockName) {
            this.lockName = lockName;
        }
    }
}
