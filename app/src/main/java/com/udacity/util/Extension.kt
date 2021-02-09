package com.udacity.util

import android.app.Activity
import android.content.res.Resources
import android.util.TypedValue
import android.widget.Toast

inline fun Activity.toast(message: Int): Toast = Toast
    .makeText(this, message, Toast.LENGTH_SHORT)
    .apply {
        show()
    }

fun Number.dpToPx(): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()
}

fun Number.spToPx(): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()
}