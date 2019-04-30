package com.app.runners.utils;

import android.content.res.Resources;
import android.util.TypedValue;

import com.app.runners.R;

/**
 * Created by sergiocirasa on 14/9/17.
 */

public class ColorHelper {

    public static int getPrimaryColorFromTheme(Resources.Theme theme) {
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(R.attr.colorDefaultPrimary,typedValue,true);
        return typedValue.data;
    }
}
