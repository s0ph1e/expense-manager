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
    private String[] colors;

    public ColorAdapter(Context context, int resource, String[] colors) {
        super(context, resource, colors);
        this.context = context;
        this.resource = resource;
        this.colors = colors;
    }

    public ColorAdapter(Context context, int resource){
        this(context, resource, context.getResources().getStringArray(R.array.default_colors));
    }

    @Override
    public String getItem(int position){
        return colors[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ColorHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);

            holder = new ColorHolder();
            holder.color = (TextView)row.findViewById(R.id.txtColor);

            row.setTag(holder);
        } else {
            holder = (ColorHolder) row.getTag();
        }
        holder.color.setBackgroundColor(Color.parseColor(colors[position]));

        return row;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ColorHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);

            holder = new ColorHolder();
            holder.color = (TextView)row.findViewById(R.id.txtColor);

            row.setTag(holder);
        } else {
            holder = (ColorHolder) row.getTag();
        }
        holder.color.setBackgroundColor(Color.parseColor(colors[position]));

        return row;
    }

    static class ColorHolder{
        TextView color;
    }

}

