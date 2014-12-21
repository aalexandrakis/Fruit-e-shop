package com.aalexandrakis.fruit_e_shop;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;




public class ContactUs extends Login  {
    Button btnSend;
    EditText editSenderEmail;
    EditText editSubject;
    EditText editEmailText;
    
    public ProgressDialog ContactUsPg;
    public ContactUsAsyncTask ContactUsAsync;
    public ContactUs THISACTIVITY=this;
    
    SharedPreferences settings;
    protected void onPause() {
    	if (ContactUsAsync != null &&
    		ContactUsAsync.getStatus() != AsyncTask.Status.FINISHED){
    		    ContactUsAsync.cancel(true);
    	}
    	super.onPause();
    }
    @Override
	protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	   setContentView(R.layout.contact_us);
	
	   ContactUsPg = new ProgressDialog(this);
	   ContactUsPg.setTitle("Contact Us");
	   ContactUsPg.setMessage("Wait to send your message");
	
	   editSenderEmail = (EditText) findViewById(R.id.editSenderEmail);
	   editSubject = (EditText) findViewById(R.id.editSubject);
	   editEmailText = (EditText) findViewById(R.id.editEmailText);
	   settings = getSharedPreferences("Fruit_e_Shop_Pref", MODE_PRIVATE);
	   editSenderEmail.requestFocus();
	   if (settings.contains("Email")){
		   editSenderEmail.setText(settings.getString("Email", ""));
	   }
	   btnSend = (Button) findViewById(R.id.btnSignIn);
 	   btnSend.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (checkConnectivity()){
				ContactUs_Routine(); 	
        	} else {
        		showAlertDialog("Conectivity Error", "No Internet Connection");
        		return;
        	}
	 	     
		}
	});
	   
    }    	 	
 
   void ContactUs_Routine(){
     if (editSenderEmail.length()==0){
    	  showAlertDialog("Missing Information", "Sender email is missing");
          return;
     }
     if (editSubject.length()==0){
       	  showAlertDialog("Missing Information", "Subject is missing");
          return;
     }
     if (editEmailText.length()==0){
   	     showAlertDialog("Missing Information", "Email text is missing");
         return;
     }
     ContactUsAsync = (ContactUsAsyncTask) new ContactUsAsyncTask(THISACTIVITY).execute(Commons.URL + "/contactUs", editSenderEmail.getText().toString(), editSubject.getText().toString(), editEmailText.getText().toString());
    }    
 }


class ContactUsAsyncTask extends AsyncTask<String, JSONObject, JSONObject>{
    public ContactUs MainClass;
   
	ContactUsAsyncTask(ContactUs a){
    	MainClass = a;
    }
	
	@Override
	protected JSONObject doInBackground(String... arg0) {
		//String url_str = "http://" + arg0[3] + arg0[0];
		JSONObject jsonResponse = new JSONObject();
		String url_str = arg0[0];
		StringBuilder rtnString = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 30000);
        HttpConnectionParams.setSoTimeout(httpClient.getParams(), 30000);
        HttpPost httpPost = new HttpPost(url_str);
     // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("email", arg0[1]));
		params.add(new BasicNameValuePair("subject", arg0[2]));
		params.add(new BasicNameValuePair("content", arg0[3]));
        try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			entity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
			entity.setContentEncoding("UTF-8");
			entity.setChunked(true);
			httpPost.setEntity(entity);
			HttpResponse response;

			response = httpClient.execute(httpPost);

			BufferedReader br = null;
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = null;
			while ((line = br.readLine()) != null){
				rtnString.append(line);
			}
			jsonResponse = new JSONObject(rtnString.toString());
			br.close();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			jsonResponse = null;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			jsonResponse = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			jsonResponse = null;
		} catch (JSONException e) {
			e.printStackTrace();
			jsonResponse = null;
		} finally {
			return jsonResponse;
		}
	}
	
	protected void onPreExecute() {
		MainClass.ContactUsPg.show();
	}
	protected void onPostExecute(JSONObject jsonResponse) {
		MainClass.ContactUsPg.dismiss();
  		try {
			this.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (jsonResponse != null && jsonResponse.optString("status").equals("SUCCESS")){
			MainClass.showAlertDialog(jsonResponse.optString("status"), jsonResponse.optString("message"));
		} else if (jsonResponse != null) {
			MainClass.showAlertDialog(jsonResponse.optString("status"), jsonResponse.optString("message"));
		} else {
			MainClass.showAlertDialog("FAILED", "Please try later");
		}
	}
}