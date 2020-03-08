package com.shevelev.android_example_carousel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val itemAdapter by lazy { CarouselAdapter() }

    private val items = listOf(
        CarouselListItem("Argentina", R.drawable.ic_argentina),
        CarouselListItem("EU", R.drawable.ic_eu),
        CarouselListItem("France", R.drawable.ic_france),
        CarouselListItem("Russia", R.drawable.ic_russia),
        CarouselListItem("Spain", R.drawable.ic_spain),
        CarouselListItem("UK", R.drawable.ic_uk),
        CarouselListItem("USA", R.drawable.ic_usa),
        CarouselListItem("Ukraine", R.drawable.ic_ukraine)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        item_list.initialize(itemAdapter)
        itemAdapter.setItems(items)

        item_list.setOnItemSelectedListener { itemId ->
            selectedItemId.text = itemId
        }

        item_list.scrollToStartIndex(6)
    }
}

