package com.langapp.russianlanguage_stepbystep.lessonFour

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.langapp.russianlanguage_stepbystep.R
import com.langapp.russianlanguage_stepbystep.adapters.VocabularyAdapter
import com.langapp.russianlanguage_stepbystep.models.ImageAndTextModel

class LexicsFour : Fragment() {

    private lateinit var vocabularyRecycler: RecyclerView
    private lateinit var dbRef: DatabaseReference
    private lateinit var vocabularyAdapter: VocabularyAdapter
    private var data: ArrayList<ImageAndTextModel> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lexics_one, container, false)

        vocabularyRecycler = view.findViewById(R.id.vocabularyRecycler)
        vocabularyRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        vocabularyRecycler.setHasFixedSize(true)

        val dbPath = arguments?.getString("databasePath") ?: return null
        dbRef = FirebaseDatabase.getInstance().reference.child(dbPath)

        readVocabularyData()

        vocabularyAdapter = VocabularyAdapter(data)
        vocabularyRecycler.adapter = vocabularyAdapter

        return view
    }

    private fun readVocabularyData() {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val icon = it.child("image").value.toString().toUri()
                    val sound = it.child("sound").value.toString()
                    val text = it.key.toString()

                    data.add(ImageAndTextModel(icon, text, sound))
                }

                vocabularyAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Ошибка чтения данных.\n Повторте попытку.", Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        data.clear()
        vocabularyAdapter.releaseMediaPlayer()
    }
}