package com.aalexandrakis.fruit_e_shop;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import android.widget.TextView;


public class View_orders extends Login  {
	private OrderAdapter adapt;
	public ProgressDialog pgd;
	GetOrdersAsyncTask getOrdersAsyncTaskObject;

	protected void onPause() {
		if (getOrdersAsyncTaskObject != null &&
				getOrdersAsyncTaskObject.getStatus() != AsyncTask.Status.FINISHED){
			    getOrdersAsyncTaskObject.cancel(true);
	    	}
		super.onPause();
	    }


	@Override
	protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	  pgd = new ProgressDialog(this); 
	  pgd.setTitle("Loading...");
	  pgd.setMessage("Please wait to load data");
	  setContentView(R.layout.view_orders);
	  GetOrdersAsyncTask getOrdersAsyncTaskObject = new GetOrdersAsyncTask(this);
	  getOrdersAsyncTaskObject.execute(Commons.URL + "/getOrders/" + String.valueOf(settings.getInt("Id", 0)));
    }
    
    @Override	 	
    public void onBackPressed(){
		this.finish();
	} 
    
    
	public void fillListWithOrders(ArrayList<Order> OrdersArray){
		//Log.i("fillListWithProducts", ItemsArray.get(0).getItemDescr());
		ListView lstView = (ListView) findViewById(R.id.OrderList);
		adapt = new OrderAdapter (this, R.layout.list_order_item, OrdersArray);
		lstView.setAdapter(adapt);
		lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				TextView txtOrderId = (TextView) arg1.findViewById(R.id.txtOrderId);
				Log.e("PutExtr", txtOrderId.getText().toString());
				Intent a = new Intent("com.aalexandrakis.fruit_e_shop.Ordered_items");
				a.putExtra("OrderId", txtOrderId.getText().toString());
				startActivity(a);
			}
		});

	}
	
}


/////////////////////////////////////////////////////////////////////////
class GetOrdersAsyncTask extends AsyncTask<String, ArrayList<Order>, ArrayList<Order>> {
		private InputStream intStrm;
		private ArrayList<Order> orders = new ArrayList<Order>();
		public View_orders thisActivity;
		DateFormat dfFormat = new SimpleDateFormat("dd-MM-yyyy");
		DateFormat dfParse = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

		public GetOrdersAsyncTask(View_orders a){
			thisActivity = a;
		}

		
		@Override
		protected ArrayList<Order> doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				if (thisActivity.checkConnectivity()) {
					HttpClient httpClient = new DefaultHttpClient();
					HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 0);
					HttpConnectionParams.setSoTimeout(httpClient.getParams(), 0);
					HttpGet httpGet = new HttpGet(params[0]);
					HttpResponse response = httpClient.execute(httpGet);
					InputStream rtnStream = response.getEntity().getContent();
					XmlPullParser xmlOrders = null;
					xmlOrders = XmlPullParserFactory.newInstance().newPullParser();
					xmlOrders.setInput(rtnStream, null);
					orders = this.fillStringArray(xmlOrders);
					return orders;
				}

			} catch (XmlPullParserException e) {
					// 	TODO Auto-generated catch block
					//e.printStackTrace();
					Log.i("doInBackground-xppe", e.getMessage());
					thisActivity.showAlertDialog("Error", "Data not found. Please try later");
					this.cancel(true);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				Log.i("doInBackground-cpe", e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				Log.i("doInBackground-ioe", e.getMessage());
			}
			return null;
		}

		public ArrayList<Order> fillStringArray(XmlPullParser xmlOrders) {
			Integer eventtype = -1;
			ArrayList<Order> orders = new ArrayList<Order>();
			try {
				eventtype = xmlOrders.getEventType();
				Order order = null;
				while (eventtype != XmlPullParser.END_DOCUMENT) {

					if (eventtype == XmlPullParser.START_TAG && xmlOrders.getName().equals("order")) {
						order = new Order();
					} else if (eventtype == XmlPullParser.START_TAG && xmlOrders.getName().equals("ammount")) {
						order.setOrderAmount(Float.valueOf(xmlOrders.nextText()));
					} else if (eventtype == XmlPullParser.START_TAG && xmlOrders.getName().equals("date")) {
						order.setOrderDate(dfFormat.format(dfParse.parse(xmlOrders.nextText())));
					} else if (eventtype == XmlPullParser.START_TAG && xmlOrders.getName().equals("orderid")) {
						order.setOrderId(Integer.valueOf(xmlOrders.nextText()));
					} else if (eventtype == XmlPullParser.START_TAG && xmlOrders.getName().equals("status")) {
						order.setOrderStatus(xmlOrders.nextText());
					} else if (eventtype == XmlPullParser.END_TAG && xmlOrders.getName().equals("order")) {
						orders.add(order);
					}else if (eventtype == XmlPullParser.END_TAG && xmlOrders.getName().equals("orders")) {
						break;
					}
					eventtype = xmlOrders.next();
				}
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}

			return orders;
		}

		@Override
		protected void onPostExecute(ArrayList<Order> orders) {
			thisActivity.fillListWithOrders(orders);
			thisActivity.pgd.dismiss();
		}
		@Override
		protected void onPreExecute(){
			thisActivity.pgd.show();
		}
	}