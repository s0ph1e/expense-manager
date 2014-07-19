package ua.pp.appdev.expense;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import ua.pp.appdev.expense.helpers.ColorAdapter;

public class AddCategoryActivity extends EditActivity implements ColorPickerDialogFragment.OnColorSelectedListener {

    // ID of color in spinner
    int previousColorSelectedPos = -1;

    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_category);

        final Spinner spinner = (Spinner) this.findViewById(R.id.spinnerColors);

        ColorAdapter colorAdapter = new ColorAdapter(this, R.layout.spinner_color_row);
        colorAdapter.add(getString(R.string.category_other_color));
        spinner.setAdapter(colorAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (((String)adapterView.getItemAtPosition(i)).equals(getString(R.string.category_other_color))) {
                    spinner.setSelection(previousColorSelectedPos);
                    // DialogFragment.show() will take care of adding the fragment
                    // in a transaction.  We also want to remove any currently showing
                    // dialog, so make our own transaction and take care of that here.
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);

                    // Create and show the dialog.
                    DialogFragment newFragment = new ColorPickerDialogFragment();
                    newFragment.show(ft, "dialog");
                } else {
                    previousColorSelectedPos = i;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onSave(View v) {

        // Get name
        EditText editText = (EditText) findViewById(R.id.etxtCategoryName);
        String name = editText.getText().toString();

        if(name.isEmpty()){
            new AlertDialog.Builder(this)
                .setTitle("Add name")
                .setMessage("Category name can't be empty.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
            return;
        }

        // Get color
        Spinner spinner = (Spinner)findViewById(R.id.spinnerColors);
        int color = Color.parseColor(spinner.getSelectedItem().toString());

        Category category = Category.add(this, name, color);

        // Send added category data back
        Intent intent = new Intent();
        intent.putExtra("new", category);
        setResult(RESULT_OK, intent);

        finish();
    }

    @Override
    protected void onCancel(View v) {
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    /**
     * Метод вызывается после выбора пользователем цвета
     * @param color Выбранный пользователем цвети
     */
    @Override
    public void onColorSelected(int color) {
        final Spinner spinner = (Spinner) this.findViewById(R.id.spinnerColors);

        ColorAdapter colorAdapter = new ColorAdapter(this, R.layout.spinner_color_row);
        colorAdapter.add(String.format("#%06X", (0xFFFFFF & color)));
        colorAdapter.add(getString(R.string.category_other_color));
        spinner.setAdapter(colorAdapter);

        // Выберем предпослений элемент
        // Это новый цвет
        spinner.setSelection(spinner.getCount() - 2);
    }
}
