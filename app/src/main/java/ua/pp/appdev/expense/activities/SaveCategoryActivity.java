package ua.pp.appdev.expense.activities;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;
import java.util.Random;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.fragments.ColorPickerDialogFragment;
import ua.pp.appdev.expense.adapters.ColorAdapter;
import ua.pp.appdev.expense.helpers.Helpers;
import ua.pp.appdev.expense.models.Category;

public class SaveCategoryActivity extends EditActivity implements ColorPickerDialogFragment.OnColorSelectedListener {

    // Позиция последнего выбранного цвета
    int previousColorSelectedPos = -1;

    private ColorAdapter colorAdapter;
    private Category category;

    // Views
    EditText categoryName;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_save_category);

        categoryName = (EditText)findViewById(R.id.etxtCategoryName);
        categoryName.setOnEditorActionListener(lostFocusAfterDone);

        spinner = (Spinner) this.findViewById(R.id.spinnerColors);

        colorAdapter = new ColorAdapter(this, R.layout.spinner_color_row);
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

        Intent callingIntent = getIntent();
        category = (Category) callingIntent.getSerializableExtra("item");

        if(category != null){
            categoryName.setText(category.name);

            String categoryColor = Helpers.colorToString(category.color);
            int colorIndex = colorAdapter.getPosition(categoryColor);

            if(colorIndex < 0) {
                colorAdapter.getColors().add(0, categoryColor);
            } else {
                spinner.setSelection(colorIndex);
            }
        } else {
            category = new Category();
            // Выбираем случайный цвет из списка
            Random random = new Random();
            int position = random.nextInt(spinner.getCount() - 1);
            spinner.setSelection(position);
        }
    }

    @Override
    protected void onSave(View v) {

        // Get name
        String name = categoryName.getText().toString();

        if(name.isEmpty()){
            new AlertDialog.Builder(this)
                .setTitle(R.string.add_name)
                .setMessage(R.string.category_name_cant_empty)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .show();
            return;
        }

        // Get color
        int color = Color.parseColor(spinner.getSelectedItem().toString());

        category.name = name;
        category.color = color;
        category.save(this);

        // Send saved category back
        Intent intent = new Intent();
        intent.putExtra("item", category);
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
        List<String> colors = colorAdapter.getColors();
        colors.add(colors.size() - 1, Helpers.colorToString(color));
        colorAdapter.notifyDataSetChanged();

        // Выберем предпослений элемент
        // Это новый цвет
        spinner.setSelection(spinner.getCount() - 2);
    }
}
