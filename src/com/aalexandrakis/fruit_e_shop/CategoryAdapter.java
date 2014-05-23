package com.aalexandrakis.fruit_e_shop;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CategoryAdapter extends ArrayAdapter<Category> {
    private ArrayList<Category> objects;
	public CategoryAdapter(Context context, int textViewResourceId, ArrayList<Category> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		this.objects = objects;
	}
	
    public View getView(int position, View convertView, ViewGroup parent){
	    View v = convertView;
	    if (v== null){
	    	LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.list_category_item, null);
	    }
	    Category i = objects.get(position);
	    if (i!= null){
	    	TextView txtCategoryCode = (TextView) v.findViewById(R.id.txtCategoryCode);
	    	TextView txtCategoryDescr = (TextView) v.findViewById(R.id.txtCategoryDescr);
	    	Log.i("getView", "TextView Completed");
	    	Log.i("Set Category Code", txtCategoryCode.getText().toString());
	    	txtCategoryCode.setText(i.getCategoryCode().toString());
	    	Log.i("Set Category Descr", txtCategoryDescr.getText().toString());
	    	txtCategoryDescr.setText(i.getCategoryDescr());
	    }	
	    return v;	
	}
}
