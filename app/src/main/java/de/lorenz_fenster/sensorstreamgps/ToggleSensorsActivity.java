package de.lorenz_fenster.sensorstreamgps;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Toast;



public class ToggleSensorsActivity extends Activity implements LocationListener 
{
	
	//private final String MDEBUG_TAG = "follow_onX";
	
	private final int mScreenDelay = SensorManager.SENSOR_DELAY_NORMAL;
	private static boolean mActive = false;
	
	
	
	
	
	private static double[] mBLH			= 	new double[3];
	private static double[] mXYZ			= 	new double[3];
	private static double[] mV_e			= 	new double[3];
	private static long 	mGPS_UCT_Time 	= 0;
	
	private double mSpeed = 0.0;
	private double mBear = 0.0;
	
	
	private static boolean mGps_available = false;
	//private int mGPS_counter = 0;
	
	private double mLatitude = 0.0;
	private double mLongitude = 0.0;
	private double mAltitude = 0.0;
	
	private  static int mBattery_Temperature = 0;
	
	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			mBattery_Temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)/10;
			
			if(mActive == true && SensorStreamActivity.isMbBat_Temp() == true)
			{
				mBat_Temp.setText(String.format("%d", mBattery_Temperature));
			}
		}
	};
	
	public class MyHardwareSensorList implements SensorEventListener
	{
		public void onSensorChanged(SensorEvent event)
	    {
			switch(event.sensor.getType())
			{
			case Sensor.TYPE_ACCELEROMETER:
				
				for(int i=0; i<event.values.length;i++)
				{
					mAcc[i].setText(String.format("%6.3f", event.values[i]));
				}
				break;
					
			case Sensor.TYPE_GYROSCOPE:
				
				for(int i=0; i<event.values.length;i++)
				{
					mGyr[i].setText(String.format("%6.3f", event.values[i]));
				}
				break;
				
			case Sensor.TYPE_MAGNETIC_FIELD:
				
				for(int i=0; i<event.values.length;i++)
				{
					mMag[i].setText(String.format("%6.3f", event.values[i]));
				}
				break;
			
			case Sensor.TYPE_PRESSURE:
				
					mPre.setText(String.format("%6.3f", event.values[0]));
					break;
					
			default: {}
				
			}
	    	
	    }
	    
	    public void onAccuracyChanged(Sensor arg0, int arg1)
	    {
	    	
	    }
	    		
	}
	public class MySoftwareSensorList implements SensorEventListener
	{
		public void onSensorChanged(SensorEvent event)
	    {
			switch(event.sensor.getType())
			{
			case Sensor.TYPE_ORIENTATION:
				
				for(int i=0; i<event.values.length;i++)
				{
					mOri[i].setText(String.format("%6.3f", event.values[i]));
				}
			break;
			
			case Sensor.TYPE_LINEAR_ACCELERATION:
				
				for(int i=0; i<event.values.length;i++)
				{
					mLin_Acc[i].setText(String.format("%6.3f", event.values[i]));
				}
			break;
			
			case Sensor.TYPE_GRAVITY:
				
				for(int i=0; i<event.values.length;i++)
				{
					mGra[i].setText(String.format("%6.3f", event.values[i]));
				}
			break;
			
			case Sensor.TYPE_ROTATION_VECTOR:
				
				for(int i=0; i<event.values.length;i++)
				{
					mRot_Vec[i].setText(String.format("%6.3f", event.values[i]));
				}
			break;
			
			default: {}
			
			}
	    	
	    }
	    
	    public void onAccuracyChanged(Sensor arg0, int arg1)
	    {
	    	
	    }
	    		
	}

	
	
	public class MyCheckBoxChangeClicker implements CheckBox.OnCheckedChangeListener
	 {
		

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) 
		{
			
			if(isChecked==true)
			{
				if(buttonView==mCheckBox_GPS)
				{
					boolean GPS_enabled = SensorStreamActivity.mLocationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			        
			        if (! GPS_enabled)
			        {
			        	Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			        	startActivity(intent);
			        }
					
					SensorStreamActivity.mLocationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ToggleSensorsActivity.this);
					for(int i=0; i<3; i++)
					{
						mGPS[i].setVisibility(View.VISIBLE);
					}
					SensorStreamActivity.setmGPS(true);
				}
				else if(buttonView==mCheckBox_Ori)
				{
					SensorStreamActivity.setMbOrientation(true);
					SensorStreamActivity.mSensor_Toggle.registerListener(mysoftwarewaresensorlist, SensorStreamActivity.mSensor_Toggle.getDefaultSensor(Sensor.TYPE_ORIENTATION), mScreenDelay);
					for(int i=0; i<3; i++)
					{
						mOri[i].setVisibility(View.VISIBLE);
					}
				}
				else if(buttonView==mCheckBox_Lin_Acc)
				{
					SensorStreamActivity.setMbLin_Acceleration(true);
					SensorStreamActivity.mSensor_Toggle.registerListener(mysoftwarewaresensorlist, SensorStreamActivity.mSensor_Toggle.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), mScreenDelay);
					for(int i=0; i<3; i++)
					{
						mLin_Acc[i].setVisibility(View.VISIBLE);
					}
				}
				else if(buttonView==mCheckBox_Grav)
				{
					SensorStreamActivity.setMbGravity(true);
					SensorStreamActivity.mSensor_Toggle.registerListener(mysoftwarewaresensorlist, SensorStreamActivity.mSensor_Toggle.getDefaultSensor(Sensor.TYPE_GRAVITY), mScreenDelay);
					for(int i=0; i<3; i++)
					{
						mGra[i].setVisibility(View.VISIBLE);
					}
				}
				else if(buttonView==mCheckBox_Rot_Vec)
				{
					SensorStreamActivity.setMbRot_Vector(true);
					SensorStreamActivity.mSensor_Toggle.registerListener(mysoftwarewaresensorlist, SensorStreamActivity.mSensor_Toggle.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), mScreenDelay);
					for(int i=0; i<3; i++)
					{
						mRot_Vec[i].setVisibility(View.VISIBLE);
					}
				}
				else if(buttonView==mCheckBox_Pre)
				{
					SensorStreamActivity.setMbPressure(true);
					SensorStreamActivity.mSensor_Toggle.registerListener(myhardwaresensorlist, SensorStreamActivity.mSensor_Toggle.getDefaultSensor(Sensor.TYPE_PRESSURE), mScreenDelay);
					mPre.setVisibility(View.VISIBLE);
				}
				else if(buttonView==mCheckBox_Bat_Temp)
				{
					SensorStreamActivity.setMbBat_Temp(true);
					ToggleSensorsActivity.this.registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
					mBat_Temp.setVisibility(View.VISIBLE);
				}
				else {}
			}
			else
			{
				if(buttonView==mCheckBox_GPS)
				{
					SensorStreamActivity.setmGPS(false);
					SensorStreamActivity.mLocationmanager.removeUpdates(ToggleSensorsActivity.this);
					for(int i=0; i<3; i++)
					{
						mGPS[i].setVisibility(View.INVISIBLE);
					}
				}
				else if(buttonView==mCheckBox_Ori)
				{
					SensorStreamActivity.setMbOrientation(false);
					SensorStreamActivity.mSensor_Toggle.unregisterListener(mysoftwarewaresensorlist, SensorStreamActivity.mSensor_Toggle.getDefaultSensor(Sensor.TYPE_ORIENTATION));
					for(int i=0; i<3; i++)
					{
						mOri[i].setVisibility(View.INVISIBLE);
					}
				}
				else if(buttonView==mCheckBox_Lin_Acc)
				{
					SensorStreamActivity.setMbLin_Acceleration(false);
					SensorStreamActivity.mSensor_Toggle.unregisterListener(mysoftwarewaresensorlist, SensorStreamActivity.mSensor_Toggle.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION));
					for(int i=0; i<3; i++)
					{
						mLin_Acc[i].setVisibility(View.INVISIBLE);
					}
				}
				else if(buttonView==mCheckBox_Grav)
				{
					SensorStreamActivity.setMbGravity(false);
					SensorStreamActivity.mSensor_Toggle.unregisterListener(mysoftwarewaresensorlist, SensorStreamActivity.mSensor_Toggle.getDefaultSensor(Sensor.TYPE_GRAVITY));
					for(int i=0; i<3; i++)
					{
						mGra[i].setVisibility(View.INVISIBLE);
					}
				}
				else if(buttonView==mCheckBox_Rot_Vec)
				{
					SensorStreamActivity.setMbRot_Vector(false);
					SensorStreamActivity.mSensor_Toggle.unregisterListener(mysoftwarewaresensorlist, SensorStreamActivity.mSensor_Toggle.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
					for(int i=0; i<3; i++)
					{
						mRot_Vec[i].setVisibility(View.INVISIBLE);
					}
				}
				else if(buttonView==mCheckBox_Pre)
				{
					SensorStreamActivity.setMbPressure(false);
					SensorStreamActivity.mSensor_Toggle.unregisterListener(myhardwaresensorlist, SensorStreamActivity.mSensor_Toggle.getDefaultSensor(Sensor.TYPE_PRESSURE));
					
					mPre.setVisibility(View.INVISIBLE);
				}
				else if(buttonView==mCheckBox_Bat_Temp)
				{
					SensorStreamActivity.setMbBat_Temp(false);
					ToggleSensorsActivity.this.unregisterReceiver(mBatInfoReceiver);
					mBat_Temp.setVisibility(View.INVISIBLE);
				}
				else {}
			}
				
	

		}

	 }
	public class MyCheckBox_Sensorupdate_Listener implements CheckBox.OnCheckedChangeListener
	{

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
		{
			if (isChecked)
			{
				SensorStreamActivity.setMbChecked_Sensor_Data(true);
				Toast.makeText( ToggleSensorsActivity.this , "Include Checked Sensors", Toast.LENGTH_SHORT).show();
						
			}
			else
			{
				SensorStreamActivity.setMbChecked_Sensor_Data(false);
				Toast.makeText( ToggleSensorsActivity.this , "Do Not Include Checked Sensors", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	
	MyHardwareSensorList myhardwaresensorlist = new MyHardwareSensorList();
	MySoftwareSensorList mysoftwarewaresensorlist = new MySoftwareSensorList();
	
	
	
	public static TextView  []mAcc 	= new TextView [3];
	public static TextView  []mGyr 	= new TextView [3];
	public static TextView  []mMag 	= new TextView [3];
	
	public static TextView  []mGPS 	= new TextView [3];
	public static TextView  []mOri 	= new TextView [3];
	public static TextView  []mLin_Acc = new TextView [3];
	public static TextView  []mGra 	= new TextView [3];
	public static TextView  []mRot_Vec = new TextView [3];
	public static TextView   mPre;
	public static TextView   mBat_Temp;
	
	

	private CheckBox mCheckBox_Acc;
	private CheckBox mCheckBox_Gyr;
	private CheckBox mCheckBox_Mag;
	
	private CheckBox mCheckBox_GPS;
	private CheckBox mCheckBox_Ori;
	private CheckBox mCheckBox_Lin_Acc;
	private CheckBox mCheckBox_Grav;
	private CheckBox mCheckBox_Rot_Vec;
	private CheckBox mCheckBox_Pre;
	private CheckBox mCheckBox_Bat_Temp;
	private CheckBox mCheckBox_Incl_Ch_Sensor;

	
	
	
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//Log.d(MDEBUG_TAG, getLocalClassName()+ " .onCreate aufgerufen");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.toggle_sensors_layout);
		
		
		mAcc[0] = (TextView) findViewById(R.id.X_Acc);
		mAcc[1] = (TextView) findViewById(R.id.Y_Acc);
		mAcc[2] = (TextView) findViewById(R.id.Z_Acc);
		
		mGyr[0] = (TextView) findViewById(R.id.X_Gyr);
		mGyr[1] = (TextView) findViewById(R.id.Y_Gyr);
		mGyr[2] = (TextView) findViewById(R.id.Z_Gyr);
		
		mMag[0] = (TextView) findViewById(R.id.X_Mag);
		mMag[1] = (TextView) findViewById(R.id.Y_Mag);
		mMag[2] = (TextView) findViewById(R.id.Z_Mag);
		
		mGPS[0] = (TextView) findViewById(R.id.Lat_GPS);
		mGPS[1] = (TextView) findViewById(R.id.Long_GPS);
		mGPS[2] = (TextView) findViewById(R.id.Alt_GPS);
		
		mOri[0] = (TextView) findViewById(R.id.X_Ori);
		mOri[1] = (TextView) findViewById(R.id.Y_Ori);
		mOri[2] = (TextView) findViewById(R.id.Z_Ori);
		
		mLin_Acc[0] = (TextView) findViewById(R.id.X_Lin_Acc);
		mLin_Acc[1] = (TextView) findViewById(R.id.Y_Lin_Acc);
		mLin_Acc[2] = (TextView) findViewById(R.id.Z_Lin_Acc);
		
		mGra[0] = (TextView) findViewById(R.id.X_Gra);
		mGra[1] = (TextView) findViewById(R.id.Y_Gra);
		mGra[2] = (TextView) findViewById(R.id.Z_Gra);
		
		mRot_Vec[0] = (TextView) findViewById(R.id.X_Rot_Vec);
		mRot_Vec[1] = (TextView) findViewById(R.id.Y_Rot_Vec);
		mRot_Vec[2] = (TextView) findViewById(R.id.Z_Rot_Vec);
		
		mPre = (TextView) findViewById(R.id.value_pressure);
		mBat_Temp = (TextView) findViewById(R.id.value_bat_temp);
		
		mCheckBox_Acc 			= (CheckBox) findViewById(R.id.CheckBox_Acc);
		mCheckBox_Gyr 			= (CheckBox) findViewById(R.id.CheckBox_Gyr);
		mCheckBox_Mag 			= (CheckBox) findViewById(R.id.CheckBox_Mag);
		
		mCheckBox_GPS			= (CheckBox) findViewById(R.id.CheckBox_GPS);
		mCheckBox_Ori 			= (CheckBox) findViewById(R.id.CheckBox_Ori);
		mCheckBox_Lin_Acc 		= (CheckBox) findViewById(R.id.CheckBox_Lin_Acc);
		mCheckBox_Grav 			= (CheckBox) findViewById(R.id.CheckBox_Gra);
		mCheckBox_Rot_Vec 		= (CheckBox) findViewById(R.id.CheckBox_Rot_Vec);
		mCheckBox_Pre 			= (CheckBox) findViewById(R.id.CheckBox_Pre);
		mCheckBox_Bat_Temp		= (CheckBox) findViewById(R.id.CheckBox_Bat_Temp);
		mCheckBox_Incl_Ch_Sensor= (CheckBox) findViewById(R.id.CheckBox_Include_Checked_Sensor);
		
		mCheckBox_GPS.setOnCheckedChangeListener(new MyCheckBoxChangeClicker());
		mCheckBox_Ori.setOnCheckedChangeListener(new MyCheckBoxChangeClicker());
		mCheckBox_Lin_Acc.setOnCheckedChangeListener(new MyCheckBoxChangeClicker());
		mCheckBox_Grav.setOnCheckedChangeListener(new MyCheckBoxChangeClicker());
		mCheckBox_Rot_Vec.setOnCheckedChangeListener(new MyCheckBoxChangeClicker());
		mCheckBox_Pre.setOnCheckedChangeListener(new MyCheckBoxChangeClicker());
		mCheckBox_Bat_Temp.setOnCheckedChangeListener(new MyCheckBoxChangeClicker());
		
		mCheckBox_Incl_Ch_Sensor.setOnCheckedChangeListener(new MyCheckBox_Sensorupdate_Listener());
		
	
	}

	private void switch_CheckBoxes_Status(boolean status)
	 {
		 mCheckBox_Acc.setEnabled(status);
		 mCheckBox_Gyr.setEnabled(status);
		 mCheckBox_Mag.setEnabled(status);
		 
		 mCheckBox_GPS.setEnabled(status);
		 mCheckBox_Ori.setEnabled(status);
		 mCheckBox_Lin_Acc.setEnabled(status);
		 mCheckBox_Grav.setEnabled(status);
		 mCheckBox_Rot_Vec.setEnabled(status);
		 mCheckBox_Pre.setEnabled(status);
		 mCheckBox_Incl_Ch_Sensor.setEnabled(status);
	    	
	 }
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(SensorStreamActivity.ismGPS() == true)
		{
			mCheckBox_GPS.setChecked(false);
			mCheckBox_Bat_Temp.setChecked(false);
		}
		
		//Log.d(MDEBUG_TAG, getLocalClassName()+ " .onDestroy aufgerufen");
	}

	
	@Override
	protected void onPause() {
		mActive=false;
		super.onPause();
		
		if(PreferencesActivity.ismStream_Active())
		{
			if(!SensorStreamActivity.isMbChecked_Sensor_Data())
			{
				if(mCheckBox_GPS.isChecked())
					for(int i=0; i<mGPS.length;i++)
					{
						mGPS[i].setVisibility(View.VISIBLE);
					}
				if(mCheckBox_Ori.isChecked())
					for(int i=0; i<mOri.length;i++)
					{
						mOri[i].setVisibility(View.VISIBLE);
					}
				if(mCheckBox_Lin_Acc.isChecked())
					for(int i=0; i<mLin_Acc.length;i++)
					{
						mLin_Acc[i].setVisibility(View.VISIBLE);
					}
				if(mCheckBox_Grav.isChecked())
					for(int i=0; i<mGra.length;i++)
					{
						mGra[i].setVisibility(View.VISIBLE);
					}
				if(mCheckBox_Rot_Vec.isChecked())
					for(int i=0; i<mRot_Vec.length;i++)
					{
						mRot_Vec[i].setVisibility(View.VISIBLE);
					}
				if(mCheckBox_Rot_Vec.isChecked())
					for(int i=0; i<mRot_Vec.length;i++)
					{
						mRot_Vec[i].setVisibility(View.VISIBLE);
					}
				if(mCheckBox_Pre.isChecked())
						mPre.setVisibility(View.VISIBLE);
					
			}
		}
		
		else
		{		
		try {
			SensorStreamActivity.mSensor_Toggle.unregisterListener(myhardwaresensorlist);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			if(SensorStreamActivity.isMbOrientation() || SensorStreamActivity.isMbLin_Acceleration() || SensorStreamActivity.isMbGravity() || SensorStreamActivity.isMbRot_Vector())
				SensorStreamActivity.mSensor_Toggle.unregisterListener(mysoftwarewaresensorlist);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		}
	
		//Log.d(MDEBUG_TAG, getLocalClassName()+ " .onPause aufgerufen");
	}

	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//Log.d(MDEBUG_TAG, getLocalClassName()+ " .onRestart aufgerufen");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mActive=true;
		
		if (PreferencesActivity.ismStream_Active())
		{
			switch_CheckBoxes_Status(false);
			if(!SensorStreamActivity.isMbChecked_Sensor_Data())
			{
				if(mCheckBox_GPS.isChecked())
					for(int i=0; i<mGPS.length;i++)
					{
						mGPS[i].setVisibility(View.INVISIBLE);
					}
				if(mCheckBox_Ori.isChecked())
					for(int i=0; i<mOri.length;i++)
					{
						mOri[i].setVisibility(View.INVISIBLE);
					}
				if(mCheckBox_Lin_Acc.isChecked())
					for(int i=0; i<mLin_Acc.length;i++)
					{
						mLin_Acc[i].setVisibility(View.INVISIBLE);
					}
				if(mCheckBox_Grav.isChecked())
					for(int i=0; i<mGra.length;i++)
					{
						mGra[i].setVisibility(View.INVISIBLE);
					}
				if(mCheckBox_Rot_Vec.isChecked())
					for(int i=0; i<mRot_Vec.length;i++)
					{
						mRot_Vec[i].setVisibility(View.INVISIBLE);
					}
				if(mCheckBox_Rot_Vec.isChecked())
					for(int i=0; i<mRot_Vec.length;i++)
					{
						mRot_Vec[i].setVisibility(View.INVISIBLE);
					}
				if(mCheckBox_Pre.isChecked())
						mPre.setVisibility(View.INVISIBLE);
					
			}
		}
	
		else
		{
			switch_CheckBoxes_Status(true);
		
			try {
				SensorStreamActivity.mSensor_Toggle.registerListener(myhardwaresensorlist, SensorStreamActivity.mSensor_Toggle.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), mScreenDelay);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				SensorStreamActivity.mSensor_Toggle.registerListener(myhardwaresensorlist, SensorStreamActivity.mSensor_Toggle.getDefaultSensor(Sensor.TYPE_GYROSCOPE), mScreenDelay);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				SensorStreamActivity.mSensor_Toggle.registerListener(myhardwaresensorlist, SensorStreamActivity.mSensor_Toggle.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), mScreenDelay);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		try {
			if(SensorStreamActivity.isMbOrientation())
				SensorStreamActivity.mSensor_Toggle.registerListener(mysoftwarewaresensorlist, SensorStreamActivity.mSensor_Toggle.getDefaultSensor(Sensor.TYPE_ORIENTATION), mScreenDelay);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			if(SensorStreamActivity.isMbLin_Acceleration())
				SensorStreamActivity.mSensor_Toggle.registerListener(mysoftwarewaresensorlist, SensorStreamActivity.mSensor_Toggle.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), mScreenDelay);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			if(SensorStreamActivity.isMbGravity())
				SensorStreamActivity.mSensor_Toggle.registerListener(mysoftwarewaresensorlist, SensorStreamActivity.mSensor_Toggle.getDefaultSensor(Sensor.TYPE_GRAVITY), mScreenDelay);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			if(SensorStreamActivity.isMbRot_Vector())
				SensorStreamActivity.mSensor_Toggle.registerListener(mysoftwarewaresensorlist, SensorStreamActivity.mSensor_Toggle.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), mScreenDelay);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			if(SensorStreamActivity.isMbPressure())
				SensorStreamActivity.mSensor_Toggle.registerListener(myhardwaresensorlist, SensorStreamActivity.mSensor_Toggle.getDefaultSensor(Sensor.TYPE_PRESSURE), mScreenDelay);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		//Log.d(MDEBUG_TAG, getLocalClassName()+ " .onResume aufgerufen");
		
	}

	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		//Log.d(MDEBUG_TAG, getLocalClassName()+ " .onStart aufgerufen");
	}

	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (SensorStreamActivity.ismGPS() == true && SensorStreamActivity.ismRun_in_Background()== false)
		{
			mCheckBox_GPS.setChecked(false);
			mCheckBox_Bat_Temp.setChecked(false);
		}
			
		
		//Log.d(MDEBUG_TAG, getLocalClassName()+ " .onStop aufgerufen");
	}

	public static double[] getmBLH() {
		return mBLH;
	}

	public static double[] getmXYZ() {
		return mXYZ;
	}

	public static double[] getmV_e() {
		return mV_e;
	}


	public static long getmGPS_UCT_Time() {
		return mGPS_UCT_Time;
	}

	public static int getmBattery_Temperature() {
		return mBattery_Temperature;
	}

	public static boolean ismActive() {
		return mActive;
	}

	public static void setmGps_available(boolean mGps_available) {
		ToggleSensorsActivity.mGps_available = mGps_available;
	}

	public static boolean ismGps_available() {
		return mGps_available;
	}

	public static void setmActive(boolean mActive) {
		ToggleSensorsActivity.mActive = mActive;
	}
	


	@Override
	public void onLocationChanged(Location location) {
		
		if(PreferencesActivity.ismStream_Active() == true && SensorStreamActivity.isMbChecked_Sensor_Data() == true)
		{
			setmGps_available(false);
			
			mBLH[0]= location.getLatitude();
			mBLH[1]= location.getLongitude();
			mBLH[2]= location.getAltitude();
			
			mSpeed = location.getSpeed();
			mBear = location.getBearing();
			
			mGPS_UCT_Time = location.getTime();
			
			mXYZ = GNSS_Arithmetic.blh_to_xyz(mBLH);
			mV_e = GNSS_Arithmetic.v_et_bear_to_v_e(mSpeed, mBear);
			
			setmGps_available(true);
		}
	
		if(mActive == true && SensorStreamActivity.ismGPS() == true)
		{
			mLatitude 	= location.getLatitude();
			mLongitude  = location.getLongitude();
			mAltitude	= location.getAltitude();
		
			mGPS[0].setText(String.format("%.5f", mLatitude));
			mGPS[1].setText(String.format("%.5f", mLongitude));
			mGPS[2].setText(String.format("%.1f", mAltitude));
		}
		
		//mGPS_counter++;
		
	}
	

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		if (provider.equals("gps"))
		{
			
			if(SensorStreamActivity.ismGPS() == true)
				{
				
				
				
					boolean GPS_enabled = SensorStreamActivity.mLocationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			        
			        if (! GPS_enabled)
			        {
			        	mCheckBox_GPS.setChecked(false);
						showDialog(R.string.error_gps_disabled);
						Toast.makeText(this , "GPS Disabled", Toast.LENGTH_SHORT).show();
						
			        }
					
					
				}
		}
		
	}
	

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		if (provider.equals("gps"))
		{
		Toast.makeText(this , "GPS Enabled", Toast.LENGTH_SHORT).show();
		}
	}
	

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
		// TODO Auto-generated method stub
		if (provider.equals("gps") && status == 0)
		{
			if(SensorStreamActivity.ismGPS() == true)
			{
				mCheckBox_GPS.setChecked(false);
				showDialog(R.string.error_gps_outofservice);
				Toast.makeText(this , "GPS OUT OF SERVICE", Toast.LENGTH_SHORT).show();
				
			}
			
		}
	}

	
	
	@Override
	protected Dialog onCreateDialog(int id) 
	{
		
		switch(id)
		{
		
	
	
		case R.string.error_gps_disabled:
			
			
			Builder builder_1 = new AlertDialog.Builder(this);
			builder_1.setMessage(getString(id));
	    	//Anonyme Implementierung der Methode OnClick
	    	builder_1.setNeutralButton("OK", new DialogInterface.OnClickListener() 
	    		{
	    	    	public void onClick(DialogInterface dialog, int which) 
	    	    	{
	    	    		dialog.dismiss();
	    	    	}
	    		}		
	    				);
	    	AlertDialog alertDialog_1 = builder_1.create();
	    	alertDialog_1.show();
	    	break;
	    	
		case R.string.error_gps_outofservice:
			
			
			Builder builder_2 = new AlertDialog.Builder(this);
			builder_2.setMessage(getString(id));
	    	//Anonyme Implementierung der Methode OnClick
	    	builder_2.setNeutralButton("OK", new DialogInterface.OnClickListener() 
	    		{
	    	    	public void onClick(DialogInterface dialog, int which) 
	    	    	{
	    	    		dialog.dismiss();
	    	    	}
	    		}		
	    				);
	    	AlertDialog alertDialog_2 = builder_2.create();
	    	alertDialog_2.show();
	    	break;
		
		}
		
		// TODO Auto-generated method stub
		return super.onCreateDialog(id);
	}

	
	

}
