package com.vuga.paybus;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.journeyapps.barcodescanner.Util;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PayActivity extends AppCompatActivity {
    private TextView mDateDisplay, txtcost, txttotal, txtstop, txtID, txtname, txtcontact, t, txtstatus, txtNo, txtlagguage;
    private Button mPickDate, btnSubmit;
    private ImageView bar, logo;
    private int mYear;
    private int mMonth;
    private int mDay;
    String barcode, seatNo;
    private String thisday;
    static final int DATE_DIALOG_ID = 0;
    JSONObject jsonobject;
    JSONArray jsonarray;
    ProgressDialog mProgressDialog;
    ArrayList<String> worldlist;
    ArrayList<Route> world;
    private ConnectionDetector cd;
    boolean canEnter = false;
    ArrayList<Payment> paymentList;
    PaymentAdapter adapter;
    static String routeFile = "route.json";
    ArrayList<String> routeList;
    ArrayList<Route> route;
    File fileRoute;


    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    TextView myLabel;
    EAN13CodeBuilder bb;
    private ImageView ivImage;
    Context context = this;

    // will enable user to enter any text to be printed
    EditText myTextbox;
    List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        cd = new ConnectionDetector(PayActivity.this);
        txtcost = (TextView) findViewById(R.id.cost);
        txttotal = (TextView) findViewById(R.id.total);
        txtstatus = (TextView) findViewById(R.id.status);

        txtNo = (TextView) findViewById(R.id.seatNo);
        txtlagguage = (TextView) findViewById(R.id.luggage);
        txtlagguage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                txttotal.setText((Double.parseDouble(txtcost.getText() + "") + Double.parseDouble(txtlagguage.getText() + "")) + "");
            }
        });
        fileRoute = new File(PayActivity.this.getFilesDir().getPath() + "/" + routeFile);
        if (fileRoute.exists()) {
            try {
                LoadRoute();
            } catch (Exception ex) {

            }
        } else {
            Toast.makeText(PayActivity.this, "No Route list !", Toast.LENGTH_LONG).show();
        }
        /***ROUTE MANAGEMENT**/
        Spinner myRoute = (Spinner) findViewById(R.id.input_route);
        // Spinner adapter
        myRoute.setAdapter(new ArrayAdapter<String>(PayActivity.this,
                android.R.layout.simple_spinner_dropdown_item, routeList));
        // Spinner on item click listener
        myRoute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

                txtcost.setText(route.get(position).getCost());
                //  txtID.setText(route.get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        txtname = (TextView) findViewById(R.id.input_name);
        txtcontact = (TextView) findViewById(R.id.input_contact);
        t = (TextView) findViewById(R.id.barCode);
        t.buildDrawingCache();
        bar = (ImageView) findViewById(R.id.bar);
        logo = (ImageView) findViewById(R.id.logo);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        logo.setBackgroundColor(Color.parseColor("#FFFFFF"));
        //String barcode="1234567891237"; //barcode must be 13 digit
        long number = (long) Math.floor(Math.random() * 9000000000000L) + 1000000000000L;
        barcode = Long.toString(number);
        // barcode="9310779300005";
        EAN13 code = new EAN13(barcode);
        Toast.makeText(PayActivity.this, barcode + "", Toast.LENGTH_LONG).show();
        // Bitmap bitmap = code.getBitmap(3400, 1400);
        Bitmap bitmap = code.getBitmap(3500, 1400);
        // bar.setScaleType(ImageView.ScaleType.FIT_XY);
        bar.setImageBitmap(bitmap);
        // we are goin to have three buttons for specific functions
        Button openButton = (Button) findViewById(R.id.open);
        Button closeButton = (Button) findViewById(R.id.close);
        myLabel = (TextView) findViewById(R.id.label);


        mDateDisplay = (TextView) findViewById(R.id.date);
        mPickDate = (Button) findViewById(R.id.datepicker);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        String root = getApplicationContext().getFilesDir().toString();
        try {

            String ul = util.FileUrl + "uploads/" + util.COMPANY_LOGO;

            File imgFile = new File(root + "/" + util.COMPANY_LOGO);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ivImage.setImageBitmap(myBitmap);
            }

        } catch (Exception e) {
            //  Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_LONG).show();

        }

        //PickDate's click event listener
        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (Integer.parseInt(txtNo.getText().toString()) > Integer.parseInt(util.MAX_SEATS)) {
                        txtNo.setError("Invalid seat number ,Value greater than allocated seat count ");
                        txtNo.requestFocus();
                        return;
                    }
                } catch (Exception p) {
                    txtNo.setError("Invalid seat number ,Value greater than allocated seat count ");
                    txtNo.requestFocus();
                    return;
                }
                if (exist(list, txtNo.getText().toString())) {
                    txtNo.setError(" SEAT OCCUPIED ");
                    txtNo.requestFocus();
                    return;
                } else {

                    if (Save()) {
                        try {
                            findBT();
                            openBT();
                        } catch (IOException ex) {
                        }
                        try {
                            sendData();
                        } catch (IOException ex) {
                        }
                        Intent startLocation = new Intent(PayActivity.this, MainActivity.class);
                        startActivity(startLocation);
                        finish();
                    }
                       /* Log.d("INTERNET",util.isInternetAvailable()+"" );
                        Register();
                        */


                }
            }
        });

        openButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    findBT();
                    openBT();
                } catch (IOException ex) {
                }
            }
        });
        // send data typed by the user to be printed
        // close bluetooth connection
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    closeBT();
                } catch (IOException ex) {
                }
            }
        });

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        // set barcode font for TextView.
        // ttf file must be placed is assets/fonts
        // Typeface font = Typeface.createFromAsset(this.getAssets(),"fonts/EanP72Tt Normal.Ttf");
        Typeface font = Typeface.createFromAsset(this.getAssets(), "fonts/eang000-244.ttf");
        t.setTypeface(font);
        // generate barcode string
        bb = new EAN13CodeBuilder("124958761310");
        t.setText(bb.getCode());
        // bar.setImageBitmap(t.getDrawingCache());
        // iv.setImageBitmap(tv1.getDrawingCache());
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        mDateDisplay.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(today.monthDay).append("-")
                        .append(today.month + 1).append("-")
                        .append(today.year).append(""));
        thisday = (today.monthDay) + "-" + (today.month + 1) + "-" + today.year + "";
        //    new DownloadJSON().execute();
        txtcost.setText(util.SESSION_COST);
        list = new ArrayList<String>();
        Status();

    }

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
            File f = new File(PayActivity.this.getFilesDir().getPath() + "/" + fileName);
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

    public boolean exist(List<String> e, String term) {

        String search = term;
        for (String str : e) {
            if (str.trim().contains(search))
                return true;
        }
        return false;

    }

    private void Status() {

        PaymentHandler databaseHelper = new PaymentHandler(PayActivity.this);
        paymentList = new ArrayList<Payment>();
        paymentList = databaseHelper.getWhere(util.SESSION_ID);
        String values = "";
        int occupied = paymentList.size();
        int balance = Integer.parseInt(util.MAX_SEATS) - occupied;
        values = ("OCCUPIED : " + occupied + " BALANCE SEATS :" + balance + "\n");
        for (int x = 0; x < paymentList.size(); x++) {
            System.out.println(paymentList.get(x).getSeat());
            values = values + (paymentList.get(x).getEmail() + " SEAT" + paymentList.get(x).getSeat() + "\n");
            list.add(paymentList.get(x).getSeat());
        }
        txtstatus.setText(values);
    }

    private void updateDate() {
        // bar.setImageBitmap(t.getDrawingCache());
        mDateDisplay.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(mDay).append("-")
                        .append(mMonth + 1).append("-")
                        .append(mYear).append(""));
        thisday = (mDay) + "-" + (mMonth + 1) + "-" + mYear + "";
        new DownloadJSON().execute();
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    //Datepicker dialog generation
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDate();
                }
            };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
        }
        return null;
    }

    private class DownloadJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // Locate the Route Class
            world = new ArrayList<Route>();
            // Create an array to populate the spinner
            worldlist = new ArrayList<String>();
            // JSON file URL address
            jsonobject = JSONfunctions.getJSONfromURL(util.Url + "route/arraylist/" + util.COMPANY_ID + "/" + thisday);
            Log.d("url:", util.Url + "route/arraylist/" + util.COMPANY_ID + "/" + thisday);
            try {
                // Locate the NodeList name
                jsonarray = jsonobject.getJSONArray("routes");
                for (int i = 0; i < jsonarray.length(); i++) {
                    jsonobject = jsonarray.getJSONObject(i);
                    Route route = new Route();
                    route.setName(jsonobject.optString("name") + "-" + jsonobject.optString("start_time"));
                    route.setCost(jsonobject.optString("cost"));
                    route.setStart(jsonobject.optString("start") + " TIME:" + jsonobject.optString("start_time"));
                    route.setStop(jsonobject.optString("stop"));
                    route.setSeat(jsonobject.optString("seat"));
                    route.setStart_time(jsonobject.optString("start_time"));
                    route.setEnd_time(jsonobject.optString("end_time"));
                    route.setId(jsonobject.optInt("id"));
                    world.add(route);
                    // Populate spinner with country names
                    worldlist.add(jsonobject.optString("name") + " TIME:" + jsonobject.optString("start_time"));
                    Log.d("Info:", jsonobject.optString("name") + "-" + jsonobject.optString("cost"));

                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            // Locate the spinner in activity_main.xml
            Spinner mySpinner = (Spinner) findViewById(R.id.input_route);
            // Spinner adapter
        }
    }

    private boolean Save() {
        PaymentHandler db = new PaymentHandler(this);
        db.addPayment(new Payment(barcode, mDateDisplay.getText().toString(), txtcontact.getText().toString(), txtcost.getText().toString(), thisday.toString(), txtNo.getText().toString(), txtcontact.getText().toString(), txtname.getText().toString(), "f", util.SESSION_ID, txtlagguage.getText().toString()));

        return true;
    }

    private boolean Register() {

        btnSubmit.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(PayActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Submitting information...");
        progressDialog.show();
        btnSubmit.setVisibility(View.GONE);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // IMEI = telephonyManager.getDeviceId();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("cost", txtcost.getText().toString());
        params.put("name", txtname.getText().toString());
        params.put("seat", txtNo.getText().toString());
        params.put("contact", txtcontact.getText().toString());
        params.put("routeID", txtID.getText());
        params.put("date", mDateDisplay.getText());
        params.put("device", "true");
        params.put("barcode", barcode);
        params.put("companyID", util.COMPANY_ID);
        client.post(util.Url + "payment/create", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                String ret = new String(responseBody);
                try {

                    JSONObject j = new JSONObject(ret);
                    if (j.get("status").toString().equals("true")) {
                        Toast.makeText(getApplicationContext(), " " + j.get("info").toString(), Toast.LENGTH_LONG).show();
                        try {
                            Intent startLocation = new Intent(PayActivity.this, MainActivity.class);
                            startActivity(startLocation);
                            finish();

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_LONG).show();
                            btnSubmit.setVisibility(View.VISIBLE);
                            progressDialog.cancel();
                        }
                    } else {

                        Toast.makeText(getApplicationContext(), " " + j.get("info").toString() + "", Toast.LENGTH_LONG).show();
                        btnSubmit.setVisibility(View.VISIBLE);
                        btnSubmit.setEnabled(true);
                        progressDialog.cancel();
                    }
                    //  Toast.makeText(getApplicationContext(), "registration successful", Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    // TODO Auto-generated catch block

                    System.out.print("data sync Error" + e);

                    System.out.print(ret);
                    e.printStackTrace();
                    progressDialog.cancel();
                    btnSubmit.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                    btnSubmit.setVisibility(View.VISIBLE);
                    btnSubmit.setEnabled(true);
                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    btnSubmit.setVisibility(View.VISIBLE);
                    btnSubmit.setEnabled(true);
                } else {
                    Toast.makeText(getApplicationContext(), "Error:" + statusCode + error.getMessage(), Toast.LENGTH_LONG).show();
                    btnSubmit.setVisibility(View.VISIBLE);
                    btnSubmit.setEnabled(true);
                }
            }
        });
        return canEnter;
    }

    // This will find a bluetooth printer device
    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                myLabel.setText("No bluetooth adapter available");
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                    .getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // MP300 is the name of the bluetooth printer device
                    if (device.getName().equals("InnerPrinter")) {
                        mmDevice = device;
                        break;
                    }
                }
            }
            myLabel.setText("Bluetooth Device Found:" + mmDevice);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Tries to open a connection to the bluetooth printer device
    void openBT() throws IOException {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();

            myLabel.setText("Bluetooth Opened");
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // After opening a connection to bluetooth printer device,
// we have to listen and check if a data were sent to be printed.
    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // This is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted()
                            && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length);
                                        final String data = new String(
                                                encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        handler.post(new Runnable() {
                                            public void run() {
                                                myLabel.setText(data);
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * This will send data to be printed by the bluetooth printer
     */
    void sendData() throws IOException {
        /// senddatatodevice();
        try {
            // the text typed by the user
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String currentDateandTime = sdf.format(new Date());
            String msg = "";
            printTitle("RECEIPT");
            printTitle(util.COMPANY);
            printCompanyLogo();
            printPhotoLogo();

            printPhoto();
            printText(barcode);
            printNewLine();

            printNewLine();
            resetPrint();
            msg += "TEL:" + txtcontact.getText().toString() + "\n";
            msg += "NAME:" + txtname.getText().toString() + "\n";
            msg += "BUS :" + util.SESSION_BUS + "\n";
            msg += "TO: " + util.SESSION_ROUTE + " " + "\n";
            msg += util.SESSION_TIME + "\n";
            msg += "COST: " + txtcost.getText() + "\n";
            msg += "LUGGAGE COST: " + txtlagguage.getText() + "\n";
            msg += "TOTAL COST: " + txttotal.getText() + "\n";
            msg += "DATE:" + mDateDisplay.getText().toString() + " SEAT No:" + txtNo.getText() + "\n";
            msg += "-----------------------" + "\n";
            msg += "T.ID:" + barcode + " " + "\n";
            msg += "------------------------" + "\n";
            msg += "NO REFUND" + "\n";
            msg += "------------------------" + "\n";
            msg += "SERVED BY : " + util.USER_NAME + "\n";
            msg += " " + currentDateandTime + " " + "\n";
            msg += "POWERED BY PAYBUS" + "\n";
            msg += "" + "\n";
            msg += "" + "\n";
            msg += "" + "\n";
            msg += "" + "\n";
            mmOutputStream.write(msg.getBytes());

            // tell the user data were sent
            myLabel.setText("Reciept printed");

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void resetPrint() {
        try {
            mmOutputStream.write(PrinterCommands.ESC_FONT_COLOR_DEFAULT);
            mmOutputStream.write(PrinterCommands.FS_FONT_ALIGN);
            mmOutputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
            mmOutputStream.write(PrinterCommands.ESC_CANCEL_BOLD);
            mmOutputStream.write(PrinterCommands.LF);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //print photo
    public void printPhoto() {
        try {
            //t.setText(bb.getCode());
            //t.buildDrawingCache();
            //bar.setImageBitmap(t.getDrawingCache());

            bar.buildDrawingCache();
            Bitmap bmp = bar.getDrawingCache();
            // Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            // Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

            if (bmp != null) {
                byte[] command = Utils.decodeBitmap(bmp);
                printText(command);
            } else {
                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }

    public void printCompanyLogo() {
        try {

            ivImage.buildDrawingCache();
            ivImage.setBackgroundColor(Color.parseColor("#FFFFFF"));
            Bitmap bmp = ivImage.getDrawingCache();
            // Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            // Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            if (bmp != null) {
                byte[] command = Utils.decodeBitmap(bmp);
                printText(command);
            } else {
                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }

    public void printPhotoLogo() {
        try {

            logo.buildDrawingCache();
            logo.setBackgroundColor(Color.parseColor("#FFFFFF"));
            Bitmap bmp = logo.getDrawingCache();
            // Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            // Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            if (bmp != null) {
                byte[] command = Utils.decodeBitmap(bmp);
                printText(command);
            } else {
                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }

    private void printTitle(String msg) {
        try {
            //Print config
            byte[] bb = new byte[]{0x1B, 0x21, 0x08};
            byte[] bb2 = new byte[]{0x1B, 0x21, 0x20};
            byte[] bb3 = new byte[]{0x1B, 0x21, 0x10};
            byte[] cc = new byte[]{0x1B, 0x21, 0x00};

            //btoutputstream.write(bb);
            //btoutputstream.write(bb2);
            mmOutputStream.write(bb3);

            //set text into center
            mmOutputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
            mmOutputStream.write(msg.getBytes());
            mmOutputStream.write(PrinterCommands.LF);
            mmOutputStream.write(PrinterCommands.LF);
            mmOutputStream.write(cc);
            // printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void printText(String msg) {
        try {
            // Print normal text
            mmOutputStream.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void printNewLine() {
        try {
            mmOutputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //print byte[]
    private void printText(byte[] msg) {
        try {
            // Print normal text
            mmOutputStream.write(msg);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            myLabel.setText("Bluetooth Closed");
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
