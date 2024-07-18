package com.langapp.russianlanguage_stepbystep.utils

import android.view.DragEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.langapp.russianlanguage_stepbystep.R
import com.langapp.russianlanguage_stepbystep.adapters.DragAndDropAdapter

class DragListener(listener: Listener) : View.OnDragListener {
    private var isDropped = false
    private val listener: Listener

    init {
        this.listener = listener
    }

    override fun onDrag(view: View, event: DragEvent): Boolean {
        if (event.action == DragEvent.ACTION_DROP) {
            isDropped = true
            var positionTarget = -1
            val viewSource = event.localState as View
            val viewId = view.id
            val cvItem: Int = R.id.cardView
            val tvEmptyListHe: Int = R.id.tvEmptyListHe
            val tvEmptyListShe: Int = R.id.tvEmptyListShe
            val tvEmptyListIt: Int = R.id.tvEmptyListIt
            val rvHe: Int = R.id.rvHe
            val rvShe: Int = R.id.rvShe
            val rvIt: Int = R.id.rvIt
            when (viewId) {
                cvItem, tvEmptyListHe, tvEmptyListShe, tvEmptyListIt, rvHe, rvShe, rvIt -> {
                    val target: RecyclerView
                    when (viewId) {
                        tvEmptyListHe, rvHe -> target =
                            view.rootView.findViewById<View>(rvHe) as RecyclerView

                        tvEmptyListShe, rvShe -> target =
                            view.rootView.findViewById<View>(rvShe) as RecyclerView

                        tvEmptyListIt, rvIt -> target =
                            view.rootView.findViewById<View>(rvIt) as RecyclerView

                        else -> {
                            target = view.parent as RecyclerView
                            positionTarget = view.tag as Int
                        }
                    }
                    if (viewSource != null) {
                        val source = viewSource.parent as RecyclerView

                        val adapterSource: DragAndDropAdapter? = source.adapter as DragAndDropAdapter?
                        val positionSource = viewSource.tag as Int
                        val sourceId = source.id

                        val list: String = adapterSource!!.list[positionSource]
                        val listSource = adapterSource.list as MutableList<String>

                        listSource.removeAt(positionSource)
                        adapterSource.updateList(listSource)
                        adapterSource.notifyDataSetChanged()

                        val adapterTarget: DragAndDropAdapter? = target.adapter as DragAndDropAdapter?
                        val customListTarget = adapterTarget!!.list as MutableList<String>
                        if (positionTarget >= 0) {
                            customListTarget.add(positionTarget, list)
                        } else {
                            customListTarget.add(list)
                        }
                        adapterTarget.updateList(customListTarget)
                        adapterTarget.notifyDataSetChanged()
                    }
                }
            }
        }
        if (!isDropped && event.localState != null) {
            (event.localState as View).visibility = View.VISIBLE
        }
        return true
    }
}