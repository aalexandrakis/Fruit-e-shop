package com.aalexandrakis.fruit_e_shop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class OrderAdapter extends ArrayAdapter<Order> {
    private ArrayList<Order> objects;
	public OrderAdapter(Context context, int textViewResourceId, ArrayList<Order> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		this.objects = objects;
	}
	
    public View getView(int position, View convertView, ViewGroup parent){
	    View v = convertView;
	    if (v== null){
	    	LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.list_order_item, null);
	    }
	    Order i = objects.get(position);
	    if (i!= null){
	    	TextView txtOrderId = (TextView) v.findViewById(R.id.txtOrderId);
	    	TextView txtOrderDate = (TextView) v.findViewById(R.id.txtOrderDate);
	    	TextView txtOrderAmount = (TextView) v.findViewById(R.id.txtOrderAmount);
	    	TextView txtOrderStatus = (TextView) v.findViewById(R.id.txtOrderStatus);
	    	
	    	txtOrderId.setText(i.getOrderId().toString());
	    	txtOrderDate.setText(i.getOrderDate());
	    	txtOrderAmount.setText(i.getOrderAmount().toString());
	    	txtOrderStatus.setText(i.getOrderStatus());
	    }	
	    return v;	
	}
}
