package com.example.demo.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.demo.R

class ExpandListAdapter(
    private val context: Context,
    private val expandableListData: Map<String, List<String>>
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return expandableListData.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return expandableListData.values.elementAt(groupPosition).size
    }

    override fun getGroup(groupPosition: Int): Any {
        return expandableListData.keys.elementAt(groupPosition)
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return expandableListData.values.elementAt(groupPosition).elementAt(childPosition)
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val groupView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.group_layout, parent, false)

        val groupName = groupView.findViewById<TextView>(R.id.groupName)
        groupName.text = getGroup(groupPosition).toString()

        val arrow = groupView.findViewById<ImageView>(R.id.arrow)
        if (isExpanded) {
            arrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
        } else {
            arrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
        }

        return groupView
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val childView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.child_layout, parent, false)

        val childName = childView.findViewById<TextView>(R.id.childName)
        childName.text = getChild(groupPosition, childPosition).toString()

        return childView
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}