package com.aalexandrakis.fruit_e_shop;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;




public class Contactus extends Login  {
    Button btnSend;
    EditText editSenderEmail;
    EditText editSubject;
    EditText editEmailText;
    
    public ProgressDialog ContactUsPg;
    public ContactUsAsyncTask ContactUsAsync;
    public Contactus THISACTIVITY=this;
    
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
        		ShowAlertDialog("Conectivity Error", "No Internet Connection");
        		return;
        	}
	 	     
		}
	});
	   
    }    	 	
 
   void ContactUs_Routine(){
     if (editSenderEmail.length()==0){
    	  ShowAlertDialog("Missing Information", "Sender email is missing"); 
          return;
     }
     if (editSubject.length()==0){
       	  ShowAlertDialog("Missing Information", "Subject is missing"); 
          return;
     }
     if (editEmailText.length()==0){
   	     ShowAlertDialog("Missing Information", "Email text is missing"); 
         return;
     }
     ContactUsAsync = (ContactUsAsyncTask) new ContactUsAsyncTask(THISACTIVITY).execute(url_Contactus, editSenderEmail.getText().toString(), editSubject.getText().toString(), editEmailText.getText().toString());
    }    
 }


class ContactUsAsyncTask extends AsyncTask<String, String, String>{
    public Contactus MainClass;
   
	ContactUsAsyncTask(Contactus a){
    	MainClass = a;
    }
	
	@Override
	protected String doInBackground(String... arg0) {
		//String url_str = "http://" + arg0[3] + arg0[0];
		String url_str = arg0[0];
		Log.i("url", url_str);
		String subject = arg0[1];
		Log.i("subject", subject);
		String mail = arg0[2];
		Log.i("mail", mail);
		String yoursmail = arg0[3];
		Log.i("yoursemail", yoursmail);
		String rtnString = "";
		HttpClient httpClient = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 30000);
        HttpConnectionParams.setSoTimeout(httpClient.getParams(), 30000);
        HttpPost httpPost = new HttpPost(url_str);
        Log.i("HttpPost", "New HttpPost");
     // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
           params.add(new BasicNameValuePair("subject", subject));
           params.add(new BasicNameValuePair("mail", mail));
           params.add(new BasicNameValuePair("yoursmail", yoursmail));
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
			Log.i("ReadLine", "rtnString");
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
		MainClass.ContactUsPg.show();
	}
	protected void onPostExecute(String RtnString) {
		MainClass.ContactUsPg.dismiss();
  		try {
			this.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  		if (RtnString.startsWith("0")){
  			MainClass.ShowAlertDialog("Message Send Failure", "Please try later");
	    }
	    if (RtnString.startsWith("1")){
	    	MainClass.ShowAlertDialog("Message Send Successfully", "We are going to contact with you as soon as possible");
	    }
	    
  		
	}
}