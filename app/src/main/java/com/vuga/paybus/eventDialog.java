package com.vuga.paybus;

/**
 * Created by DOUGLAS on 11/03/2017.
 */


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class eventDialog extends DialogFragment {
    Button mButton, btnCancel;
    EditText mEditText, txtcost, txtparticular, txttotal, txtunit, txtqty;
    onSubmitListener mListener;


    private int hour;
    private int minute;
    private String selectedTime;

    static final int TIME_DIALOG_ID = 999;

    String notify = "false";
    String court = "false";
    String priority;
    String[] bankNames = {"Medium", "High", "Low"};

    interface onSubmitListener {
        void setOnSubmitListener(String arg);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.event_dialog);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        mButton = (Button) dialog.findViewById(R.id.button1);
        btnCancel = (Button) dialog.findViewById(R.id.cancel);
        txtqty = (EditText) dialog.findViewById(R.id.qty);
        txtparticular = (EditText) dialog.findViewById(R.id.particular);
        txtunit = (EditText) dialog.findViewById(R.id.unit);
        txttotal = (EditText) dialog.findViewById(R.id.total);

        txtunit.addTextChangedListener(new TextWatcher() {
                                           public void afterTextChanged(Editable s) {

                                           }

                                           public void beforeTextChanged(CharSequence s, int start,
                                                                         int count, int after) {
                                           }

                                           public void onTextChanged(CharSequence s, int start,
                                                                     int before, int count) {
                                               try {
                                                   txttotal.setText((Double.parseDouble(txtqty.getText().toString()) * Double.parseDouble(txtunit.getText().toString())) + "");

                                               } catch (Exception c) {

                                               }
                                           }
                                       }

        );


        btnCancel.setOnClickListener(new

                                             OnClickListener() {

                                                 @Override
                                                 public void onClick(View v) {
                                                     dismiss();

                                                 }
                                             }

        );
        mButton.setOnClickListener(new

                                           OnClickListener() {

                                               @Override
                                               public void onClick(View v) {
                                                   //  Toast.makeText(getActivity(), "category Selected : ", Toast.LENGTH_LONG).show();
//                mListener.setOnSubmitListener(mEditText.getText().toString());
                                                   if (Save()) {
                                                      // Intent startLocation = new Intent(getContext(), ExpenseActivity.class);
                                                       dismiss();
                                                   }

                                               }
                                           }

        );
        return dialog;
    }

    private boolean Save() {
        ExpenseHandler db = new ExpenseHandler(getActivity());
        db.addExpense(new Expense(util.SESSION_ID, txtparticular.getText().toString(), txtqty.getText().toString(), txtunit.getText().toString(), txttotal.getText().toString(), "f"));

        return true;
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

}