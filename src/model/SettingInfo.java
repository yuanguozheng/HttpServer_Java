package model;

public class SettingInfo {

    private String mHomePath;
    private String mDefault;
    private int mPort;

    public String getHomePath() {
        return mHomePath;
    }

    public void setHomePath(String homePath) {
        mHomePath = homePath;
    }

    public int getPort() {
        return mPort;
    }

    public void setPort(int port) {
        mPort = port;
    }

    public String getDefault() {
        return mDefault;
    }

    public void setDefault(String defaultDoc) {
        mDefault = defaultDoc;
    }

}
