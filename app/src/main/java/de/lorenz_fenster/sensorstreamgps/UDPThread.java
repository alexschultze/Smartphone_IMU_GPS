package de.lorenz_fenster.sensorstreamgps;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class UDPThread  {
		
		String msensordata;
		
		public UDPThread(String sensordata)
		{
			this.msensordata = sensordata;
			
		}

		public void send()
		{
			byte bytes [] ;
			
			
			try {
				bytes = msensordata.getBytes("UTF-8");
				if (PreferencesActivity.mPacket == null || PreferencesActivity.mSocket == null)
					return ;
				
				PreferencesActivity.mPacket.setData(bytes);
				PreferencesActivity.mPacket.setLength(bytes.length);


				PreferencesActivity.mSocket.send(PreferencesActivity.mPacket);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//Log.e("Error", "SendBlock");
				return ;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//Log.e("Error", "SendBlock");
				return ;
			}
			
		}
	}