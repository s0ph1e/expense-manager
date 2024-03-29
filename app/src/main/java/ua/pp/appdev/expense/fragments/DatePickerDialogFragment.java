package ua.pp.appdev.expense.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.helpers.Helpers;
import ua.pp.appdev.expense.helpers.ViewFlipper;
import ua.pp.appdev.expense.utils.Log;

public class DatePickerDialogFragment extends DialogFragment implements View.OnClickListener, DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener{

    private final String PICKER_BUNDLE_STATE = "picker_bundle_state";
    private View view;

    private OnDateTimeSelectedListener mListener;

    private Calendar calendar;
    private DatePicker datePicker;
    private TimePicker timePicker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        calendar = new GregorianCalendar(
                getArguments().getInt("year"),
                getArguments().getInt("month"),
                getArguments().getInt("day"),
                getArguments().getInt("hour"),
                getArguments().getInt("minute")
        );

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle("Set date and time")
                .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        onTimeChanged(timePicker, timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                        mListener.onDateTimeSelected(
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH),
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE)

                        );
                    }
                })
                .setNeutralButton("Set now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        calendar = new GregorianCalendar();
                        mListener.onDateTimeSelected(
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH),
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE)

                        );
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_date_time_picker, null);

        // Set current date
        datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        datePicker.getCalendarView().setFirstDayOfWeek(Calendar.getInstance().getFirstDayOfWeek());
        datePicker.init(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                this
        );

        // Set preferred time format & current time
        timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(android.text.format.DateFormat.is24HourFormat(getActivity()));
        timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        timePicker.setOnTimeChangedListener(this);

        // Restore saved state (if exist)
        Log.i();
        if (savedInstanceState != null) {
            View dateView = this.view.findViewById(R.id.layoutDatePicker);
            View timeView = this.view.findViewById(R.id.layoutTimePicker);
            if (savedInstanceState.getInt(PICKER_BUNDLE_STATE) == 1) {
                dateView.setVisibility(View.GONE);
                timeView.setVisibility(View.VISIBLE);
            }
        }

        // Set buttons date text & listener
        TextView btnDate = (TextView)view.findViewById(R.id.btnSelectedDate);
        btnDate.setText(Helpers.calendarToShortDateString(getActivity(), calendar));
        btnDate.setOnClickListener(this);

        // Set button time text & listener
        TextView btnTime = (TextView)view.findViewById(R.id.btnSelectedTime);
        btnTime.setText(Helpers.calendarToTimeString(getActivity(), calendar));
        btnTime.setOnClickListener(this);

        adb.setView(view);

        return adb.create();
    }

    @Override
    public void onClick(View view) {
        View dateView = this.view.findViewById(R.id.layoutDatePicker);
        View timeView = this.view.findViewById(R.id.layoutTimePicker);
        onTimeChanged(timePicker, timePicker.getCurrentHour(), timePicker.getCurrentMinute());
        switch(view.getId()){
            case  R.id.btnSelectedTime:
                if(dateView.getVisibility() == View.VISIBLE)
                    ViewFlipper.flip(dateView, timeView);
                break;
            case  R.id.btnSelectedDate:
                if(timeView.getVisibility() == View.VISIBLE)
                    ViewFlipper.flip(dateView, timeView);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i();
        View dateView = this.view.findViewById(R.id.layoutDatePicker);

        if (dateView.getVisibility() == View.VISIBLE) {
            outState.putInt(PICKER_BUNDLE_STATE, 0);
        } else {
            outState.putInt(PICKER_BUNDLE_STATE, 1);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnDateTimeSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDateTimeSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
        calendar.set(Calendar.YEAR, datePicker.getYear());
        calendar.set(Calendar.MONTH, datePicker.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
        TextView btnDate = (TextView)view.findViewById(R.id.btnSelectedDate);
        btnDate.setText(Helpers.calendarToShortDateString(getActivity(), calendar));
    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        TextView btnTime = (TextView)view.findViewById(R.id.btnSelectedTime);
        btnTime.setText(Helpers.calendarToTimeString(getActivity(), calendar));
    }

    public interface OnDateTimeSelectedListener {
        public void onDateTimeSelected(int year, int month, int day, int hour, int minute);
    }
}