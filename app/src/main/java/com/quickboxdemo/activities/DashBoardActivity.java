package com.quickboxdemo.activities;

import android.Manifest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.sample.core.utils.SharedPrefsHelper;
import com.quickblox.users.model.QBUser;
import com.quickboxdemo.App;
import com.quickboxdemo.R;
import com.quickboxdemo.util.QBResRequestExecutor;
import com.quickboxdemo.utils.Consts;
import com.quickboxdemo.utils.UsersUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Permissions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Srinivas on 2/21/2018.
 */

public class DashBoardActivity extends BaseActivity {


    private SharedPreferences mSharedPreferences;
    GridViewAdapter adapter;
    public ArrayList<String> symptoms_Array;

    private GridView gridView;
    private View btnGo;
    private ArrayList<String> selectedStrings;
    private static String[] numbers;
    SharedPrefsHelper sharedPrefsHelper;

    ProgressDialog pd;
    String currentUserFullName;
    TextView userName;
    ImageButton logOUTBtn;
    protected QBResRequestExecutor requestExecutor;
    private QBUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        sharedPrefsHelper = SharedPrefsHelper.getInstance();
        requestExecutor = App.getInstance().getQbResRequestExecutor();
        initDefaultActionBar();

        pd = new ProgressDialog(DashBoardActivity.this);
        getSymptomsList();

        gridView = (GridView) findViewById(R.id.grid);
        btnGo = findViewById(R.id.button);
        selectedStrings = new ArrayList<>();
        symptoms_Array = new ArrayList<>();

        //set listener for Button event
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                me();
                Intent intent = new Intent(getApplicationContext(), OpponentsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra(Consts.EXTRA_IS_STARTED_FOR_CALL, false);
                intent.putStringArrayListExtra("SELECTED_LETTER", selectedStrings);
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_opponents, menu);

        MenuItem item=menu.findItem(R.id.update_opponents_list);
        item.setVisible(false);



        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.update_opponents_list:
                return true;

            case R.id.settings:
                // showSettings();
                return true;

            case R.id.log_out:

                removeAllUserData();
                Intent loginAct = new Intent(getApplicationContext(), LoginActivity.class);
                loginAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                finish();
                startActivity(loginAct);

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected View getSnackbarAnchorView() {
        return null;
    }

    private void removeAllUserData() {
        UsersUtils.removeUserData(getApplicationContext());
        requestExecutor.deleteCurrentUser(currentUser.getId(), new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    public void getSymptomsList() {

        RequestQueue requestQueue_Symptoms = Volley.newRequestQueue(this);
        String tag_string_req = "getSymptomsList";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        pd.setMessage("Loading");
        pd.show();


        StringRequest request = new StringRequest(Request.Method.GET, AppConfig.url_SypmtomsList, new Response.Listener<String>() {
            @Override
            public void onResponse(String responce) {
                pd.dismiss();
                Log.d("responce", "" + responce);
                if (null != responce) {
                    try {
                        JSONArray jsonArray = new JSONArray(responce);
                        if (jsonArray.length() > 0) {
                            symptoms_Array = new ArrayList<>(jsonArray.length());
                            numbers = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String symptom = jsonObject.getString("symptom");
                                symptoms_Array.add(symptom);
                                numbers[i] = symptoms_Array.get(i);
                            }

                            adapter = new GridViewAdapter(numbers, DashBoardActivity.this);
                            gridView.setAdapter(adapter);

                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                    int selectedIndex = adapter.selectedPositions.indexOf(position);
                                    if (selectedIndex > -1) {
                                        adapter.selectedPositions.remove(selectedIndex);
                                        ((GridItemView) v).display(false);
                                        selectedStrings.remove((String) parent.getItemAtPosition(position));
                                    } else {
                                        adapter.selectedPositions.add(position);
                                        ((GridItemView) v).display(true);
                                        selectedStrings.add((String) parent.getItemAtPosition(position));

                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "No Symptoms !", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {


            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; ");
                headers.put("Accept", "application/json;");

                return headers;
            }


        };
        requestQueue_Symptoms.add(request);
    }


    public void me() {
        String jsdhf = "";
        if (selectedStrings.size() > 0) {
            for (int i = 0; i < selectedStrings.size(); i++) {
                if (i < selectedStrings.size() - 1) {
                    jsdhf = jsdhf + selectedStrings.get(i) + ", ";
                } else {
                    jsdhf = jsdhf + selectedStrings.get(i) + ".";
                    Toast.makeText(getApplicationContext(), "Selected : " + jsdhf, Toast.LENGTH_LONG).show();
                }

            }
        }
    }
}