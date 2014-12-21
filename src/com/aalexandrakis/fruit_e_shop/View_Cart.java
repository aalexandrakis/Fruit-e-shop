package com.aalexandrakis.fruit_e_shop;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.*;
import android.widget.AdapterView.AdapterContextMenuInfo;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
//import android.widget.TextView;


public class View_Cart extends Login  {
	private ItemAdapter adapt;
	public ProgressDialog pgd;
	
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
	  TextView txtCartSummary = (TextView) findViewById(R.id.txtCartSummary);
	  fillListWithProducts(MyCartArray);
	  txtCartSummary.setText(GetMyCartSummary().toString());
	  if (MyCartArray.isEmpty()){
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
				FinishYourOrder();
			}
		}
	});
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
	        MyCartArray.remove(info.position);
	        fillListWithProducts(MyCartArray);
	        TextView txtCartSummary = (TextView) findViewById(R.id.txtCartSummary);
	        txtCartSummary.setText(GetMyCartSummary().toString());
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
		//Toast.makeText(getApplicationContext(), "Your cart summary is " + MyCartSummary.toString(), Toast.LENGTH_LONG).show();
		TextView txtCartSummary = (TextView) findViewById(R.id.txtCartSummary);
		txtCartSummary.setText(MyCartSummary.toString());
		
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
	
	
	protected void FinishYourOrder(){
		int i = 0;
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Document document = documentBuilder.newDocument();
		Element rootElement = document.createElement("order");//Parent Node of the xml
		document.appendChild(rootElement);
		for (i=0;i<MyCartArray.size();i++){
			Item ReadedItem = MyCartArray.get(i);
	         
			 Element childElementItem = document.createElement("item");//Child Node
			 childElementItem.setAttribute("itemid", ReadedItem.getItemCode().toString());
			 childElementItem.setAttribute("itemquan", ReadedItem.getItemQuantity().toString());
			 childElementItem.setAttribute("itemprice", ReadedItem.getItemPrice().toString());
			 rootElement.appendChild(childElementItem);
			//rootElement.appendChild(childElementItem);
			//Element childElementItem = document.createElement("item");//Child Node
			//rootElement.appendChild(childElementItem);
			
			//Element childElementItemId = document.createElement("itemid");//Child Node
			//childElementItemId .appendChild(document.createTextNode(ReadedItem.getItemCode().toString()));
			//childElementItem.appendChild(childElementItemId);

			//Element childElementItemDescr = document.createElement("itemdescr");//Child Node
			//childElementItemDescr .appendChild(document.createTextNode(ReadedItem.getItemDescr().toString()));
			//childElementItem.appendChild(childElementItemDescr);
			
			//Element childElementItemPrice = document.createElement("itemprice");//Child Node
			//childElementItemPrice .appendChild(document.createTextNode(ReadedItem.getItemPrice().toString()));
			//childElementItem.appendChild(childElementItemPrice);

			//Element childElementItemQuan = document.createElement("itemquan");//Child Node
			//childElementItemQuan .appendChild(document.createTextNode(ReadedItem.getItemQuantity().toString()));
			//childElementItem.appendChild(childElementItemQuan);
		
		}
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = factory.newTransformer();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Properties outFormat = new Properties();
		outFormat.setProperty(OutputKeys.INDENT, "yes");
		outFormat.setProperty(OutputKeys.METHOD, "xml");
		outFormat.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		outFormat.setProperty(OutputKeys.VERSION, "1.0");
		outFormat.setProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperties(outFormat);
		DOMSource domSource = new DOMSource(document.getDocumentElement());
		OutputStream output = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(output);
		try {
			transformer.transform(domSource, result);
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String strInputXML = output.toString(); //Storing into a string
		Log.i("XmlOrder", strInputXML);
		String Email = settings.getString("Email", "");
		CreateOrderAsync CreateOrderAsyncObject = new CreateOrderAsync(this);
		CreateOrderAsyncObject.execute(url_CreateOrder, Email, strInputXML);
	}
	
	public void EmptyCart() {
		MyCartArray.clear();
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
				EmptyCart();
				return false;
			}
		});
	    return true;
	}
}


////////////////////////////////////////////////////////////////////////
class CreateOrderAsync extends AsyncTask<String, String, String>{
    public View_Cart ThisActivity;
   
	CreateOrderAsync(View_Cart a){
    	ThisActivity = a;
    }
	
	@Override
	protected String doInBackground(String... arg0) {
		//String url_str = "http://" + arg0[3] + arg0[0];
		String url_str = arg0[0];
		Log.i("url", url_str);
		String email = arg0[1];
		Log.i("email", email);
		String xmlString = arg0[2];
		Log.i("xml", xmlString);
		String rtnString = "";
		HttpClient httpClient = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 30000);
        HttpConnectionParams.setSoTimeout(httpClient.getParams(), 30000);
        HttpPost httpPost = new HttpPost(url_str);
        Log.i("HttpPost", "New HttpPost");
     // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));    
	        params.add(new BasicNameValuePair("orderXML", xmlString));
	    Log.i("List", "NameValuePair");    
        try {
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
	    httpPost.setHeader("Host", "aalexandrakis.freevar.com");
		entity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
		entity.setContentEncoding("UTF-8");
		entity.setChunked(true);
	    httpPost.setEntity(entity);
	    HttpResponse response;
	    Log.i("response", "httpresponse");
		try {
			response = httpClient.execute(httpPost);
			
			BufferedReader br = null;
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			rtnString = (String) br.readLine();
			Log.i("ReadLine", rtnString);
			br.close();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i("CPE", e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i("IOE", e.getMessage());
		}
	    
		}
        catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i("UEE", e.getMessage());
	    }
		Log.i("ReturnValue", rtnString);
		return  rtnString.trim();
	}
	
	protected void onPreExecute() {
		ThisActivity.pg.show();
	}
	protected void onPostExecute(String RtnString) {
		Log.i("onPostExecute", RtnString);
		ThisActivity.pg.dismiss();
		if (RtnString.startsWith("1")){
			ThisActivity.EmptyCart();
		    Toast.makeText(ThisActivity.getApplicationContext(), "Your order uploaded successfully. Thank you.", Toast.LENGTH_LONG).show();
		}
  		//ThisActivity.LoginRoutine(RtnString);
  		try {
			this.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
