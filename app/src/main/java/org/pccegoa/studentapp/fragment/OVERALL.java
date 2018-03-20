package org.pccegoa.studentapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.pccegoa.studentapp.R;
import org.pccegoa.studentapp.api.AttendanceApiClient;
import org.pccegoa.studentapp.api.AttendanceClientCreator;
import org.pccegoa.studentapp.api.AttendancePercentile;

import java.util.ArrayList;
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
    private String mParam1;
    private String mParam2;
    PieChart attChart;
    TextView attStatus;
    private OnFragmentInteractionListener mListener;
    private AttendanceApiClient mClient = null;
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

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mClient = AttendanceClientCreator.createApiClient();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        attChart=(PieChart) getView().findViewById(R.id.attendanceChart);

        //updatePieChart(90.6f);
        fetchAttendancePercentile();
    }

    private void fetchAttendancePercentile()
    {
        SharedPreferences preferences = getActivity().getSharedPreferences
                (getString(R.string.shared_preference),Context.MODE_PRIVATE);
        int userId = preferences.getInt(getString(R.string.user_id_key),1);
        String apiToken = preferences.getString(getString(R.string.user_api_token_key),null);

        //need to be modified
        int year = 2018;
        int semester = 6;
        String auth = "Token "+apiToken;
        Call<AttendancePercentile> call = mClient.getAttendancePercentile(auth,year,semester,userId);
        call.enqueue(new Callback<AttendancePercentile>() {
            @Override
            public void onResponse(Call<AttendancePercentile> call, Response<AttendancePercentile> response) {
                AttendancePercentile percentile = response.body();
                updatePieChart(percentile.getPercentile());

            }

            @Override
            public void onFailure(Call<AttendancePercentile> call, Throwable t) {
                Toast.makeText(getContext(), "Some Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void updatePieChart(float percentile) {
        List<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(percentile, "PRESENT"));
        entries.add(new PieEntry(100-percentile, "ABSENT"));
        PieDataSet set = new PieDataSet(entries, "ATTENDANCE");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        //int[] colors = {getContext().getResources().getColor(R.color.primaryColor),
               // getContext().getResources().getColor(R.color.primaryLightColor)};
        //set.setColors(colors);

        PieData data = new PieData(set);
        data.setValueTextSize(15f);
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
