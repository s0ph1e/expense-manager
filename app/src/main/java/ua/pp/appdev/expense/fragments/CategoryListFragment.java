package ua.pp.appdev.expense.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.activities.SaveCategoryActivity;
import ua.pp.appdev.expense.helpers.CategoryAdapter;
import ua.pp.appdev.expense.helpers.UnchangeableSizeListView;
import ua.pp.appdev.expense.models.Category;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CategoryListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CategoryListFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class CategoryListFragment extends Fragment {

    public String LOG_TAG = "CAT_LIST";

    private final int CATEGORY_ADD = 0;
    private final int CATEGORY_EDIT = 1;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private CategoryAdapter categoryListAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoryListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoryListFragment newInstance(String param1, String param2) {
        CategoryListFragment fragment = new CategoryListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public CategoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final ListView categoryList = new UnchangeableSizeListView(getActivity());
        categoryList.setId(R.id.category_list);
        /*
        categoryList.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        */

        // Set scrollbar always shown
        categoryList.setScrollbarFadingEnabled(false);

        // Create contextual action mode (edit-remove categories)
        categoryList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        categoryList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                actionMode.invalidate();
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                    case R.id.actionbar_edit:
                        editSelected();
                        mode.finish();
                        return true;
                    case R.id.actionbar_remove:
                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.remove_category)
                                .setMessage(R.string.remove_category_message)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        removeSelected();
                                        mode.finish();
                                    }
                                })
                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .show();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.context, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are deselected/unchecked.
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                final int checked = categoryList.getCheckedItemCount();
                MenuItem editBtn = menu.findItem(R.id.actionbar_edit);
                boolean singleItemChecked = (checked == 1);
                editBtn.setVisible(singleItemChecked).setEnabled(singleItemChecked);
                return true;
            }

            public void removeSelected() {
                SparseBooleanArray checked = categoryList.getCheckedItemPositions();
                int len = categoryList.getCount();
                // Needed DESC order, otherwise unchecked items may be deleted instead of checked
                for (int i = len - 1; i >= 0; i--) {
                    if (checked.get(i)) {
                        CategoryAdapter adapter = (CategoryAdapter) ((HeaderViewListAdapter) categoryList.getAdapter()).getWrappedAdapter();
                        Category cat = adapter.getItem(i);
                        cat.remove(getActivity());
                        adapter.remove(cat);
                    }
                }
            }

            public void editSelected() {
                SparseBooleanArray checked = categoryList.getCheckedItemPositions();
                int checkedCount = checked.size();
                if (checkedCount != 1) {
                    Log.wtf(LOG_TAG, "Got multiple items, but only one can be edited");
                } else {
                    int key = checked.keyAt(0);
                    Category category = categoryListAdapter.getItem(key);
                    Intent i = new Intent(getActivity(), SaveCategoryActivity.class);
                    i.putExtra("category", category);
                    startActivityForResult(i, CATEGORY_EDIT);
                }
            }
        });

        // Create button for new category and put it to the end of listview
        //final Button btnAddNew = new Button(getActivity());
        final View btnAddNew = inflater.inflate(R.layout.transparent_button, null);
        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), SaveCategoryActivity.class);
                startActivityForResult(i, CATEGORY_ADD);
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

        Category newCat = (Category) data.getSerializableExtra("category");

        switch (requestCode){
            case CATEGORY_ADD:
                categoryListAdapter.add(newCat);
                categoryListAdapter.setSelected(categoryListAdapter.getCount() - 1);
                break;
            case CATEGORY_EDIT:
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
        if (categoryPos > 0) {
            categoryListAdapter.setSelected(categoryPos);
            return true;
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