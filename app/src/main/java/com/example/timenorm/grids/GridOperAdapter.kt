package com.example.timenorm.grids

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.widget.AppCompatTextView
import com.example.timenorm.R

class GridOperAdapter(val context: Context, val list: List<String>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.id_layout, parent, false)

        val gridItem =
            view.findViewById<AppCompatTextView>(R.id.item_text_view) as AppCompatTextView

        gridItem.text = list.get(position)
        //gridItem.id = list.get(position)

        return view
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

}