package com.vuga.paybus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.journeyapps.barcodescanner.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class NewActivity extends AppCompatActivity {
    Context context = this;
    ArrayList<Bus> busItems = new ArrayList<Bus>();
    ArrayAdapter busAdapter;
    ListView busListView;
    File fileBus;
    File fileRoute;
    SessionHandler db;
    static String busFile = "bus.json";
    static String routeFile = "route.json";
    ArrayList<String> routeList;
    ArrayList<Route> route;
    ArrayList<String> busList;
    ArrayList<Bus> buses;
    private TextView txtcost,  txtstart, txtname, txtseats, txtBus, txtroute,txtID;
    private Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        txtname = (TextView) findViewById(R.id.name);
        txtseats = (TextView) findViewById(R.id.seats);
        txtcost = (TextView) findViewById(R.id.cost);
        txtstart = (TextView) findViewById(R.id.start);
        txtroute = (TextView) findViewById(R.id.route);
        txtBus = (TextView) findViewById(R.id.bus);
        txtID = (TextView) findViewById(R.id.ids);
        submitBtn = (Button) findViewById(R.id.btnSubmit);

        fileBus = new File(NewActivity.this.getFilesDir().getPath() + "/" + busFile);
        if (fileBus.exists()) {
            try {
                LoadBus();
            } catch (Exception ex) {

            }
        } else {
            Toast.makeText(NewActivity.this, "No bus list !", Toast.LENGTH_LONG).show();
        }
        fileRoute = new File(NewActivity.this.getFilesDir().getPath() + "/" + routeFile);
        if (fileRoute.exists()) {
            try {
                LoadRoute();
            } catch (Exception ex) {

            }
        } else {
            Toast.makeText(NewActivity.this, "No Route list !", Toast.LENGTH_LONG).show();
        }
        Spinner mySpinner = (Spinner) findViewById(R.id.input_bus);
        // Spinner adapter
        mySpinner.setAdapter(new ArrayAdapter<String>(NewActivity.this,
                android.R.layout.simple_spinner_dropdown_item, busList));
        // Spinner on item click listener
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

                txtname.setText("NAME " + buses.get(position).getName());
                txtseats.setText( buses.get(position).getSeat());
                txtBus.setText(buses.get(position).getNoPlate());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        /***ROUTE MANAGEMENT**/
        Spinner myRoute = (Spinner) findViewById(R.id.input_route);
        // Spinner adapter
        myRoute.setAdapter(new ArrayAdapter<String>(NewActivity.this,
                android.R.layout.simple_spinner_dropdown_item, routeList));
        // Spinner on item click listener
        myRoute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

                txtstart.setText("SET OFF TIME: " + route.get(position).getStart_time());
                txtroute.setText(route.get(position).getName());
                txtcost.setText(route.get(position).getCost());
              //  txtID.setText(route.get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Save()) {
                    Toast.makeText(NewActivity.this, "New session", Toast.LENGTH_LONG).show();
                    Intent startLocation = new Intent(NewActivity.this, MainActivity.class);
                    startActivity(startLocation);
                    finish();
                }
            }
        });    }

    private boolean Save() {
        final Random r = new Random();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());
        SessionHandler db = new SessionHandler(this);
        Toast.makeText(NewActivity.this,"ROUTE "+ txtroute.getText() +" "+ txtBus.getText(), Toast.LENGTH_LONG).show();
        db.addSession(new Session("PB-" + formattedDate + "-" + Integer.toString(r.nextInt(50) + 1), formattedDate, txtroute.getText().toString(), txtseats.getText().toString(), "NEW", "f", txtBus.getText().toString(), txtcost.getText().toString()));
        util.SESSION_ID = "PB-" + formattedDate + "-" + Integer.toString(r.nextInt(50) + 1);
        util.SESSION_ROUTE = txtroute.getText().toString();
        util.SESSION_BUS = txtBus.getText().toString();
        util.SESSION_COST = txtcost.getText().toString();
        util.MAX_SEATS = txtseats.getText().toString();
        util.SESSION_ROUTE_ID = txtID.getText().toString();
        util.SESSION_TIME = txtstart.getText().toString();

        return true;
    }

    private void LoadBus() {

        buses = new ArrayList<Bus>();
        busList = new ArrayList<String>();

        String json = getData(busFile);
        try {
            JSONObject jsonRootObject = new JSONObject(json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1));

            JSONArray jsonArray = jsonRootObject.optJSONArray("buses");
            //Iterate the jsonArray and print the info of JSONObjects
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Bus v = new Bus();

                v.setCount(jsonObject.optInt("count"));
                v.setName(jsonObject.optString("name").toString());
                // String[] startArray = jsonObject.optString("start").toString().split("T");
                v.setNoPlate(jsonObject.optString("noPlate").toString());
                // String[] endArray = jsonObject.optString("end").toString().split("T");
                v.setSeat(jsonObject.optString("seat").toString());
                buses.add(v);
                busList.add(jsonObject.optString("noPlate") + "");
                //   Toast.makeText(NewActivity.this, "Looping "+ jsonObject.optString("noPlate"), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            System.out.print("data sync Error" + e);
            e.printStackTrace();
        }
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
            File f = new File(NewActivity.this.getFilesDir().getPath() + "/" + fileName);
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


}
