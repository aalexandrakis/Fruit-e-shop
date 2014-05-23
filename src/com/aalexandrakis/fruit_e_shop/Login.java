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
	public static final String url_login = "http://www.aalexandrakis.freevar.com/android-fruit-e-shop/androidLogin.php";
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
    
    
	public static String Email = "";
	public static Boolean RememberPassword = false;
	
	public ProgressDialog pg;
	public ProgressDialog ChangePassPg;    
	public GetHttpResponseAsync Async;
	public ForgotPassword ForgotPasswordAsync;
    
	Login LoginActivity=this;
    Button btnConnect;
    Button btnForgot;
    Button btnNewUser;
    AlertDialog errorInput;
	TextView editEmail;
    TextView editPassword;
    CheckBox RememberPass;
    String email;
    String password;
    
    public SharedPreferences settings;
    
     
    protected void onPause() {
    	if (Async != null &&
    		Async.getStatus() != AsyncTask.Status.FINISHED){
    	       Async.cancel(true);
    	}
    	if (ForgotPasswordAsync != null &&
    		ForgotPasswordAsync.getStatus() != AsyncTask.Status.FINISHED){
    	      ForgotPasswordAsync.cancel(true);
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
	
	   ChangePassPg = new ProgressDialog(this);
	   ChangePassPg.setTitle("Forgot Password");
	   ChangePassPg.setMessage("Wait to re-new your password");
	
	   editEmail = (TextView) findViewById(R.id.editEmail);
       editPassword = (TextView) findViewById(R.id.editPassword);
       RememberPass = (CheckBox) findViewById(R.id.checkBox1);
	   btnConnect = (Button) findViewById(R.id.btnConnect);
	   btnForgot = (Button) findViewById(R.id.btnForgot);
	   btnNewUser = (Button) findViewById(R.id.btnNewUser);
	   getSharedPreferenses();
	   
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
	
    	
   public void LoginRoutine(String HttpResp) {
 		   if (HttpResp.startsWith("1")) {
   		     SharedPreferences.Editor prefEditor = settings.edit();
   	         prefEditor.putString("Email", email);
   	         prefEditor.putString("Password", password);
   	         prefEditor.putBoolean("Remember", RememberPassword);
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
      		    RememberPassword = settings.getBoolean("Remember", RememberPassword);
                autoFill();
    		}
    	    } catch (ClassCastException CCE) {
    	    	
    	    }
    }
    
    protected void autoFill(){
    	if (RememberPassword){
    		editEmail.setText(email);
    		editPassword.setText(password);
    		RememberPass.setChecked(RememberPassword);
    	}
    }
    
    protected void Login_routine() {
	       email=editEmail.getText().toString();
	       password=editPassword.getText().toString();
		   RememberPassword = (Boolean) RememberPass.isChecked();
		   
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
        	 Async = (GetHttpResponseAsync) new GetHttpResponseAsync(this).execute(url_login, email, password);
         } else {
        	 Async = (GetHttpResponseAsync) new GetHttpResponseAsync(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url_login, email, password);
         }
     }
	
    protected void forgot_Password() {
		 email=editEmail.getText().toString();
	     password=editPassword.getText().toString();
		 RememberPassword = (Boolean) RememberPass.isChecked();
		 
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
	    ForgotPasswordAsync = (ForgotPassword) new ForgotPassword(this).execute(url_forgot, email);
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


class GetHttpResponseAsync extends AsyncTask<String, String, String>{
    private Login ThisActivity;
   
	GetHttpResponseAsync(Login a){
    	ThisActivity = a;
    }
	
	@Override
	protected String doInBackground(String... arg0) {
		//String url_str = "http://" + arg0[3] + arg0[0];
		String url_str = arg0[0];
		Log.d("url", url_str);
		String username = arg0[1];
		Log.d("username", username);
		String password = arg0[2];
		Log.d("password", password);
		String rtnString = "";
		HttpClient httpClient = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10000);
        HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10000);
        HttpPost httpPost = new HttpPost(url_str);
        Log.d("HttpPost", "New HttpPost");
     // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", username));    
	        params.add(new BasicNameValuePair("password", password));
	    Log.d("List", "NameValuePair");    
        try {
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
	    httpPost.setHeader("Host", "aalexandrakis.freevar.com");
		entity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
		entity.setContentEncoding("UTF-8");
		entity.setChunked(true);
	    httpPost.setEntity(entity);
	    HttpResponse response;
	    Log.d("response", "httpresponse");
		try {
			response = httpClient.execute(httpPost);
			
			BufferedReader br = null;
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			rtnString = (String) br.readLine();
			Log.d("ReadLine", "rtnString");
			if (rtnString.equals("1")){
				GetAllCategories();
				GetAllProducts();
				GetAllOrders();
				GetAllOrderedItems();
			}
			br.close();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("CPE", e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("IOE", e.getMessage());
		}
	    
		}
        catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("UEE", e.getMessage());
	    }
		Log.d("ReturnValue", rtnString);
		return  rtnString.trim();
	}
	
	protected void onPreExecute() {
		ThisActivity.pg.show();

	}
	protected void onPostExecute(String RtnString) {
		Log.d("SaveTo", ThisActivity.settings.getString("SaveTo", ThisActivity.getResources().getString(R.string.SaveToMemory)));

		ThisActivity.pg.dismiss();
  		ThisActivity.LoginRoutine(RtnString);
  		
  		try {
			this.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	protected void GetAllCategories(){
		    HttpClient httpClient = new DefaultHttpClient();
	        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10000);
	        HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10000);
	        HttpPost httpPost = new HttpPost(Login.url_getCategories);  
	        HttpResponse response = null;
			try {
				response = httpClient.execute(httpPost);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
				InputStream rtnStream = response.getEntity().getContent();
				try {
					    Log.d("Save To Path", Login.app_path);
					    //createAndroidDataDir();
					    final File file = new File(Login.app_path, "Categories.xml");
					    File fileToDelete = new File(Environment.getExternalStorageDirectory()+ThisActivity.getApplicationContext().getPackageName()+"/Categories.xml");
						fileToDelete.delete();
						fileToDelete = new File(ThisActivity.getFilesDir().toString()+"/Categories.xml");
						fileToDelete.delete();
						
						//final File dir = new File(Environment.getExternalStorageDirectory().toString()+ThisActivity.app_path);
						//if (dir.isDirectory()==false){
						//	dir.mkdir();
						//}
						//if (file.exists()){
						//	file.delete();
						//	Log.d("File deleted", file.toString());
						//}
						file.createNewFile();
						Log.d("File created", file.toString());

					    final OutputStream output = new FileOutputStream(file);
					    try {
					        try {
					            final byte[] buffer = new byte[1024];
					            int read;
					            while ((read = rtnStream.read(buffer)) != -1)
					                output.write(buffer, 0, read);
					            	output.flush();
					        } finally {
					            output.close();
					        }
					    } catch (Exception e) {
					        e.printStackTrace();
					        Log.d("Exception", e.getMessage());
					    }
				   	} finally {
				    rtnStream.close();
				}
				
				
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("Illegal state", e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("IOE", e.getMessage());
			}
	    

	}
	protected void GetAllProducts(){
		String url_GetAllProducts="http://www.aalexandrakis.freevar.com/android-fruit-e-shop/androidSelectAllProductsXML.php";
	        HttpClient httpClient = new DefaultHttpClient();
	        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10000);
	        HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10000);
	        HttpPost httpPost = new HttpPost(url_GetAllProducts);  
	        HttpResponse response = null;
			try {
				response = httpClient.execute(httpPost);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
				InputStream rtnStream = response.getEntity().getContent();
					try {
						//createAndroidDataDir();
						//final File file = new File(Environment.getExternalStorageDirectory().toString()+ThisActivity.app_path, "Products.xml");
						final File file = new File(Login.app_path, "Products.xml");
						File fileToDelete = new File(Environment.getExternalStorageDirectory()+ThisActivity.getApplicationContext().getPackageName()+"/Products.xml");
						fileToDelete.delete();
						fileToDelete = new File(ThisActivity.getFilesDir().toString()+"/Products.xml");
						fileToDelete.delete();
						
						//final File dir = new File(Environment.getExternalStorageDirectory().toString()+ThisActivity.app_path);
						//if (dir.isDirectory()==false){
						//	dir.mkdir();
						//}
						//if (file.exists()){
						//	file.delete();
						//	Log.d("File deleted", file.toString());
						//}
						file.createNewFile();
						Log.d("File created", file.toString());

					    final OutputStream output = new FileOutputStream(file);
					    try {
					        try {
					            final byte[] buffer = new byte[1024];
					            int read;
					            while ((read = rtnStream.read(buffer)) != -1)
					                output.write(buffer, 0, read);
					            	output.flush();
					        } finally {
					            output.close();
					        }
					    } catch (Exception e) {
					        e.printStackTrace();
					    }

					
				    } finally {
				    rtnStream.close();
				}
				
				
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    

	}
	
	//private void createAndroidDataDir(){
	//	File dirAndroid = new File(Environment.getExternalStorageDirectory().toString()+"/Android");
	//	if (dirAndroid.isDirectory()==false){
	//		if (!dirAndroid.mkdir()){
	//			Log.d("Android directory not created", dirAndroid.toString());
	//		}
	//	}
	//	File dirData = new File(Environment.getExternalStorageDirectory().toString()+"/Android/data");
	//	if (dirData.isDirectory()==false){
	//		if(!dirData.mkdir()){
	//			Log.d("Data directory not created", dirData.toString());
	//		}
	//	}
	//}
	
	protected void GetAllOrders(){
		HttpClient httpClient = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10000);
		HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10000);
		HttpPost httpPost = new HttpPost(Login.url_GetOrders);  
		// 	Building Parameters
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("email", ThisActivity.settings.getString("Email", "")));
		
		UrlEncodedFormEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		httpPost.setHeader("Host", "aalexandrakis.freevar.com");
		entity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
		entity.setContentEncoding("UTF-8");
		entity.setChunked(true);
		httpPost.setEntity(entity);
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpPost);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        try {
			InputStream rtnStream = response.getEntity().getContent();
			try {
				    
				    //createAndroidDataDir();
				    final File file = new File(Login.app_path, "Orders.xml");
				    File fileToDelete = new File(Environment.getExternalStorageDirectory()+ThisActivity.getApplicationContext().getPackageName()+"/Orders.xml");
					fileToDelete.delete();
					fileToDelete = new File(ThisActivity.getFilesDir().toString()+"/Orders.xml");
					fileToDelete.delete();
					
					//final File dir = new File(Environment.getExternalStorageDirectory().toString()+ThisActivity.app_path);
					//if (dir.isDirectory()==false){
					//	dir.mkdir();
					//}
					//if (file.exists()){
					//	file.delete();
					//	Log.d("File deleted", file.toString());
					//}
					file.createNewFile();
					Log.d("File created", file.toString());

				    final OutputStream output = new FileOutputStream(file);
				    try {
				        try {
				            final byte[] buffer = new byte[1024];
				            int read;
				            while ((read = rtnStream.read(buffer)) != -1)
				                output.write(buffer, 0, read);
				            	output.flush();
				        } finally {
				            output.close();
				        }
				    } catch (Exception e) {
				        e.printStackTrace();
				        Log.d("Exception", e.getMessage());
				    }
			   	} finally {
			    rtnStream.close();
			}
			
			
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("Illegal state", e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("IOE", e.getMessage());
		}
    

}

	protected void GetAllOrderedItems(){
		HttpClient httpClient = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10000);
		HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10000);
		HttpPost httpPost = new HttpPost(Login.url_GetAllOrderedItems);  
		// 	Building Parameters
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("email", ThisActivity.settings.getString("Email", "")));
		
		UrlEncodedFormEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		httpPost.setHeader("Host", "aalexandrakis.freevar.com");
		entity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
		entity.setContentEncoding("UTF-8");
		entity.setChunked(true);
		httpPost.setEntity(entity);
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpPost);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        try {
			InputStream rtnStream = response.getEntity().getContent();
			try {
				    //createAndroidDataDir();
				    final File file = new File(Login.app_path, "OrderedItems.xml");
				    File fileToDelete = new File(Environment.getExternalStorageDirectory()+ThisActivity.getApplicationContext().getPackageName()+"/OrderdItems.xml");
					fileToDelete.delete();
					fileToDelete = new File(ThisActivity.getFilesDir().toString()+"/OrderedItems.xml");
					fileToDelete.delete();
					
					//final File dir = new File(Environment.getExternalStorageDirectory().toString()+ThisActivity.app_path);
					//if (dir.isDirectory()==false){
					//	dir.mkdir();
					//}
					//if (file.exists()){
					//	file.delete();
					//	Log.d("File deleted", file.toString());
					//}
					file.createNewFile();
					Log.d("File created", file.toString());

				    final OutputStream output = new FileOutputStream(file);
				    try {
				        try {
				            final byte[] buffer = new byte[1024];
				            int read;
				            while ((read = rtnStream.read(buffer)) != -1)
				                output.write(buffer, 0, read);
				            	output.flush();
				        } finally {
				            output.close();
				        }
				    } catch (Exception e) {
				        e.printStackTrace();
				        Log.d("Exception", e.getMessage());
				    }
			   	} finally {
			    rtnStream.close();
			}
			
			
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("Illegal state", e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("IOE", e.getMessage());
		}
    

}


}


class ForgotPassword extends AsyncTask<String, String, String>{
    private Login ThisActivity;
   
	ForgotPassword(Login a){
    	ThisActivity = a;
    }
	
	@Override
	protected String doInBackground(String... arg0) {
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
	    Log.d("response", "httpresponse");
		try {
			response = httpClient.execute(httpPost);
			
			BufferedReader br = null;
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			rtnString = (String) br.readLine();
			Log.d("ReadLine", "rtnString");
			br.close();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("CPE", e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("IOE", e.getMessage());
		}
	    
		}
        catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("UEE", e.getMessage());
	    }
		Log.d("ReturnValue", rtnString);
		return  rtnString.trim();
	}
	
	protected void onPreExecute() {
		ThisActivity.ChangePassPg.show();
	}
	protected void onPostExecute(String RtnString) {
		ThisActivity.ChangePassPg.dismiss();
  		try {
			this.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  		if (RtnString.startsWith("0")){
  			ThisActivity.ShowAlertDialog("Password change failed", "Please try later");
	    }
	    if (RtnString.startsWith("1")){
	    	ThisActivity.ShowAlertDialog("Password change failed", "Your email not found to our database");
	    }
	    if (RtnString.startsWith("2")){
	    	ThisActivity.ShowAlertDialog("Password change failed", "Please try later.Problem with the email");
	    }
	    if (RtnString.startsWith("3")){
	    	ThisActivity.ShowAlertDialog("Password Changed", "New password was sent to your email");
	    }

  		
	}
}