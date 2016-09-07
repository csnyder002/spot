package com.IntelligentWaves.xmltest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Cody.Snyder on 8/28/2016.
 */
public class CustomDialogAdapter extends ArrayAdapter<String> {

    int resource;
    ArrayList<String> objectArray;
    Context context;

    public CustomDialogAdapter(Context context, int resource, String[] items) {
        super(context, resource, items);
        this.resource = resource;

        objectArray = new ArrayList<String>(items.length);
        for (int i=0; i<items.length;i++) {
            objectArray.add(i, items[i]);
        }

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LinearLayout listItemView;
        //Get the current alert object
        String item = objectArray.get(position);

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

        TextView textview =(TextView)listItemView.findViewById(R.id.textview);
        textview.setText(item);

        return listItemView;
    }

}