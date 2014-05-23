package com.aalexandrakis.fruit_e_shop;

import android.os.AsyncTask;
import android.os.Bundle;
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
import android.util.Log;
import android.view.View;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class New_user extends Login  {
	
	
	Button btnSignIn;
	EditText edtName;
	EditText edtAddress;
	EditText edtPhone;
	EditText edtCity;
	EditText edtEmail;
	EditText edtPassword;
	EditText edtConfPassword;
	
	String strName;
	String strAddress;
	String strPhone;
	String strCity;
	String strEmail;
	String strPassword;
	String strConfPassword;
	New_user NewUserActivity = this;
	
    protected void onPause() {
    	//if (SingInAsync != null &&
    	//	SingInAsync.getStatus() != SingInAsyncTask.Status.FINISHED){
    	//      SingInAsync.cancel(true);
    	//}
          
    	super.onPause();
    }
    
    	@Override
	protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	   setContentView(R.layout.user_account);
	   TextView txtUserHeader = (TextView) findViewById(R.id.txtUserHeader);
	   txtUserHeader.setText(R.string.NewUserHeader);
	   
	   pg = new ProgressDialog(this);
	   pg.setTitle("Signing in");
	   pg.setMessage("Wait to confirm sign in information");
	
	   edtName= (EditText) findViewById(R.id.edtName);
	   edtAddress= (EditText) findViewById(R.id.edtAddress);
	   edtPhone= (EditText) findViewById(R.id.edtPhone);
	   edtCity= (EditText) findViewById(R.id.edtCity);
	   edtEmail= (EditText) findViewById(R.id.edtEmail);
	   edtPassword= (EditText) findViewById(R.id.edtPassword);
	   edtConfPassword= (EditText) findViewById(R.id.edtConfPassword);
	   btnSignIn = (Button) findViewById(R.id.btnSignIn);
	   
	   
	   
       btnSignIn.setOnClickListener(new View.OnClickListener() {
   	       @Override
           public void onClick(View arg0) {
		        if (ValidateRoutine()){
		           GetHttpResponseSingInAsync SignInTask = new GetHttpResponseSingInAsync(NewUserActivity);
		           SignInTask.execute(url_NewUser, 
		        		              edtName.getText().toString(),
		        		              edtAddress.getText().toString(),
		        		              edtCity.getText().toString(),
		        		              edtPhone.getText().toString(),
		        		              edtEmail.getText().toString(),
		        		              edtPassword.getText().toString());
		        }
   	       }
 	      });
    	}
	
    	
   public void SignInRoutine(String HttpResp) {
	      
 		  if (HttpResp.startsWith("1")) {
 			  Log.i("ServerResponse", HttpResp);
   	          Toast.makeText(getApplicationContext(), edtName.getText().toString() + " you have successfully sign in. Pleas try to log in", Toast.LENGTH_LONG).show();
   		      Intent openstartingpoint=new Intent("com.aalexandrakis.fruit_e_shop.Login");
   	 	      startActivity(openstartingpoint);
   	 	      finish();
   		   } 
 		   else if (HttpResp.startsWith("2")) {
 			   Log.i("ServerResponse", HttpResp);
 			   ShowAlertDialog("Error sign in information", "Email already exists");
  	 	   } else {
  	 		   Log.i("ServerResponse", HttpResp);
 		       ShowAlertDialog("Sign in error", "Please try later"); 
           }

   }
	
	
    
    protected boolean ValidateRoutine() {
    	Log.i("Validate", "OK");
	       if (checkConnectivity()==false){
	    	   Log.i("ShowAlert", "OK");
      	       ShowAlertDialog("Connectivity Error", "No Internet Connection"); 
               return false;
           }
          if (edtName.length()==0){
      	         ShowAlertDialog("Missing Information", "Your name is missing");
      	         edtName.requestFocus();
                 return false;
          }
          if (edtAddress.length()==0){
   	         ShowAlertDialog("Missing Information", "Your address is missing");
   	         edtAddress.requestFocus();
              return false;
          }
          if (edtCity.length()==0){
   	         ShowAlertDialog("Missing Information", "Your city is missing");
   	          edtCity.requestFocus();
              return false;
          }
          if (edtPhone.length()==0){
    	       ShowAlertDialog("Missing Information", "Your phone is missing");
    	       edtPhone.requestFocus();
               return false;
           }
          if (edtEmail.length()==0){
   	          ShowAlertDialog("Missing Information", "Your email is missing");
   	          edtEmail.requestFocus();
              return false;
          }
          if (edtPassword.length()==0){
   	          ShowAlertDialog("Missing Information", "Your password is missing");
   	          edtPassword.requestFocus();
              return false;
          }
          if (edtConfPassword.length()==0){
   	          ShowAlertDialog("Missing Information", "Your password confirmation is missing");
   	          edtConfPassword.requestFocus();
              return false;
          }
          if (!edtConfPassword.getText().toString().equals(edtPassword.getText().toString())){
        	  Log.i("Password", edtPassword.getText().toString());
        	  Log.i("ConfPassword", edtConfPassword.getText().toString());
   	          ShowAlertDialog("Password Missmatch", "Your password differs from password confirmation");
   	          edtConfPassword.requestFocus();
              return false;
          }
          return true;
    }
	
}


class GetHttpResponseSingInAsync extends AsyncTask<String, String, String>{
    public New_user SingInAsync;
   
	GetHttpResponseSingInAsync(New_user a){
    	SingInAsync = a;
    }
	
	@Override
	protected String doInBackground(String... arg0) {
		//String url_str = "http://" + arg0[3] + arg0[0];
		String url_str = arg0[0];
		String name = arg0[1];
		String address = arg0[2];
		String city = arg0[3];
		String phone = arg0[4];
		String email = arg0[5];
		String password = arg0[6];
		String rtnString = "";
		HttpClient httpClient = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10000);
        HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10000);
        HttpPost httpPost = new HttpPost(url_str);
        Log.i("HttpPost", "New HttpPost");
     // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", name));    
            params.add(new BasicNameValuePair("address", address));
            params.add(new BasicNameValuePair("city", city));
            params.add(new BasicNameValuePair("phone", phone));
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("password", password));
            
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
		SingInAsync.pg.show();
	}
	protected void onPostExecute(String RtnString) {
		SingInAsync.pg.dismiss();
  		SingInAsync.SignInRoutine(RtnString);
  		try {
			this.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


