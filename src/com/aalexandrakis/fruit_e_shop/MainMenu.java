package com.aalexandrakis.fruit_e_shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class MainMenu extends Login  {
    
	
	//@Override
	//public boolean onCreateOptionsMenu(Menu menu) {
	//	super.onCreateOptionsMenu(menu);
	//	getMenuInflater().inflate(R.menu.choice_menu_main, menu);
	//	menu.findItem(R.id.ViewMyCart).setIntent(new Intent("com.aalexandrakis.fruit_e_shop.View_Cart"));
	 //   return true;
	//}
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	   setContentView(R.layout.list_main_menu_item);
	   ListView lstView = (ListView) findViewById(R.id.CartList);
	   String[] listItems ={getResources().getString(R.string.Aboutus),
			   getResources().getString(R.string.contactus),
			   getResources().getString(R.string.neworder),
			   getResources().getString(R.string.chgaccount),
			   getResources().getString(R.string.chgpassword),
			   getResources().getString(R.string.vieworder),
			   getResources().getString(R.string.chgsettings),
			   getResources().getString(R.string.ViewCart)};
	               
	   ArrayAdapter<String> adapt = new ArrayAdapter<String> (this, R.layout.main_menu_item, listItems);
	   lstView.setAdapter(adapt);
	   lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			TextView txtView = (TextView) arg1;
			String itemClicked = txtView.getText().toString();
			if (itemClicked.equals(getResources().getString(R.string.Aboutus))){
				Intent nextActivity= new Intent("com.aalexandrakis.fruit_e_shop.Aboutus");
        		startActivity(nextActivity);
			}
            if (itemClicked.equals(getResources().getString(R.string.contactus))){
            	Intent nextActivity= new Intent("com.aalexandrakis.fruit_e_shop.Contactus");
            	startActivity(nextActivity); 	
			}
            if (itemClicked.equals(getResources().getString(R.string.neworder))){
            	Intent nextActivity= new Intent("com.aalexandrakis.fruit_e_shop.New_order");
        		startActivity(nextActivity);
			}
            if (itemClicked.equals(getResources().getString(R.string.chgaccount))){
            	if (checkConnectivity()==true){
            		Intent nextActivity= new Intent("com.aalexandrakis.fruit_e_shop.Update_user");
            		startActivity(nextActivity);
            	} else {
            		showAlertDialog("Connectivity Error", "No Internet Connection");
            	}
			}
            if (itemClicked.equals(getResources().getString(R.string.chgpassword))){
            	if (checkConnectivity()==true){
            		Intent nextActivity= new Intent("com.aalexandrakis.fruit_e_shop.ChangePassword");
            		startActivity(nextActivity);
            	} else {
            		showAlertDialog("Connectivity Error", "No Internet Connection");
            	}
			}
            if (itemClicked.equals(getResources().getString(R.string.chgsettings))){
            	Intent nextActivity= new Intent("com.aalexandrakis.fruit_e_shop.Settings_Activity");
        		startActivity(nextActivity);
			}
            if (itemClicked.equals(getResources().getString(R.string.vieworder))){
            	Intent nextActivity= new Intent("com.aalexandrakis.fruit_e_shop.View_orders");
        		startActivity(nextActivity);
				
			}
            if (itemClicked.equals(getResources().getString(R.string.ViewCart))){
            	Intent nextActivity= new Intent("com.aalexandrakis.fruit_e_shop.View_Cart");
        		startActivity(nextActivity);
				
			}
			
		}
	});
	}
    	//@Override 
        //protected void onPause() {
    	//	
    	//	//this.onPause();
       // 	showAlertDialog("test", "main menu pause");
        //}
    	
}


