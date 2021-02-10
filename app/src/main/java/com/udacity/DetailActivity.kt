package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        okBtn.setOnClickListener {
            finish()
        }

        fileValue.text = intent.getStringExtra("fileName").toString()
        statusValue.text = intent.getStringExtra("status").toString()
    }
}
