package com.venkatesh.topnewsapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.venkatesh.topnewsapp.items.view.ItemListingFragment


class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.container, ItemListingFragment.newInstance(),ItemListingFragment::class.java.name.toString()).addToBackStack(ItemListingFragment::class.java.name.toString()).commit()
    }
}
