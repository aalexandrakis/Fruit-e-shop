package com.aalexandrakis.fruit_e_shop;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashScreen extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
		     setContentView(R.layout.splash_screen);

		     Thread timer= new Thread()
		     {
		         public void run()
		         {
		             try
		             {
		                 //Display for 3 seconds
		                 sleep(3000);
		             }
		             catch (InterruptedException e) 
		             {
		                 // TODO: handle exception
		                 e.printStackTrace();
		             }
		             finally
		             {   
		                 //Goes to Activity  StartingPoint.java(STARTINGPOINT)
		                 Intent openstartingpoint=new Intent("com.aalexandrakis.fruit_e_shop.Login");
		                 startActivity(openstartingpoint);
		             }
		         }
		     };
		     timer.start();
		 }


		 //Destroy Welcome_screen.java after it goes to next activity
		 @Override
		 protected void onPause() 
		     {
		     // TODO Auto-generated method stub
		     super.onPause();
		     finish();

		     }
	}
	  
	

