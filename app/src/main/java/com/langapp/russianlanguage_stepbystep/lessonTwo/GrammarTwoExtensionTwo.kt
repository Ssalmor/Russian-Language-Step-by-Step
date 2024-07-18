package com.langapp.russianlanguage_stepbystep.lessonTwo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.langapp.russianlanguage_stepbystep.R
import com.langapp.russianlanguage_stepbystep.adapters.DragAndDropAdapter
import com.langapp.russianlanguage_stepbystep.adapters.PhoneticsAdapter
import com.langapp.russianlanguage_stepbystep.models.TextModel
import com.langapp.russianlanguage_stepbystep.utils.Listener

class GrammarTwoExtensionTwo : Fragment(), Listener {

    private lateinit var tvEmptyListHe: TextView
    private lateinit var tvEmptyListShe: TextView
    private lateinit var tvEmptyListIt: TextView
    private lateinit var rvHe: RecyclerView
    private lateinit var rvShe: RecyclerView
    private lateinit var rvIt: RecyclerView
    private lateinit var prevBtn: Button

    private var data: MutableList<String> = mutableListOf()
    private lateinit var dbRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_grammar_two_extension_two, container, false)

        tvEmptyListHe = view.findViewById(R.id.tvEmptyListHe)
        tvEmptyListShe = view.findViewById(R.id.tvEmptyListShe)
        tvEmptyListIt = view.findViewById(R.id.tvEmptyListIt)
        rvHe = view.findViewById(R.id.rvHe)
        rvIt = view.findViewById(R.id.rvIt)
        rvShe = view.findViewById(R.id.rvShe)


        prevBtn = view.findViewById(R.id.check_btn)

        val dbPath = arguments?.getString("databasePath") ?: return null
        dbRef = FirebaseDatabase.getInstance().reference.child(dbPath)

        readData(dbRef, data)

        tvEmptyListHe.visibility = View.GONE
        tvEmptyListShe.visibility = View.GONE
        tvEmptyListIt.visibility = View.GONE

        return view
    }

    private fun setHeRecyclerView(data: MutableList<String>) {
        rvHe.setHasFixedSize(true)
        rvHe.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val heList = data.chunked(data.size / 3 + data.size % 3)[0]

        val topListAdapter = DragAndDropAdapter(heList, this)
        rvHe.adapter = topListAdapter
        tvEmptyListHe.setOnDragListener(topListAdapter.dragInstance)
        rvHe.setOnDragListener(topListAdapter.dragInstance)
    }

    private fun setSheRecyclerView(data: MutableList<String>) {
        rvShe.setHasFixedSize(true)
        rvShe.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val sheList = data.chunked(data.size / 3 + data.size % 3)[1]

        val bottomListAdapter = DragAndDropAdapter(sheList, this)
        rvShe.adapter = bottomListAdapter
        tvEmptyListShe.setOnDragListener(bottomListAdapter.dragInstance)
        rvShe.setOnDragListener(bottomListAdapter.dragInstance)
    }

    private fun setItRecyclerView(data: MutableList<String>) {
        rvIt.setHasFixedSize(true)
        rvIt.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val itList = data.chunked(data.size / 3 + data.size % 3)[2]

        val bottomListAdapter = DragAndDropAdapter(itList, this)
        rvIt.adapter = bottomListAdapter
        tvEmptyListIt.setOnDragListener(bottomListAdapter.dragInstance)
        rvIt.setOnDragListener(bottomListAdapter.dragInstance)
    }

    private fun RecyclerView.init(list: List<String>, emptyTextView: TextView) {
        this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val adapter = DragAndDropAdapter(list, this@GrammarTwoExtensionTwo)
        this.adapter = adapter
        emptyTextView.setOnDragListener(adapter.dragInstance)
        this.setOnDragListener(adapter.dragInstance)
    }

    private fun readData(databaseReference: DatabaseReference, data: MutableList<String>) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                data.clear()
                processSnapshot(snapshot, data)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Phonetics", "Failed to read value.", error.toException())
            }
        })
    }

    private fun processSnapshot(dataSnapshot: DataSnapshot, data: MutableList<String>) {
        for (childSnapshot in dataSnapshot.children) {
            val branchName = childSnapshot.key
            if (branchName != null) {
                data.add(branchName)
            }
        }

        setHeRecyclerView(data)
        setSheRecyclerView(data)
        setItRecyclerView(data)
    }

    override fun setEmptyListHe(visibility: Boolean) {
        tvEmptyListHe.visibility = if (visibility) View.VISIBLE else View.GONE
        rvHe.visibility = if (visibility) View.GONE else View.VISIBLE
    }

    override fun setEmptyListIt(visibility: Boolean) {
        tvEmptyListIt.visibility = if (visibility) View.VISIBLE else View.GONE
        rvIt.visibility = if (visibility) View.GONE else View.VISIBLE
    }

    override fun setEmptyListShe(visibility: Boolean) {
        tvEmptyListShe.visibility = if (visibility) View.VISIBLE else View.GONE
        rvShe.visibility = if (visibility) View.GONE else View.VISIBLE
    }

}