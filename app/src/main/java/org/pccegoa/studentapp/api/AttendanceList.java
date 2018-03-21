package org.pccegoa.studentapp.api;

import java.util.List;

/**
 * Created by Siddharth on 3/20/2018.
 */

public class AttendanceList {
    private int a_count;
    private int code;
    private int d_count;
    private List<AttendanceRecord> data;
    private int p_count;
    private String status;

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

    public List<AttendanceRecord> getData() {
        return data;
    }

    public void setData(List<AttendanceRecord> data) {
        this.data = data;
    }

    public int getP_count() {
        return p_count;
    }

    public void setP_count(int p_count) {
        this.p_count = p_count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
