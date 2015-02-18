package model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ResponseHeader {

    private Status mStatus;
    private String mContentType;
    private Date mDate;
    private long mContentLength;

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        mStatus = status;
    }

    public String getContentType() {
        return mContentType;
    }

    public void setContentType(String contentType) {
        mContentType = contentType;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public long getContentLength() {
        return mContentLength;
    }

    public void setContentLength(long l) {
        mContentLength = l;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("HTTP/1.1 %s\n", mStatus.getDescription()));
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        builder.append(String.format("Date: %s\n", sdf.format(mDate)));
        builder.append(String.format("Content-Type: %s\n", mContentType));
        builder.append(String.format("Content-Length: %s\r\n", mContentLength));
        builder.append("\r\n");
        return builder.toString();
    }
}
