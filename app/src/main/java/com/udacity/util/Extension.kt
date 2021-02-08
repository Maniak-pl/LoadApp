package com.udacity.util

import android.app.Activity
import android.widget.Toast

inline fun Activity.toast(message: Int): Toast = Toast
    .makeText(this, message, Toast.LENGTH_SHORT)
    .apply {
        show()
    }