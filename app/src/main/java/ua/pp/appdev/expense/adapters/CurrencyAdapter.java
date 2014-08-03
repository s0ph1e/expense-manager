package ua.pp.appdev.expense.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.models.Currency;

public class CurrencyAdapter extends ArrayAdapter<Currency> {

    private Context context;
    int resource;
    List<Currency> currencies;

    public CurrencyAdapter(Context context, int resource, List<Currency> currencies) {
        super(context, resource, currencies);
        this.context = context;
        this.resource = resource;
        this.currencies = currencies;
    }

    public CurrencyAdapter(Context context, int resource){
        this(context, resource, Currency.getAll(context));
    }

    @Override
    public Currency getItem(int position){
        return currencies.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);
        }

        TextView text = (TextView)row.findViewById(R.id.txtCurrencyRow);
        text.setText(currencies.get(position).getSymbol());

        return row;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View row = getView(position, convertView, parent);
        row.findViewById(R.id.txtCurrencyRow).setPadding(15, 15, 15, 15);
        return row;
    }

    public int getPosition(Currency currency) {
        for(int i = 0; i < currencies.size(); i++){
            if(currencies.get(i).equals(currency))
                return i;
        }
        return -1;
    }
}
