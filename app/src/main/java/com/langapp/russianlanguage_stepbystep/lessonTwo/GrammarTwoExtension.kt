package com.langapp.russianlanguage_stepbystep.lessonTwo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.langapp.russianlanguage_stepbystep.R
import com.langapp.russianlanguage_stepbystep.adapters.DialogAdapter
import com.langapp.russianlanguage_stepbystep.models.TextModel

class GrammarTwoExtension : Fragment() {

    private lateinit var grammarRecycler: RecyclerView
    private lateinit var dbRef: DatabaseReference
    private lateinit var grammarAdapter: DialogAdapter
    private var data: ArrayList<TextModel> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_grammar_two_extension, container, false)

        grammarRecycler = view.findViewById(R.id.grammarRecycler)
        grammarRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        grammarRecycler.setHasFixedSize(true)

        val dbPath = arguments?.getString("databasePath") ?: return null
        dbRef = FirebaseDatabase.getInstance().reference.child(dbPath)

        setData()

        grammarAdapter = DialogAdapter(requireContext(), data)
        grammarRecycler.adapter = grammarAdapter

        return view
    }

    private fun setData() {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val sound = it.child("sound").value.toString()
                    val text = it.child("text").value.toString()

                    data.add(TextModel(text = text, sound = sound))
                }

                grammarAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        data.clear()
    }

}