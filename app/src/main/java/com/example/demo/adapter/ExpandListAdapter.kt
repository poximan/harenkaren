package com.example.demo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.demo.R

class ExpandListAdapter(
    private val context: Context,
    private val expandableListData: Map<String, List<Pair<String, Int?>>>
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
        return expandableListData.values.elementAt(groupPosition)[childPosition]
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
            arrow.setImageResource(R.drawable.ic_arrow_up)
        } else {
            arrow.setImageResource(R.drawable.ic_arrow_down)
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

        val childData = getChild(groupPosition, childPosition) as Pair<String, Int?>
        val childName = childView.findViewById<TextView>(R.id.childName)
        val childImage = childView.findViewById<ImageView>(R.id.childImage)

        childName.text = childData.first

        if (childData.second != null) {
            childImage.setImageResource(childData.second!!)
            childImage.visibility = View.VISIBLE
        } else {
            childImage.setImageResource(0)
            childImage.visibility = View.GONE
        }

        childView.setOnClickListener {
            if (childImage.visibility == View.VISIBLE) {
                val imageScaleDown = AnimationUtils.loadAnimation(context, R.anim.ayuda_scale_out)
                val textExpand = AnimationUtils.loadAnimation(context, R.anim.ayuda_text_expand)

                imageScaleDown.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationEnd(animation: Animation?) {
                        childImage.visibility = View.GONE
                        childName.startAnimation(textExpand)
                    }
                    override fun onAnimationRepeat(animation: Animation?) {}
                })
                childImage.startAnimation(imageScaleDown)
            } else {
                val imageScaleUp = AnimationUtils.loadAnimation(context, R.anim.ayuda_scale_in)
                val textShrink = AnimationUtils.loadAnimation(context, R.anim.ayuda_text_shrink)

                textShrink.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationEnd(animation: Animation?) {
                        childImage.visibility = View.VISIBLE
                        childImage.startAnimation(imageScaleUp)
                    }
                    override fun onAnimationRepeat(animation: Animation?) {}
                })
                childName.startAnimation(textShrink)
            }
        }

        return childView
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}