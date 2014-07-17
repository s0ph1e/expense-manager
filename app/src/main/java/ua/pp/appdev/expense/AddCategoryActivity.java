package ua.pp.appdev.expense;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import ua.pp.appdev.expense.helpers.ColorAdapter;

public class AddCategoryActivity extends EditActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_category);

        Spinner spinner = (Spinner) this.findViewById(R.id.spinnerColors);
        spinner.setAdapter(new ColorAdapter(this, R.layout.spinner_color_row));
    }

    @Override
    protected void onSave(View v) {

        // Get name
        EditText editText = (EditText) findViewById(R.id.etxtCategoryName);
        String name = editText.getText().toString();

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
}
