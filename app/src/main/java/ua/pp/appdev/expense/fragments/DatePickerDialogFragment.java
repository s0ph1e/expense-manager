package ua.pp.appdev.expense.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.helpers.ViewFlipper;

public class DatePickerDialogFragment extends DialogFragment implements View.OnClickListener{

    private View view;

    private OnDateTimeSelectedListener mListener;

    private DatePicker datePicker;
    private TimePicker timePicker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int year = getArguments().getInt("year");
        int month = getArguments().getInt("month");
        int day = getArguments().getInt("day");
        int hour = getArguments().getInt("hour");
        int minute = getArguments().getInt("minute");

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle("Set date and time")
                .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mListener.onDateTimeSelected(
                                datePicker.getYear(),
                                datePicker.getMonth(),
                                datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(),
                                timePicker.getCurrentMinute()
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
        datePicker.updateDate(year, month, day);

        // Set preferred time format & current time
        timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(android.text.format.DateFormat.is24HourFormat(getActivity()));
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);

        view.findViewById(R.id.btnSetTime).setOnClickListener(this);

        view.findViewById(R.id.btnSetDate).setOnClickListener(this);

        adb.setView(view);

        return adb.create();
    }

    @Override
    public void onClick(View view) {
        View dateView = this.view.findViewById(R.id.layoutDatePicker);
        View timeView = this.view.findViewById(R.id.layoutTimePicker);

        switch (view.getId()){
            case R.id.btnSetTime:
            case R.id.btnSetDate:
                ViewFlipper.flip(dateView, timeView);
                break;
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

    public interface OnDateTimeSelectedListener {
        public void onDateTimeSelected(int year, int month, int day, int hour, int minute);
    }
}