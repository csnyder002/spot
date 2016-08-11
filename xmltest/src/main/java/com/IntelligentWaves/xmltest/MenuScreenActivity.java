package com.IntelligentWaves.xmltest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static java.lang.System.out;

public class MenuScreenActivity extends Activity {

	private static final int SELECT_PHOTO = 100;
	private static final int PHOTO_TAKEN = 200;
	private String Long="";
	private String Lat="";
	private String dateTaken="";
	private String timeTaken="";
	private String rotation="1";
	private boolean tut=false;
	public SharedPreferences preferences;
	static Uri Image;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		this.setTheme(android.R.style.Theme_NoTitleBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setupPrivateKey(); 
        preferences= PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();		

        if(preferences.getBoolean("FirstTime", true)) //do you want to start a tutorial!?
        {
        	if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD)
        	{
        		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        	}
        	if(preferences.getInt("Step", 0)==0)
        	{
	        	Builder tutorialDialog=new AlertDialog.Builder(this);
	        	tutorialDialog.setTitle("Tutorial");
	        	tutorialDialog.setMessage("Would you like a quick tutorial on how to use Spot");
	        	tutorialDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() 
				{
			        public void onClick(DialogInterface dialog, int which) 
			        { 
			        	runTutorial(0);		        	
			        }
			     });
	        	tutorialDialog.setNeutralButton(R.string.no, new DialogInterface.OnClickListener() 
				{
			        public void onClick(DialogInterface dialog, int which) 
			        { 
			        	cancelTutorial();
			        }
			     });
	        	tutorialDialog.show();
        	}
        	else
        	{
        		runTutorial(preferences.getInt("Step", 0));
        	}
        }
        else
        {
        	((RelativeLayout)findViewById(R.id.darkness)).setVisibility(View.INVISIBLE);
	    	((LinearLayout)findViewById(R.id.tutButtons)).setVisibility(View.INVISIBLE);
	    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        }
	}

	private void runTutorial(int step)  //changes the layout of the main page based on what step of the tutorial they are on
	{	
		switch(step)
		{
			case 0: //go to the setup menu
				((RelativeLayout)findViewById(R.id.darkness)).setVisibility(View.VISIBLE);
		    	((LinearLayout)findViewById(R.id.tutButtons)).setVisibility(View.VISIBLE);
		    	((TextView)findViewById(R.id.tutWords)).setText("The first step is to enter some basic information so that it can be automatically entered into your future reports.  \n\nClick on the setup button to begin");
		    	((Button)findViewById(R.id.tutSetup)).setVisibility(View.VISIBLE);
		    	((Button)findViewById(R.id.tutload)).setVisibility(View.INVISIBLE);
		    	((Button)findViewById(R.id.tutBlank)).setVisibility(View.INVISIBLE);
		    	((Button)findViewById(R.id.tutCamera)).setVisibility(View.INVISIBLE);
		    	((Button)findViewById(R.id.tutUpload)).setVisibility(View.INVISIBLE);
		    	((Button)findViewById(R.id.Setup)).setClickable(false);
		    	((Button)findViewById(R.id.Saved)).setClickable(false);
		    	//((Button)findViewById(R.id.Camera)).setClickable(false);
		    	//((Button)findViewById(R.id.Upload)).setClickable(false);
		    	((Button)findViewById(R.id.Blank)).setClickable(false);
		    	break;
			case 1: //go to the main form any way you'd like
				((RelativeLayout)findViewById(R.id.darkness)).setVisibility(View.VISIBLE);
		    	((LinearLayout)findViewById(R.id.tutButtons)).setVisibility(View.VISIBLE);
		    	((TextView)findViewById(R.id.tutWords)).setText("There are three ways to create a Spot file. \nUpload allows you to choose a picture saved on the device.  \nCamera allows you to take a picture with the devices built in camera,\nand Blank lets you create a file without an image.\n\nPlease choose whichever you like.");
		    	((Button)findViewById(R.id.tutSetup)).setVisibility(View.INVISIBLE);
		    	((Button)findViewById(R.id.tutload)).setVisibility(View.INVISIBLE);
		    	((Button)findViewById(R.id.tutBlank)).setVisibility(View.VISIBLE);
		    	((Button)findViewById(R.id.tutCamera)).setVisibility(View.VISIBLE);
		    	((Button)findViewById(R.id.tutUpload)).setVisibility(View.VISIBLE);
		    	((Button)findViewById(R.id.Setup)).setClickable(false);
		    	((Button)findViewById(R.id.Saved)).setClickable(false);
		    	//((Button)findViewById(R.id.Camera)).setClickable(false);
		    	//((Button)findViewById(R.id.Upload)).setClickable(false);
		    	((Button)findViewById(R.id.Blank)).setClickable(false);
		    	tut=true;
				break;
			case 2: //go to the load page
				((RelativeLayout)findViewById(R.id.darkness)).setVisibility(View.VISIBLE);
		    	((LinearLayout)findViewById(R.id.tutButtons)).setVisibility(View.VISIBLE);
		    	((TextView)findViewById(R.id.tutWords)).setText("The saved files button allows you to edit, upload or delete any of the files you have saved in your device");
		    	((Button)findViewById(R.id.tutSetup)).setVisibility(View.INVISIBLE);
		    	((Button)findViewById(R.id.tutload)).setVisibility(View.VISIBLE);
		    	((Button)findViewById(R.id.tutBlank)).setVisibility(View.INVISIBLE);
		    	((Button)findViewById(R.id.tutCamera)).setVisibility(View.INVISIBLE);
		    	((Button)findViewById(R.id.tutUpload)).setVisibility(View.INVISIBLE);
		    	((Button)findViewById(R.id.Setup)).setClickable(false);
		    	((Button)findViewById(R.id.Saved)).setClickable(false);
		    	//((Button)findViewById(R.id.Camera)).setClickable(false);
		    	//((Button)findViewById(R.id.Upload)).setClickable(false);
		    	((Button)findViewById(R.id.Blank)).setClickable(false);
				break;
			case 3: //finished
				((RelativeLayout)findViewById(R.id.darkness)).setVisibility(View.INVISIBLE);
		    	((LinearLayout)findViewById(R.id.tutButtons)).setVisibility(View.VISIBLE);
		    	((TextView)findViewById(R.id.tutWords)).setText("Thank you for completing this Tutorial");
		    	((Button)findViewById(R.id.Setup)).setClickable(true);
		    	((Button)findViewById(R.id.Saved)).setClickable(true);
		    	//((Button)findViewById(R.id.Camera)).setClickable(true);
		    	//((Button)findViewById(R.id.Upload)).setClickable(true);
		    	((Button)findViewById(R.id.Blank)).setClickable(true);
		    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
		    	Editor editor = preferences.edit();
				editor.putBoolean("FirstTime", false);
				editor.putInt("Step", 0);
				editor.commit();
				break;
		}
    	//TODO
	}
	
	public void cancelTutorial() //sets the tutorial values to false
	{
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
		Editor editor = preferences.edit();
		editor.putBoolean("FirstTime", false);
		editor.putInt("Step", 0);
		editor.commit();
	}
	
	public void TutSetup(View view) //duplicate of the regular setup intent which passes an extra to let the setup page know it should load as a tutorial
	{
		Intent setupScreen = new Intent(getApplicationContext(), SetupActivity.class);
		setupScreen.putExtra("Tut", true);
		startActivity(setupScreen);
	}
	
	public void TutLoad(View view)  //same as Tut setup except for the load screen
	{
		Intent loadScreen = new Intent(getApplicationContext(), loadActivity.class);
		loadScreen.putExtra("tut", true);
		startActivity(loadScreen);
	}
	
	public void TutToForm(View view) //move to input form
	{
		Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
		nextScreen.putExtra("load", false);
		nextScreen.putExtra("tut", true);
		startActivity(nextScreen);
	}
	
	public void TutUploadImage(View view) //create intent to access the gallery
	{
		tut=true;
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, Image);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, SELECT_PHOTO);  
	}
	
	public void TutTakePicture(View view) //create intent to access the camera
	{
		tut=true;
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) 
	    {
	    	File photoFile = null;
	        photoFile = createImageFile();
	        
	        if (photoFile != null) 
	        {
	        	Image=Uri.fromFile(photoFile);
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,Image);
	            startActivityForResult(takePictureIntent, PHOTO_TAKEN);
	        }
	    } 
	}

	public void ToForm(View view) //move to input form
	{
		Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
		nextScreen.putExtra("load", false);
		startActivity(nextScreen);
	}
	
	public void Setup(View view) //create intent to go to the setup screen
	{
		Intent setupScreen = new Intent(getApplicationContext(), SetupActivity.class);
		startActivity(setupScreen);
	}
	
	public void Load(View view) //move to load page
	{
		Intent loadScreen = new Intent(getApplicationContext(), loadActivity.class);
		startActivity(loadScreen);
	}
	
	public void UploadImage(View view) //create intent to access the gallery
	{
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, Image);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, SELECT_PHOTO);  
	}
	
	public void TakePicture(View view) //create intent to access the camera
	{
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) 
	    {
	    	File photoFile = null;
	        photoFile = createImageFile();
	        
	        if (photoFile != null) 
	        {
	        	Image=Uri.fromFile(photoFile);
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,Image);
	            startActivityForResult(takePictureIntent, PHOTO_TAKEN);
	        }
	    } 
	}
	
	private void setupPrivateKey()//creates a file location to store a private key
	{
		File file = new File(Environment.getExternalStorageDirectory(),"private.ppk");
		try
		{
			copy(file);
		}
		catch (IOException localIOException)
		{
            localIOException.printStackTrace();
            return;
        }
	}
	
	public void copy(File dst) throws IOException //helper method for extablishing a private key
	{
	    InputStream in = getAssets().open("private.ppk");
	    OutputStream out = new FileOutputStream(dst);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) 
	    {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}

	private File createImageFile() //creates an image file name
	{
		File tempImage=null;
		try
		{
		    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());
		    String imageFileName = timeStamp + ".jpg";
		    tempImage = new File(Environment.getExternalStorageDirectory(),imageFileName);
		    if(!tempImage.exists())
		    {
		    	tempImage.createNewFile(); 
		    }
		}
		catch(IOException ioe)
		{
			out.println("FAILURE TO CREATE IMAGE");
		}
	    return tempImage;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) //hijack results
	{ 
	   // super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 
	    Intent I = new Intent(getApplicationContext(),MainActivity.class);  //create intent to shift to main activity
	    if(tut)
	    {
	    	I.putExtra("tut", true);
	    	tut=false;
	    }
	    int inSample = 2;
    	Options opts = new BitmapFactory.Options();
		opts.inSampleSize = inSample;
        
	    switch(requestCode)
	    { 
	    case SELECT_PHOTO: //if the result is form selecting a photo	
	        if(resultCode == RESULT_OK)
	        { 
	        	try
	        	{
		            Uri selectedImage = imageReturnedIntent.getData(); //collect selected photo
		            //Bitmap yourSelectedImage = Shrink(BitmapFactory.decodeFile(selectedImage.getPath()),100,this);
		            
					Bitmap yourSelectedImage = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),selectedImage); //convert to bitmap 
					//out.println("BYTECOUNT="+yourSelectedImage.getAllocationByteCount());
					ProcessExif(getRealPathFromURI(selectedImage)); //begin processing the image
					
					I.putExtra("Long", Long); //set values gained from processing the image
					I.putExtra("Lat", Lat);
					I.putExtra("dateTaken", dateTaken);
					I.putExtra("timeTaken", timeTaken);
					I.putExtra("image", selectedImage);
					I.putExtra("imagepath", getRealPathFromURI(selectedImage));
					I.putExtra("load", false);
					out.println(getRealPathFromURI(selectedImage));
					yourSelectedImage.recycle();
					startActivity(I); // move to main
	        	}
	        	catch(IOException ioe)
	        	{
	        		out.println("FAILURE TO ADD EXTRAS");
	        		break;
	        	}
	        }
	        break;
	    case PHOTO_TAKEN:
	    	if(resultCode == RESULT_OK)
	        { 
				try
				{
					//Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Image);
					//sendBroadcast(mediaScanIntent);
					Bitmap bitmap = MediaStore.Images.Media.getBitmap( getApplicationContext().getContentResolver(),Image);
					//out.println("BYTECOUNT="+Shrink(bitmap,200,this).getAllocationByteCount());
					ProcessExif(Image.getPath()); //begin processing the image	
					FileOutputStream fOut=null;
					if(!rotation.equals("1"))
					{
						bitmap=rotate(bitmap,rotation);
						fOut  = new FileOutputStream(Image.getPath());
					    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
					}
					if (fOut != null) 
					{
						fOut.close();
			        }
					
					I.putExtra("Long", Long); //set values gained from processing the image
					I.putExtra("Lat", Lat);
					I.putExtra("dateTaken", dateTaken);
					I.putExtra("timeTaken", timeTaken);
					//I.putExtra("image", Image);
					I.putExtra("imagepath", Image.getPath());
					I.putExtra("load", false);
					
					bitmap.recycle();
					startActivity(I); // move to main
				} 
				catch (IOException ioe)
				{
					ioe.printStackTrace();
					out.println("FAILURE TO ADD EXTRAS");
					break;
				}
	        }
	    	break;
	    }
	}
	
	public Bitmap rotate(Bitmap img,String orientation) //rotate an image
	{
		Bitmap result;
		int turn=0;
		if(orientation.equals("3"))//rotate 180
		{
			turn=180;
		}
		if(orientation.equals("6")) //rotate 90
		{
			turn=90;
		}
		if(orientation.equals("8")) //rotate 270
		{
			turn=270;
		}
		
		Matrix m = new Matrix();
		m.postRotate(turn, img.getWidth()/2, img.getHeight()/2);
		result=Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), m, true);
		return result;
	}
	
	public static Bitmap Shrink(Bitmap img, int Height,Context context) //shrink an image
	{
		final float densityMultiplier = context.getResources().getDisplayMetrics().density;        

		int h= (int) (Height*densityMultiplier);
		int w= (int) (h * img.getWidth()/((double) img.getHeight()));

		img=Bitmap.createScaledBitmap(img, w, h, true);
		return img;
	}
	
	public void ProcessExif(String file) //reads exif data from an image and puts it in the correct locations
	{
		ExifInterface exif=null; //instantiate exif read/writer
		boolean longRef=true,latRef=true;
		try
		{
			exif= new ExifInterface(file); //give exif reader the image file
			out.println("exif interface created");
		}
		catch(IOException e)
		{
			out.println("FAILURE TO ADD EXIF DATA");
			e.printStackTrace();
		}
		if(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)!=null) //if the photo has GPS data
		{
			if(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF).equals("S")) //in the long or lat values are South or West they must be made negative
			{
				latRef=false; //set a bool so we know for later
			}
			if(exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF).equals("W"))
			{
				longRef=false;
			}
		}
		
		out.println("FIND ME");
		//out.println(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE));

		if(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)!=null && exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)!=null) //make sure neither GPS value is missing
		{
			out.println("lat long start");
			Lat=(String.valueOf(ConvertToDegrees(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE),latRef))); //pass latitude through converter and return as string
			Long=(String.valueOf(ConvertToDegrees(exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE),longRef))); //pass longitude through converter and return as string
			out.println("lat long done");
		}


		if(exif.getAttribute(ExifInterface.TAG_DATETIME)!=null)
		{
			out.println("DateTime reference start");
			dateTaken=(exif.getAttribute(ExifInterface.TAG_DATETIME));  //pull the datetime info from the uploaded image
			String[] spliter= dateTaken.split(" ",2); //separate the time and date information
			dateTaken=spliter[0]; //assign date information
			timeTaken=spliter[1]; //assign time information
			out.println("DateTime reference complete");
		}	
		if(exif.getAttribute(ExifInterface.TAG_ORIENTATION)!=null)
		{
			rotation=exif.getAttribute(ExifInterface.TAG_ORIENTATION);
			exif.setAttribute(ExifInterface.TAG_ORIENTATION, "1");
		}
	}
	
	private String getRealPathFromURI(Uri contentURI) //gets the data storage path of a URI
	{
	    String result;
	    Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
	    if (cursor == null) 
	    { // Source is Dropbox or other similar local file path
	        result = contentURI.getPath();
	    } 
	    else 
	    { 
	        cursor.moveToFirst(); 
	        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
	        result = cursor.getString(idx);
	        cursor.close();
	    }
	    return result;
	}
	
	public Float ConvertToDegrees(String GPS,boolean positive) //split GPS data, convert it to floats, then combine it
	{
		Float result=null;
		String[] splitter=GPS.split(",",3);
		
		String[] degrees=splitter[0].split("/",2);
		Double d0=Double.parseDouble(degrees[0]);
		Double d1=Double.parseDouble(degrees[1]);
		Double FloatDegrees=d0/d1;
		
		String[] minutes=splitter[1].split("/",2);
		Double m0=Double.parseDouble(minutes[0]);
		Double m1=Double.parseDouble(minutes[1]);
		Double FloatMinutes=m0/m1;
		
		String[] seconds=splitter[2].split("/",2);
		Double s0=Double.parseDouble(seconds[0]);
		Double s1=Double.parseDouble(seconds[1]);
		Double FloatSeconds=s0/s1;
		
		result= (float)(FloatDegrees+(FloatMinutes/60)+(FloatSeconds/3600));
		if(!positive) //if the GPS coordinate is South or West make it negative
		{
			result*=-1;
		}
		out.println(result);
		return result;
	}
}
