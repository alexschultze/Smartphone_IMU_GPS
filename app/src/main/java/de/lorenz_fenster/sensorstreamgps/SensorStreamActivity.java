package de.lorenz_fenster.sensorstreamgps;

import android.location.LocationManager;
import android.os.Bundle;
import android.app.TabActivity;
import android.content.Intent;
import android.hardware.SensorManager;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import de.lorenz_fenster.sensorstreamgps.R;



public class SensorStreamActivity extends TabActivity 
	{
	
		//private final String MDEBUG_TAG = "follow_onX";
		
		public static SensorManager mSensor_Toggle;
		public static SensorManager mSensor_Stream;
		
		public static LocationManager mLocationmanager;
		
		
		
		private static int mDelay = SensorManager.SENSOR_DELAY_NORMAL;
		private static boolean mIssending = false;
		private static String mName_SD_Card_File = "mystream";
		
		

		
		private static final boolean mbAccelerometer = true;
		private static final boolean mbGyroscope = true;
		private static final boolean mbMagnetometer = true;
		
		private static boolean mGPS = false;
		private static boolean mbOrientation = false;
		private static boolean mbLin_Acceleration = false;
		private static boolean mbGravity = false;
		private static boolean mbRot_Vector = false;
		private static boolean mbPressure = false;
		private static boolean mbBat_Temp = false;
		
		private static boolean mbChecked_Sensor_Data=false;
		
		private static boolean mRun_in_Background = false;
		
		
		TabHost mTabHost;
		
		
	
	
    	@Override
    	public void onCreate(Bundle savedInstanceState) 
    	{
    	super.onCreate(savedInstanceState);
    	//Log.d(MDEBUG_TAG, getLocalClassName()+ " .onCreate aufgerufen");
        setContentView(R.layout.main);
        
        
        mSensor_Toggle 	= 		(SensorManager) getSystemService(SENSOR_SERVICE);
		mSensor_Stream 	= 		(SensorManager) getSystemService(SENSOR_SERVICE);
		mLocationmanager =		(LocationManager) getSystemService(LOCATION_SERVICE);
        
        
        // Tab Action
        TabHost mTabHost = getTabHost();
       
        
        // Tab for Preferences
        TabSpec preferencesspec = mTabHost.newTabSpec("Preferences");
        // setting Title and Icon for the Tab
        preferencesspec.setIndicator("Preferences", getResources().getDrawable(R.drawable.icon_preferences_tab));
        Intent preferencesIntent = new Intent(this, PreferencesActivity.class);
        preferencesspec.setContent(preferencesIntent);
       
        
        // Tab for Toggling of Sensors
        TabSpec sensorspec = mTabHost.newTabSpec("Toggle Sensors");
        // setting Title and Icon for the Tab
        sensorspec.setIndicator("Toggle Sensors", getResources().getDrawable(R.drawable.icon_toggle_sensors_tab));
        Intent songsIntent = new Intent(this, ToggleSensorsActivity.class);
        sensorspec.setContent(songsIntent);
        
        // Adding all TabSpec to TabHost

        mTabHost.addTab(preferencesspec); // Adding Preferences tab
        mTabHost.addTab(sensorspec); // Adding Toogle_Sensors tab  
        //End of Tab Action
        
        
    	}
    	
    	
		
		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			//Log.d(MDEBUG_TAG, getLocalClassName()+ " .onDestroy aufgerufen");
		}

		@Override
		protected void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
			
			//mSensor_Toggle.unregisterListener((SensorEventListener) this);
			
			
			
			//Log.d(MDEBUG_TAG, getLocalClassName()+ " .onPause aufgerufen");
		}
	
		@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			
			//Log.d(MDEBUG_TAG, getLocalClassName()+ " .onResume aufgerufen");
		}
		
		@Override
		protected void onStop() {
			// TODO Auto-generated method stub
			super.onStop();
			
			//Log.d(MDEBUG_TAG, getLocalClassName()+ " .onStop aufgerufen");
		}
		
		@Override
		protected void onRestart() {
			// TODO Auto-generated method stub
			super.onRestart();
			
			//Log.d(MDEBUG_TAG, getLocalClassName()+ " .onRestart aufgerufen");
		}
		
		@Override
		protected void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
			
			//Log.d(MDEBUG_TAG, getLocalClassName()+ " .onStart aufgerufen");
		}


		
		
		
		public static boolean ismGPS() {
			return mGPS;
		}



		public static void setmGPS(boolean mGPS) {
			SensorStreamActivity.mGPS = mGPS;
		}



		public static boolean isMbOrientation() {
			return mbOrientation;
		}
		public static void setMbOrientation(boolean mbOrientation) {
			SensorStreamActivity.mbOrientation = mbOrientation;
		}
		public static boolean isMbLin_Acceleration() {
			return mbLin_Acceleration;
		}
		public static void setMbLin_Acceleration(boolean mbLin_Acceleration) {
			SensorStreamActivity.mbLin_Acceleration = mbLin_Acceleration;
		}
		public static boolean isMbGravity() {
			return mbGravity;
		}
		public static void setMbGravity(boolean mbGravity) {
			SensorStreamActivity.mbGravity = mbGravity;
		}
		public static boolean isMbRot_Vector() {
			return mbRot_Vector;
		}
		public static void setMbRot_Vector(boolean mbRot_Vector) {
			SensorStreamActivity.mbRot_Vector = mbRot_Vector;
		}
		public static boolean isMbPressure() {
			return mbPressure;
		}
		public static void setMbPressure(boolean mbPressure) {
			SensorStreamActivity.mbPressure = mbPressure;
		}
		public static boolean isMbBat_Temp() {
			return mbBat_Temp;
		}



		public static void setMbBat_Temp(boolean mbBat_Temp) {
			SensorStreamActivity.mbBat_Temp = mbBat_Temp;
		}



		public static boolean isMbaccelerometer() {
			return mbAccelerometer;
		}
		public static boolean isMbgyroscope() {
			return mbGyroscope;
		}
		public static boolean isMbmagnetometer() {
			return mbMagnetometer;
		}

		public static int getmDelay() {
			return mDelay;
		}

		public static void setmDelay(int mDelay) {
			SensorStreamActivity.mDelay = mDelay;
		}

		public static boolean isMbChecked_Sensor_Data() {
			return mbChecked_Sensor_Data;
		}

		public static void setMbChecked_Sensor_Data(boolean mbChecked_Sensor_Data) {
			SensorStreamActivity.mbChecked_Sensor_Data = mbChecked_Sensor_Data;
		}
 
		 

		public static void setmIssending(boolean mIssending) {
			SensorStreamActivity.mIssending = mIssending;
		}

		public static boolean ismIssending() {
			return mIssending;
		}

		public static String getmName_SD_Card_File() {
			return mName_SD_Card_File;
		}
		
		public static void setmName_SD_Card_File(String mName_SD_Card_File) {
			SensorStreamActivity.mName_SD_Card_File = mName_SD_Card_File;
		}



		public static void setmRun_in_Background(boolean mRun_in_Background) {
			SensorStreamActivity.mRun_in_Background = mRun_in_Background;
		}



		public static boolean ismRun_in_Background() {
			return mRun_in_Background;
		}
 
		
	}

	  
	
    
    

