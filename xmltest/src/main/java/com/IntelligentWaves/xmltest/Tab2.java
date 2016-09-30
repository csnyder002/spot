package com.IntelligentWaves.xmltest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class Tab2 extends Fragment implements View.OnClickListener{

    List<String> xmlFiles;
    SharedPreferences preferences;

    XMLreader xmlReader				= new XMLreader(getActivity());
    SubmitReportSFTP secureTransfer			= new SubmitReportSFTP(getActivity());
    List<Input> xmlData				= new ArrayList<Input>();
    List<Bitmap> bitmapList			= new ArrayList<Bitmap>();
    List<CheckBox> checkBoxList		= new ArrayList<CheckBox>();
    List<Integer> checked			= new ArrayList<Integer>();
    List<Integer> uploadedChecked	= new ArrayList<Integer>();
    Boolean beenClicked				= false;

    LinearLayout ll;//overarching layout, holds everything
    LinearLayout hl2;//layout that holds buttons
    TextView mainText;
    Button Edit;
    Button delete;
    Button UploadSelected;
    Button loadSubmitted;
    Button map_button;
    ListView listView;

    ArrayList<SpotReportObject> objectArray;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Bundle b = getActivity().getIntent().getExtras();

        objectArray = ((SlidingActivity) getActivity()).getObjectArray();

    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v =inflater.inflate(R.layout.tab2_frag,container,false);
        listView = (ListView) v.findViewById(R.id.listView);
        listView.setAdapter(new CustomAdapter(getActivity(), R.layout.spot_report_list_item_layout, objectArray));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), update_report.class);
                intent.putExtra("spotReport", objectArray.get(position));
                startActivity(intent);
            }
        });

        map_button = (Button) v.findViewById(R.id.map_button);
        map_button.setOnClickListener(this);

        /*ll = (LinearLayout) v.findViewById(R.id.LL);//overarching layout, holds everything
        hl2 = (LinearLayout) v.findViewById(R.id.HL);//layout that holds buttons
        mainText = (TextView) v.findViewById(R.id.mainText);
        Edit = (Button) v.findViewById(R.id.Edit);
        delete = (Button) v.findViewById(R.id.delete);
        UploadSelected = (Button) v.findViewById(R.id.UploadSelected);
        loadSubmitted = (Button) v.findViewById(R.id.loadSubmitted);
        listView = (ListView) v.findViewById(R.id.listView);


        Edit.setOnClickListener(this);
        delete.setOnClickListener(this);
        UploadSelected.setOnClickListener(this);
        loadSubmitted.setOnClickListener(this);*/

        //BuildLayout();
        return v;
    }

    public void onClick(View view)
    {
        switch(view.getId()) {
            case R.id.map_button:
                Intent intent = new Intent(getActivity(), MapActivity.class);
                intent.putExtra("objectArray", objectArray);
                startActivity(intent);
                break;
            /*case R.id.Edit:
                System.out.println("edit");
                Edit(view);
                break;
            case R.id.delete:
                System.out.println("delete");
                Delete(view);
                break;
            case R.id.UploadSelected:
                System.out.println("upload");
                break;
            case R.id.loadSubmitted:
                // get user's spot reports
                String url = "https://" + preferences.getString("Host", "") + "/getUserReports.php";
                String query = "SELECT * FROM spot_reports_niger WHERE name='" + preferences.getString("Name", "")+ "';";
                FetchReports reports = new FetchReports(getActivity(), query, url);
                reports.execute();
                System.out.println("load");
                break;*/
        }
    }

    public boolean isExternalStorageWritable() //make sure you can write to external storage. app loses most of its capability without this ability
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            return true;
        }
        return false;
    }

    public List<String> getAllFilesInDir(String dir) //get a list of all files saved in the chosen directory
    {
        File sdCardRoot = Environment.getExternalStorageDirectory();
        File yourDir = new File(sdCardRoot, dir);
        List<String> Files= new ArrayList<String>();
        for (File f : yourDir.listFiles())
        {
            if (f.isFile())
            {
                Files.add(f.getPath());
            }
        }
        return Files;
    }

    private void BuildLayout() //dynamically build the layout for this activity
    {
        xmlFiles=getAllFilesInDir("/.spot/");

        if(ll.getChildCount()!=0)//drops everything from the main layout, then re adds mainText to the top
        {
            ll.removeAllViews();
            ll.addView(mainText);
        }

        for(int i=0;i<xmlFiles.size();i++) //loop through and for every file in the chosen directory create a new minilayout and add it to the main LL
        {
            LinearLayout hl1=new LinearLayout(getActivity());
            hl1.setPadding(5, 10, 0, 0);
            ImageView iv=new ImageView(getActivity());
            TextView tv=new TextView(getActivity());
            CheckBox cb=new CheckBox(getActivity());

            xmlData=xmlReader.readXML(xmlFiles.get(i));

            Uri uri=LookForImagePath(xmlData);
            if(uri!=null) //if we fail to find an associated image, put the spot image up instead
            {
                bitmapList.add(Shrink(BitmapFactory.decodeFile(LookForImagePath(xmlData).getPath()),50,getActivity()));
                iv.setImageBitmap(bitmapList.get(i));
            }
            else
            {
                bitmapList.add(Shrink(BitmapFactory.decodeResource(getResources(), R.drawable.spot),50,getActivity()));
                iv.setImageBitmap(bitmapList.get(i));
            }


            tv.setText(TrimFileName(xmlFiles.get(i)));
            tv.setTextColor(getResources().getColor(R.color.textColor));

            cb.setId(i);
            cb.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    CheckCheckBoxes();
                }
            });
            checkBoxList.add(cb);

            hl1.addView(iv);
            hl1.addView(tv);
            hl1.addView(cb);


            ll.addView(hl1);
            xmlData.clear();
        }
        ll.addView(hl2); //re add the button layout to the bottom of the display
        CheckCheckBoxes();

    }

    public void ResetLayout() // called when an item is removed from the file directory to rebuild the layout
    {
        xmlData.clear();
        bitmapList.clear();
        checkBoxList.clear();
        checked.clear();
        xmlFiles.clear();
        secureTransfer=new SubmitReportSFTP(getActivity());

        Intent intent = new Intent(getActivity(), SlidingActivity.class);
        getActivity().startActivity(intent);
    }

    public Uri LookForImagePath(List<Input> I) //loops through an xml file and reutrns an Image URI
    {
        Uri result=null;

        for(int j=0;j<I.size();j++)
        {
            if(I.get(j).code.equals("ImageFilePath"))
            {
                File temp=new File((I.get(j).data));
                if(temp.isFile())
                {
                    result= Uri.fromFile(new File((I.get(j).data)));

                    return result;
                }
            }
        }
        return result;
    }

    public String LookForFilePath(List<Input> I) //loops thorugh an xml document and returns the filepath as a String
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

    public String TrimFileName(String toTrim)//cuts off all file path info from the file name so its easier to read
    {
        String[] splitholder;
        String result;
        splitholder=toTrim.split("/");
        splitholder=splitholder[splitholder.length-1].split("__",2);
        result=splitholder[0];

        return result;
    }

    public Bitmap Shrink(Bitmap img, int Height, Context context) //makes a Bitmap smaller
    {
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h= (int) (Height*densityMultiplier);
        int w= (int) (h * img.getWidth()/((double) img.getHeight()));

        img=Bitmap.createScaledBitmap(img, w, h, true);
        return img;
    }

    public void Edit(View view) // ONCLICK - sends the selected xml document to the main  form to be edited
    {
        Intent main = new Intent(getActivity().getApplicationContext(), SlidingActivity.class);

        CheckCheckBoxes();
        if(checked.size()==1)
        {
            main.putExtra("load", true);
            out.println(xmlFiles.get(checked.get(0)));
            main.putExtra("file",xmlFiles.get(checked.get(0)));
            main.putExtra("FileName",TrimFileName(xmlFiles.get(checked.get(0))));
        }
        else
        {
            main.putExtra("load", false);
        }
        startActivity(main);
    }

    public void Delete(View view) // ONCLICK - removes all files that have been checked
    {
        CheckCheckBoxes();
        for (int i=0;i<checked.size();i++)
        {
            File f= new File(xmlFiles.get(checked.get(i)));
            f.delete();
            checkBoxList.remove(checked.get(i));
        }

        Intent intent = new Intent(getActivity(), SplashActivity.class);
        getActivity().startActivity(intent);
    }

    public void CheckCheckBoxes() // finds which check boxes are checked and adds them to a list. also determines which buttons to show
    {
        int count=0;
        checked.clear();
        for(int i=0;i<checkBoxList.size();i++)
        {
            if(checkBoxList.get(i).isChecked())
            {
                count++;
                checked.add(i);
            }
        }
        if(count==0)
        {
            Edit.setVisibility(View.INVISIBLE);
            delete.setVisibility(View.INVISIBLE);
            UploadSelected.setVisibility(View.INVISIBLE);
        }
        else if(count==1)
        {
            Edit.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            UploadSelected.setVisibility(View.VISIBLE);
        }
        else if(count>=2)
        {
            Edit.setVisibility(View.INVISIBLE);
            delete.setVisibility(View.VISIBLE);
            UploadSelected.setVisibility(View.VISIBLE);
        }
    }

}