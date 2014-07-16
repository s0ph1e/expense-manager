package ua.pp.appdev.expense.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import ua.pp.appdev.expense.R;


public class ColorAdapter extends ArrayAdapter<String> {

    private Context context;
    int resource;
    private int[] colors;
    private int mSelectedPosition = -1;

    public ColorAdapter(Context context, int resource, String[] colors) {
        super(context, resource, colors);
        this.context = context;
        this.resource = resource;
        this.colors = context.getResources().getIntArray(R.array.default_colors);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View row = inflater.inflate(resource, parent, false);
        TextView color = (TextView) row.findViewById(R.id.txtColor);
        color.setBackgroundColor(colors[position]);
        return row;
    }


    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View row = inflater.inflate(resource, parent, false);
        TextView color = (TextView) row.findViewById(R.id.txtColor);
        color.setBackgroundColor(colors[position]);
        return row;
    }
}

