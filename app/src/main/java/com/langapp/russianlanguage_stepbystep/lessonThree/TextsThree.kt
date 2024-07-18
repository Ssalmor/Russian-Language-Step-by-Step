package com.langapp.russianlanguage_stepbystep.lessonThree

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.langapp.russianlanguage_stepbystep.R
import com.langapp.russianlanguage_stepbystep.adapters.TextsAdapter
import com.langapp.russianlanguage_stepbystep.adapters.VocabularyAdapter
import com.langapp.russianlanguage_stepbystep.models.ImageAndTextModel

class TextsThree : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dbRef: DatabaseReference
    private lateinit var textsAdapter: TextsAdapter
    private var data: ArrayList<ImageAndTextModel> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_texts_three, container, false)

        recyclerView = view.findViewById(R.id.textsRecycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        val dbPath = arguments?.getString("databasePath") ?: return null
        dbRef = FirebaseDatabase.getInstance().reference.child(dbPath)

        setData()

        textsAdapter = TextsAdapter(data)
        recyclerView.adapter = textsAdapter

        return view
    }

    private fun setData() {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val icon = it.child("image").value.toString().toUri()
                    val sound = it.child("sound").value.toString()
                    val text = it.child("text").value.toString().replace("\\n", "\n")

                    Log.d("Text", "$icon, $sound, $text")

                    data.add(ImageAndTextModel(icon, text, sound))
                }

                textsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        data.clear()
        textsAdapter.releaseMediaPlayer()
    }
}