package com.aalexandrakis.fruit_e_shop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

public class Settings_Activity extends Login {
    protected CheckBox chkRememberPassword;
    protected Spinner spinSaveDataTo;
    protected static String SourcePath;
    protected static String TargetPath;
    protected String SAVETOMEMORY;
    protected String SAVETOSDCARD;
    
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SAVETOMEMORY = getResources().getString(R.string.SaveToMemory);
	    SAVETOSDCARD = getResources().getString(R.string.SaveToSdCard);
		setContentView(R.layout.settings_screen);
		chkRememberPassword = (CheckBox) findViewById(R.id.chkRememberPassword);
		spinSaveDataTo = (Spinner) findViewById(R.id.spinSaveData);
		chkRememberPassword.setChecked(settings.getBoolean("Remember", false));
		fillSpinSaveDataTo();
		if (spinSaveDataTo.getSelectedItem().toString().equals(SAVETOMEMORY)){
			   SourcePath = this.getFilesDir().toString();
			} else {
	 		    SourcePath = Environment.getExternalStorageDirectory()+ "/Android/data/" + getApplicationContext().getPackageName();
			}
	}
	
	protected void fillSpinSaveDataTo(){
		Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

		List<String> SpinArray = new ArrayList<String>();
		SpinArray.add(SAVETOMEMORY);
		if (isSDPresent){
			SpinArray.add(SAVETOSDCARD);	
		}
		
		ArrayAdapter<String> SaveToAdapt = new ArrayAdapter<String>(this,R.layout.spiner_saveto_item, SpinArray);
		spinSaveDataTo.setAdapter(SaveToAdapt);
		Log.i("Set Spiner Adapter", "Ok");
		if (settings.getString("SaveTo", SAVETOMEMORY).equals(SAVETOMEMORY)) {
		    spinSaveDataTo.setSelection(0);
		} else {
			spinSaveDataTo.setSelection(1);
		}
		Log.i("Set Spiner Selected Item", "Ok");
	}
	
	public void onSaveClick(View v){
		Editor PrefEditor = settings.edit();
		PrefEditor.putBoolean("Remember", chkRememberPassword.isChecked());
		PrefEditor.putString("SaveTo", spinSaveDataTo.getSelectedItem().toString());
		PrefEditor.commit();
		if (spinSaveDataTo.getSelectedItem().toString().equals(SAVETOMEMORY)){
		   app_path = this.getFilesDir().toString();
		   TargetPath = this.getFilesDir().toString();
		} else {
			CreateSdCardDirectory();
 		    app_path = Environment.getExternalStorageDirectory()+ "/Android/data/" + getApplicationContext().getPackageName();
 		    TargetPath = Environment.getExternalStorageDirectory()+ "/Android/data/" + getApplicationContext().getPackageName();
		}
		
		if (!SourcePath.equals(TargetPath)){
			File srcCategories = new File(SourcePath+"/Categories.xml");
			File srcProducts = new File(SourcePath+"/Products.xml");
			File srcOrders = new File(SourcePath+"/Orders.xml");
			File srcOrderedItems = new File(SourcePath+"/OrderedItems.xml");
			File trgCategories = new File(TargetPath+"/Categories.xml");
			File trgProducts = new File(TargetPath+"/Products.xml");
			File trgOrders = new File(TargetPath+"/Orders.xml");
			File trgOrderedItems = new File(TargetPath+"/OrderedItems.xml");
			try {
				copyFile(srcCategories, trgCategories);
				copyFile(srcProducts, trgProducts);
				copyFile(srcOrders, trgOrders);
				copyFile(srcOrderedItems, trgOrderedItems);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {
			Log.i("Change Settings", "Path not changed");
		}
		
		this.finish();
		
	}
	
	public static void copyFile(File src, File dst) throws IOException
	{
		//if (!dst.exists()){
	    //	dst.createNewFile();
	    //} else {
	    //	dst.delete();
	    //	dst.createNewFile();
	    //}
		Log.i("Copying file", "From "+ SourcePath + " to " + TargetPath);
	    @SuppressWarnings("resource")
		FileChannel inChannel = new FileInputStream(src).getChannel();
	    @SuppressWarnings("resource")
		FileChannel outChannel = new FileOutputStream(dst).getChannel();
	    
	    
	    
	    try
	    {
	        inChannel.transferTo(0, inChannel.size(), outChannel);
	        src.delete();
	    }
	    finally
	    {
	        if (inChannel != null)
	            inChannel.close();
	        if (outChannel != null)
	            outChannel.close();
	        
	    }
	}
}
