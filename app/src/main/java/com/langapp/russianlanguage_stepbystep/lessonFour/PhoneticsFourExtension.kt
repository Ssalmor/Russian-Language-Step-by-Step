package com.langapp.russianlanguage_stepbystep.lessonFour

import android.os.Bundle
import android.util.Log
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

class PhoneticsFourExtension : Fragment() {

    private lateinit var phoneticsRecycler: RecyclerView
    private lateinit var dbRef: DatabaseReference
    private lateinit var phoneticsAdapter: DialogAdapter
    private var data: ArrayList<TextModel> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_phonetics_one, container, false)

        phoneticsRecycler = view.findViewById(R.id.phoneticsRecycler)
        phoneticsRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        phoneticsRecycler.setHasFixedSize(true)

        val dbPath = arguments?.getString("databasePath") ?: return null
        dbRef = FirebaseDatabase.getInstance().reference.child(dbPath)

        setData()

        phoneticsAdapter = DialogAdapter(requireContext(), data)
        phoneticsRecycler.adapter = phoneticsAdapter


        return view
    }

    private fun setData() {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val text = it.child("text").value.toString()
                    val sound = it.child("sound").value.toString()

                    data.add(TextModel(text, sound))
                }
                phoneticsAdapter.notifyDataSetChanged()
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