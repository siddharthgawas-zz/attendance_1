package org.pccegoa.studentapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.transition.Fade;
import android.support.transition.Slide;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

public class SplashActivity extends AppCompatActivity {

    private ProgressBar mBar = null;
    //flag checks if the App is run for the first time
    private  boolean mFirstRun = true;
    //This task provides delay to splash screen
    LoadAppTask mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set splash screen to full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //
        setContentView(R.layout.activity_splash);
        mBar = findViewById(R.id.progressBar);

    }
    @Override
    protected void onStart() {
        super.onStart();
        //Use shared preferences to store the value of mFirstRun flag.
        SharedPreferences preferences = getSharedPreferences(getString(R.string.shared_preference),
                MODE_PRIVATE);
        //return flag value from shared preferences. By default it is true.
        mFirstRun = preferences.getBoolean(getString(R.string.first_run_key),true);
        //start delay
        mTask = new LoadAppTask(this);
        mTask.execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //if the application stops in between close the mTask thread.
        mTask.cancel(true);
    }

    private class LoadAppTask extends AsyncTask<Void,Integer,Void> {
        private Context mContext;
        public LoadAppTask(Context context) {
            super();
            this.mContext = context;
        }

        //This function runs on worker thread and provides delay
        @Override
        protected Void doInBackground(Void... voids)
        {
            for(int i = 1; i <= 100;i++)
            {
                //publish value of progress bar. Calls onProgressUpdate callback.
                publishProgress(i);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //set value of progress bar
            mBar.setProgress(values[0]);
        }

        //when delay function is completed this callback is executed.
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //if it is first run then show TutorialActivity
            if (mFirstRun)
            {
                Intent intent = new Intent(mContext,TutorialActivity.class);
                startActivity(intent);
                overridePendingTransition(R.transition.fade_in,R.transition.fade_out);
                SharedPreferences preferences = getSharedPreferences(getString(R.string.shared_preference),
                        MODE_PRIVATE);
                preferences.edit().putBoolean(getString(R.string.first_run_key),false).apply();
            }
            //else show login Activity
            else
            {
                Intent intent = new Intent(mContext,LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.transition.fade_in,R.transition.fade_out);
            }
            //close splash activity
            finish();

        }
    }
}
