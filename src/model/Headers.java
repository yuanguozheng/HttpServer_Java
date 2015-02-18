package model;

import util.TextUtils;

import java.util.HashMap;

public class Headers {

    private String mMethod;
    private String mQueryString;
    private String mVersion;
    private HashMap<String, String> mAllHeaders;

    public Headers() {
        mAllHeaders = new HashMap<String, String>();
    }

    public String getMethod() {
        return mMethod;
    }

    public void setMethod(String method) {
        mMethod = method.toUpperCase();
    }

    public String getQueryString() {
        return mQueryString;
    }

    public void setQueryString(String queryString) {
        mQueryString = queryString;
    }

    public String getVersion() {
        return mVersion;
    }

    public void setVersion(String version) {
        mVersion = version.toUpperCase();
    }

    public String getHeader(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        if (mAllHeaders == null) {
            return null;
        }
        String k = key.toLowerCase();
        if (!mAllHeaders.containsKey(k)) {
            return null;
        }
        return mAllHeaders.get(k);
    }

    public void addHeader(String key, String value) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            return;
        }
        mAllHeaders.put(key.toLowerCase(), value);
    }
}
