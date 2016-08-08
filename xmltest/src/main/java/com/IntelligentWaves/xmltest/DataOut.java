package com.IntelligentWaves.xmltest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.util.List;
import java.util.Properties;

//import org.apache.commons.io.FileUtils;

public class DataOut extends AsyncTask<String,Integer,Long> 
{
	Context active;
	String securityType;
	String targetIp="192.168.135.33"; // changed from: 10.10.10.20 -CODY
	SharedPreferences preferences;
	Boolean success=true;
	XMLreader xmlReader;
	private loadActivity parentActivity;
	private MainActivity mainActivity;
	ProgressDialog progressDialog;
	
	public DataOut(Context C)
	{
		active=C;
		parentActivity=(loadActivity) C;
		xmlReader=new XMLreader(active);
	}
	
	public DataOut(Context C,Boolean main)
	{
		active=C;
		mainActivity=(MainActivity) C;
		xmlReader=new XMLreader(active);
	}
	
	public void onPreExecute() //show spinning wheel to let people know its loading
	
	{
		progressDialog = new ProgressDialog(active);
		progressDialog.setMessage("uploading data ");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setIndeterminate(true);
		progressDialog.show();
		//progressDialog= ProgressDialog.show(R.layout.progress_activity, "Progress Dialog Title Text","Process Description Text", true);
	}
	
	public Uri LookForImagePath(List<Input> I) //looks for an image path and returns it as a URI
	{
		Uri result=null;
		
		for(int j=0;j<I.size();j++)
		{
			if(I.get(j).code.equals("ImageFilePath"))
			{
				File temp=new File((I.get(j).data));
				if(temp.isFile())
				{
					result=Uri.fromFile(new File((I.get(j).data)));
					return result;
				}
			}
		}
		return result;
	}
	
	public String LookForFilePath(List<Input> I)//looks for an image path and returns it as a String
	{
		String result=null;
		
		for(int j=0;j<I.size();j++)
		{
			if(I.get(j).code.equals("FilePath"))
			{
				File temp=new File((I.get(j).data));
				if(temp.isFile())
				{
					result= I.get(j).data;
					
					return result;
				}
			}
		}
		return result;
	}
	
	@Override
	protected Long doInBackground(String... xmlFiles) //all the data transfer
	{
		//FileName=FileName.replace(" ", "");
		ChannelSftp sftpChannel;
		preferences = PreferenceManager.getDefaultSharedPreferences(active);
		securityType = preferences.getString("security", "Password");
		
		
		if(securityType.equals("Password"))  //3 different types of SSH sign-in Password, Key, Key + Password
		{
			try
			{
				JSch jsch = new JSch();

				Session session = jsch.getSession(preferences.getString("User", "speck.administrator"),preferences.getString("Host",targetIp),22);

				String Pass=preferences.getString("Pass", "hi");
		        Properties prop = new Properties();
		        prop.put("StrictHostKeyChecking", "no");
		        prop.put("PreferredAuthentications","password");
		        
		        session.setConfig(prop);

		        session.setPassword(Pass);
		        System.out.println("!!!!!!!!!! Host:"+session.getHost()+" User:"+session.getUserName()+" Pass:"+preferences.getString("Pass", "fail") + " !!!!!!!!!!");
				session.connect();
		        System.out.println("!!!!!!!!!! in !!!!!!!!!!");

		        sftpChannel = (ChannelSftp)session.openChannel("sftp");

				sftpChannel.connect();

				System.out.println("!!!!!!!!!! xmlFiles[0]=" + xmlFiles[0].toString() + " !!!!!!!!!!");
				System.out.println("!!!!!!!!!! xmlFiles[1]=" + xmlFiles[1].toString() + " !!!!!!!!!!");

				sftpChannel.put(xmlFiles[0],"/incoming");
				if(!xmlFiles[1].equals("null"))
				{
					System.out.println("!!!!!!!!!! xmlFiles[1].equals(blank): " + xmlFiles[1].equals("") + " !!!!!!!!!!");
					System.out.println("!!!!!!!!!! xmlFiles[1]==(null): " + xmlFiles[1].equals("null") + " !!!!!!!!!!");
					sftpChannel.put(xmlFiles[1],"/incoming/photos");
				}

				/*
				for(int i=0;i<xmlFiles.length;i++)
		        {
		        	if(!xmlFiles[i].equals("") || xmlFiles[i]!=null)
		        	{
		        		sftpChannel.put(xmlFiles[i+1],"/incoming/photos");
		        	}

		        	sftpChannel.put(xmlFiles[i],"/incoming");
		        	i++;
		        }
				*/

		        sftpChannel.disconnect();
		        session.disconnect();
			}
			catch(JSchException | SftpException e)
			{
				success=false;

				System.out.println(e.getMessage());				
			}
		}
		else if(securityType.equals("Key")) // try to use this one to limit how much users have to do in order to sign in
		{
			try
			{
				JSch jsch=new JSch();			 
		        Properties prop = new Properties();
		        prop.put("StrictHostKeyChecking", "no");// Avoid asking for key confirmation
		        
		        
		        /*
		        /v important lines v
		        byte[] pk=FileUtils.readFileToByteArray(new File(Environment.getExternalStorageDirectory()+"private.ppk")); 
		        jsch.addIdentity(preferences.getString("User", "key.user1"), pk, null, new byte[0]);
		        //^ important lines ^
		        */
		        
		        //jsch.addIdentity(preferences.getString("User", "key.user1"), null, null, new byte[0]);
				Session session = jsch.getSession("key.user1",targetIp,22);
		        session.setConfig(prop);
		        session.connect();
		        sftpChannel= (ChannelSftp) session.openChannel("sftp");
		        sftpChannel.connect();
		        //TODO
		        for(int i=0;i<xmlFiles.length;i++)
		        {
		        	sftpChannel.put(xmlFiles[i],"/incoming");
		        	i++;
		        	if(!xmlFiles[i].equals("")||xmlFiles[i]==null)
		        	{
		        		sftpChannel.put(xmlFiles[i],"/incoming/photos");
		        	}
		        }
		
		        sftpChannel.disconnect();
		        session.disconnect();
			}
			catch(JSchException | SftpException /*| IOException*/ e)
			{
				System.out.println(e.getMessage());
				success=false;
			}
		}
		else if(securityType.equals("Key+Password"))
		{
			try
			{
				//TODO
				JSch jsch=new JSch();
				String privateKey=Environment.getExternalStorageDirectory()+"private.ppk";
		        jsch.addIdentity(privateKey);
				Session session = jsch.getSession("key.user1",targetIp,22);
				 // Avoid asking for key confirmation
		        Properties prop = new Properties();
		        prop.put("StrictHostKeyChecking", "no");
		          
		        session.setConfig(prop);
		        session.setPassword("P@55w0rd");
		        session.connect();
		        sftpChannel= (ChannelSftp) session.openChannel("sftp");
		        sftpChannel.connect();
		          
		        for(int i=0;i<xmlFiles.length;i++)
		        {
		        	sftpChannel.put(xmlFiles[i],"/incoming");
		        	i++;
		        	if(!xmlFiles[i].equals("")||xmlFiles[i]==null)
		        	{
		        		sftpChannel.put(xmlFiles[i],"/incoming/photos");
		        	}
		        }
		
		        sftpChannel.disconnect();
		        session.disconnect();
			}
			catch(JSchException | SftpException e)
			{
				System.out.println(e.getMessage());
				success=false;
			}
		}
		return ((long)0);
	}
	
	@Override
    protected void onPostExecute(Long result) //when the data transfer is complete run this
    {
		progressDialog.dismiss();
		if(parentActivity!=null)
		{
			parentActivity.cleanUp(success);
		}
		
		if(mainActivity!=null)
		{
			mainActivity.transferComplete(success);
		}
      //  Toast.makeText(active, "sent " + result + " bytes", Toast.LENGTH_SHORT).show();
    }
	
	protected void ProgressUpdate(Integer i)
	{
		progressDialog.setProgress(i);
	}
}
