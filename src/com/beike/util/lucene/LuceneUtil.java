package com.beike.util.lucene;

public class LuceneUtil {
	public static final double RATE_MILE_TO_KM = 1.609344; // 英里和公里的比率
	private static final double MAX_RANGE = 15.0; // 索引支持的最大范围，单位是千米
	private static final double MIN_RANGE = 3.0; // 索引支持的最小范围，单位是千米
	
	public static int mile2Meter(double miles) {  
        double dMeter = miles * RATE_MILE_TO_KM * 1000;  
  
        return (int) dMeter;  
    }  
  
    public static double km2Mile(double km) {  
        return km / RATE_MILE_TO_KM;  
    }  
    
    public static double mile2KM(double miles){
    	return miles * RATE_MILE_TO_KM;
    }
}
