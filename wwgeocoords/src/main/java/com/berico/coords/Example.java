package com.berico.coords;


public class Example 
{
    public static void main( String[] args )
    {	
    		String mgrs = Coordinates.mgrsFromLatLon(37.10, -112.12);
    		
    		System.out.println(mgrs);
    		
    		double[] latLon = Coordinates.latLonFromMgrs(mgrs);
    		
    		System.out.println(
    			String.format("%s, %s", latLon[0], latLon[1]));
    }
}
