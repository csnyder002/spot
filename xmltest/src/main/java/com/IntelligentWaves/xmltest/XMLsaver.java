package com.IntelligentWaves.xmltest;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.*;

import static java.lang.System.out;

import java.io.*;
import java.util.List;

public class XMLsaver extends Activity{
//takes information from struct class Input and assembles it into XML form
	String fileName;
	List<Input> I;
	public XMLsaver(List<Input> data,String filename)
	{
		out.println("built");
		I=data;
		fileName=filename;
	}
	
	public void setFileName(String s)
	{
		fileName=s;
	}
	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	    	return true;	        
	    }
	    return false;
	}
	
	public void write()
	{
		WriteToFile(I);
	}
	
	//creates and xml file and saves it to the mobile devices external storage device
	void WriteToFile(List<Input> info)
	{
		if(isExternalStorageWritable())
		{	
			String filename=fileName;
	       try
			{
				final String outString=writeXml(info);
				FileOutputStream fos = new FileOutputStream(filename);
	    	 	fos.write(outString.getBytes());
	    	 	fos.close();
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
		}	
		else
		{
		     try
			{
	    	 	FileOutputStream fos = openFileOutput("/.spot/"+fileName, Context.MODE_PRIVATE);
	    	 	final String outString=writeXml(info);
	    	 	fos.write(outString.getBytes());
	    	 	fos.close();
				
				out.println("printed");
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
	}
	
	private String writeXml(List<Input> info)
	{
		XmlSerializer writer= Xml.newSerializer(); //builds XML
		StringWriter combiner = new StringWriter(); //converts XML to string
		out.println("xml written");
		try 
		{
			writer.setOutput(combiner);
			writer.startDocument("UTF-8",true);
			writer.startTag("", "data");
			for(Input I:info) // loop through and build and fill all XML tags
			{
				writer.startTag("", I.code);
				writer.text(I.data);
				writer.endTag("", I.code);
			}
			out.println("xloop done");
			
			//WriteTimestamps(writer,d1,d2,t1,t2);
			writer.endTag("", "data");
			writer.endDocument();
			out.println("XML complete");
			
			return combiner.toString();	
		} 
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}	
}
