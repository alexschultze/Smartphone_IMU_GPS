package de.lorenz_fenster.sensorstreamgps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

import android.os.Environment;
import android.os.StatFs;

public class SD_Card_Setup 

{
	private static BufferedWriter mBufferedwriter= null;
	
	private static BufferedWriter mIMUwriter = null;
	private static BufferedWriter mMAGwriter = null;
	private static BufferedWriter mGNSSwriter = null;
	private static BufferedWriter mBAROwriter = null;
	
	private static String whitespace =  " ";
	
	
	private static int ACC_ID_1 = 20001;
	private static int ACC_ID_2 = 20002;
	private static int ACC_ID_3 = 20003;
	
	private static int GYRO_ID_1 = 30001;
	private static int GYRO_ID_2 = 30002;
	private static int GYRO_ID_3 = 30003;
	
	private static int MAG_ID_1 = 50001;
	private static int MAG_ID_2 = 50002;
	private static int MAG_ID_3 = 50003;
	
	private static int GNSS_ID = 10001;
	private static int BARO_ID = 70001;
	
	
	
	private static int [] acc_ids = {ACC_ID_1, ACC_ID_2, ACC_ID_3};
	private static int [] gyro_ids = {GYRO_ID_1, GYRO_ID_2, GYRO_ID_3};
	private static int [] mag_ids = {MAG_ID_1, MAG_ID_2, MAG_ID_3};
	

	
	
	public static boolean createfile(String filename) 
	{
		File sdCard_dir = Environment.getExternalStorageDirectory() ;
		
        
        if (sdCard_dir.exists() && sdCard_dir.canWrite())
        {
        	
        	
        	StatFs statFs = new StatFs(sdCard_dir.getPath());
        	long availablebytes = (long) statFs.getAvailableBlocks() * (long) statFs.getBlockSize();
        	long megabytes = (long) (availablebytes /Math.pow(1024, 2));
        	if (megabytes < 10)
        	{
        		return false;
        	}
        	
        	//sdCard_dir.
        	File folder = new File(sdCard_dir.toString()+"/MySensorStreams");
        	folder.mkdirs();

        	
        	
        	
        	  //Create New file and name it 
        	File sdCard_file = new File(folder.toString(), filename+".csv");
        	File IMU_file = new File(folder.toString(), filename + "_IMU.txt");
        	File MAG_file = new File(folder.toString(), filename + "_MAG.txt");
        	File GNSS_file = new File(folder.toString(), filename + "_GNSS.txt");
        	File BARO_file = new File(folder.toString(), filename + "_BARO.txt");
        	
        	
				
        	try {
        		
				
					mBufferedwriter = new BufferedWriter(new FileWriter(sdCard_file));
					mIMUwriter = new BufferedWriter(new FileWriter(IMU_file));
					mMAGwriter = new BufferedWriter(new FileWriter(MAG_file));
					mGNSSwriter = new BufferedWriter(new FileWriter(GNSS_file));
					mBAROwriter = new BufferedWriter(new FileWriter(BARO_file));

				
				} 
        	catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					try {
						mBufferedwriter.close();
						mIMUwriter.close();
						mMAGwriter.close();
						mGNSSwriter.close();
						mBAROwriter.close();
						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					return false;
				}
			
			return true;
        	
        }
        
        else
        {
        	return false;
        }
        	
		
	}
	
	public static void write (String SensorData)
	{
		if(mBufferedwriter != null)
		{
			try 
				{
					
					mBufferedwriter.write(SensorData + "\r\n");
					//mBufferedwriter.flush();
					
				} catch (IOException e) 
				{
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
				
			
			}
			else
			{
				return;
			}	
	
	}
	
	
	public static void write_IMU(double timestamp, double [] accdata, double [] gyrodata, boolean gyro_ready)
	{
		if(mIMUwriter != null)
		{
			try 
				{
					StringBuilder mystringbuilder = new StringBuilder();
					
					mystringbuilder.append(String.format(Locale.ENGLISH, "%.4f", timestamp) + whitespace);
					mystringbuilder.append("3" + whitespace);
					
					if(gyro_ready == true)
					{
						mystringbuilder.append("3" + whitespace);
					}
					else
					{
						mystringbuilder.append("0" + whitespace);
					}
					
					for (int i = 0; i< accdata.length; i++)
					{
						
						mystringbuilder.append(acc_ids[i] + whitespace);
						mystringbuilder.append(String.format(Locale.ENGLISH, "%.4f", accdata[i]) + whitespace);
						
					}
					
					for (int i = 0; i< accdata.length; i++)
					{
						
						mystringbuilder.append(gyro_ids[i] + whitespace);
						if(gyro_ready == true)
						{
							mystringbuilder.append(String.format(Locale.ENGLISH, "%.4f", gyrodata[i]) + whitespace);
						}
						else
						{
							mystringbuilder.append("0.0"+ whitespace);
						}
	
					}
	
					mIMUwriter.write(mystringbuilder.toString()+ "\r\n");

					//mBufferedwriter.flush();
						
				} catch (IOException e) 
				{
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
				
			
			}
			else
			{
				return;
			}	
		
		
		
		
	}
	
	
	
	public static void write_MAG(double timestamp, double [] magdata, boolean mag_ready)
	{
		if(mMAGwriter != null)
		{
			try 
				{	
					if(mag_ready == false)
						return;
					else
					{
				
					StringBuilder mystringbuilder = new StringBuilder();
					
					mystringbuilder.append(String.format(Locale.ENGLISH, "%.4f", timestamp) + whitespace);
					mystringbuilder.append("3" + whitespace);
					
					
					
					for (int i = 0; i< magdata.length; i++)
					{
						
						mystringbuilder.append(mag_ids[i] + whitespace);
						mystringbuilder.append(String.format(Locale.ENGLISH, "%.4f", magdata[i]*Math.pow(10, 3)) + whitespace);
						
					}
					
					
					
					
					mMAGwriter.write(mystringbuilder.toString()+ "\r\n");
					
					
					}
					//mBufferedwriter.flush();
					
					
				} catch (IOException e) 
				{
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
				
			
			}
			else
			{
				return;
			}	
		
		
		
		
	}
	
	
	
	
	public static void write_GNSS(double timestamp, boolean gnss_ready)
	{
		if(mGNSSwriter != null)
		{
			try 
				{	
				if(gnss_ready == false)
					return;
				else
				{
			
				StringBuilder mystringbuilder = new StringBuilder();
				
				mystringbuilder.append(String.format(Locale.ENGLISH, "%.4f", timestamp) + whitespace);
				mystringbuilder.append("1" + whitespace);
				
				mystringbuilder.append(GNSS_ID + whitespace);
				
				for (int i = 0; i< 3; i++)
				{

					mystringbuilder.append(String.format(Locale.ENGLISH, "%.3f", ToggleSensorsActivity.getmXYZ()[i]) + whitespace);
					
				}
				
				
				for (int i = 0; i< 3; i++)
				{

					mystringbuilder.append(String.format(Locale.ENGLISH, "%.3f", ToggleSensorsActivity.getmV_e()[i]) + whitespace);
					
				}
				
				
				mystringbuilder.append(String.format(Locale.ENGLISH, "%d", ToggleSensorsActivity.getmGPS_UCT_Time()) + whitespace);
				
				mGNSSwriter.write(mystringbuilder.toString()+ "\r\n");
				
				
				}
				//mBufferedwriter.flush();
					

										
				} catch (IOException e) 
				{
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
				
			
			}
			else
			{
				return;
			}	
						
		
	}
	
	
	
	
	
	public static void write_BARO(double timestamp, double pressure_hectopas,  boolean baro_ready, boolean temp_ready)
	{
		if(mBAROwriter != null)
		{
			try 
				{	
				if(baro_ready == false)
					return;
				else
				{
			
				StringBuilder mystringbuilder = new StringBuilder();
				
				mystringbuilder.append(String.format(Locale.ENGLISH, "%.4f", timestamp) + whitespace);
				mystringbuilder.append("1" + whitespace);
				
				mystringbuilder.append(BARO_ID + whitespace);
				
				
				mystringbuilder.append(String.format(Locale.ENGLISH, "%.1f", pressure_hectopas*100) + whitespace);
					
			
				if(temp_ready == true)
				{
					
					mystringbuilder.append(String.format(Locale.ENGLISH, "%d", ToggleSensorsActivity.getmBattery_Temperature()) + whitespace);
					
				}
				

				mBAROwriter.write(mystringbuilder.toString()+ "\r\n");
				
				
				}
				//mBufferedwriter.flush();
					

										
				} catch (IOException e) 
				{
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
				
			
			}
			else
			{
				return;
			}	
						
		
	}
	
	
	
	
	
	public static void close ()
	{
		try {
			if(mBufferedwriter != null)
			mBufferedwriter.close();
			
			mBufferedwriter = null;
			
			
			if(mIMUwriter != null)
				mIMUwriter.close();
			
			mIMUwriter= null;
				
				
			if(mMAGwriter != null)
				mMAGwriter.close();
				
			mMAGwriter = null;
			
			
			if(mGNSSwriter != null)
				mGNSSwriter.close();
				
			mGNSSwriter = null;
			
			
			if(mBAROwriter != null)
				mBAROwriter.close();
				
			mBAROwriter = null;
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public static BufferedWriter getmBufferedwriter() {
		return mBufferedwriter;
	}

	
	public static void setmBufferedwriter(BufferedWriter mBufferedwriter) {
		SD_Card_Setup.mBufferedwriter = mBufferedwriter;
	}


}
