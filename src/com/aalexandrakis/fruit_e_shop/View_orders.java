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
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
//import android.widget.TextView;


public class View_orders extends Login  {
	private OrderAdapter adapt;
	public ProgressDialog pgd;
	GetOrders GetOrdersObject;
	View_orders ThisActivity=this;
	
	protected void onPause() {
		if (GetOrdersObject != null &&
				GetOrdersObject.getStatus() != AsyncTask.Status.FINISHED){
			    GetOrdersObject.cancel(true);
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
	  //fillListWithOrders(MyOrdersArray);
	 //ArrayList<Order> OrdersArray = new ArrayList<Order>();
	 GetOrders GetOrdersObject = new GetOrders(this);
	 //GetOrdersObject.execute(url_GetOrders, settings.getString("Email", ""), OrdersArray);
	 GetOrdersObject.execute(url_GetOrders, settings.getString("Email", "")); 
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
class GetOrders extends AsyncTask<Object, ArrayList<Order>, ArrayList<Order>> {
		private InputStream intStrm;
		private String url;
		private String req_email;
		private ArrayList<Order> Orders = new ArrayList<Order>();
		boolean Result;
		public View_orders ThisActivity;


		public GetOrders(View_orders a){
			ThisActivity = a;
		}

		
		@Override
		protected ArrayList<Order> doInBackground(Object... params) {
			// TODO Auto-generated method stub
			url = (String) params[0];
			req_email = (String) params[1];
			//Orders = (ArrayList<Order>) params[2];
			Result = false;

			try {
				if (ThisActivity.checkConnectivity()){
					List<NameValuePair> paramsA = new ArrayList<NameValuePair>();
					paramsA.add(new BasicNameValuePair("email", req_email));
					Log.i("paramsA", paramsA.get(0).toString());
					intStrm = this.getHttpResponseXml(paramsA, url);
				} else {
					final File file = new File(Login.app_path, "Orders.xml");
					if (file.exists()){
						FileInputStream fis = new FileInputStream(file);
						intStrm = fis;
						Log.i("No internet connection", "Getting data from "+file.toString());
					}
				}
				
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				Log.i("doInBackground-cpe", e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				Log.i("doInBackground-ioe", e.getMessage());
			}
			XmlPullParser xmlOrders = null;  
			try {
				xmlOrders = XmlPullParserFactory.newInstance().newPullParser();
				xmlOrders.setInput(intStrm, null);	
			} catch (XmlPullParserException e) {
				// 	TODO Auto-generated catch block
				//e.printStackTrace();
				Log.i("doInBackground-xppe", e.getMessage());
				ThisActivity.showAlertDialog("Error", "Data not found. Please try later");
				this.cancel(true);
			}
			Orders = this.FillStringArray(xmlOrders);
			Result = true;
			return Orders;
		}

		public ArrayList<Order>  FillStringArray(XmlPullParser xmlOrders) {
			Integer eventtype = -1;
			ArrayList<Order> Orders = new ArrayList<Order>();
			String OrderIdStr = null;
			String OrderAmountStr = null;
			
			Integer OrderId;
			String OrderDate = null;
			Float OrderAmount = Float.parseFloat("0.0");
			String OrderStatus= null; 
			
			while (eventtype!=XmlPullParser.END_DOCUMENT){
				if (eventtype==XmlPullParser.START_TAG){
					if(xmlOrders.getName().equals("order")){
						OrderIdStr = xmlOrders.getAttributeValue(null, "orderid");
						OrderDate = xmlOrders.getAttributeValue(null, "orderdate");
						OrderAmountStr = xmlOrders.getAttributeValue(null, "orderamount");
						OrderStatus = xmlOrders.getAttributeValue(null, "orderstatus");
						OrderId = Integer.parseInt(OrderIdStr);
						OrderAmount = Float.parseFloat(OrderAmountStr);
						Order NewOrder = new Order(OrderId, OrderDate, OrderAmount, OrderStatus);
						Orders.add(NewOrder);
					}
				}
				try {
					eventtype = xmlOrders.next();
				} catch (XmlPullParserException e) {
					Log.i("XmlPullParserException-next", e.getMessage());
				} catch (IOException e) {
					Log.i("IOException-next", e.getMessage());
				}
			}	
			Log.i("doInBackground", "end loop");
			return Orders;
		}


		InputStream  getHttpResponseXml(List<NameValuePair> params, String url_str) throws ClientProtocolException, IOException {
			Log.i("url", url_str);
			Log.i("email", params.get(0).toString());
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 30000);
			HttpConnectionParams.setSoTimeout(httpClient.getParams(), 30000);
			HttpPost httpPost = new HttpPost(url_str);  
			// 	Building Parameters
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			httpPost.setHeader("Host", "aalexandrakis.freevar.com");
			entity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
			entity.setContentEncoding("UTF-8");
			entity.setChunked(true);
			httpPost.setEntity(entity);
			HttpResponse response = httpClient.execute(httpPost);
			InputStream rtnStream = response.getEntity().getContent();
			
			//	BufferedReader br = null;
			//	br = new BufferedReader(new InputStreamReader(rtnStream));
			//	String rtnString = (String) br.readLine();
			//Log.i("ReadLine", rtnString);
			//br.close();
			//if (rtnString.startsWith("0")){
			//   this.cancel(true);
			//}
			//rtnStream.reset();
			return rtnStream;
		}


		@Override
		protected void onPostExecute(ArrayList<Order> Orders) {
			Log.i("onPostExecute", "onPostExecute");
			//	Log.i("fillListWithProducts", Items.get(1).getItemDescr());
			ThisActivity.fillListWithOrders(Orders);
			ThisActivity.pgd.dismiss();
		}
		@Override
		protected void onPreExecute(){
			ThisActivity.pgd.show();
		}
	}