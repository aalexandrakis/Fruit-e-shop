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


public class Select_item extends Login  {
	private ItemAdapter adapt;
	public ProgressDialog pgd;
	GetItems GetItemsObject;
	private int CatId;
	
	protected void onPause() {
	if (GetItemsObject != null &&
			GetItemsObject.getStatus() != AsyncTask.Status.FINISHED){
		    GetItemsObject.cancel(true);
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
			   (!MyCartArray.contains(adapt.getItem(i)))){
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
	 GetItems GetItemsObject = new GetItems(this);
	 //GetItemsObject.execute(url_Products, this.CatId, ItemsArray);
	 GetItemsObject.execute(url_Products, this.CatId);
    }
    
    
    @Override	 	
    public void onBackPressed(){
    	if (GetItemsObject != null &&
    			GetItemsObject.getStatus() != AsyncTask.Status.FINISHED){
    		    GetItemsObject.cancel(true);
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
		
		if (!MyCartArray.isEmpty() && MyCartArray.contains(SelectedItem)){
			Log.i("MyCartArray", "Contains Selected Item");
			MyCartArray.remove(SelectedItem);
			Log.i("MyCartAray", "SelectedItem Removed");
		}
			
		MyCartArray.add(SelectedItem);
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
					//ShowAlertDialog("Type Error", "Quantity not valid.  Try again");
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
				MyCartArray.clear();
				adapt.notifyDataSetChanged();
				return false;
			}
		});
		
	    return true;
	}
}


/////////////////////////////////////////////////////////////////////////
 class GetItems extends AsyncTask<Object, ArrayList<Item>, ArrayList<Item>> {
	private InputStream intStrm;
	private String url_getItems;
	private Integer CatId;
	private ArrayList<Item> Items = new ArrayList<Item>();
	boolean Result;
	public Select_item ThisActivity;
	
	
	public GetItems(Select_item a){
		ThisActivity = a;
	}
	
	
	@Override
	protected ArrayList<Item> doInBackground(Object... params) {
		// TODO Auto-generated method stub
		url_getItems = (String) params[0];
		CatId = (Integer) params[1];
		//Items = (ArrayList<Item>) params[2];
		Result = false;
        
		try {
			List<NameValuePair> paramsA = new ArrayList<NameValuePair>();
			paramsA.add(new BasicNameValuePair("catid", CatId.toString()));
			Log.i("paramsA.CatId", paramsA.get(0).toString());
			if (ThisActivity.checkConnectivity()){
				intStrm = this.getHttpResponseXml(paramsA, url_getItems);
			} else {
				final File file = new File(Login.app_path, "Products.xml");
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
    Integer ItemCode;
    String ItemDescr = null;
    String ItemMm = null;
    Float ItemPrice;
    Float ItemQuantity = Float.parseFloat("0.0");
    Float ItemSummary = Float.parseFloat("0.0");
    
	while (eventtype!=XmlPullParser.END_DOCUMENT){
		if (eventtype==XmlPullParser.START_TAG){
			if (xmlItems.getName().equals("item") && 
				xmlItems.getAttributeValue(null, "catid").equals(CatId.toString())){
				String CatIdXml = xmlItems.getAttributeValue(null, "catid");
				Integer CatIdXmlInt = Integer.parseInt(CatIdXml);
				Log.i("Into while loop XML catid", CatIdXml);
				if (CatIdXmlInt > CatId){
	                break;				
				}
				ItemCodeStr = xmlItems.getAttributeValue(null, "itemid");
				ItemDescr = xmlItems.getAttributeValue(null, "itemdescr");
				ItemMm = xmlItems.getAttributeValue(null, "mm");
				ItemPriceStr = xmlItems.getAttributeValue(null, "price");
				Log.i("ItemRead", "New Item Addition");
				ItemCode = Integer.parseInt(ItemCodeStr);
				ItemPrice = Float.parseFloat(ItemPriceStr);
				ItemQuantity= ThisActivity.ReturnQuantityIfExists(ItemCode);
				ItemSummary = ItemPrice * ItemQuantity;
				Log.i("Item Addition", "ItemCode="+ItemCode.toString()+" ItemDescr="+ItemDescr+" ItemMm="+ItemMm+" ItemPrice="+ItemPrice.toString());
				Item NewItem = new Item(ItemCode, ItemDescr, ItemMm, ItemPrice, ItemQuantity, ItemSummary);
				Items.add(NewItem);
			}
		}
		try {
			eventtype = xmlItems.next();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	Log.i("doInBackground", "end loop");
	return Items;
  }
	
	
	InputStream  getHttpResponseXml(List<NameValuePair> params, String url_str) throws ClientProtocolException, IOException {
		Log.i("url", url_str);
		Log.i("catid", params.get(0).toString());
		
	    HttpClient httpClient = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 30000);
        HttpConnectionParams.setSoTimeout(httpClient.getParams(), 30000);
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