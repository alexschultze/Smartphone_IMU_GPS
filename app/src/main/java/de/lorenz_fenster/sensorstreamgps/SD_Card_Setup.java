package de.lorenz_fenster.sensorstreamgps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;
import android.os.StatFs;

public class SD_Card_Setup 

{
	private static BufferedWriter mBufferedwriter= null;
	
	
	
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

				
        	try {
        		
				
					mBufferedwriter = new BufferedWriter(new FileWriter(sdCard_file));
				

				
				} 
        	catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					try {
						mBufferedwriter.close();
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
	
	public static void close ()
	{
		try {
			if(mBufferedwriter != null)
			mBufferedwriter.close();
			
			mBufferedwriter = null;
			
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
