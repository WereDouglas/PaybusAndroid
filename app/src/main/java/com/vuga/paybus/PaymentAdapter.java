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
public class PaymentAdapter extends BaseAdapter {
    Context context;
    ArrayList<Payment> empList;
    private static LayoutInflater inflater = null;

    public PaymentAdapter(Context context, ArrayList<Payment> empList) {
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
            convertView = inflater.inflate(R.layout.payment_grid, null);

        TextView barcodeTextView = (TextView) convertView.findViewById(R.id.barCode);
        TextView dateTextView = (TextView) convertView.findViewById(R.id.date);
        TextView contactTextView = (TextView) convertView.findViewById(R.id.contact);
        TextView costTextView = (TextView) convertView.findViewById(R.id.cost);
        TextView createdTextView = (TextView) convertView.findViewById(R.id.created);
        TextView seatTextView = (TextView) convertView.findViewById(R.id.seat);
        TextView emailTextView = (TextView) convertView.findViewById(R.id.email);
        TextView nameTextView = (TextView) convertView.findViewById(R.id.name);
        TextView lugTextView = (TextView) convertView.findViewById(R.id.luggage);

        Payment e = new Payment();
        e = empList.get(position);
        barcodeTextView.setText(" BARCODE: "+ String.valueOf(e.getBarcode()));
        nameTextView.setText( " SYNC: "+e.getName());
        emailTextView.setText(" NAME: "+e.getEmail());
        dateTextView.setText( " DATE: "+e.getDate());
        contactTextView.setText(" CONTACT: "+ e.getContact());
        costTextView.setText(" COST: "+ e.getCost());
        createdTextView.setText(" CREATED: "+e.getCreated());
        seatTextView.setText(" SEAT No.: "+ e.getSeat());
        lugTextView.setText(" LUGGAGE : "+ e.getLuggage());
        return convertView;
    }

}