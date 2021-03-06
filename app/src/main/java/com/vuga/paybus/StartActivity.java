package com.vuga.paybus;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

public class StartActivity extends AppCompatActivity {
    private ConnectionDetector cd;
    Button btnNew;
    Button btnPay, btnScan, btnSession, btnExp, btnComplete;

    SessionAdapter adapter;
    ListView sessionView;
    ArrayList<Session> sessionList;
    static TextView logoutLink, username, companyname = null, payments, expenses, unsync_payment, unsync_expense;
    File file;
    private ImageView ivImage, UsrImage;
    Context context = this;
    String imageName, userImage;
    ArrayList<Expense> expenseList;
    ArrayList<Expense> expensesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        btnNew = (Button) findViewById(R.id.newBtn);
        btnExp = (Button) findViewById(R.id.buttonExp);
        btnScan = (Button) findViewById(R.id.buttonScan);
        btnSession = (Button) findViewById(R.id.buttonSession);
        btnComplete = (Button) findViewById(R.id.complete);
        PaymentHandler databaseHelper = new PaymentHandler(StartActivity.this);
        try {
            LoadRoute();
        } catch (Exception c) {


        }
      /*  final Random r = new Random();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        SessionHandler db = new SessionHandler(this);
        db.addSession(new Session("PB-" + formattedDate + "-" + Integer.toString(r.nextInt(50) + 1), formattedDate,"", "", "NEW", "false",""));
*/
        logoutLink = (TextView) findViewById(R.id.link_logout);
        logoutLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Logging out", Toast.LENGTH_LONG).show();
                SharedPreferences myPrefs = getSharedPreferences(util.PREFS_NAME, 0);
                SharedPreferences.Editor editor = myPrefs.edit();
                editor.clear();
                editor.commit();

                Intent myIntent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(myIntent);// Commit the edits!
                finish();
            }
        });
        btnExp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startLocation = new Intent(StartActivity.this, ExpenseActivity.class);
                startActivity(startLocation);
                // finish();
            }
        });
        btnSession.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startLocation = new Intent(StartActivity.this, SessionActivity.class);
                startActivity(startLocation);
                // finish();
            }
        });


        btnScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startLocation = new Intent(StartActivity.this, ScanActivity.class);
                startActivity(startLocation);
                //finish();
            }
        });
        databaseHelper = new PaymentHandler(StartActivity.this);
        final ExpenseHandler expenseHelper = new ExpenseHandler(StartActivity.this);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        UsrImage = (ImageView) findViewById(R.id.UsrImage);
        username = (TextView) findViewById(R.id.username);
        companyname = (TextView) findViewById(R.id.companyname);

        paymentList = new ArrayList<Payment>();
        paymentList = databaseHelper.Sync();
        paymentsList = new ArrayList<Payment>();
        paymentsList = databaseHelper.getWhere();
        double sum_pay = 0;
        for (Payment p : paymentsList) {
            sum_pay += Double.parseDouble(p.getCost());
        }
        payments = (TextView) findViewById(R.id.payments);
        unsync_payment = (TextView) findViewById(R.id.payment_sync);
        payments.setText("Payments: " + paymentsList.size() + " :" + sum_pay);
        unsync_payment.setText("Unsynchronised Payments: " + paymentList.size());

        expenseList = new ArrayList<Expense>();
        expenseList = expenseHelper.Sync();
        expensesList = new ArrayList<Expense>();
        expensesList = expenseHelper.getWhere();
        expenses = (TextView) findViewById(R.id.expenses);
        unsync_expense = (TextView) findViewById(R.id.expense_sync);
        double sum_exp = 0;
        for (Expense e : expensesList) {
            sum_exp += Double.parseDouble(e.getTotal());
        }
        expenses.setText("Expenses: " + expensesList.size() + " :" + sum_exp);
        unsync_expense.setText("Unsynchronised Expenses: " + expenseList.size());

        btnComplete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (expenseList.size() > 1) {
                    Toast.makeText(StartActivity.this, "You have unsynchronised expense information ", Toast.LENGTH_LONG).show();
                    return;
                } else if (paymentList.size() > 1) {
                    Toast.makeText(StartActivity.this, "You have unsynchronised payment information ", Toast.LENGTH_LONG).show();
                    return;
                } else {

                    if (cd.isConnectingToInternet()) {
                        Complete();
                        Intent startLocation = new Intent(StartActivity.this, StartActivity.class);

                        startActivity(startLocation);
                        finish();
                    }
                    else {
                        Toast.makeText(StartActivity.this, "This action requires an internet connection ", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        String root = getApplicationContext().getFilesDir().toString();
        //  Log.i(TAG,  util.COMPANY_LOGO);
        username.setText(util.USER_NAME);
        companyname.setText(util.COMPANY);

        cd = new ConnectionDetector(this);
        if (cd.isConnectingToInternet()) {
            Buses();
            Routes();

        }
        btnNew.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startLocation = new Intent(StartActivity.this, PayActivity.class);
                startActivity(startLocation);
                //finish();
            }
        });
        /*sessionView = (ListView) findViewById(R.id.listView1);
        SessionHandler databaseHelper = new SessionHandler (StartActivity.this);
        sessionList = new ArrayList<Session>();

        sessionList = databaseHelper.getAllSessions();
        adapter = new SessionAdapter(StartActivity.this, sessionList);
        sessionView.setAdapter(adapter);
        sessionView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // txtcost.setText(sessionList.get(position).getSessionID());
                Toast.makeText(StartActivity.this, " " + sessionList.get(position).getSessionID(), Toast.LENGTH_LONG).show();
                util.SESSION_ID = sessionList.get(position).getSessionID();
                util.SESSION_ROUTE = sessionList.get(position).getRoute();
                util.SESSION_BUS = sessionList.get(position).getBus();
                util.SESSION_COST = sessionList.get(position).getCost();
                util.MAX_SEATS = sessionList.get(position).getSeats();
                for(Route d : route){
                    if(d.getName().contains(util.SESSION_ROUTE)) {
                        util.SESSION_TIME = d.getStart_time();
                    }
                }


                // util.SESSION_ROUTE_ID = sessionList.get(position).get();

                Intent startLocation = new Intent(StartActivity.this, MainActivity.class);
                startActivity(startLocation);

            }
        });*/
        cd = new ConnectionDetector(StartActivity.this);
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
                        //   uploadSession();

                    }
                }
            };
            mainHandler.post(myRunnable);
        }
        try {
            imageName = util.COMPANY_LOGO;
            String ul = util.FileUrl + "uploads/" + util.COMPANY_LOGO;

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
        cd = new ConnectionDetector(StartActivity.this);
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

    static String routeFile = "route.json";
    ArrayList<String> routeList;
    ArrayList<Route> route;

    private void LoadRoute() {

        route = new ArrayList<Route>();
        routeList = new ArrayList<String>();

        String json = getData(routeFile);
        try {
            JSONObject jsonRootObject = new JSONObject(json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1));

            JSONArray jsonArray = jsonRootObject.optJSONArray("routes");
            //Iterate the jsonArray and print the info of JSONObjects
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Route v = new Route();
                v.setName(jsonObject.optString("name").toString());
                // String[] startArray = jsonObject.optString("start").toString().split("T");
                v.setCost(jsonObject.optString("cost").toString());
                // String[] endArray = jsonObject.optString("end").toString().split("T");
                v.setStart_time(jsonObject.optString("start").toString());
                v.setEnd_time(jsonObject.optString("end").toString());
                route.add(v);
                routeList.add(jsonObject.optString("name") + "");
                //   Toast.makeText(NewActivity.this, "Looping "+ jsonObject.optString("noPlate"), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            System.out.print("data sync Error" + e);
            e.printStackTrace();
        }
    }

    public String getData(String fileName) {
        try {
            File f = new File(StartActivity.this.getFilesDir().getPath() + "/" + fileName);
            //check whether file exists
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer);
        } catch (IOException e) {
            Log.e("TAG", "Error in Reading: " + e.getLocalizedMessage());
            return null;
        }
    }

    ArrayList<Session> sessionLists;

    public void Complete() {
        // Locate the Route Class
       final PaymentHandler databaseHelper = new PaymentHandler(StartActivity.this);
        paymentsList = new ArrayList<Payment>();
        paymentsList = databaseHelper.getWhere();
        double sum_pay = 0;
        for (Payment p : paymentsList) {
            sum_pay += Double.parseDouble(p.getCost());
        }
      final   ExpenseHandler expenseHelper = new ExpenseHandler(StartActivity.this);

        expensesList = new ArrayList<Expense>();
        expensesList = expenseHelper.getWhere();

        double sum_exp = 0;
        for (Expense e : expensesList) {
            sum_exp += Double.parseDouble(e.getTotal());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy/HH:mm");
        String currentDateandTime = sdf.format(new Date());
        String initial = util.COMPANY.substring(0, Math.min(util.COMPANY.length(), 3)) + "-" + currentDateandTime;

        SyncHttpClient client = new SyncHttpClient();
        RequestParams parama = new RequestParams();

        parama.put("sessionID", initial);
        parama.put("companyID", util.COMPANY_ID.toString());
        parama.put("payments", sum_pay + "");
        parama.put("expenses", sum_exp + "");
        parama.put("payment_counts", paymentsList.size() + "");
        parama.put("expense_counts", expensesList.size() + "");
        parama.put("device", "true");
        parama.put("userID", util.USER_ID.toString());

        client.post(util.Url + "sessions/create", parama, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                String ret = new String(responseBody);
                try {

                    JSONObject j = new JSONObject(ret);
                    if (j.get("status").toString().equals("true")) {
                                databaseHelper.delete();
                                expenseHelper.delete();
                        Toast.makeText(getApplicationContext(), " " + j.get("Info") + "", Toast.LENGTH_LONG).show();


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

    ArrayList<Payment> paymentList;
    ArrayList<Payment> paymentsList;

    public void uploadPayment() {
        // Locate the Route Class
        final PaymentHandler databaseHelper = new PaymentHandler(StartActivity.this);
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
            parama.put("name", paymentList.get(x).getName().toString());
            parama.put("seat", paymentList.get(x).getSeat().toString());
            parama.put("contact", paymentList.get(x).getContact().toString());
            parama.put("routeID", paymentList.get(x).getRoute().toString());
            //  parama.put("routeID","route");
            parama.put("bus", paymentList.get(x).getBus().toString());
            //parama.put("bus", "GH123J");
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

    private void Routes() {

        final ProgressDialog progressDialog = new ProgressDialog(StartActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Getting Route list..");
        progressDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        // params.put("name", util.USER_NAME);
        params.put("companyID", util.COMPANY_ID);

        client.post(util.Url + "mobile/routes", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);

                Log.d("data string", String.valueOf(result));
                //  details.setText("UPCOMING EVENTS ON TIME SHEET:" + "\n");
                saveData(StartActivity.this, result, "route.json");
                //Toast.makeText(this, " " + result, Toast.LENGTH_LONG).show();
                progressDialog.cancel();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                if (statusCode == 404) {
                    Toast.makeText(StartActivity.this, "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(StartActivity.this, "Something went wrong at server end", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(StartActivity.this, "Error:" + statusCode + error.getMessage(), Toast.LENGTH_LONG).show();

                }
                progressDialog.cancel();
            }

        });
    }

    private void Buses() {

        final ProgressDialog progressDialog = new ProgressDialog(StartActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Getting Bus list.........");
        progressDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        // params.put("name", util.USER_NAME);
        // params.put("orgID", util.ORG_ID);

        client.post(util.Url + "mobile/bus", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                Log.d("data string", String.valueOf(result));
                //  details.setText("UPCOMING EVENTS ON TIME SHEET:" + "\n");
                saveData(StartActivity.this, result, "bus.json");
                //   Toast.makeText(StartActivity.this, " " + result, Toast.LENGTH_LONG).show();
                progressDialog.cancel();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                if (statusCode == 404) {
                    Toast.makeText(StartActivity.this, "Requested resource not found", Toast.LENGTH_LONG).show();

                } else if (statusCode == 500) {
                    Toast.makeText(StartActivity.this, "Something went wrong at server end", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(StartActivity.this, "Error:" + statusCode + error.getMessage(), Toast.LENGTH_LONG).show();
                }
                progressDialog.cancel();
            }

        });
    }

    public static void saveData(Context context, String mJsonResponse, String fileName) {
        try {
            FileWriter file = new FileWriter(context.getFilesDir().getPath() + "/" + fileName);

            file.write("");
            file.write(mJsonResponse);
            file.flush();
            file.close();
        } catch (IOException e) {
            Log.e("TAG", "Error in Writing: " + e.getLocalizedMessage());
        }

    }

}
