package com.aalexandrakis.fruit_e_shop;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;
import com.paypal.android.MEP.*;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

//import android.widget.TextView;


public class ViewCart extends Login  {

	public static String resultTitle;
	public static String resultInfo;
	public static String resultExtra;
	public static String payKey;

	CheckoutButton launchPayPalButton;

	private ItemAdapter adapt;
	public ProgressDialog pgd;

	private ViewCart viewCart = this;

	protected void onPause() {
	    super.onPause();
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	  pgd = new ProgressDialog(this); 
	  pgd.setTitle("Loading...");
	  pgd.setMessage("Please wait to load data");
	  
	  pg = new ProgressDialog(this); 
	  pg.setTitle("Uploading...");
	  pg.setMessage("Please wait to upload your order");
	  
	  setContentView(R.layout.view_cart);
	  Button btnFinish = (Button) findViewById(R.id.btnFinishOrder);
	  registerForContextMenu(findViewById(R.id.CartList));
	  final TextView txtCartSummary = (TextView) findViewById(R.id.txtCartSummary);
	  fillListWithProducts(myCartArray);
	  txtCartSummary.setText(getMyCartSummary().toString());
	  if (myCartArray.isEmpty()){
		  btnFinish.setEnabled(false);
	  } else {
		  btnFinish.setEnabled(true);
	  }
		  
	  btnFinish.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (checkConnectivity()==false){
				showAlertDialog("Connectivity Error", "No Internet Connection");
			} else {
				finishYourOrder();
			}
		}
	  });
		//Paypal instatiation
		PayPal pp = PayPal.getInstance();

//		if (pp == null) {  // Test to see if the library is already initialized

		// This main initialization call takes your Context, AppID, and target server
		pp = PayPal.initWithAppID(this, "APP-80W284485P519543T", PayPal.ENV_SANDBOX);

		// Required settings:

		// Set the language for the library
		pp.setLanguage("en_US");

		// Generate the PayPal checkout button and save it for later use
		launchPayPalButton = pp.getCheckoutButton(this, PayPal.BUTTON_278x43, CheckoutButton.TEXT_PAY);

		// The OnClick listener for the checkout button
		launchPayPalButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Create a basic PayPal payment
				PayPalPayment payment = new PayPalPayment();

				// Set the currency type
				payment.setCurrencyType("EUR");

				// Set the recipient for the payment (can be a phone number)
				payment.setRecipient("aalexandrakis-fruitshop@hotmail.com");

				// Set the payment amount, excluding tax and shipping costs
				if (myCartArray.size() == 0){
					showAlertDialog(getString(R.string.paymentError), getString(R.string.cart_is_empty));
					launchPayPalButton.updateButton();
				} else {
					payment.setSubtotal(new BigDecimal(txtCartSummary.getText().toString()));

					// Set the payment type--his can be PAYMENT_TYPE_GOODS,
					payment.setPaymentType(PayPal.PAYMENT_TYPE_GOODS);
					payment.setMerchantName("Fruit Shop Alexandrakis");
					payment.setCustomID(String.valueOf(settings.getInt("Id", 0)));
					// PayPalInvoiceData can contain tax and shipping amounts, and an
					// ArrayList of PayPalInvoiceItem that you can fill out.
					ArrayList<PayPalInvoiceItem> list = new ArrayList<PayPalInvoiceItem>();
					for (Item item : myCartArray){
						PayPalInvoiceItem ppItem = new PayPalInvoiceItem();
						ppItem.setID(String.valueOf(item.getItemCode()));
						ppItem.setName(item.getItemDescr());
						ppItem.setQuantity(Math.round(item.getItemQuantity()));
						ppItem.setUnitPrice(new BigDecimal(item.getItemPrice()));
						ppItem.setTotalPrice(new BigDecimal(item.getItemSummary()));
						list.add(ppItem);
					}
					PayPalInvoiceData ppData = new PayPalInvoiceData();
					ppData.setInvoiceItems(list);
					payment.setInvoiceData(ppData);
					// These are not required for any transaction.
//						PayPalInvoiceData invoice = new PayPalInvoiceData();
					// Set the tax amount
//						invoice.setTax(new BigDecimal("23"));
					Intent paypalIntent = PayPal.getInstance().checkout(payment, viewCart, new ResultDelegate(settings.getString("Email", "")));
					startActivityForResult(paypalIntent, 1);
				}
			}
		});

		// Add the listener to the layout
//		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.FILL_PARENT,
//				RelativeLayout.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
//		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//		params.topMargin = 20;
		params.bottomMargin = 20;
		launchPayPalButton.setLayoutParams(params);
		launchPayPalButton.setId(10001);
		((LinearLayout) findViewById(R.id.viewCartButtonsLayout)).addView(launchPayPalButton);
		((LinearLayout) findViewById(R.id.viewCartButtonsLayout)).setGravity(Gravity.CENTER_HORIZONTAL);
    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		PayPalActivityResult(requestCode, resultCode, data);
	}

	public void PayPalActivityResult(int requestCode, int resultCode, Intent intent) {
		if(requestCode != 1) {
			return;
		} else {
//		    Toast.makeText(getActivity() ,resultTitle , Toast.LENGTH_SHORT).show();
			if (resultTitle.equals("SUCCESS")){
				emptyCart();
				Toast.makeText(viewCart.getApplicationContext(), "Your order uploaded successfully. Thank you.", Toast.LENGTH_LONG).show();
				launchPayPalButton.updateButton();
				finish();
			} else {
				showAlertDialog(getString(R.string.paymentError), resultInfo + "\n" + resultExtra);
				launchPayPalButton.updateButton();
			}
//		    System.out.println("PayKey " + payKey);
//		    System.out.println("Request Code  " + requestCode);
		}
	}

	@Override
	public void onResume() {
		/**
		 * The CheckoutButton has to be updated each time the Activity is
		 * resumed, otherwise the onClickListener of CheckoutButton will not work
		 **/
		if (launchPayPalButton != null && (launchPayPalButton instanceof CheckoutButton))
			launchPayPalButton.updateButton();
		super.onResume();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	        ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.context_menu_item, menu);
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
	            .getMenuInfo();
	 
	    switch (item.getItemId()) {
	    case R.id.RemoveFromCart:
	        myCartArray.remove(info.position);
	        fillListWithProducts(myCartArray);
	        TextView txtCartSummary = (TextView) findViewById(R.id.txtCartSummary);
	        txtCartSummary.setText(getMyCartSummary().toString());
	        return true;
	    }
	    return false;
	}
    @Override	 	
    public void onBackPressed(){
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
				//Log.i("arg2", String.valueOf(arg2));
				//Log.i("arg3", String.valueOf(arg3));
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
		//Log.i("Update Adapter", "Item Dialog Ends Here");
	   // Log.i("OnClick-Position", String.valueOf(pos));
	   // Log.i("OnClick-Quantity", Quantity.toString());

		Item SelectedItem = adapt.getItem(pos);

	    adapt.getItem(pos).setItemQuantity(Quantity);
	    adapt.getItem(pos).setItemSummary(Quantity * adapt.getItem(pos).getItemPrice());
		adapt.notifyDataSetChanged();
		
		if (!myCartArray.isEmpty() && myCartArray.contains(SelectedItem)){
			//Log.i("myCartArray", "Contains Selected Item");
			myCartArray.remove(SelectedItem);
			//Log.i("MyCartAray", "SelectedItem Removed");
		}
			
		myCartArray.add(SelectedItem);
		//Log.i("MyCartAray", "Selected Item Added");
		Float MyCartSummary = getMyCartSummary();
		//Toast.makeText(getApplicationContext(), "Your cart summary is " + MyCartSummary.toString(), Toast.LENGTH_LONG).show();
		TextView txtCartSummary = (TextView) findViewById(R.id.txtCartSummary);
		txtCartSummary.setText(MyCartSummary.toString());
		
		//Log.i("Aray Adapter", "Notify changes");
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
		txtItemQuantity.setText(String.valueOf(SelectedItem.getItemQuantity().toString()));
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		builder.setTitle("Select Quantity");
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
	
	
	protected void finishYourOrder(){
		CreateOrderAsync CreateOrderAsyncObject = new CreateOrderAsync(this);
		CreateOrderAsyncObject.execute(Commons.URL_COMPLETE_ORDER, String.valueOf(settings.getInt("Id", 0)));
	}
	
	public void emptyCart() {
		myCartArray.clear();
		TextView txtCartSummary = (TextView) findViewById(R.id.txtCartSummary);
		txtCartSummary.setText("");
		adapt.notifyDataSetChanged();
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.choice_menu_cart, menu);
		menu.findItem(R.id.EmptyCart).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				emptyCart();
				return false;
			}
		});
	    return true;
	}
}


////////////////////////////////////////////////////////////////////////
class CreateOrderAsync extends AsyncTask<String, String, String>{
    public ViewCart viewCart;
   
	CreateOrderAsync(ViewCart a){
    	viewCart = a;
    }
	
	@Override
	protected String doInBackground(String... arg0) {
		//String url_str = "http://" + arg0[3] + arg0[0];
		String url_str = arg0[0];

		String rtnString = "";
		HttpClient httpClient = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 0);
        HttpConnectionParams.setSoTimeout(httpClient.getParams(), 0);
        HttpPost httpPost = new HttpPost(url_str);
       // Log.i("HttpPost", "New HttpPost");
     // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("custom", arg0[1]));
		params.add(new BasicNameValuePair("txn_id" , "PAY ON DELIVERY"));
		params.add(new BasicNameValuePair("num_cart_items" , String.valueOf(viewCart.myCartArray.size())));
		params.add(new BasicNameValuePair("mc_gross" , String.valueOf(viewCart.getMyCartSummary())));
		params.add(new BasicNameValuePair("payment_status" , "Completed"));
		int i = 1;
		for (Item item : viewCart.myCartArray){
			params.add(new BasicNameValuePair("item_number" + String.valueOf(i), item.getItemCode().toString()));
			params.add(new BasicNameValuePair("item_name" + String.valueOf(i), item.getItemDescr()));
			params.add(new BasicNameValuePair("quantity" + String.valueOf(i), item.getItemQuantity().toString()));
			params.add(new BasicNameValuePair("mc_gross" + String.valueOf(i), String.valueOf(item.getItemQuantity() * item.getItemPrice())));

			i++;
		}
        try {
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
		entity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
		entity.setContentEncoding("UTF-8");
		entity.setChunked(true);
	    httpPost.setEntity(entity);
	    HttpResponse response;
		try {
			response = httpClient.execute(httpPost);
			return String.valueOf(response.getStatusLine().getStatusCode());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Log.i("CPE", e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Log.i("IOE", e.getMessage());
		}
	    
		}
        catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Log.i("UEE", e.getMessage());
	    }
		//Log.i("ReturnValue", rtnString);
		return  rtnString.trim();
	}
	
	protected void onPreExecute() {
		viewCart.pg.show();
	}
	protected void onPostExecute(String rtnString) {
		viewCart.pg.dismiss();
		if (rtnString.equals("200")){
			viewCart.emptyCart();
		    Toast.makeText(viewCart.getApplicationContext(), "Your order uploaded successfully. Thank you.", Toast.LENGTH_LONG).show();
			viewCart.finish();
		}
  		//viewCart.LoginRoutine(RtnString);
  		try {
			this.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
