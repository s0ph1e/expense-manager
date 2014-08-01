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
import ua.pp.appdev.expense.models.Expense;

public class CategoryMultiChoiceListFragment extends Fragment implements AdapterView.OnItemClickListener{

    private static final String SELECTED_CATEGORIES_IDS = "selectedCategoriesIds";

    private CategoryMultiChoiceAdapter adapter;

    private OnCategorySelectedListener mListener;

    private View viewAllCategories;

    public CategoryMultiChoiceListFragment() {
        // Required empty public constructor
    }

    public static CategoryMultiChoiceListFragment newInstance(String[] categories){
        CategoryMultiChoiceListFragment fragment = new CategoryMultiChoiceListFragment();
        Bundle args = new Bundle();
        args.putStringArray(SELECTED_CATEGORIES_IDS, categories);
        fragment.setArguments(args);
        return fragment;
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
        ((TextView) viewAllCategories.findViewById(R.id.txtHistoryCategoryExpensesCount)).setText("("+ Expense.getCount(getActivity()) +")");
        viewAllCategories.findViewById(R.id.txtHistoryCategoryColor).setVisibility(View.GONE);
        viewAllCategories.setActivated(true);
        categoryList.addHeaderView(viewAllCategories);

        // Get array of categories and set adapter
        List<Category> categories = Category.getAll(getActivity());
        adapter = new CategoryMultiChoiceAdapter(getActivity(), R.layout.listview_category_multichoice_row, categories);
        categoryList.setAdapter(adapter);

        // Set selected categories
        Bundle args = getArguments();
        if (args != null) {
            setSelectedCategories(args.getStringArray(SELECTED_CATEGORIES_IDS));
        }

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
        changeItemState(i);
    }


    /**
     * Change state of item at <tt>position</tt> position
     * If 'All categories' is selected (position 0) - deselect all categories
     * if some of categories is selected (position 1 - adapter.getCount()) - deselect 'All categories'
     * if nothing is selected - set 'All categories' selected
     * @param position
     */
    public void changeItemState(int position){
        String[] checkedCategoriesIds = null;
        if(position == 0){     /** 'All categories' clicked  - deselect categories*/
            for(int j = 1; j <= adapter.getCount(); j++){
                adapter.getItem(j - 1).checked = false;
            }
            //adapter.notifyDataSetChanged();
            viewAllCategories.setActivated(true);
            //mListener.onAllCategoriesSelected();
        } else {                        /** Category clicked - deselect 'All categories' */
            Category category = adapter.getItem(position - 1);
            category.checked = !category.checked;   // Change category 'checked' status

            adapter.notifyDataSetChanged();         // Notify adapter to redraw listview
            viewAllCategories.setActivated(false);  // Deselect 'all categories'

            checkedCategoriesIds = adapter.getCheckedCategoriesIds();    // All selected ids
            if(checkedCategoriesIds.length == 0){   // If nothing selected - select 'All categories'
                viewAllCategories.setActivated(true);
                //mListener.onAllCategoriesSelected();
            }
        }
        adapter.notifyDataSetChanged();
        mListener.onCategorySelected(checkedCategoriesIds);
    }

    /**
     * Select categories
     * @param ids - categories ids to select
     */
    public void setSelectedCategories(String[] ids){
        if(ids == null){
            changeItemState(0);
        } else {
            for (String id : ids) {
                Category cat = Category.getById(getActivity(), Long.valueOf(id));
                int position = adapter.getPosition(cat);
                if(position > 0){
                    changeItemState(position);
                }
            }
        }
    }

    public interface OnCategorySelectedListener {
        public void onCategorySelected(String[] ids);
        public void onAllCategoriesSelected();
    }
}
