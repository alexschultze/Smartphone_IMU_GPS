 package de.lorenz_fenster.sensorstreamgps;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

public class PreferencesActivity extends Activity implements  OnItemSelectedListener{
	
	
	//private long temp1 = 0;
	//private long temp2 = 0;
	
	
	//private final String MDEBUG_TAG = "follow_onX";
	
	private String mSD_Card_Filename;
	private static boolean mStream_Active = false;
	
	public static DatagramSocket mSocket = null;
    public static DatagramPacket mPacket = null;
	
    private static final int CSV_ID_BLH				= 1;
    
    private static final int CSV_ID_ACCELEROMETER 	= 3;
    private static final int CSV_ID_GYROSCOPE     	= 4;
    private static final int CSV_ID_MAG          	= 5;
    private static final int CSV_ID_XYZ_WGS84       = 6;
    private static final int CSV_ID_VELOCITY_WGS84  = 7;
    
    private static final int CSV_ID_GPS_UTC_TIME	= 8;
   
    
    private static final int CSV_ID_ORI				= 81;
    private static final int CSV_ID_LIN_ACC			= 82;
    private static final int CSV_ID_GRA				= 83;
    private static final int CSV_ID_ROT_VEC			= 84;
    private static final int CSV_ID_ORI_MATRIX		= 85;
    private static final int CSV_ID_QUATERNIONS		= 86;
    private static final int CSV_ID_PRE				= 87;
    private static final int CSV_ID_BAT_TEMP		= 88;
    
  
    
    
    StringBuilder mStrBuilder = new StringBuilder(256);
    private String mSensordata;
    
    


    private double[] mAccBuffer 		= new double[3];
    private double[] mGyroBuffer 		= new double[3];
    private double[] mMagBuffer 		= new double[3];
    
  
    private double[] mOriBuffer 		= new double[3];
    private double[] mLin_Acc_Buffer 	= new double[3];
    private double[] mGraBuffer 		= new double[3];
    private double[] mRot_Vec_Buffer 	= new double[3];
    private double mPreBuffer;
    
    private boolean mAccBufferReady 		= false;
    private boolean mGyroBufferReady 		= false;
    private boolean mMagBufferReady 		= false;
    private boolean mOriBufferReady			= false;
    private boolean mLin_Acc_BufferReady	= false;
    private boolean mGraBufferReady			= false;
    private boolean mRot_Vec_BufferReady	= false;
    private boolean mPreBufferReady			= false;
    
    private double mAccTime = 0;
    private double mGyroTime = 0;
    private double mMagTime = 0;
    private double mOriTime = 0;
    private double mLin_Acc_Time = 0;
    private double mGraTime = 0;
    private double mRot_Vec_Time = 0;
    private double mPreTime = 0;
    private static final double mMaxSecDiff = 0.5; // max time diff between gyro and acc.
    private static final double NS2S = 1.0f / 1000000000.0f; // nanosec to sec

    private int mCounter = 0;
    private int mScreenDelay = 15;

	private EditText mSD_Card_Dialog_EditText;
	private EditText mIP_Adress;
	private EditText mPort;
	
	private Spinner mSpinner;
	
	private ToggleButton mToggleButton_Stream;
	public static CheckBox mCheckBox_Background;
	
	private RadioGroup mRadioGroup;
	private RadioButton mUDP_SD_Stream;
	private RadioButton mUDP_Stream;
	private RadioButton mSD_Card_Stream;
	
	private ProgressBar mProgessBar;
	private TextView mSendingState;
	
	private PowerManager mgr;
	private WakeLock wakeLock;
	

	
	

	public class My_Hardware_SensorListener implements  SensorEventListener
	{

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSensorChanged(SensorEvent event) 
		{
			
			
			//temp1 = SystemClock.uptimeMillis();		
			
			double timestamp_sec = event.timestamp * NS2S;
			//double x = event.values[0];
	        //double y = event.values[1];
	        //double z = event.values[2];
					
		       
			switch (event.sensor.getType()) {
			
		        case Sensor.TYPE_ACCELEROMETER:
		        	
		        	mCounter++;
		        	mAccBuffer[0] = event.values[0];
		            mAccBuffer[1] = event.values[1];
		            mAccBuffer[2] = event.values[2];
		            mAccBufferReady = true;
		            mAccTime = timestamp_sec;
		            break;
		        case Sensor.TYPE_GYROSCOPE:
		        	mGyroBuffer[0] = event.values[0];
		        	mGyroBuffer[1] = event.values[1];
		        	mGyroBuffer[2] = event.values[2];
		            mGyroBufferReady = true;
		            mGyroTime = timestamp_sec;
		            break;
		        case Sensor.TYPE_MAGNETIC_FIELD:
		        	mMagBuffer[0] = event.values[0];
		        	mMagBuffer[1] = event.values[1];
		        	mMagBuffer[2] = event.values[2];
		        	mMagBufferReady = true;
		            mMagTime = timestamp_sec;
		            break;
		            
		        case Sensor.TYPE_PRESSURE:
		        	
		        	mPreBuffer = event.values[0];
		        	mPreBufferReady = true;
		            mPreTime = timestamp_sec;
		            break;
		            
		        default:
		            return;
		        }

			if (mAccBufferReady == false)
			{
				return; 
			}
	               
		        // update the screen at a lower rate
			
			
			if ((ToggleSensorsActivity.ismActive() && mCounter % mScreenDelay ==0)) 
			{		        	

		            	
		            	for(int i=0; i<3;i++)
			            {
		            		ToggleSensorsActivity.mAcc[i].setText(String.format("%6.3f", mAccBuffer[i]));
		            		ToggleSensorsActivity.mGyr[i].setText(String.format("%6.3f", mGyroBuffer[i]));
		            		ToggleSensorsActivity.mMag[i].setText(String.format("%6.3f", mMagBuffer[i]));
			            }
		               
		            	//if (SensorStreamActivity.isMbChecked_Sensor_Data())
		            	//{
		            		
		            		if(SensorStreamActivity.isMbOrientation())
		    				{
		    					for(int i=0; i<3;i++)
		    		            {
		    	            		ToggleSensorsActivity.mOri[i].setText(String.format("%6.3f", mOriBuffer[i]));
		    		            }
		    				}
		    				
		    				if(SensorStreamActivity.isMbLin_Acceleration())
		    				{
		    					for(int i=0; i<3;i++)
		    		            {
		    	            		ToggleSensorsActivity.mLin_Acc[i].setText(String.format("%6.3f", mLin_Acc_Buffer[i]));
		    		            }
		    				}
		    				
		    				if(SensorStreamActivity.isMbGravity())
		    				{
		    					for(int i=0; i<3;i++)
		    		            {
		    	            		ToggleSensorsActivity.mGra[i].setText(String.format("%6.3f", mGraBuffer[i]));
		    		            }
		    				}
		    				
		    				if(SensorStreamActivity.isMbRot_Vector())
		    				{
		    					for(int i=0; i<3;i++)
		    		            {
		    	            		ToggleSensorsActivity.mRot_Vec[i].setText(String.format("%6.3f", mRot_Vec_Buffer[i]));
		    		            }
		    				}
		    				
		            		if(SensorStreamActivity.isMbPressure())
		            		{
		            			ToggleSensorsActivity.mPre.setText(String.format("%6.3f", mPreBuffer));
					            
		            		}
		            	//}
		                
		            
		            
		        }
		        
			
			
		
		        
		        boolean gyroReady =
		            ( (Math.abs(mGyroTime - mAccTime) < mMaxSecDiff) &&
		              (mGyroBufferReady == true) );
		        boolean magReady =
		            ( (Math.abs(mMagTime - mAccTime) < mMaxSecDiff) &&
		              (mMagBufferReady == true) );
		        
		        mStrBuilder.setLength(0);
		           
		        addSensorToString(mStrBuilder, CSV_ID_ACCELEROMETER, mAccBuffer);
		        mAccBufferReady = false;
		        
		        if (gyroReady == true) {
		             addSensorToString(mStrBuilder, CSV_ID_GYROSCOPE, mGyroBuffer);
		            mGyroBufferReady = false;
		        }
		        if (magReady == true) {
		            addSensorToString(mStrBuilder, CSV_ID_MAG,  mMagBuffer);
		            mMagBufferReady = false;
		        }

		        
		        
		        
		        //if(SensorStreamActivity.isMbChecked_Sensor_Data())
		        //{
		        	
		        	if(SensorStreamActivity.ismGPS())
		        	{
		        		if(ToggleSensorsActivity.ismGps_available() == true)
		        		{

		        			
		        			mStrBuilder.insert(0, String.format(Locale.ENGLISH, ", %d, %10.6f,%11.6f,%6.1f", CSV_ID_BLH, ToggleSensorsActivity.getmBLH()[0],ToggleSensorsActivity.getmBLH()[1],ToggleSensorsActivity.getmBLH()[2]));
		        			mStrBuilder.append(String.format(Locale.ENGLISH, ", %d, %12.3f,%12.3f,%12.3f", CSV_ID_XYZ_WGS84, ToggleSensorsActivity.getmXYZ()[0],ToggleSensorsActivity.getmXYZ()[1],ToggleSensorsActivity.getmXYZ()[2]));
		        			mStrBuilder.append(String.format(Locale.ENGLISH, ", %d, %6.3f,%6.3f,%6.3f", CSV_ID_VELOCITY_WGS84, ToggleSensorsActivity.getmV_e()[0],ToggleSensorsActivity.getmV_e()[1],ToggleSensorsActivity.getmV_e()[2]));
		        			mStrBuilder.append(String.format(Locale.ENGLISH, ", %d, %d", CSV_ID_GPS_UTC_TIME, ToggleSensorsActivity.getmGPS_UCT_Time()));
		        			
		        			
		        			
		        			/*
		        			 * 
		        			 * 
		        			addGNSSToString(mStrBuilder, CSV_ID_BLH, ToggleSensorsActivity.getmBLH());
		        			addGNSSToString(mStrBuilder, CSV_ID_XYZ_WGS84, ToggleSensorsActivity.getmXYZ());
		        			addGNSSToString(mStrBuilder, CSV_ID_VELOCITY_WGS84, ToggleSensorsActivity.getmV_e());
		        			
		        			mStrBuilder.insert(0, String.format(Locale.ENGLISH, ", %d, %10.6f,%11.6f,%6.1f", CSV_ID_BLH, ToggleSensorsActivity.getmBLH()));
		        			mStrBuilder.append(String.format(Locale.ENGLISH, ", %d, %12.3f,%12.3f,%12.3f", CSV_ID_XYZ_WGS84, ToggleSensorsActivity.getmXYZ()));
		        			mStrBuilder.append(String.format(Locale.ENGLISH, ", %d, %6.3f,%6.3f,%6.3f", CSV_ID_VELOCITY_WGS84, ToggleSensorsActivity.getmV_e()));
		        			
		        			mStrBuilder.insert(0, String.format(Locale.ENGLISH, ", %d, %10.6f,%11.6f,%5.1f", sensorid, values[0], values[1], values[2]));
		        			
		        			
		        			else if(sensorid == 6)
		        			{
		        				strbuilder.append(String.format(Locale.ENGLISH, ", %d, %12.3f,%12.3f,%12.3f", sensorid, values[0], values[1], values[2]));
		        			}
		        			
		        			else if(sensorid == 7)
		        			{
		        				strbuilder.append(String.format(Locale.ENGLISH, ", %d, %6.3f,%6.3f,%6.3f", sensorid, values[0], values[1], values[2]));
		        				//strbuilder.append(String.format(Locale.ENGLISH, ", Absolute Speed %.3f", ToggleSensorsActivity.mSpeed));
		        			}
		        			*/
		        			
		        		}
		        	}
		        	if(SensorStreamActivity.isMbOrientation())
		        	{
		        		boolean oriReady =
				            ( (Math.abs(mOriTime - mAccTime) < mMaxSecDiff) &&
				              (mOriBufferReady == true) );
		        		
		        		if (oriReady == true) {
				             addSensorToString(mStrBuilder, CSV_ID_ORI, mOriBuffer);
				            mOriBufferReady = false;
				        }
		        	}
		        	
		        	if(SensorStreamActivity.isMbLin_Acceleration())
		        	{
		        		boolean linaccReady =
				            ( (Math.abs(mLin_Acc_Time - mAccTime) < mMaxSecDiff) &&
				              (mLin_Acc_BufferReady == true) );
		        		
		        		if (linaccReady == true) {
				             addSensorToString(mStrBuilder, CSV_ID_LIN_ACC, mLin_Acc_Buffer);
				            mLin_Acc_BufferReady = false;
				        }
		        	}
		        	
		        	if(SensorStreamActivity.isMbGravity())
		        	{
		        		boolean gravReady =
				            ( (Math.abs(mGraTime - mAccTime) < mMaxSecDiff) &&
				              (mGraBufferReady == true) );
		        		
		        		if (gravReady == true) {
				             addSensorToString(mStrBuilder, CSV_ID_GRA, mGraBuffer);
				            mGraBufferReady = false;
				        }
		        	}
		        	
		        	if(SensorStreamActivity.isMbRot_Vector())
		        	{
		        		boolean rotvecReady =
				            ( (Math.abs(mRot_Vec_Time - mAccTime) < mMaxSecDiff) &&
				              (mRot_Vec_BufferReady == true) );
		        		
		        		if (rotvecReady == true) {
				             addSensorToString(mStrBuilder, CSV_ID_ROT_VEC, mRot_Vec_Buffer);
				             
				             float ori_matrix [] = new float [9];
				             float quaternions [] = new float [4];
				             float[] rotationVector_f = new float [3];
				             
				             for(int i = 0; i<3; i++)
				             {
				            	 rotationVector_f[i] =  (float) mRot_Vec_Buffer[i];    	 
				             }
				             	             
				             SensorManager.getRotationMatrixFromVector(ori_matrix, rotationVector_f);
				             SensorManager.getQuaternionFromVector(quaternions, rotationVector_f);
				             add_ori_quat_ToString(mStrBuilder, CSV_ID_ORI_MATRIX, CSV_ID_QUATERNIONS, ori_matrix, quaternions);

  
				             mRot_Vec_BufferReady = false;
				        }
		        	}
		        	
		        	if(SensorStreamActivity.isMbPressure())
		        	{
		        		boolean preReady =
				            ( (Math.abs(mPreTime - mAccTime) < mMaxSecDiff) &&
				              (mPreBufferReady == true) );
		        		
		        		if (preReady == true) {
				             addSensorToString(mStrBuilder, CSV_ID_PRE, mPreBuffer);
				            
				        }
		        	}
		        	if(SensorStreamActivity.isMbBat_Temp())
		        	{
		        		mStrBuilder.append(String.format(Locale.ENGLISH, ", %d, %d", CSV_ID_BAT_TEMP, ToggleSensorsActivity.getmBattery_Temperature()));
		        	}
		        //}
		  	
		        
		        mStrBuilder.insert(0,String.format(Locale.ENGLISH, "%.5f", timestamp_sec));
		        mSensordata = mStrBuilder.toString();
	          
		        if(mUDP_SD_Stream.isChecked())
		        {
		        	new UDPThread(mSensordata).send();
		        	
		        	if (SD_Card_Setup.getmBufferedwriter() != null)
		        	{
		        		SD_Card_Setup.write(mSensordata);
		        		SD_Card_Setup.write_IMU(timestamp_sec, mAccBuffer, mGyroBuffer, gyroReady);
		        		SD_Card_Setup.write_MAG(timestamp_sec, mMagBuffer, magReady);
		        		
		        		//if(SensorStreamActivity.isMbChecked_Sensor_Data())
				        //{
		        			if(SensorStreamActivity.ismGPS())
				        	{		        				
		        				SD_Card_Setup.write_GNSS(timestamp_sec, ToggleSensorsActivity.ismGps_available());
		        				ToggleSensorsActivity.setmGps_available(false);
				        	}
		        			
		        			if(SensorStreamActivity.isMbPressure())
		        			{	
		        				SD_Card_Setup.write_BARO(timestamp_sec, mPreBuffer, ( (Math.abs(mPreTime - mAccTime) < mMaxSecDiff) && (mPreBufferReady == true) ) , SensorStreamActivity.isMbBat_Temp());
		        				mPreBufferReady = false;
		        			}
				        //}	
		        	}
		        	
		        }
		        else if(mUDP_Stream.isChecked())
		        {
		        		new UDPThread(mSensordata).send();
		        }
		        else if(mSD_Card_Stream.isChecked())
		        {
		        	if (SD_Card_Setup.getmBufferedwriter() != null)
		        	{
		        		SD_Card_Setup.write(mSensordata);
		        		SD_Card_Setup.write_IMU(timestamp_sec, mAccBuffer, mGyroBuffer, gyroReady);
		        		SD_Card_Setup.write_MAG(timestamp_sec, mMagBuffer, magReady);
		        	
		        		//if(SensorStreamActivity.isMbChecked_Sensor_Data())
				        //{
		        			if(SensorStreamActivity.ismGPS())
				        	{		        				
		        				SD_Card_Setup.write_GNSS(timestamp_sec, ToggleSensorsActivity.ismGps_available());
		        				ToggleSensorsActivity.setmGps_available(false);
				        	}
		        			
		        			if(SensorStreamActivity.isMbPressure())
		        			{	
		        				SD_Card_Setup.write_BARO(timestamp_sec, mPreBuffer, ( (Math.abs(mPreTime - mAccTime) < mMaxSecDiff) && (mPreBufferReady == true) ) , SensorStreamActivity.isMbBat_Temp());
		        				mPreBufferReady = false;
		        			}
				        //}
		        	}
		        }
		        
		}
		
	}
	
	public class My_Software_SensorListener implements SensorEventListener
	{

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		
		@Override
		public void onSensorChanged(SensorEvent event) {
			double timestamp_sec = event.timestamp * NS2S;

			switch (event.sensor.getType())
			{
				case Sensor.TYPE_ORIENTATION:
		        	
		        	for(int i=0; i<event.values.length;i++)
		            {
		            	mOriBuffer[i]=event.values[i];
		            }
		        	mOriBufferReady = true;
		            mOriTime = timestamp_sec;
		            break;
		            
				case Sensor.TYPE_LINEAR_ACCELERATION:
		        	
		        	for(int i=0; i<event.values.length;i++)
		            {
		            	mLin_Acc_Buffer[i]=event.values[i];
		            }
		        	mLin_Acc_BufferReady = true;
		            mLin_Acc_Time = timestamp_sec;
		            break;
		            
				case Sensor.TYPE_GRAVITY:
		        	
		        	for(int i=0; i<event.values.length;i++)
		            {
		            	mGraBuffer[i]=event.values[i];
		            }
		        	mGraBufferReady = true;
		            mGraTime = timestamp_sec;
		            break;
		            
				case Sensor.TYPE_ROTATION_VECTOR:
		        	
		        	for(int i=0; i<event.values.length;i++)
		            {
		            	mRot_Vec_Buffer[i]=event.values[i];
		            }
		        	mRot_Vec_BufferReady = true;
		            mRot_Vec_Time = timestamp_sec;
		            break;
			}
			
			
		}
		
	}
	
	public class MyToggle_Button_Listener implements ToggleButton.OnCheckedChangeListener
	{

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) 
		{
			if (buttonView == mToggleButton_Stream)
			{
				if (mStream_Active == true) {
	                stopStreaming();
	            }
	            else 
	            {
	            	if (mUDP_SD_Stream.isChecked() || mSD_Card_Stream.isChecked())
	            	{
	            		showDialog(R.layout.sd_card_dialog);
	            	}
	            	else
	            	{
	            		boolean streaming = startStreaming();
		                if(streaming ==false)
		                {
		                	stopStreaming();
		                }
	            	}
	                
	            }
				mToggleButton_Stream.setChecked(mStream_Active);
			}
			
			else {mToggleButton_Stream.setChecked(false);}
		}
		
	}
	
	My_Hardware_SensorListener myhardwaresensorlistener = new My_Hardware_SensorListener();
	My_Software_SensorListener mysoftwarewaresensorlistener = new My_Software_SensorListener();

	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		//Log.d(MDEBUG_TAG, getLocalClassName()+ " .onCreate aufgerufen");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_layout);
        
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        //dalvik.system.VMRuntime.getRuntime().setMinimumHeapSize(32 * 1024 * 1024);
        
        
        
        this.mgr = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        this.wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        
        
        
        mIP_Adress 					= (EditText) 		findViewById(R.id.Edit_Address_Box);
        mPort						= (EditText) 		findViewById(R.id.Edit_Port_Box);
        mToggleButton_Stream		= (ToggleButton) 	findViewById(R.id.ToggleButton_Stream);        
        mCheckBox_Background		= (CheckBox)		findViewById(R.id.CheckBox_Run_Background);
        mRadioGroup					= (RadioGroup) 		findViewById(R.id.RadioGroup);
        mUDP_SD_Stream				= (RadioButton) 	findViewById(R.id.UDP_SD_Stream);
        mUDP_Stream					= (RadioButton)		findViewById(R.id.UDP_Stream);
        mSD_Card_Stream				= (RadioButton) 	findViewById(R.id.SD_Stream);
        
        mProgessBar					= (ProgressBar)		findViewById(R.id.ProgressBar);
        mSendingState				= (TextView)		findViewById(R.id.Sending_State);
        
       
        mSpinner = (Spinner) findViewById(R.id.spinner1);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.spinner_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSpinner.setAdapter(adapter);
        
        mSpinner.setOnItemSelectedListener(this);
        mSpinner.setSelection(2);
        mToggleButton_Stream.setOnCheckedChangeListener(new MyToggle_Button_Listener());
        
        mCheckBox_Background.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked == true)
				{
					SensorStreamActivity.setmRun_in_Background(true);
				}
				else if (isChecked == false)
				{
					SensorStreamActivity.setmRun_in_Background(false);
				}
			}
		});
        
        
       
        loadSavedPreferences();
             
        
	}
	

    
    private void loadSavedPreferences() {
		// TODO Auto-generated method stub
		SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
		mIP_Adress.setText(sharedpreferences.getString("ip_adress", "192.168.0.1"));
		mPort.setText(sharedpreferences.getString("port", "5555"));
		
		int radio_selection = sharedpreferences.getInt("radiogroup", 0);
		
		switch(radio_selection)
		{
		case R.id.UDP_SD_Stream: 
			mRadioGroup.check(R.id.UDP_SD_Stream);
			break;
		
		case R.id.UDP_Stream: 
			mRadioGroup.check(R.id.UDP_Stream);
			break;
		
		case  R.id.SD_Stream: 
			mRadioGroup.check(R.id.SD_Stream);
			break;
			
		default:
			mRadioGroup.check(R.id.UDP_SD_Stream);
			break;
		}
		
		int spinner_selection = sharedpreferences.getInt("spinner", 2);
		mSpinner.setSelection(spinner_selection);
		
		boolean check_box_background = sharedpreferences.getBoolean("run_background", false);
		mCheckBox_Background.setChecked(check_box_background);
		
		boolean gps_set = sharedpreferences.getBoolean("gps", false);
		SensorStreamActivity.setmGPS(gps_set);
		
		
		boolean ori_set = sharedpreferences.getBoolean("ori", false);
		SensorStreamActivity.setMbOrientation(ori_set);
		
		
		boolean lin_set = sharedpreferences.getBoolean("lin", false);
		SensorStreamActivity.setMbLin_Acceleration(lin_set);
		
		boolean gra_set = sharedpreferences.getBoolean("gra", false);
		SensorStreamActivity.setMbGravity(gra_set);
		
		boolean rot_set = sharedpreferences.getBoolean("rot", false);
		SensorStreamActivity.setMbRot_Vector(rot_set);
		
		boolean pre_set = sharedpreferences.getBoolean("pre", false);
		SensorStreamActivity.setMbPressure(pre_set);
		
		boolean bat_set = sharedpreferences.getBoolean("bat", false);
		SensorStreamActivity.setMbBat_Temp(bat_set);

		
    }
    
    	private void savePreferences(String ip_adress, String port) {
    		SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
    		Editor editor = sharedpreferences.edit(); //edit the SharedPreferences 'sharedPreferences'  
    		
    		if(ip_adress != null)
    		{
    		editor.putString("ip_adress", ip_adress); //put the string and lable it as "string1"  
    		editor.commit(); //commit it  
    		}
    		
    		if(port != null)
    		{
    		editor.putString("port", port); //put the string and lable it as "string1"  
    		editor.commit(); //commit it 
    		}
 		
    		editor.putInt("radiogroup", mRadioGroup.getCheckedRadioButtonId());
    		editor.commit();
    		
    		editor.putInt("spinner", mSpinner.getSelectedItemPosition());
    		editor.commit();
    		
    		editor.putBoolean("run_background", mCheckBox_Background.isChecked());
    		editor.commit();
    		
    		
    		boolean is_gps = SensorStreamActivity.ismGPS();
    		editor.putBoolean("gps", is_gps);
    		editor.commit();
    		
    		boolean is_ori = SensorStreamActivity.isMbOrientation();
    		editor.putBoolean("ori", is_ori);
    		editor.commit();
    		
    		boolean is_lin = SensorStreamActivity.isMbLin_Acceleration();
    		editor.putBoolean("lin", is_lin);
    		editor.commit();
    		
    		
    		boolean is_gra = SensorStreamActivity.isMbGravity();
    		editor.putBoolean("gra", is_gra);
    		editor.commit();
    		
    		boolean is_rot = SensorStreamActivity.isMbRot_Vector();
    		editor.putBoolean("rot", is_rot);
    		
    		boolean is_pre = SensorStreamActivity.isMbPressure();
    		editor.putBoolean("pre", is_pre);
    		editor.commit();
    		
    		boolean is_bat = SensorStreamActivity.isMbBat_Temp();
    		editor.putBoolean("bat", is_bat);
    		editor.commit();
    		
    }



	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		//Log.d(MDEBUG_TAG, getLocalClassName()+ " .onStart aufgerufen");
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
		//Log.d(MDEBUG_TAG, getLocalClassName()+ " .onRestart aufgerufen");
		
	}
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
        //Log.d(MDEBUG_TAG, getLocalClassName()+ " .onResume aufgerufen");
        
	}
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		
		//Log.d(MDEBUG_TAG, getLocalClassName()+ " .onPause aufgerufen");
		
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		if (SensorStreamActivity.ismRun_in_Background()== false && mStream_Active == true) 
		{
        	mToggleButton_Stream.setChecked(false);
        	stopStreaming();
        }
		
		
		//Log.d(MDEBUG_TAG, getLocalClassName()+ " .onStop aufgerufen");

	}
    
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@SuppressLint("Wakelock")
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		if (mStream_Active == true) {
            stopStreaming();
        }
		
		savePreferences(mIP_Adress.getText().toString(), mPort.getText().toString());
		if(this.wakeLock.isHeld() == true)
		{
			this.wakeLock.release();
		}
		
		//Log.d(MDEBUG_TAG, getLocalClassName()+ " .onDestroy aufgerufen");
	}


	public boolean start_Hardware_Sensors() {
        
		
        try {
            SensorStreamActivity.mSensor_Stream.registerListener(myhardwaresensorlistener,
                    SensorStreamActivity.mSensor_Stream.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorStreamActivity.getmDelay());
            
        	} catch (Exception e) {
        	e.printStackTrace();
            return false;
        	}
        
        try {
            SensorStreamActivity.mSensor_Stream.registerListener(myhardwaresensorlistener,
                    SensorStreamActivity.mSensor_Stream.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorStreamActivity.getmDelay());
            
        	} catch (Exception e) {
        	e.printStackTrace();
            return false;
        	}
        	
        try {
            SensorStreamActivity.mSensor_Stream.registerListener(myhardwaresensorlistener,
                    SensorStreamActivity.mSensor_Stream.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorStreamActivity.getmDelay());
            
        	} catch (Exception e) {
        	e.printStackTrace();
            return false;
        	}
        	
        //if(SensorStreamActivity.isMbChecked_Sensor_Data())
        //{
        	if(SensorStreamActivity.isMbPressure() )
            {
            try {
                SensorStreamActivity.mSensor_Stream.registerListener(myhardwaresensorlistener,
                        SensorStreamActivity.mSensor_Stream.getDefaultSensor(Sensor.TYPE_PRESSURE), SensorStreamActivity.getmDelay());
                
            	} catch (Exception e) {
            		e.printStackTrace();
            		return false;
            		}
            }
       
        //}
    
		return true;
	}
	
	public boolean start_Software_Sensors()
	{
		//if (SensorStreamActivity.isMbChecked_Sensor_Data())
		//{
		if (SensorStreamActivity.isMbOrientation())
		try {
			SensorStreamActivity.mSensor_Stream.registerListener(mysoftwarewaresensorlistener, SensorStreamActivity.mSensor_Stream.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorStreamActivity.getmDelay());
			} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		if(SensorStreamActivity.isMbLin_Acceleration())
		try {
			SensorStreamActivity.mSensor_Stream.registerListener(mysoftwarewaresensorlistener, SensorStreamActivity.mSensor_Stream.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorStreamActivity.getmDelay());
			} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		if(SensorStreamActivity.isMbGravity())
		try {
			SensorStreamActivity.mSensor_Stream.registerListener(mysoftwarewaresensorlistener, SensorStreamActivity.mSensor_Stream.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorStreamActivity.getmDelay());
			} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		if(SensorStreamActivity.isMbRot_Vector())
		try {
			SensorStreamActivity.mSensor_Stream.registerListener(mysoftwarewaresensorlistener, SensorStreamActivity.mSensor_Stream.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorStreamActivity.getmDelay());
			} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		//}
		else
			return true;
		
		return true;
	
	}

	public void stop_Hardware_Sensors()
	{
	
		SensorStreamActivity.mSensor_Stream.unregisterListener(myhardwaresensorlistener);
	
		mAccBufferReady 	= false;
        mGyroBufferReady 	= false;
        mMagBufferReady 	= false;
		mPreBufferReady 	= false;
		ToggleSensorsActivity.setmGps_available(false);
	}
	
	public  void stop_Software_Sensors ()
	{
		
		SensorStreamActivity.mSensor_Stream.unregisterListener(mysoftwarewaresensorlistener);
		
		mOriBufferReady			= false;
		mLin_Acc_BufferReady 	= false;
		mGraBufferReady 		= false;
		mRot_Vec_BufferReady	= false;
		
		
		
	}
	

	private boolean start_UDP_Stream()
	{
		
		boolean isOnWifi = isOnWifi();
    	if(isOnWifi == false)
    	{
    		showDialog(R.string.error_warningwifi);
    		return false;
    	}
    	
    	
    	
		InetAddress client_adress = null;
        try {
            client_adress = InetAddress.getByName(mIP_Adress.getText().toString());
        } catch (UnknownHostException e) {
        	showDialog(R.string.error_invalidaddr);
            return false;
        }
        try {
            mSocket = new DatagramSocket();
            mSocket.setReuseAddress(true);
        } catch (SocketException e) {
            mSocket = null;
        	showDialog(R.string.error_neterror);
            return false;}
        
        byte[] buf = new byte[256];
        int port;
        try {
            port = Integer.parseInt(mPort.getText().toString());
            mPacket = new DatagramPacket(buf, buf.length, client_adress, port);
        } catch (Exception e) {
        	mSocket.close();
        	mSocket = null;
        	showDialog(R.string.error_neterror);
        	return false;
        }

        return true;
        
        
	}
	
	private void stop_UDP_Stream()
	    {
	    	if (mSocket != null)
	        	mSocket.close();
	        mSocket = null;
	        mPacket = null;
	    	
	    }
	
	@SuppressLint("Wakelock")
	private boolean startStreaming()
	{
		 if(mUDP_SD_Stream.isChecked())
	        {
			 
			 boolean udp_ready = start_UDP_Stream();
			 if (udp_ready == false)
			 {
				 return false;
			 }
	        
			 
			 boolean sdcard_ready = SD_Card_Setup.createfile(mSD_Card_Filename);
			 
			 if(sdcard_ready == false)
			 {	showDialog(R.string.error_sd_card);
				return false;
			 }
			 
			 
	        }
	        else if(mUDP_Stream.isChecked())
	        {
	        	
	        	boolean udp_ready = start_UDP_Stream();
				if (udp_ready == false)
					{
					 	return false;
					}
	        }
	        else if(mSD_Card_Stream.isChecked())
	        {
	        	
				 boolean sdcard_ready = SD_Card_Setup.createfile(mSD_Card_Filename);
				 
				 if(sdcard_ready == false)
				 {	showDialog(R.string.error_sd_card);
					return false;
				 }
	        }
		 
		mCounter =1;
		if(mSpinner.getSelectedItemPosition() == 0)
		{
			mScreenDelay = 24;
		}
		else if(mSpinner.getSelectedItemPosition() == 1)
		{
			mScreenDelay = 11;
		}
		else if(mSpinner.getSelectedItemPosition() == 2)
		{
			mScreenDelay = 3;
		}
		else if(mSpinner.getSelectedItemPosition() == 3)
		{
			mScreenDelay = 1;
		}
		
		boolean sensor_ready = start_Hardware_Sensors();
		if(sensor_ready == false)
			{
			showDialog(R.string.error_sensorerror);
			return false;
			}
		
		//if(SensorStreamActivity.isMbChecked_Sensor_Data())
		//{
			if(SensorStreamActivity.isMbOrientation() || SensorStreamActivity.isMbLin_Acceleration() || SensorStreamActivity.isMbGravity() || SensorStreamActivity.isMbRot_Vector())
			{
				sensor_ready = start_Software_Sensors();
			
			if(sensor_ready == false)
			{
				showDialog(R.string.error_sensorerror);
				return false;
			}
			}
		//}
		
		mStream_Active=true;
		mIP_Adress.setEnabled(false);
		mPort.setEnabled(false);
		mSpinner.setEnabled(false);
		
		mRadioGroup.setEnabled(false);
		for(int i=0; i<mRadioGroup.getChildCount();i++)
		{
		mRadioGroup.getChildAt(i).setEnabled(false);
		}
		
		mProgessBar.setVisibility(View.VISIBLE);
		mSendingState.setText(R.string.Stream_Established);
		
		if(this.wakeLock.isHeld() == false)
		{
			this.wakeLock.acquire();
		}
		
		
		return true;		
	}
	
	private void stopStreaming() 
	{
		mProgessBar.setVisibility(View.GONE);
		mSendingState.setText(R.string.Not_Sending);
		
		 if(mUDP_SD_Stream.isChecked())
	        {
	        	stop_UDP_Stream();
	        	SD_Card_Setup.close();
	        }
	        else if(mUDP_Stream.isChecked())
	        {
	        	stop_UDP_Stream();
	        }
	        else if(mSD_Card_Stream.isChecked())
	        {
	        	SD_Card_Setup.close();
	        }
		mStream_Active = false;
		stop_Hardware_Sensors();
	        
		//if(SensorStreamActivity.isMbChecked_Sensor_Data())
		//{
			if(SensorStreamActivity.isMbOrientation() || SensorStreamActivity.isMbLin_Acceleration() || SensorStreamActivity.isMbGravity() || SensorStreamActivity.isMbRot_Vector())
			{
				stop_Software_Sensors();
			}
		//}
	    
		
		mIP_Adress.setEnabled(true);
		mPort.setEnabled(true);
		mSpinner.setEnabled(true);
		
		mRadioGroup.setEnabled(true);
		for(int i=0; i<mRadioGroup.getChildCount();i++)
		{
		mRadioGroup.getChildAt(i).setEnabled(true);
		}
		
		if(this.wakeLock.isHeld() == true)
		{
			this.wakeLock.release();
		}
	    }
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) 
	{
		switch(pos)
		{
		case 0: SensorStreamActivity.setmDelay(SensorManager.SENSOR_DELAY_FASTEST);	break;
		case 1: SensorStreamActivity.setmDelay(SensorManager.SENSOR_DELAY_GAME);	break;
		case 2: SensorStreamActivity.setmDelay(SensorManager.SENSOR_DELAY_UI);	break;
		case 3: SensorStreamActivity.setmDelay(SensorManager.SENSOR_DELAY_NORMAL);		break;
		default: SensorStreamActivity.setmDelay(SensorManager.SENSOR_DELAY_UI);	break;	
		}	
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	private boolean isOnWifi() {
    	
		WifiManager wifimanager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
		if (wifimanager.isWifiEnabled()){
			return true;
		} else {
			return false;
		}
	
    }
	
	 

	private static void addSensorToString(StringBuilder strbuilder,
            int sensorid, double ...values ) 
	{
		if(values.length == 3)
		{
			strbuilder.append(String.format(Locale.ENGLISH, ", %d, %.3f,%.3f,%.3f", sensorid, values[0], values[1], values[2]));
		}
		
		else if (values.length == 1)	
		{
			strbuilder.append(String.format(Locale.ENGLISH, ", %d, %.3f", sensorid, values[0]));
		}
	}
	
	private static void add_ori_quat_ToString(StringBuilder strbuilder,
            int sensorid_ori_matrix, int sensorid_quat, float [] ori_matrix_values, float [] quaternion_values ) 
	{
		if(ori_matrix_values.length == 9)
		{
			strbuilder.append(String.format(Locale.ENGLISH, ", %d,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f", sensorid_ori_matrix, ori_matrix_values[0], ori_matrix_values[1], ori_matrix_values[2], ori_matrix_values[3], ori_matrix_values[4], ori_matrix_values[5], ori_matrix_values[6], ori_matrix_values[7], ori_matrix_values[8]));
		}
		
		if(quaternion_values.length == 4)
		{
			strbuilder.append(String.format(Locale.ENGLISH, ", %d,%.3f,%.3f,%.3f,%.3f", sensorid_quat, quaternion_values[0], quaternion_values[1], quaternion_values[2], quaternion_values[3]));
		}
		
	
	}

	/*
	private static void addGNSSToString(StringBuilder strbuilder,
            int sensorid, double [] values ) 
	{
		if(sensorid == 1)
		{
			strbuilder.insert(0, String.format(Locale.ENGLISH, ", %d, %10.6f,%11.6f,%5.1f", sensorid, values[0], values[1], values[2]));
		}
		
		else if(sensorid == 6)
		{
			strbuilder.append(String.format(Locale.ENGLISH, ", %d, %12.3f,%12.3f,%12.3f", sensorid, values[0], values[1], values[2]));
		}
		
		else if(sensorid == 7)
		{
			strbuilder.append(String.format(Locale.ENGLISH, ", %d, %6.3f,%6.3f,%6.3f", sensorid, values[0], values[1], values[2]));
			//strbuilder.append(String.format(Locale.ENGLISH, ", Absolute Speed %.3f", ToggleSensorsActivity.mSpeed));
		}
	}
	*/

	
	@Override
	protected Dialog onCreateDialog(int id) 
	{
		
		switch(id)
		{
		
		case R.layout.sd_card_dialog:
			

			AlertDialog.Builder builder;
			AlertDialog alertDialog;
			
			//Context mContext = getApplicationContext();
			LayoutInflater inflater = (LayoutInflater) this.getLayoutInflater();
			View layout = inflater.inflate(R.layout.sd_card_dialog,
			                               (ViewGroup) findViewById(R.id.layout_root));
			

			mSD_Card_Dialog_EditText = (EditText) layout.findViewById(R.id.EditText_Sd_Card_Dialog);
			mSD_Card_Filename = "mystream";
			Calendar cal = Calendar.getInstance(); 
			mSD_Card_Dialog_EditText.setText(mSD_Card_Filename + "_" + String.valueOf(1+cal.get(Calendar.MONTH)) + "_" + cal.get(Calendar.DAY_OF_MONTH) + "_" + cal.get(Calendar.HOUR_OF_DAY) + "_" + cal.get(Calendar.MINUTE)+ "_" + cal.get(Calendar.SECOND));
			
			builder = new AlertDialog.Builder(this);
			builder.setView(layout);
			alertDialog = builder.create();
			
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(!(mSD_Card_Dialog_EditText.getText().toString().equals("")))
					mSD_Card_Filename = mSD_Card_Dialog_EditText.getText().toString();
					else
					{
						Calendar cal = Calendar.getInstance();
						mSD_Card_Filename += "_" + String.valueOf(1+cal.get(Calendar.MONTH)) + "_" + cal.get(Calendar.DAY_OF_MONTH) + "_" + cal.get(Calendar.HOUR_OF_DAY) + "_" + cal.get(Calendar.MINUTE)+ "_" + cal.get(Calendar.SECOND);
					}
					boolean streaming = startStreaming();
		            if(streaming ==false)
		            {
		            	stopStreaming();
		            }
					
					dialog.dismiss();
				
				}
			
			});
			alertDialog = builder.create();
			alertDialog.show();	
			break;
		
	
		case R.string.error_warningwifi:
			
			
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
	    	
	    	
		case R.string.error_invalidaddr: 	
			
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
	    	
	    	
		case R.string.error_neterror: 	
			
			Builder builder_3 = new AlertDialog.Builder(this);
			builder_3.setMessage(getString(id));
	    	//Anonyme Implementierung der Methode OnClick
	    	builder_3.setNeutralButton("OK", new DialogInterface.OnClickListener() 
	    		{
	    	    	public void onClick(DialogInterface dialog, int which) 
	    	    	{
	    	    		dialog.dismiss();
	    	    	}
	    		}		
	    				);
	    	AlertDialog alertDialog_3 = builder_3.create();
	    	alertDialog_3.show();
	    	break;
	    	
	    	
		case R.string.error_sensorerror: 	
		
			Builder builder_4 = new AlertDialog.Builder(this);
			builder_4.setMessage(getString(id));
			//Anonyme Implementierung der Methode OnClick
			builder_4.setNeutralButton("OK", new DialogInterface.OnClickListener() 
    			{
    	    		public void onClick(DialogInterface dialog, int which) 
    	    		{
    	    			dialog.dismiss();
    	    		}
    			}		
    				);
			AlertDialog alertDialog_4 = builder_4.create();
			alertDialog_4.show();
			break;
			
		case R.string.error_sd_card: 	
			
			Builder builder_5 = new AlertDialog.Builder(this);
			builder_5.setMessage(getString(id));
			//Anonyme Implementierung der Methode OnClick
			builder_5.setNeutralButton("OK", new DialogInterface.OnClickListener() 
    			{
    	    		public void onClick(DialogInterface dialog, int which) 
    	    		{
    	    			dialog.dismiss();
    	    		}
    			}		
    				);
			AlertDialog alertDialog_5 = builder_5.create();
			alertDialog_5.show();
			break;
	
		}
		
		
		// TODO Auto-generated method stub
		return super.onCreateDialog(id);
	}


	public static boolean ismStream_Active() {
		return mStream_Active;
	}


	public static void setmStream_Active(boolean mStream_Active) {
		PreferencesActivity.mStream_Active = mStream_Active;
	}


}
