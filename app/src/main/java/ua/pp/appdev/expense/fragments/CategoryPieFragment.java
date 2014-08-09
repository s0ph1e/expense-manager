package ua.pp.appdev.expense.fragments;

import android.animation.Animator;
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

public class CategoryPieFragment extends Fragment {

    private PieGraph pieGraph;

    private ListView categoriesList;

    private View detailsView;

    private CategoryBaseSingleChoiceAdapter categoriesAdapter;

    private List<Category> categories;

    private int selected = -1;

    private OnCategoryPieSelectedListener mListener;

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
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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
        pieGraph.setAnimationListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                updateList();
                Animation animFadeIn = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_in);
                detailsView.setAnimation(animFadeIn);
                detailsView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        new AsyncGetCategories().execute();

        return view;
    }

    public void updateGraph(){
        pieGraph.removeSlices();
        PieSlice slice;

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
        }

        // Trick to correct animation - add slice with value of total expenses sum
        slice = new PieSlice();
        slice.setValue(Expense.getSum(getActivity()).floatValue());
        slice.setGoalValue(0);
        slice.setColor(getResources().getColor(android.R.color.transparent));
        pieGraph.addSlice(slice);

        pieGraph.setInterpolator(new AccelerateDecelerateInterpolator());
        pieGraph.setDuration(1000);
        pieGraph.animateToGoalValues();
    }

    public void updateList(){

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
        updateList();

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
            super.onPostExecute(categories);
            categories = categories1;
            updateGraph();
            categoriesAdapter.addAll(categories);
        }
    }
}
