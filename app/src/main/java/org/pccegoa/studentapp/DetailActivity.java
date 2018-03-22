package org.pccegoa.studentapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.pccegoa.studentapp.adapter.TodayListAdapter;
import org.pccegoa.studentapp.api.AttendanceApiClient;
import org.pccegoa.studentapp.api.AttendanceClientCreator;
import org.pccegoa.studentapp.api.AttendanceList;
import org.pccegoa.studentapp.api.AttendanceRecord;
import org.pccegoa.studentapp.fragment.AttendanceRecordDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements
            DatePickerDialog.OnDateSetListener, SwipeRefreshLayout.OnRefreshListener {


    private DatePickerDialog mDatePickerDialog = null;
    private FloatingActionButton mSelectDateButton = null;
    private FloatingActionButton mLoadEverythingButton = null;
    private TextView mDateTextView = null;
    private Calendar mDateFilter = null;
    private SwipeRefreshLayout mSwipeRefreshLayout = null;
    private Call<AttendanceList> mCall = null;
    private Call<AttendanceList> mCall2 = null;
    private final AttendanceApiClient mClient = AttendanceClientCreator.createApiClient();
    private ListView mListView = null;
    public static final String FETCH_PRESENT = "present";
    public static final String FETCH_ABSENT = "absent";
    public static final String FETCH_ARGUMENT = DetailActivity.class.getCanonicalName()+".fetch_mark";

    private String FETCH_MARK = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setUpFetchMark();
        setUpViews();
        mSwipeRefreshLayout.setRefreshing(true);
        fetchAttendanceDetails();
    }

    private void fetchAttendanceDetails()
    {

        SharedPreferences userPreferences = getSharedPreferences(getString(R.string.shared_preference),
                MODE_PRIVATE);
        SharedPreferences filterPreferences = getSharedPreferences(getString(R.string.
                shared_preference_filters),MODE_PRIVATE);

        int year = filterPreferences.getInt(getString(R.string.year_key),mDateFilter.get(Calendar.
                YEAR));
        int semester = filterPreferences.getInt(getString(R.string.semester_key),1);
        String auth = "Token "+ userPreferences.getString(getString(R.string.user_api_token_key),null);
        int userId = userPreferences.getInt(getString(R.string.user_id_key),-1);

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String today = format.format(mDateFilter.getTime());
        mCall = mClient.getAttendanceDetailsFromTo(auth,FETCH_MARK,year,semester,userId,
                today,today);
        mCall.enqueue(new Callback<AttendanceList>() {
            @Override
            public void onResponse(Call<AttendanceList> call, Response<AttendanceList> response) {
                AttendanceList attendanceList = response.body();
                List<AttendanceRecord> records =attendanceList.getData();

                if(records.size() == 0)
                    Toast.makeText(DetailActivity.this, "No Records Found",
                            Toast.LENGTH_SHORT).show();
                TodayListAdapter adapter = new TodayListAdapter(getApplicationContext(),records);
                mListView.setAdapter(adapter);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<AttendanceList> call, Throwable t) {
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(DetailActivity.this, "Some Error Occurred",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setUpFetchMark()
    {
        Intent i = getIntent();
        String arg = i.getStringExtra(FETCH_ARGUMENT);
        if(arg.equals(FETCH_PRESENT))
        {
            FETCH_MARK = FETCH_PRESENT;
            getSupportActionBar().setTitle("Present Records");
        }
        else if(arg.equals(FETCH_ABSENT))
        {
            FETCH_MARK = FETCH_ABSENT;
            getSupportActionBar().setTitle("Absent Records");
        }

    }
    private void setUpViews()
    {
        mListView = findViewById(R.id.listView);
        mLoadEverythingButton = findViewById(R.id.loadEverythingButton);
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mDateTextView = findViewById(R.id.dateView);
         mDateFilter = GregorianCalendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yy");
        String dateString = format.format(mDateFilter.getTime());
        mDateTextView.setText(dateString);
        mDatePickerDialog = new DatePickerDialog(this,R.style.DatePickerTheme,
                this,mDateFilter.get(Calendar.YEAR),mDateFilter.get(Calendar.MONTH),
                mDateFilter.get(Calendar.DAY_OF_MONTH));

        mSelectDateButton = findViewById(R.id.selectDateButton);
        mSelectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePickerDialog.show();
            }
        });
        mLoadEverythingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchEverything();
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TodayListAdapter adapter = (TodayListAdapter) parent.getAdapter();
                AttendanceRecord record = adapter.getItem(position);
                DialogFragment dialogFragment = new AttendanceRecordDialog();

                Bundle arguments = new Bundle();
                arguments.putString(AttendanceRecordDialog.ARG_SUBJECT_NAME,record.getSubject_name());
                arguments.putString(AttendanceRecordDialog.ARG_FACULTY_NAME,record.getFaculty_name());
                arguments.putString(AttendanceRecordDialog.ARG_ATTENDANCE_STATUS,record.getAtt_status());
                arguments.putString(AttendanceRecordDialog.ARG_DATE,record.getDate_of_attendance());
                arguments.putString(AttendanceRecordDialog.ARG_SUBJECT_TYPE,
                        record.getSubject_type());
                dialogFragment.setArguments(arguments);

                dialogFragment.show(getSupportFragmentManager(),"Details");
            }
        });

    }

    private void fetchEverything()
    {

        mSwipeRefreshLayout.setRefreshing(true);
        SharedPreferences userPreferences = getSharedPreferences(getString(R.string.shared_preference),
                MODE_PRIVATE);
        SharedPreferences filterPreferences = getSharedPreferences(getString(R.string.
                shared_preference_filters),MODE_PRIVATE);

        int year = filterPreferences.getInt(getString(R.string.year_key),mDateFilter.get(Calendar.
                YEAR));
        int semester = filterPreferences.getInt(getString(R.string.semester_key),1);
        String auth = "Token "+ userPreferences.getString(getString(R.string.user_api_token_key),null);
        String rollNo = userPreferences.getString(getString(R.string.user_roll_no),null);

        mCall2 = mClient.getAllAttendanceByMark(auth,year,semester,rollNo,
                FETCH_MARK.toUpperCase().charAt(0)+"");
        mCall2.enqueue(new Callback<AttendanceList>() {
            @Override
            public void onResponse(Call<AttendanceList> call, Response<AttendanceList> response) {
                AttendanceList attendanceList = response.body();
                List<AttendanceRecord> records =attendanceList.getData();

                if(records.size() == 0)
                    Toast.makeText(DetailActivity.this, "No Records Found",
                            Toast.LENGTH_SHORT).show();
                TodayListAdapter adapter = new TodayListAdapter(getApplicationContext(),records);
                mListView.setAdapter(adapter);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<AttendanceList> call, Throwable t) {
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(DetailActivity.this, "Some Error Occurred",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mDateFilter.set(year,month,dayOfMonth);
        SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yy");
        String dateString = format.format(mDateFilter.getTime());
        mDateTextView.setText(dateString);
    }

    @Override
    protected void onStop() {
        if(mCall!=null)
            mCall.cancel();
        if(mCall2!=null)
            mCall2.cancel();
        super.onStop();
    }

    @Override
    public void onRefresh() {
        fetchAttendanceDetails();
    }
}
