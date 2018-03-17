package org.pccegoa.studentapp.fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.pccegoa.studentapp.LoginActivity;
import org.pccegoa.studentapp.R;

/**
 * Created by siddharth on 26/1/18.
 */

public class TutorialFragment extends Fragment {
    public final static String ARG_LAYOUT = "com.pccegoa.studentapp.tutorial_fragment.arg_layout";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        int layoutId = getArguments().getInt(ARG_LAYOUT,R.layout.intro_layout);
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                layoutId, container, false);
        Button whatNextButton = rootView.findViewById(R.id.whatNextButton);
        if(whatNextButton!=null)
            whatNextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    ((AppCompatActivity) getContext()).finish();
                }
            });
        return rootView;
    }
}
