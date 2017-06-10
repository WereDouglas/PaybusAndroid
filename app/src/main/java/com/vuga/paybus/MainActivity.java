package com.vuga.paybus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.Util;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button btnPay, btnScan, btnSession, btnExp;
    static TextView logoutLink, username, companyname = null;
    File file;
    private ImageView ivImage, UsrImage;
    Context context = this;
    String imageName, userImage;
    private ConnectionDetector cd;
    private static final String TAG = "MainActivity";
    SessionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnPay = (Button) findViewById(R.id.buttonPay);
        btnExp = (Button) findViewById(R.id.buttonExp);
        btnSession = (Button) findViewById(R.id.buttonSession);
        btnScan = (Button) findViewById(R.id.buttonScan);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        UsrImage = (ImageView) findViewById(R.id.UsrImage);
        username = (TextView) findViewById(R.id.username);
        companyname = (TextView) findViewById(R.id.companyname);
        String root = getApplicationContext().getFilesDir().toString();
        final SessionHandler databaseHelper = new SessionHandler(MainActivity.this);
        final PaymentHandler paymentHelper = new PaymentHandler(MainActivity.this);
        final ExpenseHandler expenseHelper = new ExpenseHandler(MainActivity.this);
        //  Log.i(TAG,  util.COMPANY_LOGO);
        username.setText(util.USER_NAME);
        companyname.setText(util.COMPANY);
        try {
            imageName = util.COMPANY_LOGO;
            String ul = util.FileUrl + "uploads/" + util.COMPANY_LOGO;
            Log.i(TAG, ul);
            File imgFile = new File(root + "/" + util.COMPANY_LOGO);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ivImage.setImageBitmap(myBitmap);
            } else {
                Picasso.with(context).load(util.FileUrl + "uploads/" + util.COMPANY_LOGO).resize(80, 80)
                        .centerCrop().into(ivImage);
                new DownloadLogo().execute((util.FileUrl + "uploads/" + util.COMPANY_LOGO).trim());
            }
        } catch (Exception e) {
            //  Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_LONG).show();

        }
        try {
            userImage = util.USER_IMAGE;
            String ul = util.FileUrl + "uploads/" + util.USER_IMAGE;
            Log.i(TAG, ul);
            File imgFile2 = new File(root + "/" + util.USER_IMAGE);
            if (imgFile2.exists()) {
                Bitmap myBitmap2 = BitmapFactory.decodeFile(imgFile2.getAbsolutePath());
                // myBitmap2.setWidth(40);
                // myBitmap2.setHeight(40);
                UsrImage.setImageBitmap(myBitmap2);

            } else {
                Picasso.with(context).load(util.FileUrl + "uploads/" + util.USER_IMAGE).resize(50, 50)
                        .centerCrop().into(UsrImage);
                new DownloadImage().execute((util.FileUrl + "uploads/" + util.USER_IMAGE).trim());
            }
        } catch (Exception e) {
            //  Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_LONG).show();

        }

        btnPay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startLocation = new Intent(MainActivity.this, PayActivity.class);
                startActivity(startLocation);
                // finish();
            }
        });
        btnExp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startLocation = new Intent(MainActivity.this, ExpenseActivity.class);
                startActivity(startLocation);
                // finish();
            }
        });
        btnSession.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startLocation = new Intent(MainActivity.this, SessionActivity.class);
                startActivity(startLocation);
                // finish();
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startLocation = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(startLocation);
                //finish();
            }
        });
        logoutLink = (TextView) findViewById(R.id.link_logout);
        logoutLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        databaseHelper.deleteSession(util.SESSION_ID);
                        paymentHelper.deleteSession(util.SESSION_ID);
                        expenseHelper.deleteSession(util.SESSION_ID);

                        Intent myIntent = new Intent(MainActivity.this, StartActivity.class);
                        startActivity(myIntent);// Commit the edits!
                        finish();
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();



            }
        });
        cd = new ConnectionDetector(MainActivity.this);
        if (cd.isConnectingToInternet()) {
            //
            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    //Code that uses AsyncHttpClient in your case ConsultaCaract()
                    int SDK_INT = android.os.Build.VERSION.SDK_INT;
                    if (SDK_INT > 8) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                .permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        //uploadSession();
                        uploadPayment();
                        // uploadExpense();
                    }
                }
            };
            mainHandler.post(myRunnable);
        }
    }

    private class DownloadLogo extends AsyncTask<String, Void, Bitmap> {
        private String TAG = "DownloadImage";

        private Bitmap downloadImageBitmap(String sUrl) {
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(sUrl).openStream();   // Download Image from URL
                bitmap = BitmapFactory.decodeStream(inputStream);       // Decode Bitmap
                inputStream.close();
            } catch (Exception e) {
                Log.d(TAG, "Exception 1, Something went wrong!");
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadImageBitmap(params[0]);
        }

        protected void onPostExecute(Bitmap result) {
            saveImage(getApplicationContext(), result, imageName);
        }

        public void saveImage(Context context, Bitmap b, String imageName) {
            FileOutputStream foStream;
            try {
                foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
                b.compress(Bitmap.CompressFormat.PNG, 100, foStream);
                foStream.close();
            } catch (Exception e) {
                Log.d("saveImage", "Exception 2, Something went wrong!");
                e.printStackTrace();
            }
        }
    }

    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        private String TAG = "DownloadImage";

        private Bitmap downloadImageBitmap(String sUrl) {
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(sUrl).openStream();   // Download Image from URL
                bitmap = BitmapFactory.decodeStream(inputStream);       // Decode Bitmap
                inputStream.close();
            } catch (Exception e) {
                Log.d(TAG, "Exception 1, Something went wrong!");
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadImageBitmap(params[0]);
        }

        protected void onPostExecute(Bitmap result) {
            saveImage(getApplicationContext(), result, userImage);
        }

        public void saveImage(Context context, Bitmap b, String imageName) {
            FileOutputStream foStream;
            try {
                foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
                b.compress(Bitmap.CompressFormat.PNG, 100, foStream);
                foStream.close();
            } catch (Exception e) {
                Log.d("saveImage", "Exception 2, Something went wrong!");
                e.printStackTrace();
            }
        }
    }

    ArrayList<Payment> paymentList;

    public void uploadPayment() {
        // Locate the Route Class
        final PaymentHandler databaseHelper = new PaymentHandler(MainActivity.this);
        paymentList = new ArrayList<Payment>();
        paymentList = databaseHelper.Sync();
        for (int x = 0; x < paymentList.size(); x++) {
            System.out.println("EMAIL " + paymentList.get(x).getEmail());
            System.out.println("ROUTE " + util.SESSION_ROUTE);
            System.out.println("USER ID " + util.USER_ID);
            System.out.println("CONTACT " + paymentList.get(x).getContact());
            final String bar = paymentList.get(x).getBarcode();
            SyncHttpClient client = new SyncHttpClient();
            RequestParams parama = new RequestParams();

            parama.put("cost", paymentList.get(x).getCost().toString());
            parama.put("name", paymentList.get(x).getEmail().toString());
            parama.put("seat", paymentList.get(x).getSeat().toString());
            parama.put("contact", paymentList.get(x).getContact().toString());
          //  parama.put("routeID", paymentList.get(x).getRoute().toString());
            parama.put("routeID","route");
           // parama.put("bus", paymentList.get(x).getBus().toString());
            parama.put("bus", "GH123J");
            parama.put("date", paymentList.get(x).getDate().toString());
            parama.put("device", "true");
            parama.put("barcode", paymentList.get(x).getBarcode());
            parama.put("luggage", paymentList.get(x).getLuggage());
            parama.put("companyID", util.COMPANY_ID.toString());
            parama.put("sessionID", "not applicable");
            parama.put("userID", util.USER_ID.toString());
            client.post(util.Url + "payment/create", parama, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                    String ret = new String(responseBody);
                    try {

                        JSONObject j = new JSONObject(ret);
                        if (j.get("status").toString().equals("true")) {
                            String ANS = databaseHelper.updateSync(bar);
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
