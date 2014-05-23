package com.aalexandrakis.fruit_e_shop;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ItemAdapter extends ArrayAdapter<Item> {
    private ArrayList<Item> objects;
	public ItemAdapter(Context context, int textViewResourceId, ArrayList<Item> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		this.objects = objects;
	}
	
    public View getView(int position, View convertView, ViewGroup parent){
	    View v = convertView;
	    if (v== null){
	    	LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.list_item_item, null);
	    }
	    Item i = objects.get(position);
	    if (i!= null){
	    	TextView txtItemCode = (TextView) v.findViewById(R.id.txtItemId);
	    	TextView txtItemDescr = (TextView) v.findViewById(R.id.txtItemDescr);
	    	TextView txtItemMm = (TextView) v.findViewById(R.id.txtItemMm);
	    	TextView txtItemPrice = (TextView) v.findViewById(R.id.txtItemPrice);
	    	TextView txtItemQuantity = (TextView) v.findViewById(R.id.txtItemQuantity);
	    	TextView txtItemSummary = (TextView) v.findViewById(R.id.txtItemSummary);
	    	Log.i("getView", "TextView Completed");
	    	Log.i("Set Item Code", i.getItemCode().toString());
	    	txtItemCode.setText(i.getItemCode().toString());
	    	Log.i("Set Item Descr", i.getItemDescr());
	    	txtItemDescr.setText(i.getItemDescr());
	    	Log.i("Set Item Mm", i.getItemMm());
	    	txtItemMm.setText(i.getItemMm());
	    	Log.i("Set Item Price", i.getItemPrice().toString());
	    	txtItemPrice.setText(i.getItemPrice().toString());
	    	Log.i("Set Item Quantity", i.getItemQuantity().toString());
	    	txtItemQuantity.setText(i.getItemQuantity().toString());
	    	Log.i("Set Item Summary", i.getItemSummary().toString());
	    	txtItemSummary.setText(i.getItemSummary().toString());    
	    	
	    }	
	    return v;	
	}
}
