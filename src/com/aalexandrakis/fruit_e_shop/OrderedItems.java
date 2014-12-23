package com.aalexandrakis.fruit_e_shop;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
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


public class OrderedItems extends Login  {
	private ItemAdapter adapt;
	public ProgressDialog pgd;
	GetOrderedItemsAsyncTask getOrderedItemsAsyncTaskObject;
	private String orderId;
	
	protected void onPause() {
	if (getOrderedItemsAsyncTaskObject != null &&
			getOrderedItemsAsyncTaskObject.getStatus() != AsyncTask.Status.FINISHED){
		    getOrderedItemsAsyncTaskObject.cancel(true);
    	}
	super.onPause();
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 Bundle b = getIntent().getExtras();
	 this.orderId = b.getString("orderId");
	 
	 pgd = new ProgressDialog(this); 
	 pgd.setTitle("Loading...");
	 pgd.setMessage("Please wait to load data");
	 setContentView(R.layout.select_item);
	 TextView txtUserHeader = (TextView) findViewById(R.id.txtUserHeader);
	 txtUserHeader.setText("Order items");
	 if (checkConnectivity()) {
		GetOrderedItemsAsyncTask getOrderedItemsAsyncTaskObject = new GetOrderedItemsAsyncTask(this);
		getOrderedItemsAsyncTaskObject.execute(Commons.URL + "/getOrderItems/" + this.orderId);
	 } else {
		showAlertDialog("Error", "No internet connection");
	 }
    }
    
    
    @Override	 	
    public void onBackPressed(){
    	if (getOrderedItemsAsyncTaskObject != null &&
    			getOrderedItemsAsyncTaskObject.getStatus() != AsyncTask.Status.FINISHED){
    		    getOrderedItemsAsyncTaskObject.cancel(true);
        	}
		this.finish();
	} 
    
    
	public void fillListWithProducts(ArrayList<Item> ItemsArray){
		ListView lstView = (ListView) findViewById(R.id.CartList);
		adapt = new ItemAdapter (this, R.layout.list_item_item, ItemsArray);
		lstView.setAdapter(adapt);
	}

	
}


/////////////////////////////////////////////////////////////////////////
 class GetOrderedItemsAsyncTask extends AsyncTask<String, ArrayList<Item>, ArrayList<Item>> {
	private InputStream intStrm;
	private ArrayList<Item> items = new ArrayList<Item>();
	public OrderedItems thisActivity;
	
	
	public GetOrderedItemsAsyncTask(OrderedItems a){
		thisActivity = a;
	}
	
	
	@Override
	protected ArrayList<Item> doInBackground(String... params) {
		// TODO Auto-generated method stub
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 30000);
			HttpConnectionParams.setSoTimeout(httpClient.getParams(), 30000);
			HttpGet httpGet = new HttpGet(params[0]);
			HttpResponse response = httpClient.execute(httpGet);
			intStrm = response.getEntity().getContent();
			XmlPullParser xmlItems = null;
			xmlItems = XmlPullParserFactory.newInstance().newPullParser();
			xmlItems.setInput(intStrm, null);
			items = this.fillStringArray(xmlItems);
			return items;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();;
			//Log.i("doInBackground-cpe", e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();;
			//Log.i("doInBackground-ioe", e.getMessage());
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();;
			//Log.i("doInBackground-xppe", e.getMessage());
			thisActivity.showAlertDialog("Error", "Data not found. Please try later");
			this.cancel(true);
		}
		return null;
	}
	
	public ArrayList<Item> fillStringArray(XmlPullParser xmlItems) {
		Integer eventtype = -1;
		ArrayList<Item> items = new ArrayList<Item>();
		try {
			eventtype = xmlItems.getEventType();
			Item item = null;
			while (eventtype != XmlPullParser.END_DOCUMENT) {

				if (eventtype == XmlPullParser.START_TAG && xmlItems.getName().equals("ordereditem")) {
					item = new Item();
				} else if (eventtype == XmlPullParser.START_TAG && xmlItems.getName().equals("descr")) {
					item.setItemDescr(xmlItems.nextText());
				} else if (eventtype == XmlPullParser.START_TAG && xmlItems.getName().equals("mm")) {
					item.setItemMm(xmlItems.nextText());
				} else if (eventtype == XmlPullParser.START_TAG && xmlItems.getName().equals("itemid")) {
					item.setItemCode(Integer.valueOf(xmlItems.nextText()));
				} else if (eventtype == XmlPullParser.START_TAG && xmlItems.getName().equals("quantity")) {
					item.setItemQuantity(Float.valueOf(xmlItems.nextText()));
				} else if (eventtype == XmlPullParser.START_TAG && xmlItems.getName().equals("price")) {
					item.setItemPrice(Float.valueOf(xmlItems.nextText()));
				} else if (eventtype == XmlPullParser.START_TAG && xmlItems.getName().equals("amount")) {
					item.setItemSummary(Float.valueOf(xmlItems.nextText()));
				} else if (eventtype == XmlPullParser.END_TAG && xmlItems.getName().equals("ordereditem")) {
					items.add(item);
				}else if (eventtype == XmlPullParser.END_TAG && xmlItems.getName().equals("ordereditems")) {
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
		thisActivity.fillListWithProducts(Items);
		thisActivity.pgd.dismiss();
	}
	@Override
	protected void onPreExecute(){
		thisActivity.pgd.show();
	}
	
	
	
	
	
}