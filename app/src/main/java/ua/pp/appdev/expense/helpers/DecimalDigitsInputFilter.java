package ua.pp.appdev.expense.helpers;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by:
 * Ilya Antipenko <ilya@antipenko.pp.ua>
 * Sophia Nepochataya <sophia@nepochataya.pp.ua>
 */
public class DecimalDigitsInputFilter implements InputFilter {
    int mDigitsAfterPoint;

    public DecimalDigitsInputFilter(int digitsAfterPoint) {
        mDigitsAfterPoint = digitsAfterPoint;

    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        int dotPos = dest.toString().indexOf(".");

        if (dotPos >= 0) {

            // protects against many dots
            if (source.equals(".") || source.equals(","))
            {
                return "";
            }
            // if the text is entered before the dot
            if (dend <= dotPos) {
                return null;
            }
            if (dest.length() - dotPos > mDigitsAfterPoint) {
                return "";
            }
        }

        return null;
    }
}
