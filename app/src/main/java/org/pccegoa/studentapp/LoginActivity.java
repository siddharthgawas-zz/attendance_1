package org.pccegoa.studentapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.pccegoa.studentapp.api.ApiURl;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        Response.Listener<JSONObject>,Response.ErrorListener
{
    private RequestQueue mQueue;
    private final static String REQUEST_TAG = "org.pccegoa.studentapp.request_tag";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Check for api_token key.
        If it exists then user is already signed and skip this activity*/
        SharedPreferences preferences = getSharedPreferences(getString(R.string.shared_preference),
                MODE_PRIVATE);
        String api_key = preferences.getString(getString(R.string.user_api_token_key),null);
        if(api_key != null)
        {
            Intent i = new Intent(this,HomeActivity.class);
            startActivity(i);
            finish();
            return;
        }
        setContentView(R.layout.activity_login);
        mQueue = Volley.newRequestQueue(this);
        Button loginButton = findViewById(R.id.login);
        Button clearButton = findViewById(R.id.clear);
        loginButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mQueue.cancelAll(REQUEST_TAG);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.login:
                login();
                break;
            case R.id.clear:
                clear();
                break;
        }
    }

    private void login()
    {
        String rollNo = ((EditText)findViewById(R.id.rollNoEditText)).getText().toString();
        String pwd = ((EditText)findViewById(R.id.pwdEditText)).getText().toString();
        if(rollNo.isEmpty() || pwd.isEmpty())
        {
            Toast.makeText(this, "Please Enter RollNo and Password",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        final String auth = Base64.encodeToString((rollNo+":"+pwd).getBytes(),Base64.DEFAULT);

        JsonObjectRequest request =
                new JsonObjectRequest(Request.Method.POST,ApiURl.LOGIN_URI.toString(),
                        null,this,this){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String,String> headers = new HashMap<>();
                        headers.put("X-Application-Key",getResources().getString(R.string.app_key));
                        headers.put("Authorization","Basic "+auth);
                        return headers;
                    }
                };
        request.setTag(REQUEST_TAG);
        mQueue.add(request);
        Button loginButton = findViewById(R.id.login);
        Button clearButton = findViewById(R.id.clear);
        ProgressBar progressBar = findViewById(R.id.loginProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        loginButton.setEnabled(false);
        clearButton.setEnabled(false);
    }
    private void clear()
    {
        EditText text = findViewById(R.id.rollNoEditText);
        text.getText().clear();
        text = findViewById(R.id.pwdEditText);
        text.getText().clear();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        try {
            JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data));
            int code = jsonObject.getInt("code");
            if(code == 1001)
            {
                Toast.makeText(this, "Invalid User", Toast.LENGTH_SHORT).show();
            }
            else if (code == 1002)
                Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show();
            Log.d(LoginActivity.class.getCanonicalName(),jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        Button loginButton = findViewById(R.id.login);
        Button clearButton = findViewById(R.id.clear);
        ProgressBar progressBar = findViewById(R.id.loginProgressBar);
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.GONE);
        loginButton.setEnabled(true);
        clearButton.setEnabled(true);
    }

    @Override
    public void onResponse(JSONObject response) {
        Log.d(LoginActivity.class.getCanonicalName(),response.toString());
        try {
            String api_key = response.getString("api_token");
            int user_id = response.getInt("user_id");
            SharedPreferences preferences = getSharedPreferences(getString(R.string.shared_preference)
                    ,MODE_PRIVATE);
            preferences.edit().putString(getString(R.string.user_api_token_key),api_key)
                    .putInt(getString(R.string.user_id_key),user_id).apply();
            startActivity(new Intent(this,HomeActivity.class));
            finish();
        } catch (JSONException e) {
            Toast.makeText(this, "Some Unexpected Error Occurred", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        finally {
            Button loginButton = findViewById(R.id.login);
            Button clearButton = findViewById(R.id.clear);
            ProgressBar progressBar = findViewById(R.id.loginProgressBar);
            progressBar.setIndeterminate(false);
            progressBar.setVisibility(View.GONE);
            loginButton.setEnabled(true);
            clearButton.setEnabled(true);
        }

    }
}
