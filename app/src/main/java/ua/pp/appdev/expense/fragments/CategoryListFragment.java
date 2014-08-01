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
import ua.pp.appdev.expense.adapters.CategoryAdapter;
import ua.pp.appdev.expense.helpers.EditableItemListView;
import ua.pp.appdev.expense.models.Category;

import static ua.pp.appdev.expense.helpers.EditableItemListView.ADD;
import static ua.pp.appdev.expense.helpers.EditableItemListView.EDIT;

public class CategoryListFragment extends Fragment implements View.OnTouchListener {

    private OnFragmentInteractionListener mListener;

    private CategoryAdapter categoryListAdapter;

    private ListView categoryList;

    public CategoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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
        categoryListAdapter = new CategoryAdapter(getActivity(), R.layout.listview_category_row, categories);
        categoryList.setAdapter(categoryListAdapter);

        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                categoryListAdapter.setSelected(i);
                mListener.onCategorySelected(categoryListAdapter.getItem(i));
            }
        });

        return categoryList;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || resultCode != Activity.RESULT_OK) {return;}

        Category newCat = (Category) data.getSerializableExtra("item");

        switch (requestCode){
            case ADD:
                categoryListAdapter.add(newCat);
                categoryListAdapter.setSelected(categoryListAdapter.getCount() - 1);
                mListener.onCategorySelected(newCat);
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

    public boolean setCategory(Category category) {
        int categoryPos = categoryListAdapter.getPosition(category);
        if (categoryPos >= 0) {
            categoryListAdapter.setSelected(categoryPos);
            // This is required for scrolling to selected category is if is outside of the container
            categoryList.setSelection(categoryPos);
            return true;
        }
        return false;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onCategorySelected(Category category);
    }

}
