package com.aalexandrakis.fruit_e_shop;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
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

public class Update_user extends Login  {
	
	
	Button btnSignIn;
	EditText edtName;
	EditText edtAddress;
	EditText edtPhone;
	EditText edtCity;
	EditText edtEmail;
	EditText edtPassword;

	String idString;
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
	   setContentView(R.layout.update_user);
	   TextView txtUserHeader = (TextView) findViewById(R.id.txtUserHeader);
	   txtUserHeader.setText(R.string.UpdateUserHeader);
	   settings = getSharedPreferences(FRUIT_E_SHOP_PREF, MODE_PRIVATE);

	   pg = new ProgressDialog(this);
	   pg.setTitle("Change User Information");
	   pg.setMessage("Please wait to update your information");
	
	   GetInfoPg = new ProgressDialog(this);
	   GetInfoPg.setTitle("Retrieve Information");
	   GetInfoPg.setMessage("Please wait to retrieve your user account data.");
	   
	   edtName= (EditText) findViewById(R.id.edtName);
	   edtName.setText(settings.getString("Name", ""));
	   edtAddress= (EditText) findViewById(R.id.edtAddress);
	   edtAddress.setText(settings.getString("Address", ""));
	   edtPhone= (EditText) findViewById(R.id.edtPhone);
	   edtPhone.setText(settings.getString("Phone", ""));
	   edtCity= (EditText) findViewById(R.id.edtCity);
       edtCity.setText(settings.getString("City", ""));
	   edtEmail= (EditText) findViewById(R.id.edtEmail);
	   edtEmail.setText(settings.getString("Email", ""));
	   edtPassword= (EditText) findViewById(R.id.edtPassword);
	   idString = String.valueOf(settings.getInt("Id", 0));
	   btnSignIn = (Button) findViewById(R.id.btnSignIn);

	   btnSignIn.setText("Update");
//	   btnSignIn.setEnabled(false);

	   btnSignIn.setOnClickListener(new View.OnClickListener() {
   	       @Override
           public void onClick(View arg0) {
		        if (ValidateRoutine()){
		           UpdateUserInfoAsync UpdateUserTask = new UpdateUserInfoAsync(UpdateUserActivity);
		           UpdateUserTask.execute(Commons.URL + "/updateUser", idString,
		        		              edtName.getText().toString(),
		        		              edtAddress.getText().toString(),
		        		              edtCity.getText().toString(),
		                              edtPhone.getText().toString(),
		        		              edtEmail.getText().toString());
		        }
   	       }
 	      });
    	}
	
    	
   public void UpdateRoutine(JSONObject jsonResponse) {
	      
 		  if (jsonResponse != null && jsonResponse.optString("status").equals("SUCCESS")) {
			  SharedPreferences.Editor prefEditor = settings.edit();
			  prefEditor.remove("Email");
			  prefEditor.putString("Email", edtEmail.getText().toString());
			  prefEditor.remove("Name");
			  prefEditor.putString("Name", edtName.getText().toString());
			  prefEditor.remove("Address");
			  prefEditor.putString("Address", edtAddress.getText().toString());
			  prefEditor.remove("Phone");
			  prefEditor.putString("Phone", edtPhone.getText().toString());
			  prefEditor.remove("City");
			  prefEditor.putString("City", edtCity.getText().toString());
			  prefEditor.commit();
 			  Toast.makeText(getApplicationContext(), edtName.getText().toString() + " your data updated succesfully", Toast.LENGTH_LONG).show();
			  this.finish();
		  } else if (jsonResponse == null) {
			  showAlertDialog("Update account error", "Please try later");
  	 	   } else {
			  showAlertDialog(jsonResponse.optString("status"), jsonResponse.optString("message"));
           }

   }
	
	
    
    protected boolean ValidateRoutine() {
    	Log.i("Validate", "OK");
	       if (checkConnectivity()==false){
	    	   Log.i("ShowAlert", "OK");
      	       showAlertDialog("Connectivity Error", "No Internet Connection");
               return false;
           }
          if (edtName.length()==0){
      	         showAlertDialog("Missing Information", "Your name is missing");
      	         edtName.requestFocus();
                 return false;
          }
          if (edtAddress.length()==0){
   	         showAlertDialog("Missing Information", "Your address is missing");
   	         edtAddress.requestFocus();
              return false;
          }
          if (edtCity.length()==0){
   	         showAlertDialog("Missing Information", "Your city is missing");
   	          edtCity.requestFocus();
              return false;
          }
          if (edtPhone.length()==0){
    	       showAlertDialog("Missing Information", "Your phone is missing");
    	       edtPhone.requestFocus();
               return false;
           }
          if (edtEmail.length()==0){
   	          showAlertDialog("Missing Information", "Your email is missing");
   	          edtEmail.requestFocus();
              return false;
          }
          if (edtPassword.length()==0){
   	          showAlertDialog("Missing Information", "Your password is missing");
   	          edtPassword.requestFocus();
              return false;
          }

          if (!edtPassword.getText().toString().equals(settings.getString("Password", "NO PASSWORD FOUND"))){
   	          showAlertDialog("Password Missmatch", "Your password is error. Please try again");
   	          edtPassword.requestFocus();
              return false;
          }
          return true;
    }

}

///////////////////////////////////////////////////////////////////////////////
class UpdateUserInfoAsync extends AsyncTask<String, JSONObject, JSONObject>{
    public Update_user ThisActivity;
   
	UpdateUserInfoAsync(Update_user a){
    	ThisActivity = a;
    }
	
	@Override
	protected JSONObject doInBackground(String... arg0) {
		String url_str = arg0[0];
		String id = arg0[1];
		String name = arg0[2];
		String address = arg0[3];
		String city = arg0[4];
		String phone = arg0[5];
		String email = arg0[6];
		String rtnString = "";
		HttpClient httpClient = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 30000);
        HttpConnectionParams.setSoTimeout(httpClient.getParams(), 30000);
        HttpPost httpPost = new HttpPost(url_str);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("address", address));
		params.add(new BasicNameValuePair("city", city));
		params.add(new BasicNameValuePair("phone", phone));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("id", id));

		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			entity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
			entity.setContentEncoding("UTF-8");
			entity.setChunked(true);
			httpPost.setEntity(entity);
			HttpResponse response;

			response = httpClient.execute(httpPost);
			
			BufferedReader br = null;
			StringBuilder responseString = new StringBuilder();
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String str = null;
			while((str = br.readLine()) != null) {
				responseString.append(str);
			}
			br.close();
			return new JSONObject(responseString.toString());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected void onPreExecute() {
		ThisActivity.pg.show();
	}
	protected void onPostExecute(JSONObject jsonResponse) {
		ThisActivity.pg.dismiss();
  		ThisActivity.UpdateRoutine(jsonResponse);
  		try {
			this.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
