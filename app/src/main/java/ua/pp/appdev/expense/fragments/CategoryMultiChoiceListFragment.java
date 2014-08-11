package ua.pp.appdev.expense.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.adapters.CategoryMultiChoiceAdapter;
import ua.pp.appdev.expense.models.Category;
import ua.pp.appdev.expense.models.Expense;
import ua.pp.appdev.expense.utils.Log;

public class CategoryMultiChoiceListFragment extends DialogFragment implements AdapterView.OnItemClickListener{

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
        super.onCreate(savedInstanceState);
        Log.i();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(R.string.filter);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i();

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_category_multichoice, null);
        ListView categoryList = (ListView) layout.findViewById(R.id.listViewCategoriesMultiChoice);

        // Set scrollbar always shown
        categoryList.setScrollbarFadingEnabled(false);

        // Set header
        viewAllCategories = inflater.inflate(R.layout.listview_category_multichoice_row, null);
        ((TextView) viewAllCategories.findViewById(R.id.txtHistoryCategoryName)).setText(R.string.all_categories);
        ((TextView) viewAllCategories.findViewById(R.id.txtHistoryCategoryExpensesCount)).setText("("+ Expense.getCount(getActivity()) +")");
        viewAllCategories.findViewById(R.id.txtHistoryCategoryColor).setVisibility(View.GONE);
        viewAllCategories.setActivated(true);
        categoryList.addHeaderView(viewAllCategories);

        Button filterBtn = (Button) layout.findViewById(R.id.btnHistoryCategoriesFilter);
        if(filterBtn != null){
            filterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDialog().dismiss();
                }
            });
        }

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

        return layout;
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
        changeItemState(position, true);
    }

    private void changeItemState(int position, boolean executeCallback){
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
            }
        }
        adapter.notifyDataSetChanged();

        if(executeCallback){
            mListener.onCategorySelected(checkedCategoriesIds);
        }
    }

    /**
     * Select categories
     * @param ids - categories ids to select
     */
    private void setSelectedCategories(String[] ids){
        if(ids == null){
            changeItemState(0, false);
        } else {
            for (String id : ids) {
                Category cat = Category.getById(getActivity(), Long.valueOf(id));
                int position = adapter.getPosition(cat);
                if(position > 0){
                    changeItemState(position, false);
                }
            }
        }
    }

    public interface OnCategorySelectedListener {
        public void onCategorySelected(String[] ids);
    }
}
