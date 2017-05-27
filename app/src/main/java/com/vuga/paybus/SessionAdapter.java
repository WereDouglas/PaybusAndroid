package com.vuga.paybus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by DOUGLAS on 27/03/2017.
 */
public class SessionAdapter extends BaseAdapter {
    Context context;
    ArrayList<Session> empList;
    private static LayoutInflater inflater = null;

    public SessionAdapter(Context context, ArrayList<Session> empList) {
        this.context = context;
        this.empList = empList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return empList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null)
            convertView = inflater.inflate(R.layout.session_grid, null);

        TextView sessionIDTextView = (TextView) convertView.findViewById(R.id.sessionID);
        TextView dateTextView = (TextView) convertView.findViewById(R.id.date);
        TextView routeTextView = (TextView) convertView.findViewById(R.id.route);
        TextView seatsTextView = (TextView) convertView.findViewById(R.id.seats);
        TextView statusTextView = (TextView) convertView.findViewById(R.id.status);
        TextView syncTextView = (TextView) convertView.findViewById(R.id.sync);
        TextView busTextView = (TextView) convertView.findViewById(R.id.bus);
        TextView costTextView = (TextView) convertView.findViewById(R.id.cost);


        Session e = new Session();
        e = empList.get(position);
        sessionIDTextView.setText("ID: " + String.valueOf(e.getSessionID()));
        dateTextView.setText(" DATE: " + e.getDate());
        routeTextView.setText(" ROUTE: " + e.getRoute());
        seatsTextView.setText(" SEATS " + e.getSeat());
        statusTextView.setText(" STATUS: " + e.getStatus());
        syncTextView.setText(" SYNC: " + e.getSync());
        busTextView.setText("BUS: " + e.getBus());
        costTextView.setText(" COST: " + e.getCost());
        return convertView;
    }

}