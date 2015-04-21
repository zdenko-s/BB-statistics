package com.example.bbstatistics.util;

import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.bbstatistics.Consts;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zdenko on 2015-04-19.
 */
public class DialogHelper {
    /**
     * Shows TimePicker dialog. Reads time from text view and initializes dialog value according to
     * text in TextView. If user presses Set button, TextView is updated by value present at Dialog
     *
     * @param textView TextView to read initial value of time and set new value.
     */
    public static void getTime(TextView textView) {
        final TextView textViewF = textView;
        final int hour, minute;
        final Calendar calendar = Calendar.getInstance();
        final DateFormat timeFormatter = DateFormat.getTimeInstance();
        String timeString = textView.getText().toString();
        if (timeString.length() > 0) {
            Log.d(Consts.TAG, "Parsing time:" + timeString);
            try {
                Date formTime = timeFormatter.parse(timeString);
                calendar.setTime(formTime);
            } catch (ParseException e) {
                Log.d(Consts.TAG, "Parsing time failed:" + timeString);
            }
        }
        // Process to get Time
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog tpd = new TimePickerDialog(textView.getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfHour) {
                        // Display Selected time in TextView
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minuteOfHour);
                        textViewF.setText(timeFormatter.format(calendar.getTime()));
                    }
                }, hour, minute, true);
        tpd.show();
    }
}
