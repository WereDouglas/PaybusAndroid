package com.vuga.paybus;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;

import java.util.ArrayList;

public class SessionActivity extends Activity {
    GridView gridView;
    ArrayList<Payment> paymentList;
    PaymentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        gridView = (GridView) findViewById(R.id.gv_emp);
        PaymentHandler databaseHelper = new PaymentHandler (SessionActivity.this);
        paymentList = new ArrayList<Payment>();
        paymentList = databaseHelper.getWhere();
       // paymentList = databaseHelper.getAllPayments();
        adapter = new PaymentAdapter(SessionActivity.this, paymentList);
        gridView.setAdapter(adapter);

    }

}
