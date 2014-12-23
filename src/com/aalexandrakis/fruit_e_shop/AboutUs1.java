package com.aalexandrakis.fruit_e_shop;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;




public class AboutUs1 extends Login  {
    TextView txtAboutUs;
    
   @SuppressWarnings("deprecation")
@Override
	protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	   setContentView(R.layout.about_us);
	   txtAboutUs = (TextView) findViewById(R.id.txtAboutUs);
	   InputStream iFile = getResources().openRawResource(R.raw.aboutus);
	   StringBuffer sBuffer = new StringBuffer();
	   DataInputStream dataIO = new DataInputStream(iFile);
	   String strLine;
	   try {
		   while ((strLine = dataIO.readLine()) != null){
			  sBuffer.append(strLine + "\n");  
		   }
		   dataIO.close();
		   iFile.close();
	   } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	   }
	   
	   
	   txtAboutUs.setText(sBuffer.toString());
    }    	 	

   public void displayLocation (View v) {
	   if (checkConnectivity()==false){
		   showAlertDialog("Connectivity Error", "No Internet Connection");
		   return;
	   }
	   //showAlertDialog("test","test");
	   float latitude = (float) 37.959233;
	   float longitude= (float) 23.756;
	   String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
	   Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
	   startActivity(i);
   }
}