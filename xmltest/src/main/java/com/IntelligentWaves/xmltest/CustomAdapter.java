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

        Boolean flag = false;
        if (item.getSynopsis().equals("Unable to connect to database") & objectArray.size() == 1) {
            flag = true;
        }

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

        TextView synopsis_textView =(TextView)listItemView.findViewById(R.id.synopsis_textView);
        TextView date_textView =(TextView)listItemView.findViewById(R.id.date_textView);
        TextView fullReport_textView =(TextView)listItemView.findViewById(R.id.fullReport_textView);

        //Assign the appropriate data from our alert object above
        synopsis_textView.setText(item.getSynopsis());

        if (flag) { // if unable to connect to db
            listItemView.setOnClickListener(null);
        } else {
            date_textView.setText(item.getNiceTime());
            if (!item.getSynopsis().equals(item.getFullReport())) {
                fullReport_textView.setText(item.getFullReport());
            }

        }

        return listItemView;
    }

}