package org.pccegoa.studentapp.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.pccegoa.studentapp.R;

/**
 * Created by Siddharth on 3/22/2018.
 */

public class AttendanceRecordDialog extends DialogFragment {
    private String mSubjectName = null;
    private String mFacultyName = null;
    private String mDate = null;
    private String mSubjectType = null;
    private String mAttendanceStatus = null;
    public static final String ARG_SUBJECT_NAME = "subject_name";
    public static final String ARG_FACULTY_NAME = "faculty_name";
    public static final String ARG_DATE = "date";
    public static final String ARG_SUBJECT_TYPE= "subject_type";
    public static final String ARG_ATTENDANCE_STATUS = "attendance_type";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.attendance_record_dialog,null);
        builder.setView(view);
        Dialog d = builder.create();
        setUpViews(view);
        return d;
    }

    private void setUpViews(View view)
    {
        Bundle arguments = getArguments();
        mSubjectName = arguments.getString(ARG_SUBJECT_NAME);
        mFacultyName = arguments.getString(ARG_FACULTY_NAME);
        mDate = arguments.getString(ARG_DATE);
        mSubjectType = arguments.getString(ARG_SUBJECT_TYPE);
        mAttendanceStatus = arguments.getString(ARG_ATTENDANCE_STATUS);
        if(mAttendanceStatus.equals("P"))
            mAttendanceStatus = "Present";
        else if(mAttendanceStatus.equals("A"))
            mAttendanceStatus = "Absent";
        else if(mAttendanceStatus.equals("D"))
            mAttendanceStatus = "Duty";

        ((TextView)view.findViewById(R.id.subjectNameView)).setText(mSubjectName);
        ((TextView)view.findViewById(R.id.facultyNameView)).setText(mFacultyName);
        ((TextView)view.findViewById(R.id.dateView)).setText(mDate);
        ((TextView)view.findViewById(R.id.typeView)).setText(mSubjectType);
        ((TextView)view.findViewById(R.id.statusView)).setText(mAttendanceStatus);
    }
}
