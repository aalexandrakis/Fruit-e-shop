package com.aalexandrakis.fruit_e_shop;




import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
//import android.widget.TextView;


public class Ordered_items extends Login  {
	private ItemAdapter adapt;
	public ProgressDialog pgd;
	GetOrderedItems GetOrderedItemsObject;
	private String OrderId;
	
	protected void onPause() {
	if (GetOrderedItemsObject != null &&
			GetOrderedItemsObject.getStatus() != AsyncTask.Status.FINISHED){
		    GetOrderedItemsObject.cancel(true);
    	}
	super.onPause();
    }

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 Bundle b = getIntent().getExtras();
	 this.OrderId = b.getString("OrderId");
	 
	 pgd = new ProgressDialog(this); 
	 pgd.setTitle("Loading...");
	 pgd.setMessage("Please wait to load data");
	 setContentView(R.layout.select_item);
	 TextView txtUserHeader = (TextView) findViewById(R.id.txtUserHeader);
	 txtUserHeader.setText("Order Items");
	 //ArrayList<Item> ItemsArray = new ArrayList<Item>();
	 GetOrderedItems GetOrderedItemsObject = new GetOrderedItems(this);
	 //GetItemsObject.execute(url_Products, this.CatId, ItemsArray);
	 GetOrderedItemsObject.execute(url_GetOrderedItems, this.OrderId);
    }
    
    
    @Override	 	
    public void onBackPressed(){
    	if (GetOrderedItemsObject != null &&
    			GetOrderedItemsObject.getStatus() != AsyncTask.Status.FINISHED){
    		    GetOrderedItemsObject.cancel(true);
        	}
		this.finish();
	} 
    
    
	public void fillListWithProducts(ArrayList<Item> ItemsArray){
		//Log.i("fillListWithProducts", ItemsArray.get(0).getItemDescr());
		ListView lstView = (ListView) findViewById(R.id.CartList);
		adapt = new ItemAdapter (this, R.layout.list_item_item, ItemsArray);
		lstView.setAdapter(adapt);

	}

	
}


/////////////////////////////////////////////////////////////////////////
 class GetOrderedItems extends AsyncTask<Object, ArrayList<Item>, ArrayList<Item>> {
	private InputStream intStrm;
	private String url;
	private String OrderId;
	private ArrayList<Item> Items = new ArrayList<Item>();
	boolean Result;
	public Ordered_items ThisActivity;
	
	
	public GetOrderedItems(Ordered_items a){
		ThisActivity = a;
	}
	
	
	@Override
	protected ArrayList<Item> doInBackground(Object... params) {
		// TODO Auto-generated method stub
		url = (String) params[0];
		OrderId = (String) params[1];
		//Items = (ArrayList<Item>) params[2];
		Result = false;
        
		try {
			if (ThisActivity.checkConnectivity()){
				List<NameValuePair> paramsA = new ArrayList<NameValuePair>();
				paramsA.add(new BasicNameValuePair("orderid", OrderId));
				Log.i("paramsA.OrderId", paramsA.get(0).toString());
				intStrm = this.getHttpResponseXml(paramsA, url);
			} else {
				final File file = new File(Login.app_path, "OrderedItems.xml");
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
		XmlPullParser xmlItems = null;  
	    try {
			xmlItems = XmlPullParserFactory.newInstance().newPullParser();
			xmlItems.setInput(intStrm, null);	
	    } catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
	    	Log.i("doInBackground-xppe", e.getMessage());
	    	ThisActivity.ShowAlertDialog("Error","Data not found. Please try later");
	    	this.cancel(true);
		}
		Items = this.FillStringArray(xmlItems);
		Result = true;
	    return Items;
	}
	
	public ArrayList<Item>  FillStringArray(XmlPullParser xmlItems) {
	Integer eventtype = -1;
    ArrayList<Item> Items = new ArrayList<Item>();
    String ItemCodeStr = null;
    String ItemPriceStr = null;
    String ItemQuantityStr = null;
    String ItemSummaryStr = null;
    Integer ItemCode;
    String ItemDescr = null;
    String ItemMm = null;
    Float ItemPrice;
    Float ItemQuantity = Float.parseFloat("0.0");
    Float ItemSummary = Float.parseFloat("0.0");
    
	while (eventtype!=XmlPullParser.END_DOCUMENT){
		if (eventtype==XmlPullParser.START_TAG){
			if (xmlItems.getName().equals("item") &&
				xmlItems.getAttributeValue(null, "orderid").equals(OrderId.toString())){
				ItemCodeStr = xmlItems.getAttributeValue(null, "itemid");
				ItemDescr = xmlItems.getAttributeValue(null, "itemdescr");
				ItemMm = xmlItems.getAttributeValue(null, "mm");
				ItemPriceStr = xmlItems.getAttributeValue(null, "price");
				ItemQuantityStr = xmlItems.getAttributeValue(null, "quantity");
				ItemSummaryStr = xmlItems.getAttributeValue(null, "itemsummary");
				ItemCode = Integer.parseInt(ItemCodeStr);
				ItemPrice = Float.parseFloat(ItemPriceStr);
				ItemQuantity= Float.parseFloat(ItemQuantityStr);
				ItemSummary = Float.parseFloat(ItemSummaryStr);
				Item NewItem = new Item(ItemCode, ItemDescr, ItemMm, ItemPrice, ItemQuantity, ItemSummary);
				Items.add(NewItem);
			}
		}	
			
		try {
			 eventtype = xmlItems.next();
		   } catch (XmlPullParserException e) {
			   Log.i("XmlPullParserException-next", e.getMessage());
		   } catch (IOException e) {
			   Log.i("IOException-next", e.getMessage());
		   }
		
	}	
	Log.i("doInBackground", "end loop");
	return Items;
  }
	
	
	InputStream  getHttpResponseXml(List<NameValuePair> params, String url_str) throws ClientProtocolException, IOException {
		Log.i("url", url_str);
		Log.i("OrderId", params.get(0).toString());
		
	    HttpClient httpClient = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10000);
        HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10000);
        HttpPost httpPost = new HttpPost(url_str);  
     // Building Parameters
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
	    httpPost.setHeader("Host", "aalexandrakis.freevar.com");
		entity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
		entity.setContentEncoding("UTF-8");
		entity.setChunked(true);
	    httpPost.setEntity(entity);
	    HttpResponse response = httpClient.execute(httpPost);
        InputStream rtnStream = response.getEntity().getContent();
        
        //BufferedReader br = null;
		//br = new BufferedReader(new InputStreamReader(rtnStream));
		//String rtnString = (String) br.readLine();
		//Log.i("ReadLine", rtnString);
		//br.close();
		//if (rtnString.startsWith("0")){
	    //   this.cancel(true);
		//}
		//rtnStream.reset();
        return rtnStream;
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