package ua.pp.appdev.expense.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;

import java.util.Iterator;
import java.util.List;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.models.Category;
import ua.pp.appdev.expense.models.Expense;
import ua.pp.appdev.expense.utils.Log;

public class CategoryPieFragment extends Fragment {

    private static final String SELECTED_CATEGORY_POSITION = "selectedCategoryPosition";

    private PieGraph pieGraph;
    private List<Category> categories;

    private int selected = -1;
    private boolean needGraphRedraw = true;

    private OnCategoryPieSelectedListener mListener;

    public static CategoryPieFragment newInstance(List<Category> categories) {
        CategoryPieFragment fragment = new CategoryPieFragment();
        fragment.categories = categories;
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

        if(savedInstanceState != null){
            selected = savedInstanceState.getInt(SELECTED_CATEGORY_POSITION);
            needGraphRedraw = false;
        } else {
            needGraphRedraw = true;
        }

        View view =  inflater.inflate(R.layout.fragment_category_pie, container, false);

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

        updateGraph();

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
            slice.setValue(0);
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
        if(needGraphRedraw) {
            pieGraph.setDuration(1000);
        } else {
            pieGraph.setDuration(0);
            needGraphRedraw = false;
        }
        pieGraph.animateToGoalValues();
    }

    public void setSelected(int position){
        if(position == selected){
            return;
        } else if(position >= 0 && position < categories.size()){
            mListener.onCategoryPieSelected(categories.get(position).id);
        } else {
            mListener.onCategoryPieSelected(0);
        }
        selected = position;
    }

    public void setSelectedById(long categoryId) {
        Category cat = Category.getById(getActivity(), categoryId);
        int position = categories.indexOf(cat);
        selected = position;
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

}
