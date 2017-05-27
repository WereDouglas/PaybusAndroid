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
public class ExpenseAdapter extends BaseAdapter {
    Context context;
    ArrayList<Expense> empList;
    private static LayoutInflater inflater = null;

    public ExpenseAdapter(Context context, ArrayList<Expense> empList) {
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
            convertView = inflater.inflate(R.layout.expense_grid, null);

        TextView particularTextView = (TextView) convertView.findViewById(R.id.particular);
        TextView qtyTextView = (TextView) convertView.findViewById(R.id.qty);
        TextView unitTextView = (TextView) convertView.findViewById(R.id.unit);
        TextView totalTextView = (TextView) convertView.findViewById(R.id.total);
        TextView syncTextView = (TextView) convertView.findViewById(R.id.sync);

        Expense e = new Expense();
        e = empList.get(position);
        particularTextView.setText(" P/T: "+ String.valueOf(e.getParticular()));
        qtyTextView.setText( " QTY: "+e.getQty());
        unitTextView.setText(" @ : "+e.getUnit());
        totalTextView.setText( " TOTAL : "+e.getTotal());
        syncTextView.setText(" SYNC : "+ e.getSync());

        return convertView;
    }

}