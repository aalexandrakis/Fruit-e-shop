package com.aalexandrakis.fruit_e_shop;

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
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;

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
		if (checkConnectivity()) {
			if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
				Async = (GetCategories) new GetCategories(this).execute(Commons.URL + "/getCategories");
			} else {
				Async = (GetCategories) new GetCategories(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Commons.URL + "/getCategories");
			}
		} else {
			showAlertDialog("Connectivity Error", "No Internet Connection");
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

	@Override
	protected ArrayList<Category> doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		String url = (String) arg0[0];
		InputStream rtnStream = null;

		HttpClient httpClient = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 30000);
		HttpConnectionParams.setSoTimeout(httpClient.getParams(), 30000);
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response;
		try {
			response = httpClient.execute(httpGet);
			rtnStream = response.getEntity().getContent();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//parse xml
		XmlPullParser xmlCategories = null;
		ArrayList<Category> categoryArrayList = new ArrayList<Category>();
		try {
			xmlCategories = XmlPullParserFactory.newInstance().newPullParser();
			xmlCategories.setInput(rtnStream, null);
			Integer eventtype = -1;
			String categoryCodeStr = null;
			Integer CategoryCode;
			String categoryDescr = null;
			eventtype = xmlCategories.getEventType();
			while (eventtype != XmlPullParser.END_DOCUMENT) {

				if (eventtype == XmlPullParser.START_TAG && xmlCategories.getName().equals("item_category")) {
					categoryCodeStr = "";
					categoryDescr = "";
				} else if (eventtype == XmlPullParser.START_TAG && xmlCategories.getName().equals("category")) {
						categoryDescr = xmlCategories.nextText();
				} else if (eventtype == XmlPullParser.START_TAG && xmlCategories.getName().equals("categoryid")) {
						categoryCodeStr = xmlCategories.nextText();
				} else if (eventtype == XmlPullParser.END_TAG && xmlCategories.getName().equals("item_category")) {
					CategoryCode = Integer.parseInt(categoryCodeStr);
					Category newCategory = new Category(CategoryCode,
							categoryDescr);
					categoryArrayList.add(newCategory);
				}else if (eventtype == XmlPullParser.END_TAG && xmlCategories.getName().equals("categories")) {
					break;
				}
				eventtype = xmlCategories.nextTag();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("doInBackground-xppe", e.getMessage());
			mainActivity.showAlertDialog("Error", "Data not found try later");
		} finally {
			Log.e("finally", categoryArrayList.toString());
		}
	
		return categoryArrayList;
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
