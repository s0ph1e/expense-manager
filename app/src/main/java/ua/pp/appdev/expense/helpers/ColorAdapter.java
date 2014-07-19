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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ua.pp.appdev.expense.R;


public class ColorAdapter extends ArrayAdapter<String> {

    private Context context;
    int resource;
    private List<String> colors;

    public ColorAdapter(Context context, int resource, ArrayList<String> colors) {
        super(context, resource, colors);
        this.context = context;
        this.resource = resource;
        this.colors = colors;
    }

    public ColorAdapter(Context context, int resource){
        this(context, resource, new ArrayList<String>(Arrays.asList(context.getResources().getStringArray(R.array.default_colors))));
    }

    @Override
    public void add(String object) {
        colors.add(object);
    }

    @Override
    public String getItem(int position){
        return colors.get(position);
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


        if (colors.get(position).charAt(0) != '#') {
            holder.color.setBackgroundColor(Color.TRANSPARENT);
            holder.color.setText(colors.get(position));
        } else {
            holder.color.setText("");
            holder.color.setBackgroundColor(Color.parseColor(colors.get(position)));
        }

        return row;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    static class ColorHolder{
        TextView color;
    }

}

