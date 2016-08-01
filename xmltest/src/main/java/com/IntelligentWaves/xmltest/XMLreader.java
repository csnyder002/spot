package com.IntelligentWaves.xmltest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import android.content.Context;

public class XMLreader
{
	Context context;
	FileInputStream fis;
	InputStreamReader isr;
	char[] inputBuffer;
	String data;
	List<Input> returnData=new ArrayList<Input>();
	
	public XMLreader(Context c)
	{	
		context=c;
	}
	//takes an XML file and builds a list of Input objects which each have 2 String variables
	//Code(which refers to the XML tag) and Data(which refers to the text in the tag)
	public List<Input> readXML(String XML) 
	{
		//final String xmlFile = XML;
		File xmlFile=new File(XML);
		String holderTag="";
		String holderText="";
		try //setup the reader
		{
		    fis = new FileInputStream(xmlFile);
		    isr = new InputStreamReader(fis);
		    inputBuffer = new char[fis.available()];
		    isr.read(inputBuffer);
		    data = new String(inputBuffer);
		    isr.close();
		    fis.close();
		}
		catch (IOException e) 
		{
		    e.printStackTrace();
		}
		
		XmlPullParserFactory factory = null;
		XmlPullParser xpp = null;
		int eventType = 0;
		
		try //setup the xml parser
		{
		    factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
		    xpp = factory.newPullParser();
		    xpp.setInput(new StringReader(data));
		    eventType = xpp.getEventType();
		}
		catch (XmlPullParserException e2) 
		{
		    e2.printStackTrace();
		}
		
		while (eventType != XmlPullParser.END_DOCUMENT) //loop through and build List
		{
		    if (eventType == XmlPullParser.START_DOCUMENT) 
		    {
		        System.out.println("Start document");
		    }
		    else if (eventType == XmlPullParser.START_TAG) 
		    {
		    	holderTag=xpp.getName();
		    }
		    else if (eventType == XmlPullParser.END_TAG) 
		    {
		    	returnData.add(new Input(holderTag,holderText));
		    	holderTag="";
		    	holderText="";
		    }
		    else if(eventType == XmlPullParser.TEXT) 
		    {
		    	holderText=xpp.getText();
		    }
		    try 
		    {
		        eventType = xpp.next();
		    }
		    catch (XmlPullParserException | IOException e) 
		    {
		        e.printStackTrace();
		    }
		}
		
		return returnData;
	}
}
