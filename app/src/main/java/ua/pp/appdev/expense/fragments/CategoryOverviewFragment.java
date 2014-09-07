package ua.pp.appdev.expense.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.adapters.CategoryBaseSingleChoiceAdapter;
import ua.pp.appdev.expense.adapters.CategoryOverviewAdapter;
import ua.pp.appdev.expense.helpers.Helpers;
import ua.pp.appdev.expense.helpers.SharedPreferencesHelper;
import ua.pp.appdev.expense.models.Category;
import ua.pp.appdev.expense.models.Expense;
import ua.pp.appdev.expense.utils.Log;

public class CategoryOverviewFragment extends Fragment {
    private static final String SELECTED_CATEGORY_POSITION = "selectedCategoryPosition";
    private static final String CATEGORIES_BUNDLE = "categories";
    private static final String SELECTED_CATEGORY_ID_BUNDLE = "selectedCategoryId";

    private OnCategoryOverviewSelectedListener mListener;

    private List<Category> categories;
    private View detailsView;
    private CategoryBaseSingleChoiceAdapter categoriesAdapter;
    private int selected = -1;

    public static CategoryOverviewFragment newInstance(ArrayList<Category> categories, long selectedCategoryId) {
        CategoryOverviewFragment fragment = new CategoryOverviewFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(CATEGORIES_BUNDLE, categories);
        args.putLong(SELECTED_CATEGORY_ID_BUNDLE, selectedCategoryId);
        fragment.setArguments(args);
        return fragment;
    }

    public CategoryOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i();
        long selectedCategoryId = -1;

        Bundle args = getArguments();
        if (args != null) {
            categories = args.getParcelableArrayList(CATEGORIES_BUNDLE);
            selectedCategoryId = args.getLong(SELECTED_CATEGORY_ID_BUNDLE);
        }

        if(savedInstanceState != null){
            selected = savedInstanceState.getInt(SELECTED_CATEGORY_POSITION);
        }

        View view =  inflater.inflate(R.layout.fragment_category_overview, container, false);
        detailsView = view.findViewById(R.id.overviewDetails);
        ListView categoriesList = (ListView) view.findViewById(R.id.listviewCategoryOverview);
        categoriesAdapter = new CategoryOverviewAdapter(
                getActivity(),
                R.layout.listview_category_overview_row,
                new ArrayList<Category>(categories));

        categoriesList.setAdapter(categoriesAdapter);
        categoriesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (categoriesAdapter.getCount() > 1) {
                    setSelected(i);
                }
            }
        });
        setSelectedById(selectedCategoryId);
        return view;
    }

    public void updateText(){
        if(categories == null || categories.size() == 0) {
            detailsView.setVisibility(View.GONE);
            return;
        }

        View allCategoriesView = detailsView.findViewById(R.id.overviewAllCategories);
        List<Category> newCategories = new ArrayList<Category>();

        // If none selected  - show all
        if(!(selected >= 0 && selected < categories.size())){
            TextView totalSumTxt = (TextView) allCategoriesView.findViewById(R.id.txtOverviewAllCategoriesSum);
            String totalSumString = Helpers.sumToString(Expense.getSum(getActivity()), SharedPreferencesHelper.getBaseCurrency(getActivity()));
            totalSumTxt.setText(totalSumString);
            allCategoriesView.setVisibility(View.VISIBLE);
            newCategories.addAll(categories);
        } else {    // show selected category
            allCategoriesView.setVisibility(View.GONE);
            newCategories.add(categories.get(selected));
        }

        categoriesAdapter.clear();
        categoriesAdapter.addAll(newCategories);

        // Make details view visible if it is invisible
        if(detailsView.getVisibility() != View.VISIBLE){
            Animation animFadeIn = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_in);
            detailsView.setAnimation(animFadeIn);
            detailsView.setVisibility(View.VISIBLE);
        }
    }

    public void setSelected(int position){
        if(position == selected){
            return;
        } else if(position >= 0 && position < categories.size()){
            categoriesAdapter.setSelected(position);
            mListener.OnCategoryOverviewSelected(categories.get(position).id);
        } else {
            mListener.OnCategoryOverviewSelected(0);
        }
        selected = position;
        updateText();
    }

    public void setSelectedById(long categoryId) {
        Category cat = Category.getById(getActivity(), categoryId);
        int position = categories.indexOf(cat);
        selected = position;
        updateText();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_CATEGORY_POSITION, selected);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCategoryOverviewSelectedListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCategoryOverviewSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnCategoryOverviewSelectedListener {
        public void OnCategoryOverviewSelected(long categoryId);
    }

}
