package model;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class Request {

    private Headers mHeaders;
    private String mBody;

    public Headers getHeaders() {
        return mHeaders;
    }

    public void setHeaders(Headers headers) {
        mHeaders = headers;
    }

    public String getBody() {
        try {
            return URLDecoder.decode(mBody, null);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setBody(String body) {
        mBody = body;
    }

}
