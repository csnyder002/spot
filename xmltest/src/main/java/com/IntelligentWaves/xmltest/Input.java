package com.IntelligentWaves.xmltest;

public class Input {
	public String code;
	public String data;
	public String[] Dates;
	
	public Input(String c,String d)
	{
		code=c;
		data=d;
	}
	
	public void setup() //most Input instances wont use the Dates so only set them up if we need them
	{
		Dates=new String[12];
		Dates[0]="Jan";
		Dates[1]="Feb";
		Dates[2]="Mar";
		Dates[3]="Apr";
		Dates[4]="May";
		Dates[5]="Jun";
		Dates[6]="Jul";
		Dates[7]="Aug";
		Dates[8]="Sep";
		Dates[9]="Oct";
		Dates[10]="Nov";
		Dates[11]="Dec";
	}
	
	public String getDate(int d) //array is base zero so we have to decrement the month data to get the corresponding month code
	{
		d--;
		return Dates[d];
	}
	
}
