package org.pccegoa.studentapp.api;

/**
 * Created by Siddharth on 3/20/2018.
 */

public class AttendanceRecord {
    private String att_status;
    private String date_of_attendance;
    private String faculty_name;
    private int id;
    private String lecture_starttime;
    private String subject_name;
    private String subject_type;

    public String getAtt_status() {
        return att_status;
    }

    public void setAtt_status(String att_status) {
        this.att_status = att_status;
    }

    public String getDate_of_attendance() {
        return date_of_attendance;
    }

    public void setDate_of_attendance(String date_of_attendance) {
        this.date_of_attendance = date_of_attendance;
    }

    public String getFaculty_name() {
        return faculty_name;
    }

    public void setFaculty_name(String faculty_name) {
        this.faculty_name = faculty_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLecture_starttime() {
        return lecture_starttime;
    }

    public void setLecture_starttime(String lecture_starttime) {
        this.lecture_starttime = lecture_starttime;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getSubject_type() {
        return subject_type;
    }

    public void setSubject_type(String subject_type) {
        this.subject_type = subject_type;
    }
}
