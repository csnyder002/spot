package com.IntelligentWaves.xmltest;

import java.util.Calendar;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment 
{

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
	{
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int min = c.get(Calendar.MINUTE);

        // Create a new instance of DatePickerDialog and return it
        return new TimePickerDialog(getActivity(), (MainActivity)getActivity(), hour, min,true);
    }

    public void onTimeSet(TimePicker view, int hour, int min)
    {
        // Do something with the date chosen by the user
    }
}
