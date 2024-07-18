package com.langapp.russianlanguage_stepbystep.lessonTwo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.langapp.russianlanguage_stepbystep.utils.FirebaseContentReader

class LexicsTwo : Fragment() {

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

        val contentReader = FirebaseContentReader(dbRef)
        contentReader.readVocabularyContent {
            data = it
            vocabularyAdapter = VocabularyAdapter(data)
            vocabularyRecycler.adapter = vocabularyAdapter
        }


        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        data.clear()
        vocabularyAdapter.releaseMediaPlayer()
    }
}