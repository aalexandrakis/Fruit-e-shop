package com.aalexandrakis.fruit_e_shop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class New_order extends Login {
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.choice_menu_categories, menu);
		menu.findItem(R.id.ViewMyCart).setIntent(
				new Intent("com.aalexandrakis.fruit_e_shop.View_Cart"));
		menu.findItem(R.id.EmptyCart).setOnMenuItemClickListener(
				new MenuItem.OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						// TODO Auto-generated method stub
						MyCartArray.clear();
						return false;
					}
				});
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.new_order);
		GetCategories Async = null;
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
       	 Async = (GetCategories) new GetCategories(this).execute(url_getCategories);
        } else {
       	 Async = (GetCategories) new GetCategories(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url_getCategories);
        }
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}

	public void fillListWithCategories(ArrayList<Category> CategoriesArray) {
		Log.i("FillListWithCategories", CategoriesArray.get(0)
				.getCategoryCode().toString());
		ListView lstView = (ListView) findViewById(R.id.CartList);
		CategoryAdapter adapt = new CategoryAdapter(this,
				R.layout.list_category_item, CategoriesArray);
		lstView.setAdapter(adapt);
		lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				TextView txtView = (TextView) arg1
						.findViewById(R.id.txtCategoryCode);
				String CategoryCodeText = txtView.getText().toString();
				Intent nextactivity = new Intent(
						"com.aalexandrakis.fruit_e_shop.Select_item");
				nextactivity.putExtra("CatId",
						Integer.parseInt(CategoryCodeText));
				startActivity(nextactivity);
			}
		});

	}

	
}

class GetCategories extends AsyncTask<String, ArrayList<Category>, ArrayList<Category>> {

	New_order mainActivity;
	ProgressDialog pgd;
		
	public GetCategories(New_order mainActivity) {
		super();
		this.mainActivity = mainActivity;
	}

	static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
	@Override
	protected ArrayList<Category> doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		String url = (String) arg0[0];
		InputStream rtnStream = null;
		
		//get xml from internet or local storage
		if (mainActivity.checkConnectivity()){
			HttpClient httpClient = new DefaultHttpClient();
	        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10000);
	        HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10000);
	        HttpPost httpPost = new HttpPost(url);  
	        HttpResponse response;
			try {
				response = httpClient.execute(httpPost);
		        rtnStream = response.getEntity().getContent();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("Get XML from internet", e.getMessage());
			}
		} else {
			final File file = new File(Login.app_path, "Categories.xml");
			if (file.exists()){
				try {
					rtnStream = new FileInputStream(file);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					Log.e("File from local storage", e.getMessage());
					
				}
				Log.i("No internet connection", "Getting data from "+file.toString());
			}
		}
		//parse xml
		XmlPullParser xmlCategories = null;
		ArrayList<Category> CategoriesA = new ArrayList<Category>();
		try {
			xmlCategories = XmlPullParserFactory.newInstance().newPullParser();
			xmlCategories.setInput(rtnStream, null);
			Integer eventtype = -1;
			String CategoryCodeStr = null;
			Integer CategoryCode;
			String CategoryDescr = null;
			eventtype = xmlCategories.getEventType();
			while (eventtype != XmlPullParser.END_DOCUMENT) {
				if (eventtype == XmlPullParser.START_TAG) {
					if (xmlCategories.getName().equals("category")) {
						CategoryCodeStr = xmlCategories.getAttributeValue(null,
								"categoryid");
						CategoryDescr = xmlCategories.getAttributeValue(null,
								"categoryname");
						CategoryCode = Integer.parseInt(CategoryCodeStr);
						Category NewCategory = new Category(CategoryCode,
								CategoryDescr);
						CategoriesA.add(NewCategory);
					}
				} if (eventtype == XmlPullParser.END_TAG && xmlCategories.getName().equals("categories")){
					break;
				}
				eventtype = xmlCategories.next();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("doInBackground-xppe", e.getMessage());
			mainActivity.ShowAlertDialog("Error", "Data not found try later");
		} finally {
			Log.e("finally", CategoriesA.toString());
		}
	
		

		return CategoriesA;
	}

	@Override
	protected void onPostExecute(ArrayList<Category> result) {
		// TODO Auto-generated method stub
		Log.e("onPostExecute", result.get(0).getCategoryDescr());
		super.onPostExecute(result);
		mainActivity.fillListWithCategories(result);
		pgd.dismiss();
		
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		pgd = new ProgressDialog(mainActivity);
		pgd.setTitle("Loading...");
		pgd.setMessage("Please wait to load data");
		pgd.show();
	}

	@Override
	protected void onProgressUpdate(ArrayList<Category>... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
	}

}
