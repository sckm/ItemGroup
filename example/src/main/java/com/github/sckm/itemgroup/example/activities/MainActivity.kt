package com.github.sckm.itemgroup.example.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.sckm.itemgroup.example.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        item_group_button.setOnClickListener {
            UpdateActivity.startActivity(context = this, useItemGroup = true)
        }

        section_button.setOnClickListener {
            UpdateActivity.startActivity(context = this, useItemGroup = false)
        }
    }
}