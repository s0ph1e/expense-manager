package ua.pp.appdev.expense.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.activities.SaveCategoryActivity;
import ua.pp.appdev.expense.adapters.CategorySettingsAdapter;
import ua.pp.appdev.expense.helpers.EditableItemListView;
import ua.pp.appdev.expense.models.Category;
import ua.pp.appdev.expense.utils.Log;

import static ua.pp.appdev.expense.helpers.EditableItemListView.ADD;
import static ua.pp.appdev.expense.helpers.EditableItemListView.EDIT;

public class CategoryListSettingsFragment extends Fragment implements View.OnTouchListener {

    private static final String SELECTED_BUNDLE = "selected";

    private CategorySettingsAdapter categoryListAdapter;

    private ListView categoryList;

    public CategoryListSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.i();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i();

        categoryList = new EditableItemListView(getActivity());
        categoryList.setOnTouchListener(this);
        categoryList.setId(R.id.category_list);

        // Create button for new category and put it to the end of listview
        //final Button btnAddNew = new Button(getActivity());
        final View btnAddNew = inflater.inflate(R.layout.transparent_button, null);
        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), SaveCategoryActivity.class);
                startActivityForResult(i, ADD);
            }
        });
        btnAddNew.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        ((TextView)btnAddNew.findViewById(R.id.txtTransparentButton)).setText(R.string.add_category);

        // Note: When first introduced, this method could only be called before setting
        // the adapter with setAdapter(ListAdapter).
        // Starting with KITKAT, this method may be called at any time.
        categoryList.addFooterView(btnAddNew);

        // Get array of categories and set adapter
        List<Category> categories = Category.getAll(getActivity());
        categoryListAdapter = new CategorySettingsAdapter(getActivity(), R.layout.listview_category_settings_row, categories);
        categoryList.setAdapter(categoryListAdapter);

        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), SaveCategoryActivity.class);
                intent.putExtra("item", categoryListAdapter.getItem(i));
                getActivity().startActivityForResult(intent, EDIT);
            }
        });

        Bundle args = getArguments();
        if(args != null){
            Category cat = (Category) args.getSerializable(SELECTED_BUNDLE);
            setSelectedCategory(cat);
        }

        return categoryList;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || resultCode != Activity.RESULT_OK) {return;}

        Category newCat = (Category) data.getSerializableExtra("item");

        switch (requestCode){
            case ADD:
                categoryListAdapter.add(newCat);
                categoryListAdapter.setSelected(categoryListAdapter.getCount() - 1);
                break;
            case EDIT:
                int position = categoryListAdapter.getPosition(newCat);
                if(position >= 0){
                    List<Category> categories = categoryListAdapter.getCategories();
                    categories.remove(position);
                    categories.add(position, newCat);
                    categoryListAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    public boolean setSelectedCategory(Category category) {
        int categoryPos = categoryListAdapter.getPosition(category);
        if (categoryPos >= 0 && categoryPos < categoryListAdapter.getCount()) {
            categoryListAdapter.setSelected(categoryPos);
            // This is required for scrolling to selected category is if is outside of the container
            categoryList.setSelection(categoryPos);
            return true;
        }
        return false;
    }

    public Category getSelectedCategory(){
        return categoryListAdapter.getSelectedItem();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (((EditableItemListView)categoryList).getTotalHeight() > ((FrameLayout)categoryList.getParent()).getHeight()/* && (getLastVisiblePosition())< getCount() - 1*/) {
            view.getParent().requestDisallowInterceptTouchEvent(true);
        } else {
            view.getParent().requestDisallowInterceptTouchEvent(false);
        }

        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i();
        outState.putSerializable(SELECTED_BUNDLE, categoryListAdapter.getSelectedItem());
    }

}
