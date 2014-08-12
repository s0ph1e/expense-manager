package ua.pp.appdev.expense.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.adapters.CategoryBaseSingleChoiceAdapter;
import ua.pp.appdev.expense.adapters.CategoryOverviewAdapter;
import ua.pp.appdev.expense.helpers.Helpers;
import ua.pp.appdev.expense.helpers.SharedPreferencesHelper;
import ua.pp.appdev.expense.models.Category;
import ua.pp.appdev.expense.models.Expense;
import ua.pp.appdev.expense.utils.Log;

public class CategoryPieFragment extends Fragment {

    private PieGraph pieGraph;

    private ListView categoriesList;

    private View detailsView;

    private CategoryBaseSingleChoiceAdapter categoriesAdapter;

    private List<Category> categories;

    private int selected = -1;

    private OnCategoryPieSelectedListener mListener;

    AsyncGetCategories asyncGetCategories;

    public static CategoryPieFragment newInstance() {
        CategoryPieFragment fragment = new CategoryPieFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public CategoryPieFragment() {
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

        View view =  inflater.inflate(R.layout.fragment_category_pie, container, false);
        detailsView = view.findViewById(R.id.overviewDetails);
        categoriesList = (ListView) view.findViewById(R.id.listviewCategoryOverview);
        categoriesAdapter = new CategoryOverviewAdapter(
                getActivity(),
                R.layout.listview_category_overview_row,
                new ArrayList<Category>());

        categoriesList.setAdapter(categoriesAdapter);
        categoriesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setSelected(i);
            }
        });

        pieGraph = (PieGraph) view.findViewById(R.id.categoriesPieGraph);
        pieGraph.setInnerCircleRatio(150);
        pieGraph.setPadding(5);
        pieGraph.setOnSliceClickedListener(new PieGraph.OnSliceClickedListener() {
            @Override
            public void onClick(int i) {
                setSelected(i);
            }
        });
        pieGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelected(-1);
            }
        });

        asyncGetCategories = new AsyncGetCategories();
        asyncGetCategories.execute();

        return view;
    }

    @Override
    public void onDestroyView() {
        Log.i();
        asyncGetCategories.cancel(true);
        super.onDestroyView();
    }

    public void updateGraph(){
        pieGraph.removeSlices();
        PieSlice slice;
        int slicesCount = 0;

        Iterator it = categories.iterator();
        while(it.hasNext()) {
            Category cat = (Category) it.next();
            float sum = cat.getExpensesSum(getActivity()).floatValue();
            if(sum <= 0){
                it.remove();
                continue;
            }
            slice = new PieSlice();
            slice.setColor(cat.color);
            slice.setValue(1);
            slice.setGoalValue(sum);
            slice.setTitle(cat.name);
            pieGraph.addSlice(slice);
            slicesCount++;
        }

        if(slicesCount > 1){
            pieGraph.setPadding(2);
        } else {
            pieGraph.setPadding(0);
        }

        // Trick to correct animation - add slice with value of total expenses sum
        slice = new PieSlice();
        slice.setValue(Expense.getSum(getActivity()).floatValue());
        slice.setGoalValue(0.0001f);
        slice.setColor(getResources().getColor(android.R.color.transparent));
        pieGraph.addSlice(slice);

        pieGraph.setInterpolator(new AccelerateDecelerateInterpolator());
        pieGraph.setDuration(1000);
        pieGraph.animateToGoalValues();
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
            mListener.onCategoryPieSelected(categories.get(position).id);
        } else {
            mListener.onCategoryPieSelected(0);
        }
        selected = position;
        updateText();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCategoryPieSelectedListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCategoryPieSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        pieGraph.cancelAnimating();
        mListener = null;
    }

    public interface OnCategoryPieSelectedListener {
        public void onCategoryPieSelected(long categoryId);
    }

    class AsyncGetCategories extends AsyncTask<Void, Void, List<Category>> {

        @Override
        protected List<Category> doInBackground(Void... voids) {
            return Category.getAll(getActivity());
        }

        @Override
        protected void onPostExecute(List<Category> categories1) {
            categories = categories1;
            categoriesAdapter.addAll(categories);
            updateGraph();
            updateText();
        }
    }
}
