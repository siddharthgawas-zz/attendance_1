package org.pccegoa.studentapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.pccegoa.studentapp.DetailActivity;
import org.pccegoa.studentapp.R;
import org.pccegoa.studentapp.adapter.TodayListAdapter;
import org.pccegoa.studentapp.api.AttendanceApiClient;
import org.pccegoa.studentapp.api.AttendanceClientCreator;
import org.pccegoa.studentapp.api.AttendanceList;
import org.pccegoa.studentapp.api.AttendancePercentile;
import org.pccegoa.studentapp.api.AttendanceRecord;
import org.pccegoa.studentapp.utility.AttendancePredictor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OVERALL.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OVERALL#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OVERALL extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private final static String PRESENT = "PRESENT";
    private final static String ABSENT = "ABSENT";
    public final static float ATTENDANCE_CUTOFF = 0.75f;
    PieChart attChart;
    TextView attStatus;
    private OnFragmentInteractionListener mListener;
    private AttendanceApiClient mClient = null;
    private Call<AttendanceList> attendanceListCall = null;
    private Call<AttendancePercentile> attendancePercentileCall = null;
    public OVERALL() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OVERALL.
     */
    // TODO: Rename and change types and number of parameters
    public static OVERALL newInstance(String param1, String param2) {
        OVERALL fragment = new OVERALL();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClient = AttendanceClientCreator.createApiClient();

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        attChart=(PieChart) getView().findViewById(R.id.attendanceChart);

        attChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PieEntry entry = (PieEntry)e;
                Intent i = new Intent(getActivity(),DetailActivity.class);
                if(entry.getLabel().equals(PRESENT))
                    i.putExtra(DetailActivity.FETCH_ARGUMENT,DetailActivity.FETCH_PRESENT);
                else if (entry.getLabel().equals(ABSENT))
                    i.putExtra(DetailActivity.FETCH_ARGUMENT,DetailActivity.FETCH_ABSENT);
                startActivity(i);
            }

            @Override
            public void onNothingSelected() {

            }
        });
        ListView todayListView = getView().findViewById(R.id.att_list);
        todayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

                dialogFragment.show(getActivity().getSupportFragmentManager(),"Details");
            }
        });
        SwipeRefreshLayout swipeRefreshLayout = getView().findViewById(R.id.overallRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.primaryColor));
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchAttendancePercentile();
                fetchTodayAttendanceList();
            }
        });
        //updatePieChart(90.6f);
        swipeRefreshLayout.setRefreshing(true);
        fetchAttendancePercentile();
        fetchTodayAttendanceList();
    }
    
    @Override
    public void onStop() {
        super.onStop();
        if(attendancePercentileCall != null && !attendancePercentileCall.isCanceled())
            attendancePercentileCall.cancel();
        if(attendanceListCall !=null && !attendanceListCall.isCanceled())
            attendanceListCall.cancel();
    }

    private void fetchTodayAttendanceList()
    {
        SharedPreferences preferences = getActivity().getSharedPreferences
                (getString(R.string.shared_preference),Context.MODE_PRIVATE);
        String rollNo = preferences.getString(getString(R.string.user_roll_no),null);
        String apiToken = preferences.getString(getString(R.string.user_api_token_key),null);

        SharedPreferences filterPreferences = getActivity().getSharedPreferences
                (getString(R.string.shared_preference_filters),Context.MODE_PRIVATE);
        Calendar calendar = GregorianCalendar.getInstance();

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String today = format.format(calendar.getTime());
        int year = filterPreferences.getInt(getString(R.string.year_key),calendar.get(Calendar.YEAR));
        int semester =  filterPreferences.getInt(getString(R.string.semester_key),1);
        String auth = "Token "+apiToken;

        attendanceListCall = mClient.getAttendancRecordsFromTo(auth,year,
                semester,rollNo,today,today);
        attendanceListCall.enqueue(new Callback<AttendanceList>() {
            @Override
            public void onResponse(Call<AttendanceList> call, Response<AttendanceList> response) {
                AttendanceList attendanceList = response.body();
                List<AttendanceRecord> records = attendanceList.getData();
                TodayListAdapter adapter = new TodayListAdapter(getContext(),records);
                ListView todayListView = getView().findViewById(R.id.att_list);
                todayListView.setAdapter(adapter);
                stopRefreshing();
            }

            @Override
            public void onFailure(Call<AttendanceList> call, Throwable t) {
                stopRefreshing();

            }
        });

    }

    private void stopRefreshing()
    {
        boolean condition = attendanceListCall.isExecuted() && attendancePercentileCall.isExecuted();
        View view =  getView();
        if(condition && view!=null)
        {
            SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.overallRefreshLayout);
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    private void fetchAttendancePercentile()
    {
        SharedPreferences preferences = getActivity().getSharedPreferences
                (getString(R.string.shared_preference),Context.MODE_PRIVATE);
        int userId = preferences.getInt(getString(R.string.user_id_key),1);
        String apiToken = preferences.getString(getString(R.string.user_api_token_key),null);

        SharedPreferences filterPreferences = getActivity().getSharedPreferences
                (getString(R.string.shared_preference_filters),Context.MODE_PRIVATE);
        Calendar calendar = GregorianCalendar.getInstance();
        int year = filterPreferences.getInt(getString(R.string.year_key),calendar.get(Calendar.YEAR));
        int semester =  filterPreferences.getInt(getString(R.string.semester_key),1);

        String auth = "Token "+apiToken;
        attendancePercentileCall = mClient.getAttendancePercentile(auth,year,semester,userId);
       attendancePercentileCall.enqueue(new Callback<AttendancePercentile>() {
            @Override
            public void onResponse(Call<AttendancePercentile> call, Response<AttendancePercentile> response) {
                AttendancePercentile percentile = response.body();
                stopRefreshing();
                if (percentile == null  && getContext()!=null)
                {
                    Toast.makeText(getContext(), "Attendance Not Found", Toast.LENGTH_SHORT).show();
                    updatePieChart(0f);
                    return;
                }
                makePredication(percentile);
                updatePieChart(percentile.getPercentile());

            }

            @Override
            public void onFailure(Call<AttendancePercentile> call, Throwable t) {
                if(getContext()!=null)
                    Toast.makeText(getContext(), "Some Error Occurred", Toast.LENGTH_SHORT).show();
                stopRefreshing();
            }
        });
    }
    private void makePredication(AttendancePercentile percentile)
    {
        int p = percentile.getP_count();
        int a = percentile.getA_count();
        int d = percentile.getD_count();
        AttendancePredictor predictor = new AttendancePredictor(ATTENDANCE_CUTOFF);
        int attend = predictor.classesToAttend(p,a,d);
        int bunk = predictor.classesToBunk(p,a,d);
        String predictionText = null;
        if(attend > 0)
            predictionText = "Attend "+attend+" or more classes to avoid trouble.";
        else
            predictionText = "Safe to bunk "+bunk+" and still avoid trouble.";
        TextView textView = getView().findViewById(R.id.predictTextView);
        textView.setText(predictionText);
    }
    public void updatePieChart(float percentile) {
        List<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(percentile, PRESENT));
        entries.add(new PieEntry(100-percentile, ABSENT));
        PieDataSet set = new PieDataSet(entries, "ATTENDANCE");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData data = new PieData(set);
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);
        attChart.setData(data);
        set.setValueFormatter(new PercentFormatter());

        
        attChart.getDescription().setEnabled(false);
        attChart.setHoleRadius(50);
        attChart.setCenterTextSize(12);
        attChart.setTransparentCircleAlpha(0);
        attChart.animateY(1250, Easing.EasingOption.EaseInOutCirc);

        if(percentile>75.0f)
        {
            attChart.setCenterText("SAFE!!!");
            attChart.setCenterTextColor(Color.GREEN);
        }
        else if(percentile>70.0f && percentile<75.0f)
        {
            attChart.setCenterText("BEWARE!!");
            attChart.setCenterTextColor(Color.CYAN);
        }
        else
        {
            attChart.setCenterText("DANGER!!!");
            attChart.setCenterTextColor(Color.RED);
        }
        attChart.invalidate();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_overall, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
