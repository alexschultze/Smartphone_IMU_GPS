package de.lorenz_fenster.sensorstreamgps;



public class GNSS_Arithmetic 
{

	private static final double WGS84_a = 6378137.0;
	private static final double WGS84_b = 6356752.3142;
	private static final double WGS84_c = Math.pow(WGS84_a, 2) / WGS84_b;
	
	private static final double WGS84_e2 = (Math.pow(WGS84_a, 2) - Math.pow(WGS84_b, 2))/Math.pow(WGS84_a, 2);
	private static final double WGS84_e_2 = (Math.pow(WGS84_a, 2) - Math.pow(WGS84_b, 2))/Math.pow(WGS84_b, 2);
	
	private static double sinB = 0.0;
	private static double cosB = 0.0;
	
	private static double sinL = 0.0;
	private static double cosL = 0.0;
	
	private static double h = 0.0;
	private static double N = 0.0;
	
	private static double sinlat = 0.0;
	private static double coslat = 0.0;
	private static double sinlon = 0.0;
	private static double coslon = 0.0;
	
	private  static double XYZ [] = new double [3];

	private  static double v_n [] = new double [3];
	private  static double v_e [] = new double [3];
	
	private  static double R_n_e [][] = new double [3][3];
	private  static double BLH_Rad [] = new double [3];
	
	
	
	
	public static double [] blh_to_xyz( double [] blh)
	{
		BLH_Rad[0] = blh[0] * Math.PI/180;
		BLH_Rad[1] = blh[1] * Math.PI/180;
		BLH_Rad[2] = blh[2] ;
		
		assert blh.length == 3 && Math.abs(BLH_Rad[0]) <= 0.5* Math.PI && Math.abs(BLH_Rad[1]) <= 2 * Math.PI;
		
		
		
		sinB = Math.sin(BLH_Rad[0]);
		cosB = Math.cos(BLH_Rad[0]);
		
		sinL = Math.sin(BLH_Rad[1]);
		cosL = Math.cos(BLH_Rad[1]);
		
		h = blh[2];
		N = WGS84_c/Math.sqrt(1.0 + WGS84_e_2 * Math.pow(cosB, 2)); 
		
		XYZ[0] = (N+h)*cosB*cosL;
		XYZ[1] = (N+h)*cosB*sinL;
		XYZ[2] = ((1.0 - WGS84_e2)*N+h)*sinB;
		
		return XYZ;
		
		
	}
		
	public static double [] v_et_bear_to_v_e(double v, double bear)
	{
		bear *= Math.PI/180;
		
		
		assert  bear <= 2 * Math.PI;
		
		
		v_n[0] = Math.cos(bear)* v;
		v_n[1] = Math.sin(bear)* v;
		v_n[2] = 0.0;
		
		sinlat = Math.sin(BLH_Rad[0]);
		coslat = Math.cos(BLH_Rad[0]);
		sinlon = Math.sin(BLH_Rad[1]);
		coslon = Math.cos(BLH_Rad[1]);
		
		R_n_e [0][0] = -sinlat*coslon;
		R_n_e [0][1] = -sinlon;
		R_n_e [0][2] = -coslat*coslon;
		
		R_n_e [1][0] = -sinlat*sinlon;
		R_n_e [1][1] =  coslon;
		R_n_e [1][2] = -coslat*sinlon;
		
		R_n_e [2][0] = coslat;
		R_n_e [2][1] =  0.0;
		R_n_e [2][2] = -sinlat;
		
		v_e [0] = 0.0;
		v_e [1] = 0.0;
		v_e [2] = 0.0;
	
		for(int j =0; j<3; j++)
		{
		for(int i =0; i<3; i++)
		{
			v_e [j] += R_n_e[j][i] * v_n[i];
		}
		}
		return v_e;
	}
	

}
