package ua.pp.appdev.expense.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.adapters.CategoryMultiChoiceAdapter;
import ua.pp.appdev.expense.models.Category;

public class CategoryMultiChoiceListFragment extends Fragment implements AdapterView.OnItemClickListener{

    private CategoryMultiChoiceAdapter adapter;

    private OnCategorySelectedListener mListener;

    private View viewAllCategories;

    private boolean isVisible = true;

    public CategoryMultiChoiceListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ListView categoryList = new ListView(getActivity());

        // Set header
        viewAllCategories = inflater.inflate(R.layout.listview_category_multichoice_row, null);
        ((TextView) viewAllCategories.findViewById(R.id.txtHistoryCategoryName)).setText(R.string.all_categories);
        ((TextView) viewAllCategories.findViewById(R.id.txtHistoryCategoryExpensesCount)).setText("(7)");
        viewAllCategories.findViewById(R.id.txtHistoryCategoryColor).setVisibility(View.GONE);
        viewAllCategories.setActivated(true);
        categoryList.addHeaderView(viewAllCategories);

        // Get array of categories and set adapter
        List<Category> categories = Category.getAll(getActivity());
        adapter = new CategoryMultiChoiceAdapter(getActivity(), R.layout.listview_category_multichoice_row, categories);
        categoryList.setAdapter(adapter);

        categoryList.setOnItemClickListener(this);

        return categoryList;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.action_add_expense, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCategorySelectedListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getParentFragment().toString()
                    + " must implement OnCategoryItemSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(i == 0){     /** 'All categories' clicked  - deselect categories*/
            for(int j = 1; j <= adapter.getCount(); j++){
                adapter.getItem(j - 1).checked = false;
            }
            adapter.notifyDataSetChanged();
            viewAllCategories.setActivated(true);
            mListener.onAllCategoriesSelected();
        } else {                        /** Category clicked - deselect 'All categories' */
            Category category = adapter.getItem(i - 1);
            category.checked = !category.checked;   // Change category 'checked' status

            adapter.notifyDataSetChanged();         // Notify adapter to redraw listview
            viewAllCategories.setActivated(false);  // Deselect 'all categories'

            String[] checkedCategoriesIds = adapter.getCheckedCategoriesIds();    // All selected ids
            if(checkedCategoriesIds.length == 0){   // If nothing selected - select 'All categories'
                viewAllCategories.setActivated(true);
                mListener.onAllCategoriesSelected();
            } else {
                mListener.onCategorySelected(checkedCategoriesIds);
            }
        }
    }

    public interface OnCategorySelectedListener {
        public void onCategorySelected(String[] ids);
        public void onAllCategoriesSelected();
    }
}
