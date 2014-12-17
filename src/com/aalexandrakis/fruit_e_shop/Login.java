package com.aalexandrakis.fruit_e_shop;

import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

@SuppressLint("NewApi")
public class Login extends Activity  {
	public static final String FRUIT_E_SHOP_PREF = "Fruit_e_Shop_Pref" ;
	public static final String MYCART = "MyCart" ;
	public static ArrayList<Item> MyCartArray = new ArrayList<Item>();
	//private static final String url_login = "http://10.0.2.2/android-fruit-e-shop/androidLogin.php";
	//private static final String url_forgot = "http://10.0.2.2/android-fruit-e-shop/androidForgotPassword.php";
	//public static final String url_getCategories = "http://10.0.2.2/android-fruit-e-shop/androidSelectCategoryXML.php";
	//public static final String url_Contactus="http://10.0.2.2/android-fruit-e-shop/androidLogin.php";
	public static final String url_forgot = "http://www.aalexandrakis.freevar.com/android-fruit-e-shop/androidforgotpassword.php";
	public static final String url_getCategories = "http://www.aalexandrakis.freevar.com/android-fruit-e-shop/androidSelectCategoryXML.php";
    public static final String url_Contactus="http://www.aalexandrakis.freevar.com/android-fruit-e-shop/androidcontactus.php";
    public static final String url_Products="http://www.aalexandrakis.freevar.com/android-fruit-e-shop/androidSelectProductsXML.php";
    public static final String url_NewUser="http://www.aalexandrakis.freevar.com/android-fruit-e-shop/androidNewUser.php";
    public static final String url_UpdateUser="http://www.aalexandrakis.freevar.com/android-fruit-e-shop/androidUpdateUser.php";
    public static final String url_GetUserInfo="http://www.aalexandrakis.freevar.com/android-fruit-e-shop/androidGetUserInfo.php";
    public static final String url_GetOrders="http://www.aalexandrakis.freevar.com/android-fruit-e-shop/androidGetOrders.php";
    public static final String url_CreateOrder="http://www.aalexandrakis.freevar.com/android-fruit-e-shop/androidCreateOrder.php";
    public static final String url_GetOrderedItems="http://www.aalexandrakis.freevar.com/android-fruit-e-shop/androidGetOrderedItems.php";
    public static final String url_GetAllOrderedItems="http://www.aalexandrakis.freevar.com/android-fruit-e-shop/androidGetAllOrderedItems.php";
    public static String app_path="";
    
    
	public static Boolean rememberPassword = false;
	
	public ProgressDialog pg;
	public ProgressDialog changePassPg;
	public postHttpResponseAsync async;
	public ForgotPassword forgotPasswordAsync;
    
    Button btnConnect;
    Button btnForgot;
    Button btnNewUser;
	TextView editEmail;
    TextView editPassword;
    CheckBox RememberPass;
    String email;
    String password;
    
    public SharedPreferences settings;
    
     
    protected void onPause() {
    	if (async != null &&
    		async.getStatus() != AsyncTask.Status.FINISHED){
    	       async.cancel(true);
    	}
    	if (forgotPasswordAsync != null &&
    		forgotPasswordAsync.getStatus() != AsyncTask.Status.FINISHED){
    	      forgotPasswordAsync.cancel(true);
    	}      
    	super.onPause();
    }
    
    	@Override
	protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	   //settings = getSharedPreferences(FRUIT_E_SHOP_PREF, MODE_PRIVATE);
	   
	   setContentView(R.layout.login);
	   settings = getSharedPreferences(FRUIT_E_SHOP_PREF, MODE_PRIVATE);
	   pg = new ProgressDialog(this);
	   pg.setTitle("Loging in");
	   pg.setMessage("Wait to confirm login information");
	
	   changePassPg = new ProgressDialog(this);
	   changePassPg.setTitle("Forgot Password");
	   changePassPg.setMessage("Wait to re-new your password");
	
	   editEmail = (TextView) findViewById(R.id.editEmail);
       editPassword = (TextView) findViewById(R.id.editPassword);
       RememberPass = (CheckBox) findViewById(R.id.checkBox1);
	   btnConnect = (Button) findViewById(R.id.btnConnect);
	   btnForgot = (Button) findViewById(R.id.btnForgot);
	   btnNewUser = (Button) findViewById(R.id.btnNewUser);
	   getSharedPreferenses();

	   editEmail.setText("aalexandrakis@hotmail.com");
	   editPassword.setText("b12021982");
	   if (settings.getString("SaveTo", getResources().getString(R.string.SaveToMemory)).equals(
		   getResources().getString(R.string.SaveToMemory))){
		  		Login.app_path = getFilesDir().toString();
	   } else {
		   CreateSdCardDirectory();
		   Login.app_path = Environment.getExternalStorageDirectory()+"/Android/data/"+getApplicationContext().getPackageName();
	   }
	   
	   btnForgot.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			forgot_Password();
		}
	});
	   
       btnConnect.setOnClickListener(new View.OnClickListener() {
   	       @Override
           public void onClick(View arg0) {
		        Login_routine();
   	       }
 	      });
       
       btnNewUser.setOnClickListener(new View.OnClickListener() {
   	       @Override
           public void onClick(View arg0) {
   	    	if (checkConnectivity()==true){
   	    	   Intent a = new Intent("com.aalexandrakis.fruit_e_shop.New_user");
		        startActivity(a);
        	} else {
        		ShowAlertDialog("Connectivity Error", "No Internet Connection");
        	}
		     
   	       }
 	      });
    	}
	
    	
   public void LoginRoutine(JSONObject jsonCust) {
 		   if (jsonCust.optString("email") != "") {
   		     SharedPreferences.Editor prefEditor = settings.edit();
   	         prefEditor.putString("Email", email);
   	         prefEditor.putString("Password", password);
   	         prefEditor.putBoolean("Remember", rememberPassword);
   	         prefEditor.putString("SaveTo", getResources().getString(R.string.SaveToMemory));
   	         prefEditor.commit();
   	         
   		     Intent openstartingpoint=new Intent("com.aalexandrakis.fruit_e_shop.MainMenu");
   	 	     startActivity(openstartingpoint);
   	 	     finish();
   		   } else {
   		     ShowAlertDialog("Error login information", "Username or pasword not valid"); 
             return;
   		   }

       }
	
    
	public boolean  checkConnectivity(){
		try {
		    ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		    return conMgr.getActiveNetworkInfo().isConnected();
		}
		catch (java.lang.NullPointerException ex)
		{
			return false;
		}
	}
	
    public void ShowAlertDialog(String title, String message){
    	new AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
                // continue with delete
            }
         })
         .show();
    }
         
    
    public void getSharedPreferenses(){
    	
    	try{
    		if (settings.contains("Remember")){
    			email=settings.getString("Email", email);
      	        password=settings.getString("Password", password);
      		    rememberPassword = settings.getBoolean("Remember", rememberPassword);
                autoFill();
    		}
    	    } catch (ClassCastException CCE) {
    	    	
    	    }
    }
    
    protected void autoFill(){
    	if (rememberPassword){
    		editEmail.setText(email);
    		editPassword.setText(password);
    		RememberPass.setChecked(rememberPassword);
    	}
    }
    
    protected void Login_routine() {
	       email=editEmail.getText().toString();
	       password=editPassword.getText().toString();
		   rememberPassword = (Boolean) RememberPass.isChecked();
		   
           if (email.length()==0){
      	      ShowAlertDialog("Missing Information", "Username or pasword not valid"); 
              return;
           }
           if (password.length()==0){
     	      ShowAlertDialog("Missing Information", "Username or pasword not valid"); 
              return;
          }
          if (checkConnectivity()==false) {
			   if(settings.getBoolean("Remember", false)==false){
				   ShowAlertDialog("Connectivity Error", "No Internet Connection"); 
		           return;   
			   } else {
				   Intent openstartingpoint=new Intent("com.aalexandrakis.fruit_e_shop.MainMenu");
		   	 	   startActivity(openstartingpoint);
		   	 	   this.finish();
		   	 	   return;
			   }
      	      
          } 
         if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
        	 async = (postHttpResponseAsync) new postHttpResponseAsync(this).execute(Commons.URL + "/login", email, password);
         } else {
        	 async = (postHttpResponseAsync) new postHttpResponseAsync(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Commons.URL + "/login", email, password);
         }
     }
	
    protected void forgot_Password() {
		 email=editEmail.getText().toString();
	     password=editPassword.getText().toString();
		 rememberPassword = (Boolean) RememberPass.isChecked();
		 
         if (checkConnectivity()==false){
      	  ShowAlertDialog("Connectivity Error", "No Internet Connection"); 
            return;
         }
        if (email.length()==0){
      	    ShowAlertDialog("Missing Information", "Username or pasword not valid"); 
            return;
         }
		List<NameValuePair> params_forgot = new ArrayList<NameValuePair>();
	      params_forgot.add(new BasicNameValuePair("email", email));
	    forgotPasswordAsync = (ForgotPassword) new ForgotPassword(this).execute(Commons.URL + "/resetPassword" , email);
	 }
    
    public Float GetMyCartSummary(){
    	Float MyCartSummary=Float.parseFloat("0.0");
    	int i;
    	Log.d("GetMyCartSummary",String.valueOf(MyCartArray.size()));
    	for (i=0;i<MyCartArray.size();i++){
    		Log.d("GetMyCartSummary",MyCartArray.get(i).getItemDescr());
    		Log.d("GetMyCartSummary",MyCartArray.get(i).getItemPrice().toString());
    		Log.d("GetMyCartSummary",MyCartArray.get(i).getItemQuantity().toString());
    		MyCartSummary = MyCartSummary + (MyCartArray.get(i).getItemPrice() * MyCartArray.get(i).getItemQuantity());
    	}
    	return MyCartSummary;
    }
    
    public Float ReturnQuantityIfExists(int ItemCode){
    	int i;
    	//Log.d("ReturnQuantity","Current ItemCode="+String.valueOf(ItemCode));
    	for (i=0;i<MyCartArray.size();i++){
    		//Log.d("ReturnQuantity","MyCartArray ItemCode="+MyCartArray.get(i).getItemCode().toString());
    		if (ItemCode == MyCartArray.get(i).getItemCode()){
    			return MyCartArray.get(i).getItemQuantity();
    		}
    	}
    	return Float.parseFloat("0.0");
    }

    public void CreateSdCardDirectory(){
    	File AndroidDir = new File(Environment.getExternalStorageDirectory().toString()+"/Android");
    	if (!AndroidDir.isDirectory()){
    		AndroidDir.mkdir();
    		Log.d("Create Android Directory", AndroidDir.getName());
    	}
    	File AndroidDataDir = new File(Environment.getExternalStorageDirectory().toString()+"/Android/data");
    	if (!AndroidDataDir.isDirectory()){
    		AndroidDataDir.mkdir();
    		Log.d("Create Android Data Directory", AndroidDataDir.getName());
    	}
    	File PackageDir = new File(Environment.getExternalStorageDirectory().toString()+"/Android/data/"+ getApplicationContext().getPackageName());
    	if (!PackageDir.isDirectory()){
    		PackageDir.mkdir();
    		Log.d("Create Package Directory", PackageDir.getName());
    	}
    }
    
}


class postHttpResponseAsync extends AsyncTask<String, JSONObject, JSONObject>{
    private Login ThisActivity;
   
	postHttpResponseAsync(Login a){
    	ThisActivity = a;
    }
	
	@Override
	protected JSONObject doInBackground(String... arg0) {
		JSONObject jsonCust = new JSONObject();
		HttpClient httpClient = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10000);
        HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10000);
        HttpPost httpPost = new HttpPost(arg0[0]);
        Log.d("HttpPost", "New HttpPost");
     // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", arg0[1]));
	        params.add(new BasicNameValuePair("password", Commons.encryptPassword(arg0[2])));
	    Log.d("List", "NameValuePair");    
        try {
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
	    entity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
		entity.setContentEncoding("UTF-8");
		entity.setChunked(true);
	    httpPost.setEntity(entity);
	    HttpResponse response;
	    try {
			response = httpClient.execute(httpPost);
			
			BufferedReader br = null;
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String rtnString = "";
			String line = br.readLine();
			while (line != null){
				rtnString += line;
				line = br.readLine();
			}
			br.close();
			return new JSONObject(rtnString);

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("CPE", e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("IOE", e.getMessage());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		}
        catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("UEE", e.getMessage());
	    }

		return null;
	}
	
	protected void onPreExecute() {
		ThisActivity.pg.show();

	}
	protected void onPostExecute(JSONObject jsonCust) {
		Log.d("SaveTo", ThisActivity.settings.getString("SaveTo", ThisActivity.getResources().getString(R.string.SaveToMemory)));

		ThisActivity.pg.dismiss();
  		ThisActivity.LoginRoutine(jsonCust);
  		
  		try {
			this.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


class ForgotPassword extends AsyncTask<String, JSONObject, JSONObject>{
    private Login ThisActivity;
   
	ForgotPassword(Login a){
    	ThisActivity = a;
    }
	
	@Override
	protected JSONObject doInBackground(String... arg0) {
		//String url_str = "http://" + arg0[3] + arg0[0];
		String url_str = arg0[0];
		Log.d("url", url_str);
		String email = arg0[1];
		Log.d("email", email);
		String rtnString = "";
		HttpClient httpClient = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10000);
        HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10000);
        HttpPost httpPost = new HttpPost(url_str);
        Log.d("HttpPost", "New HttpPost");
     // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));    
	    Log.d("List", "NameValuePair");    
        try {
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
	    httpPost.setHeader("Host", "aalexandrakis.freevar.com");
		entity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
		entity.setContentEncoding("UTF-8");
		entity.setChunked(true);
	    httpPost.setEntity(entity);
	    HttpResponse response;
		try {
			response = httpClient.execute(httpPost);
			
			BufferedReader br = null;
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			String str = br.readLine();
			while(str != null){
				rtnString += str;
			}
			br.close();
			return new JSONObject(rtnString);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("CPE", e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("IOE", e.getMessage());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		}
        catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("UEE", e.getMessage());
	    }
		return  null;
	}
	
	protected void onPreExecute() {
		ThisActivity.changePassPg.show();
	}
	protected void onPostExecute(JSONObject json) {
		ThisActivity.changePassPg.dismiss();
  		try {
			this.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ThisActivity.ShowAlertDialog(json.optString("status"), json.optString("message"));

  		
	}
}