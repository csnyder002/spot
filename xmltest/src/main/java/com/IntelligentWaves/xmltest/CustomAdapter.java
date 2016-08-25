package com.IntelligentWaves.xmltest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<SpotReportObject> {

    int resource;
    ArrayList<SpotReportObject> objectArray;
    Context context;
    //Initialize adapter
    public CustomAdapter(Context context, int resource, ArrayList<SpotReportObject> items) {
        super(context, resource, items);
        this.resource=resource;
        this.objectArray = items;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LinearLayout listItemView;
        //Get the current alert object
        SpotReportObject item = objectArray.get(position);

        //Inflate the view
        if(convertView==null)
        {
            listItemView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi;
            vi = (LayoutInflater)getContext().getSystemService(inflater);
            vi.inflate(resource, listItemView, true);
        }
        else
        {
            listItemView = (LinearLayout) convertView;
        }
        //Get the text boxes from the listitem.xml file
        TextView alertText =(TextView)listItemView.findViewById(R.id.title_textView);

        //Assign the appropriate data from our alert object above
        alertText.setText(item.getUUID());

        return listItemView;
    }

}