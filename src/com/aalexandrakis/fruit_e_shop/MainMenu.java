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
				Intent nextactivity= new Intent("com.aalexandrakis.fruit_e_shop.Aboutus");
        		startActivity(nextactivity);
			}
            if (itemClicked.equals(getResources().getString(R.string.contactus))){
            	Intent nextactivity= new Intent("com.aalexandrakis.fruit_e_shop.Contactus");
            	startActivity(nextactivity); 	
			}
            if (itemClicked.equals(getResources().getString(R.string.neworder))){
            	Intent nextactivity= new Intent("com.aalexandrakis.fruit_e_shop.New_order");
        		startActivity(nextactivity);
			}
            if (itemClicked.equals(getResources().getString(R.string.chgaccount))){
            	if (checkConnectivity()==true){
            		Intent nextactivity= new Intent("com.aalexandrakis.fruit_e_shop.Update_user");
            		startActivity(nextactivity);
            	} else {
            		ShowAlertDialog("Connectivity Error", "No Internet Connection");
            	}
			}
            if (itemClicked.equals(getResources().getString(R.string.chgsettings))){
            	Intent nextactivity= new Intent("com.aalexandrakis.fruit_e_shop.Settings_Activity");
        		startActivity(nextactivity);
			}
            if (itemClicked.equals(getResources().getString(R.string.vieworder))){
            	Intent nextactivity= new Intent("com.aalexandrakis.fruit_e_shop.View_orders");
        		startActivity(nextactivity);
				
			}
            if (itemClicked.equals(getResources().getString(R.string.ViewCart))){
            	Intent nextactivity= new Intent("com.aalexandrakis.fruit_e_shop.View_Cart");
        		startActivity(nextactivity);
				
			}
			
		}
	});
	}
    	//@Override 
        //protected void onPause() {
    	//	
    	//	//this.onPause();
       // 	ShowAlertDialog("test", "main menu pause");
        //}
    	
}


