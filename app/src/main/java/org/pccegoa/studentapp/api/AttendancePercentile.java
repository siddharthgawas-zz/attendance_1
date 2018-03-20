package org.pccegoa.studentapp.api;

/**
 * Created by Siddharth on 3/20/2018.
 */

public class AttendancePercentile {
    private int a_count;
    private int code;
    private int d_count;
    private int p_count;
    private float percentile;
    private String status;
    private String time_stamp;

    public int getA_count() {
        return a_count;
    }

    public void setA_count(int a_count) {
        this.a_count = a_count;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getD_count() {
        return d_count;
    }

    public void setD_count(int d_count) {
        this.d_count = d_count;
    }

    public int getP_count() {
        return p_count;
    }

    public void setP_count(int p_count) {
        this.p_count = p_count;
    }

    public float getPercentile() {
        return percentile;
    }

    public void setPercentile(float percentile) {
        this.percentile = percentile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }
}
