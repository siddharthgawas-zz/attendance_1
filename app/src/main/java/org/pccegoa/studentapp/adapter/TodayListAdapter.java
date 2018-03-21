package org.pccegoa.studentapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.pccegoa.studentapp.R;
import org.pccegoa.studentapp.api.AttendanceRecord;

import java.util.List;

/**
 * Created by Siddharth on 3/21/2018.
 */

public class TodayListAdapter extends ArrayAdapter<AttendanceRecord> {

    private List<AttendanceRecord> attendanceRecords = null;
   public TodayListAdapter(Context context,List<AttendanceRecord> attendanceRecords)
   {
       super(context, R.layout.list_item);
       this.attendanceRecords = attendanceRecords;
   }

    @Override
    public int getCount() {
        return attendanceRecords.size();
    }

    @Nullable
    @Override
    public AttendanceRecord getItem(int position) {
        return attendanceRecords.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

       View listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,
               false);
       AttendanceRecord record = getItem(position);
       String name = record.getSubject_name();
       String mark = record.getAtt_status();

        TextView subjectTextView = listItemView.findViewById(R.id.list_subject);
        TextView statusTextView = listItemView.findViewById(R.id.list_status);

        subjectTextView.setText(name);
        int[] colors = {getContext().getResources().getColor(R.color.greenSafe),
                getContext().getResources().getColor(R.color.yellowDanger)};
        if(mark.equals("P"))
        {
            statusTextView.setTextColor(colors[0]);
            statusTextView.setText("Present");
        }
        else if(mark.equals("A"))
        {
            statusTextView.setTextColor(colors[1]);
            statusTextView.setText("Absent");
        }
        else if(mark.equals("D"))
        {
            statusTextView.setTextColor(colors[0]);
            statusTextView.setText("Duty");
        }

        return listItemView;
    }
}
