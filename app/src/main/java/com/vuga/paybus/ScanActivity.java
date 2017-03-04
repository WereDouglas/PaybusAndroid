package com.vuga.paybus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ScanActivity extends AppCompatActivity {
    private TextView label;
    private ConnectionDetector cd;
    private String barcode;
    private boolean canEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        cd = new ConnectionDetector(ScanActivity.this);

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
        integrator.setPrompt("Scan a barcode");
        // Use a specific camera of the device
        integrator.setBeepEnabled(true);
        integrator.initiateScan();
//

        label = (TextView) findViewById(R.id.label);
    }

    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                barcode = result.getContents();
                if (cd.isConnectingToInternet()) {
                    Register();
                } else {
                    Toast.makeText(ScanActivity.this, "No Internet Connection !", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private boolean Register() {

        label.setText(barcode);
        final ProgressDialog progressDialog = new ProgressDialog(ScanActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Submitting information...");
        progressDialog.show();


        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // IMEI = telephonyManager.getDeviceId();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("device", "true");
        params.put("barcode", barcode);
        client.post(util.Url + "payment/code", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                String ret = new String(responseBody);
                try {
                    JSONObject j = new JSONObject(ret);
                    if (j.get("status").toString().equals("true")) {
                        progressDialog.cancel();
                        String contents = "NAME:"+j.get("name").toString()+ "\n";
                        contents += "CONTACT:"+j.get("contact").toString()+ "\n";
                        contents += "BUS:"+j.get("bus").toString()+ "\n";
                        contents += "SEAT:"+j.get("seat").toString()+ "\n";
                        contents += barcode;
                        label.setText(contents);

                    } else {

                        Toast.makeText(getApplicationContext(), " " + j.get("info").toString() + "", Toast.LENGTH_LONG).show();
                        label.setText(barcode);
                        progressDialog.cancel();
                    }
                    //  Toast.makeText(getApplicationContext(), "registration successful", Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    // TODO Auto-generated catch block

                    System.out.print("data sync Error" + e);

                    System.out.print(ret);
                    e.printStackTrace();
                    progressDialog.cancel();

                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();

                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Error:" + statusCode + error.getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        });
        return canEnter;
    }

}
