package com.harishsk.saveblood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.harishsk.saveblood.R
import com.harishsk.saveblood.model.Blood

class ListViewAdapter internal constructor(
    private val activity: AppCompatActivity?,
    private val BlList: List<Blood>
) : BaseAdapter() {
    override fun getCount(): Int {
        return BlList.size
    }

    override fun getItem(position: Int): Any {
        return BlList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(
        position: Int,
        convertView: View,
        parent: ViewGroup
    ): View {
        val inflater = activity!!.baseContext
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemview = inflater.inflate(R.layout.list_adapter_data, null)
        val name = itemview.findViewById<TextView>(R.id.lt_name)
        val gender = itemview.findViewById<TextView>(R.id.lt_gender)
        val age = itemview.findViewById<TextView>(R.id.lt_age)
        val blgrp = itemview.findViewById<TextView>(R.id.lt_blgrp)
        val phno = itemview.findViewById<TextView>(R.id.lt_phno)
        val place = itemview.findViewById<TextView>(R.id.lt_place)
        name.text = BlList[position].name
        gender.text = BlList[position].gender
        age.text = BlList[position].age
        blgrp.text = BlList[position].blgrp
        phno.text = BlList[position].phno
        place.text = BlList[position].place
        return itemview
    }

}