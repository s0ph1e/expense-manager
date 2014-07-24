package ua.pp.appdev.expense;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.util.Calendar;

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

        view = getActivity().getLayoutInflater().inflate(R.layout.date_time_picker_layout, null);

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
                flip(dateView, timeView);
                break;
        }
    }

    private Interpolator accelerator = new AccelerateInterpolator();
    private Interpolator decelerator = new DecelerateInterpolator();
    private void flip(View a, View b) {
        final View visibleList;
        final View invisibleList;
        if (a.getVisibility() == View.GONE) {
            visibleList = b;
            invisibleList = a;
        } else {
            invisibleList = b;
            visibleList = a;
        }
        ObjectAnimator visToInvis = ObjectAnimator.ofFloat(visibleList, "rotationY", 0f, 90f);
        visToInvis.setDuration(500);
        visToInvis.setInterpolator(accelerator);
        final ObjectAnimator invisToVis = ObjectAnimator.ofFloat(invisibleList, "rotationY",
                -90f, 0f);
        invisToVis.setDuration(500);
        invisToVis.setInterpolator(decelerator);
        visToInvis.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator anim) {
                visibleList.setVisibility(View.GONE);
                invisToVis.start();
                invisibleList.setVisibility(View.VISIBLE);
            }
        });
        visToInvis.start();
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