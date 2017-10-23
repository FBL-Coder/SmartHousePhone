package cn.etsoft.smarthome.domain;

import java.util.List;

/**
 * Author：FBL  Time： 2017/10/23.
 */

public class APK_Version {


    /**
     * code : 0
     * msg :
     * data : [{"mobile":{"version":"v1.0.2_20171023_release","version1":"1","version2":"0","version3":"2","version4":"release","describe":"新版本","createTime":"1508717728","url":"http://123.206.104.89:25000/uploads/59ed349237b86.apk"},"ipad":[]}]
     */

    private int code;
    private String msg;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * mobile : {"version":"v1.0.2_20171023_release","version1":"1","version2":"0","version3":"2","version4":"release","describe":"新版本","createTime":"1508717728","url":"http://123.206.104.89:25000/uploads/59ed349237b86.apk"}
         * ipad : []
         */

        private MobileBean mobile;
        private List<?> ipad;

        public MobileBean getMobile() {
            return mobile;
        }

        public void setMobile(MobileBean mobile) {
            this.mobile = mobile;
        }

        public List<?> getIpad() {
            return ipad;
        }

        public void setIpad(List<?> ipad) {
            this.ipad = ipad;
        }

        public static class MobileBean {
            /**
             * version : v1.0.2_20171023_release
             * version1 : 1
             * version2 : 0
             * version3 : 2
             * version4 : release
             * describe : 新版本
             * createTime : 1508717728
             * url : http://123.206.104.89:25000/uploads/59ed349237b86.apk
             */

            private String version;
            private String version1;
            private String version2;
            private String version3;
            private String version4;
            private String describe;
            private String createTime;
            private String url;

            public String getVersion() {
                return version;
            }

            public void setVersion(String version) {
                this.version = version;
            }

            public String getVersion1() {
                return version1;
            }

            public void setVersion1(String version1) {
                this.version1 = version1;
            }

            public String getVersion2() {
                return version2;
            }

            public void setVersion2(String version2) {
                this.version2 = version2;
            }

            public String getVersion3() {
                return version3;
            }

            public void setVersion3(String version3) {
                this.version3 = version3;
            }

            public String getVersion4() {
                return version4;
            }

            public void setVersion4(String version4) {
                this.version4 = version4;
            }

            public String getDescribe() {
                return describe;
            }

            public void setDescribe(String describe) {
                this.describe = describe;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}
