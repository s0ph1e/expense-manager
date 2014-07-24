package ua.pp.appdev.expense.helpers;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Magic for preventing listviews' resize when keyboard appears
 */
public class UnchangeableSizeListView extends ListView implements View.OnTouchListener{

    private int minRowsCount = 2;

    public UnchangeableSizeListView(Context context) {
        super(context);
        setOnTouchListener(this);
        //setOnScrollListener(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int minHeight = minRowsCount * getRowHeight();
        //Log.e("", "getLayoutParams().height " + getLayoutParams().height );
        //Log.e("", "getHeight() " + getHeight());
        //Log.e("", "minHeight " + minHeight);
        if(getHeight() < minHeight){
            Log.e("", getHeight() + " < " + minHeight);
            getLayoutParams().height = minHeight;
            requestLayout();
        } else {
            getLayoutParams().height = getHeight();
            requestLayout();
        }
    }

    public int getRowHeight(){
        ListAdapter mAdapter = getAdapter();

        if (mAdapter.getCount() > 0) {
            View mView = mAdapter.getView(0, null, this);
            mView.measure(
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),

                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            return mView.getMeasuredHeight();
        } else
            return 0;
    }

    public int getTotalHeight() {

        ListAdapter mAdapter = getAdapter();

        int totalHeight = 0;

        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, this);

            mView.measure(
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),

                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

            totalHeight += mView.getMeasuredHeight();
            Log.w("HEIGHT" + i, String.valueOf(totalHeight));

        }
        return  totalHeight;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //Log.e("", "getHeight()=" + getHeight());
        //Log.e("", "((LinearLayout)this.getParent()).getHeight()=" + ((LinearLayout)this.getParent()).getHeight());
        if (getTotalHeight() > ((LinearLayout)this.getParent()).getHeight()/* && (getLastVisiblePosition())< getCount() - 1*/) {
            view.getParent().requestDisallowInterceptTouchEvent(true);
        } else {
            view.getParent().requestDisallowInterceptTouchEvent(false);
        }

        return false;
    }
}
