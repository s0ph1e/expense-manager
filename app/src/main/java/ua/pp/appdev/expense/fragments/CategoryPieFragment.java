package ua.pp.appdev.expense.fragments;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.helpers.Helpers;
import ua.pp.appdev.expense.helpers.SharedPreferencesHelper;
import ua.pp.appdev.expense.models.Category;
import ua.pp.appdev.expense.models.Expense;

public class CategoryPieFragment extends Fragment {

    private PieGraph pieGraph;

    private View view;

    private View descriptionView;

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

        view =  inflater.inflate(R.layout.fragment_category_pie, container, false);
        descriptionView = view.findViewById(R.id.overviewDescriptionLayout);
        pieGraph = (PieGraph) view.findViewById(R.id.categoriesPieGraph);
        pieGraph.setInnerCircleRatio(150);
        pieGraph.setPadding(5);
        pieGraph.setAnimationListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                updateText();
                Animation animFadeIn = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_in);
                descriptionView.setAnimation(animFadeIn);
                descriptionView.setVisibility(View.VISIBLE);
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

    public void updateText(){
        Context context = getActivity();
        String categoryName = "", categoryFirstLetter = "", expensesSumString = "";
        BigDecimal totalSum = Expense.getSum(context);
        BigDecimal expensesSum = new BigDecimal(BigInteger.ZERO);
        Category cat;
        int expensesCount = 0, color = 0;

        if(selected < 0){
            categoryName = getResources().getString(R.string.all_categories);
            expensesCount = Expense.getCount(context);
            expensesSum = Expense.getSum(context);
        } else if(selected < categories.size()){
            cat = categories.get(selected);
            categoryName = cat.name;
            categoryFirstLetter = String.valueOf(cat.name.charAt(0)).toUpperCase();
            expensesCount = cat.getExpensesCount(context);
            expensesSum = cat.getExpensesSum(context);
            color = cat.color;
        }
        expensesSumString = Helpers.sumToString(expensesSum, SharedPreferencesHelper.getBaseCurrency(context));

        ((TextView) view.findViewById(R.id.txtCategoryName)).setText(categoryName);

        TextView categoryIconView = (TextView) view.findViewById(R.id.txtCategoryColor);
        if(categoryFirstLetter.isEmpty()){
            categoryIconView.setVisibility(View.GONE);
        } else {
            categoryIconView.setVisibility(View.VISIBLE);
            categoryIconView.setText(categoryFirstLetter);
            categoryIconView.setBackgroundColor(color);
        }

        ((TextView) view.findViewById(R.id.txtCategoryPercent)).setText(Helpers.percentageString(expensesSum, totalSum));
        ((TextView) view.findViewById(R.id.txtCategoryTotalExpensesCount)).setText(String.valueOf(expensesCount));
        ((TextView) view.findViewById(R.id.txtCategoryTotalSum)).setText(expensesSumString);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnCategoryPieSelectedListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnCategoryPieSelectedListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnCategoryPieSelectedListener {
        public void onCategoryPieSelected();
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
        }
    }
}
