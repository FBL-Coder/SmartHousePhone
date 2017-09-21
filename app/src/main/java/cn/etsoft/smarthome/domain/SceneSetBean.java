package cn.etsoft.smarthome.domain;

/**
 * Created by Say GoBay on 2016/8/24.
 * 情景设置的bean类
 */
public class SceneSetBean {
        private int imageId;
        private String titleId;
        private int huiId;

        public SceneSetBean() {
            super();
        }

        public SceneSetBean(int imageId, String titleId, int huiId) {
            super();
            this.imageId = imageId;
            this.titleId = titleId;
            this.huiId = huiId;
        }

        public String getTitle() {
            return titleId;
        }

        public void setTitle(String title) {
            this.titleId = title;
        }

        public int getImageId() {
            return imageId;
        }

        public void setImageId(int imageId) {
            this.imageId = imageId;
        }
        public int getHuiId() {
            return huiId;
        }

        public void setHuiId(int huiId) {
            this.huiId = huiId;
        }
}
