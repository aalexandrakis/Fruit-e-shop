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
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;
import android.view.View;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class Update_user extends Login  {
	
	
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
	Update_user UpdateUserActivity = this;
	
	ProgressDialog GetInfoPg;
	
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
	   txtUserHeader.setText(R.string.UpdateUserHeader);
	   
	   pg = new ProgressDialog(this);
	   pg.setTitle("Change User Information");
	   pg.setMessage("Please wait to update your information");
	
	   GetInfoPg = new ProgressDialog(this);
	   GetInfoPg.setTitle("Retrieve Information");
	   GetInfoPg.setMessage("Please wait to retrieve your user account data.");
	   
	   edtName= (EditText) findViewById(R.id.edtName);
	   edtAddress= (EditText) findViewById(R.id.edtAddress);
	   edtPhone= (EditText) findViewById(R.id.edtPhone);
	   edtCity= (EditText) findViewById(R.id.edtCity);
	   edtEmail= (EditText) findViewById(R.id.edtEmail);
	   edtPassword= (EditText) findViewById(R.id.edtPassword);
	   edtConfPassword= (EditText) findViewById(R.id.edtConfPassword);
	   btnSignIn = (Button) findViewById(R.id.btnSignIn);
	   
	   edtEmail.setEnabled(false);
	   btnSignIn.setText("Update");
	   btnSignIn.setEnabled(false);
	   
	   String email="";
	   email = settings.getString("Email", "");
	   
	   edtEmail.setText(email);
	   edtPassword.setText(settings.getString("Password", ""));
	   
	   //Retrieve user account 
       GetUserInfoAsync GetUserInfoTask = new GetUserInfoAsync(UpdateUserActivity);
       GetUserInfoTask.execute(url_GetUserInfo, email);
       

       btnSignIn.setOnClickListener(new View.OnClickListener() {
   	       @Override
           public void onClick(View arg0) {
		        if (ValidateRoutine()){
		           UpdateUserInfoAsync UpdateUserTask = new UpdateUserInfoAsync(UpdateUserActivity);
		           UpdateUserTask.execute(url_UpdateUser, 
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
	
    	
   public void UpdateRoutine(String HttpResp) {
	      
 		  if (HttpResp.startsWith("1")) {
 			  Log.i("ServerResponse", HttpResp);
   	          Toast.makeText(getApplicationContext(), edtName.getText().toString() + " your data updated succesfully", Toast.LENGTH_LONG).show();
   		      Intent openstartingpoint=new Intent("com.aalexandrakis.fruit_e_shop.MainMenu");
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

    public void LoadData(ArrayList<String> UserInf){
    	edtName.setText(UserInf.get(0));
    	edtAddress.setText(UserInf.get(1));
    	edtCity.setText(UserInf.get(2));
    	edtPhone.setText(UserInf.get(3));
    	btnSignIn.setEnabled(true);
    }
}

///////////////////////////////////////////////////////////////////////////////
class UpdateUserInfoAsync extends AsyncTask<String, String, String>{
    public Update_user ThisActivity;
   
	UpdateUserInfoAsync(Update_user a){
    	ThisActivity = a;
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
		Log.e("doInBackGround", "Parms ok");
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
		ThisActivity.pg.show();
	}
	protected void onPostExecute(String RtnString) {
		ThisActivity.pg.dismiss();
  		ThisActivity.UpdateRoutine(RtnString);
  		try {
			this.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}



/////////////////////////////////////////////////////////////////////////
class GetUserInfoAsync extends AsyncTask<Object, ArrayList<String>, ArrayList<String>> {
		private InputStream intStrm;
		private String url;
		private String requested_email;
		private ArrayList<String> UserInfo = new ArrayList<String>();
		
		boolean Result;
		public Update_user ThisActivity;


		public GetUserInfoAsync(Update_user a){
			ThisActivity = a;
		}

	
		protected ArrayList<String> doInBackground(Object... params) {
			// TODO Auto-generated method stub
			url = (String) params[0];
			requested_email = (String) params[1];
			Result = false;

			try {
				List<NameValuePair> paramsA = new ArrayList<NameValuePair>();
				paramsA.add(new BasicNameValuePair("email", requested_email));
				Log.i("paramsA.requested_email", paramsA.get(0).toString());
				intStrm = this.getHttpResponseXml(paramsA, url);
				Log.i("HttpResponse", "ends here");
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				Log.i("doInBackground-cpe", e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				Log.i("doInBackground-ioe", e.getMessage());
			}
			XmlPullParser xmlUser = null;  
			try {
				xmlUser = XmlPullParserFactory.newInstance().newPullParser();
				xmlUser.setInput(intStrm, null);	
				Log.i("xmlUser.SetInput", "ends here");
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				Log.i("doInBackground-xppe", e.getMessage());
				ThisActivity.ShowAlertDialog("Error","Data not found. Please try later");
				this.cancel(true);
			}
			UserInfo = this.FillStringArray(xmlUser);
			Result = true;
			return UserInfo;
		}

public ArrayList<String>  FillStringArray(XmlPullParser xmlUser) {
		Integer eventtype = -1;
		ArrayList<String> UserInf = new ArrayList<String>();
		
		while (eventtype!=XmlPullParser.END_DOCUMENT){
			if (eventtype==XmlPullParser.START_TAG){
				if (xmlUser.getName().equals("username")
					|| xmlUser.getName().equals("address")
					|| xmlUser.getName().equals("city")
					|| xmlUser.getName().equals("phone")){
						try {
							UserInf.add(xmlUser.nextText().toString());
							//UserInf.add(new BasicNameValuePair("UserName", xmlUser.nextText().toString()));
							Log.i("UserName", UserInf.get(0).toString());
						} catch (XmlPullParserException e) {
							Log.e("FillStringArry-nextText", e.getMessage());
						} catch (IOException e) {
							Log.e("FillStringArry-nextText", e.getMessage());
						}
				}
			}	
			try {
				eventtype = xmlUser.next();
			} catch (XmlPullParserException e) {
				Log.i("XmlPullParserException-next", e.getMessage());
			} catch (IOException e) {
				Log.i("IOException-next", e.getMessage());
			}	
		}
		Log.i("doInBackground", "end loop");
		
		return UserInf;
}


		InputStream  getHttpResponseXml(List<NameValuePair> params, String url_str) throws ClientProtocolException, IOException {
			Log.i("url", url_str);
			Log.i("email", params.get(0).toString());

			HttpClient httpClient = new DefaultHttpClient();
			HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10000);
			HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10000);
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
		protected void onPostExecute(ArrayList<String> User) {
			Log.i("onPostExecute", "onPostExecute");
			//	Log.i("fillListWithProducts", Items.get(1).getItemDescr());
			ThisActivity.LoadData(User);
			ThisActivity.GetInfoPg.dismiss();
		}
		@Override
		protected void onPreExecute(){
			ThisActivity.GetInfoPg.show();
		}





}