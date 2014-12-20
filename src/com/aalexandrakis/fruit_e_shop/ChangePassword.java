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

public class ChangePassword extends Login  {


    Button btnSignIn;
    EditText edtOldPassword;
    EditText edtNewPassword;
    EditText edtConfPassword;

    String idString;
    ChangePassword changePasswordActivity = this;

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
        setContentView(R.layout.change_password);
        TextView txtUserHeader = (TextView) findViewById(R.id.txtUserHeader);
        txtUserHeader.setText(R.string.UpdateUserHeader);
        settings = getSharedPreferences(FRUIT_E_SHOP_PREF, MODE_PRIVATE);

        pg = new ProgressDialog(this);
        pg.setTitle("Change User Information");
        pg.setMessage("Please wait to update your information");

        edtOldPassword= (EditText) findViewById(R.id.edtOldPassword);
        edtNewPassword= (EditText) findViewById(R.id.edtNewPassword);
        edtConfPassword= (EditText) findViewById(R.id.edtConfPassword);

        idString = String.valueOf(settings.getInt("Id", 0));
        btnSignIn = (Button) findViewById(R.id.btnSignIn);


        //for test only
        edtOldPassword.setText(settings.getString("Password", ""));

        btnSignIn.setText("Update");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (ValidateRoutine()){
                    ChangePasswordAsyncTask changePasswordAsyncTask = new ChangePasswordAsyncTask(changePasswordActivity);
                    changePasswordAsyncTask.execute(Commons.URL + "/updatePassword", idString,
                            Commons.encryptPassword(edtNewPassword.getText().toString()));
                }
            }
        });
    }


    public void UpdateRoutine(JSONObject jsonResponse) {

        if (jsonResponse != null && jsonResponse.optString("status").equals("SUCCESS")) {
            SharedPreferences.Editor prefEditor = settings.edit();
            prefEditor.remove("Password");
            prefEditor.putString("Password", edtNewPassword.getText().toString());
            prefEditor.commit();
            Toast.makeText(getApplicationContext(), settings.getString("Name", "User") + " your password changed succesfully", Toast.LENGTH_LONG).show();
            this.finish();
        } else if (jsonResponse == null) {
            ShowAlertDialog("Change password error", "Please try later");
        } else {
            ShowAlertDialog(jsonResponse.optString("status"), jsonResponse.optString("message"));
        }

    }



    protected boolean ValidateRoutine() {
        Log.i("Validate", "OK");
        if (checkConnectivity()==false){
            Log.i("ShowAlert", "OK");
            ShowAlertDialog("Connectivity Error", "No Internet Connection");
            return false;
        }
        if (edtOldPassword.length()==0){
            ShowAlertDialog("Missing Information", "Your name is missing");
            edtOldPassword.requestFocus();
            return false;
        }
        if (edtNewPassword.length()==0){
            ShowAlertDialog("Missing Information", "Your address is missing");
            edtNewPassword.requestFocus();
            return false;
        }
        if (edtConfPassword.length()==0){
            ShowAlertDialog("Missing Information", "Your city is missing");
            edtConfPassword.requestFocus();
            return false;
        }

        if (!edtOldPassword.getText().toString().equals(settings.getString("Password", "NO PASSWORD FOUND"))){
            ShowAlertDialog("Password Missmatch", "Your old password is error. Please try again");
            edtOldPassword.requestFocus();
            return false;
        }

        if (!edtNewPassword.getText().toString().equals(edtConfPassword.getText().toString())){
            ShowAlertDialog("Password Missmatch", "Your new password is not the same with password confirmation");
            edtConfPassword.requestFocus();
            return false;
        }
        return true;
    }

}

///////////////////////////////////////////////////////////////////////////////
class ChangePasswordAsyncTask extends AsyncTask<String, JSONObject, JSONObject>{
    public ChangePassword ThisActivity;

    ChangePasswordAsyncTask(ChangePassword a){
        ThisActivity = a;
    }

    @Override
    protected JSONObject doInBackground(String... arg0) {
        String url_str = arg0[0];
        String id = arg0[1];
        String password = arg0[2];

        HttpClient httpClient = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 30000);
        HttpConnectionParams.setSoTimeout(httpClient.getParams(), 30000);
        HttpPost httpPost = new HttpPost(url_str);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("password", password));

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
