package com.aalexandrakis.fruit_e_shop;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
//import android.widget.TextView;


public class Select_item extends Login  {
	private ItemAdapter adapt;
	public ProgressDialog pgd;
	GetItemsAsyncTask getItemsAsyncTask;
	private int CatId;
	
	protected void onPause() {
	if (getItemsAsyncTask != null &&
			getItemsAsyncTask.getStatus() != AsyncTask.Status.FINISHED){
		    getItemsAsyncTask.cancel(true);
    	}
	super.onPause();
    }

	protected void onResume(){
		super.onResume();
		if (adapt == null){
			return;
		}
		int i=0;
		ProgressDialog RefreshProgress = new ProgressDialog(this);
		RefreshProgress.setTitle("Refreshing Data");
		RefreshProgress.setMessage("Please wait to refresh data");
		RefreshProgress.show();
		boolean atLeastOneChange=false;
		for (i=0;i<adapt.getCount();i++){
			if (adapt.getItem(i).getItemQuantity() > Float.parseFloat("0.0") &&
			   (!myCartArray.contains(adapt.getItem(i)))){
				   adapt.getItem(i).setItemQuantity(Float.parseFloat("0.0"));
				   adapt.getItem(i).setItemSummary(Float.parseFloat("0.0"));
				   atLeastOneChange = true;
			}
		}
		if (atLeastOneChange) {
			adapt.notifyDataSetChanged();
		}
		RefreshProgress.dismiss();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();
		this.CatId = b.getInt("CatId");

		pgd = new ProgressDialog(this);
		pgd.setTitle("Loading...");
		pgd.setMessage("Please wait to load data");
		setContentView(R.layout.select_item);
		//ArrayList<Item> ItemsArray = new ArrayList<Item>();
		GetItemsAsyncTask getItemsAsyncTask = new GetItemsAsyncTask(this);
		//GetItemsObject.execute(url_Products, this.CatId, ItemsArray);
		if (checkConnectivity()) {
			getItemsAsyncTask.execute(Commons.URL + "/getItems/" + this.CatId);
		} else {
			showAlertDialog("Connectivity Error", "No Internet Connection");
		}
	}

	@Override
    public void onBackPressed(){
    	if (getItemsAsyncTask != null &&
    			getItemsAsyncTask.getStatus() != AsyncTask.Status.FINISHED){
    		    getItemsAsyncTask.cancel(true);
        	}
		this.finish();
	} 
    
    
	public void fillListWithProducts(ArrayList<Item> ItemsArray){
		//Log.i("fillListWithProducts", ItemsArray.get(0).getItemDescr());
		ListView lstView = (ListView) findViewById(R.id.CartList);
		adapt = new ItemAdapter (this, R.layout.list_item_item, ItemsArray);
		lstView.setAdapter(adapt);
		lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				TextView txtItemCode = (TextView) arg1.findViewById(R.id.txtItemId);
				TextView txtItemDescr = (TextView) arg1.findViewById(R.id.txtItemDescr);
				TextView txtItemMm = (TextView) arg1.findViewById(R.id.txtItemMm);
				TextView txtItemPrice = (TextView) arg1.findViewById(R.id.txtItemPrice);
				TextView txtItemQuantity = (TextView) arg1.findViewById(R.id.txtItemQuantity);
				TextView txtItemSummary = (TextView) arg1.findViewById(R.id.txtItemSummary);
				Log.i("arg2", String.valueOf(arg2));
				Log.i("arg3", String.valueOf(arg3));
				//////
				Float Quantity;
				Float Summary;
				try {
					Quantity=Float.parseFloat(txtItemQuantity.getText().toString());
					Summary=Float.parseFloat(txtItemSummary.getText().toString());
				}	
				catch (NumberFormatException e)
				{
					Quantity = Float.parseFloat("0.0"); 
					Summary = Float.parseFloat("0.0"); 
				}
				
				//////
				Item SelectedItem=new Item(Integer.parseInt(txtItemCode.getText().toString()),
						                    txtItemDescr.getText().toString(),
						                    txtItemMm.getText().toString(),
						                    Float.parseFloat(txtItemPrice.getText().toString()),
						                    Quantity, Summary);
				ShowItemDialog(SelectedItem, arg2);
				
			}
		});

	}

	private void UpdateAdapter(int pos, Float Quantity){
		Log.i("Update Adapter", "Item Dialog Ends Here");
	    Log.i("OnClick-Position", String.valueOf(pos));
	    Log.i("OnClick-Quantity", Quantity.toString());
        
	    if (Quantity == null || Quantity == 0.0){
	    	return;
	    }
	    
		Item SelectedItem = adapt.getItem(pos);

	    adapt.getItem(pos).setItemQuantity(Quantity);
	    adapt.getItem(pos).setItemSummary(Quantity * adapt.getItem(pos).getItemPrice());
		adapt.notifyDataSetChanged();
		
		if (!myCartArray.isEmpty() && myCartArray.contains(SelectedItem)){
			Log.i("myCartArray", "Contains Selected Item");
			myCartArray.remove(SelectedItem);
			Log.i("MyCartAray", "SelectedItem Removed");
		}
			
		myCartArray.add(SelectedItem);
		Log.i("MyCartAray", "Selected Item Added");
		Float MyCartSummary = GetMyCartSummary();
		Toast.makeText(getApplicationContext(), "Your cart summary is " + MyCartSummary.toString(), Toast.LENGTH_LONG).show();
		
		Log.i("Aray Adapter", "Notify changes");
	}
	
	private void ShowItemDialog(Item SelectedItem, int ItPos){
		//Log.e("ShowItemDialog-Item", SelectedItem)
		final int ItemPosition = ItPos;
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.dialog_item, (ViewGroup) findViewById(R.id.root));
		final TextView txtItemCode = (TextView) layout.findViewById(R.id.txtItemId);
		final TextView txtItemDescr = (TextView) layout.findViewById(R.id.txtItemDescr);
		final TextView txtItemMm = (TextView) layout.findViewById(R.id.txtItemMm);
		final TextView txtItemPrice = (TextView) layout.findViewById(R.id.txtItemPrice);
		final TextView txtItemQuantity = (TextView) layout.findViewById(R.id.txtItemQuantity);
		
		txtItemCode.setText(SelectedItem.getItemCode().toString());
		txtItemDescr.setText(SelectedItem.getItemDescr());
		txtItemMm.setText(SelectedItem.getItemMm());
		txtItemPrice.setText(SelectedItem.getItemPrice().toString());
		if (String.valueOf(SelectedItem.getItemQuantity().toString())=="0.0"){
			txtItemQuantity.setText("");	
		} else {
		    txtItemQuantity.setText(String.valueOf(SelectedItem.getItemQuantity().toString()));
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		builder.setTitle("Select Quantity");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				//Editor editor = mycart.edit();
				//editor.putFloat(txtItemCode.getText().toString(), Float.parseFloat(txtItemQuantity.getText().toString()));
				//editor.commit();
				try {
				   UpdateAdapter(ItemPosition, Float.parseFloat(txtItemQuantity.getText().toString()));
				   dialog.dismiss();
				} catch(NumberFormatException e) {
					//showAlertDialog("Type Error", "Quantity not valid.  Try again");
					Toast.makeText(getApplicationContext(), "Quantity not valid", Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					//return;
				}
			}
		} );
		
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		} );
		
  
		AlertDialog ItemDialog = builder.create();
		ItemDialog.show();
  }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.choice_menu_items, menu);
		menu.findItem(R.id.ViewMyCart).setIntent(new Intent("com.aalexandrakis.fruit_e_shop.View_Cart"));
		menu.findItem(R.id.EmptyCart).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				myCartArray.clear();
				adapt.notifyDataSetChanged();
				return false;
			}
		});
		
	    return true;
	}
}


/////////////////////////////////////////////////////////////////////////
 class GetItemsAsyncTask extends AsyncTask<Object, ArrayList<Item>, ArrayList<Item>> {
	private InputStream intStrm;
	private String url_getItems;
	private Integer CatId;
	private ArrayList<Item> Items = new ArrayList<Item>();
	public Select_item ThisActivity;


	public GetItemsAsyncTask(Select_item a){
		ThisActivity = a;
	}


	@Override
	protected ArrayList<Item> doInBackground(Object... params) {
		// TODO Auto-generated method stub
		url_getItems = (String) params[0];
		//Items = (ArrayList<Item>) params[2];

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 30000);
			HttpConnectionParams.setSoTimeout(httpClient.getParams(), 30000);
			HttpGet httpGet = new HttpGet(url_getItems);
			HttpResponse response = httpClient.execute(httpGet);
			intStrm = response.getEntity().getContent();
			XmlPullParser xmlItems = null;
			xmlItems = XmlPullParserFactory.newInstance().newPullParser();
			xmlItems.setInput(intStrm, null);
			Items = this.FillStringArray(xmlItems);
			return Items;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Log.i("doInBackground-cpe", e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Log.i("doInBackground-ioe", e.getMessage());
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Log.i("doInBackground-xppe", e.getMessage());
			ThisActivity.showAlertDialog("Error", "Data not found. Please try later");
			this.cancel(true);
		}
		return null;
	}

	
	public ArrayList<Item>  FillStringArray(XmlPullParser xmlItems) {
	Integer eventtype = -1;
    ArrayList<Item> items = new ArrayList<Item>();
	try {
		eventtype = xmlItems.getEventType();
		Item item = null;
		while (eventtype != XmlPullParser.END_DOCUMENT) {
			if (eventtype == XmlPullParser.START_TAG && xmlItems.getName().equals("item")) {
				item = new Item();
//				item.setItemQuantity(Float.parseFloat("0.0"));
//				item.setItemSummary(Float.parseFloat("0.0"));
			} else if (eventtype == XmlPullParser.START_TAG && xmlItems.getName().equals("categoryid")) {
				item.setCategoryId(Integer.valueOf(xmlItems.nextText()));
			} else if (eventtype == XmlPullParser.START_TAG && xmlItems.getName().equals("descr")) {
				item.setItemDescr(xmlItems.nextText());
			} else if (eventtype == XmlPullParser.START_TAG && xmlItems.getName().equals("display")) {
				item.setDisplay(Integer.valueOf(xmlItems.nextText()));
			} else if (eventtype == XmlPullParser.START_TAG && xmlItems.getName().equals("itemid")) {
				item.setItemCode(Integer.valueOf(xmlItems.nextText()));
			} else if (eventtype == XmlPullParser.START_TAG && xmlItems.getName().equals("lastupdate")) {
				item.setLastUpdate(Integer.valueOf(xmlItems.nextText()));
			} else if (eventtype == XmlPullParser.START_TAG && xmlItems.getName().equals("mm")) {
				item.setItemMm(xmlItems.nextText());
			} else if (eventtype == XmlPullParser.START_TAG && xmlItems.getName().equals("price")) {
				item.setItemPrice(Float.valueOf(xmlItems.nextText()));
			} else if (eventtype == XmlPullParser.END_TAG && xmlItems.getName().equals("item")) {
				item.setItemQuantity(ThisActivity.returnQuantityIfExists(item.getItemCode()));
				items.add(item);
			}else if (eventtype == XmlPullParser.END_TAG && xmlItems.getName().equals("items")) {
				break;
			}
			eventtype = xmlItems.next();
		}
	} catch (XmlPullParserException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	return items;
  }

	@Override
    protected void onPostExecute(ArrayList<Item> Items) {
		Log.i("onPostExecute", "onPostExecute");
		//Log.i("fillListWithProducts", Items.get(1).getItemDescr());
		ThisActivity.fillListWithProducts(Items);
		ThisActivity.pgd.dismiss();
	}
	@Override
	protected void onPreExecute(){
		ThisActivity.pgd.show();
	}
}