package com.vuga.paybus;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ExpenseActivity extends AppCompatActivity {
    private ConnectionDetector cd;
    Button btnNew;
    ArrayList<Session> sessionList;

    ListView sessionView;
    ListView gridView;
    ArrayList<Expense> expenseList;
    ExpenseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);
        btnNew = (Button) findViewById(R.id.newBtn);


        btnNew.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                eventDialog dialogFragment = new eventDialog ();
                dialogFragment.show(fm, "New Expense");
            }
        });

        gridView = (ListView) findViewById(R.id.listView1);
        ExpenseHandler databaseHelper = new ExpenseHandler (ExpenseActivity.this);
        expenseList = new ArrayList<Expense>();
        expenseList = databaseHelper.getWhere(util.SESSION_ID);
        // paymentList = databaseHelper.getAllPayments();
        adapter = new ExpenseAdapter(ExpenseActivity.this, expenseList);
        gridView.setAdapter(adapter);
        cd = new ConnectionDetector(ExpenseActivity.this);
        if (cd.isConnectingToInternet()) {
            //
            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    //Code that uses AsyncHttpClient in your case ConsultaCaract()
                    int SDK_INT = android.os.Build.VERSION.SDK_INT;
                    if (SDK_INT > 8)
                    {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                .permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        uploadExpense();
                    }
                }
            };
            mainHandler.post(myRunnable);
        }


    }
    ArrayList<Expense> expensesList;
    public void uploadExpense() {
        // Locate the Route Class
        final  ExpenseHandler databaseHelper = new ExpenseHandler(ExpenseActivity.this);
        expensesList = new ArrayList<Expense>();
        expensesList = databaseHelper.Sync(util.SESSION_ID.toString());
        for (int x = 0; x < expensesList.size(); x++) {

            final String particular = expensesList.get(x).getParticular();
            final String  sessionID = expensesList.get(x).getSessionID();
            SyncHttpClient client = new SyncHttpClient();
            RequestParams parama = new RequestParams();
            parama.put("particular",expensesList.get(x).getParticular().toString());
            parama.put("qty", expensesList.get(x).getQty().toString());
            parama.put("unit", expensesList.get(x).getUnit().toString());
            parama.put("total", expensesList.get(x).getTotal().toString());
            parama.put("device", "true");
            parama.put("companyID", util.COMPANY_ID.toString());
            parama.put("sessionID", util.SESSION_ID.toString());
            parama.put("userID", util.USER_ID.toString());
            client.post(util.Url + "expense/create", parama, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                    String ret = new String(responseBody);
                    try {

                        JSONObject j = new JSONObject(ret);
                        if (j.get("status").toString().equals("true")) {
                            String ANS =    databaseHelper.updateSync(sessionID,particular);
                            Toast.makeText(getApplicationContext(), " " + ANS + "", Toast.LENGTH_LONG).show();

                        } else {

                            Toast.makeText(getApplicationContext(), " " + j.get("info").toString() + "", Toast.LENGTH_LONG).show();

                        }
                        //  Toast.makeText(getApplicationContext(), "registration successful", Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block

                        System.out.print("data sync Error" + e);

                        System.out.print(ret);
                        e.printStackTrace();

                    }
                }

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                    if (statusCode == 404) {
                        Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();

                    } else if (statusCode == 500) {
                        Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "Error: " + statusCode + error.getMessage(), Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }





}
