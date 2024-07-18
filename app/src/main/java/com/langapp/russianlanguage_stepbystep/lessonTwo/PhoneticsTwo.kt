package com.langapp.russianlanguage_stepbystep.lessonTwo

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.langapp.russianlanguage_stepbystep.R
import com.langapp.russianlanguage_stepbystep.adapters.ExpandableAdapter
import com.langapp.russianlanguage_stepbystep.models.CollectionItem
import com.langapp.russianlanguage_stepbystep.models.ExpandableModel
import com.langapp.russianlanguage_stepbystep.utils.FirebaseContentReader

class PhoneticsTwo : Fragment() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var expandableAdapter: ExpandableAdapter
    private lateinit var dbRef: DatabaseReference
    private var phoneticsCount = 0
    private var phoneticsContentMap = mutableMapOf<String, List<Pair<String, String>>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_phonetics_one, container, false)

        recyclerView = view.findViewById(R.id.phoneticsRecycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        val dbPath = arguments?.getString("databasePath") ?: return null
        dbRef = FirebaseDatabase.getInstance().reference.child(dbPath)

        expandableAdapter = ExpandableAdapter(
            requireContext(),
            arrayListOf(),
            itemClickListener = { collectionItem ->
                mediaPlayer?.release()
                mediaPlayer = null

                mediaPlayer = MediaPlayer.create(requireContext(), Uri.parse(collectionItem.sound)).apply {
                    setOnCompletionListener { it.release() }
                    start()
                }
            }
        )

        recyclerView.adapter = expandableAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        readPhonetics()
    }

    private fun readPhonetics() {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val contentReader = FirebaseContentReader(dbRef)
                snapshot.children.forEach { phoneticsSnapshot ->
                    phoneticsCount++
                    contentReader.readPhoneticsContent(phoneticsSnapshot.key!!) {
                        phoneticsContentMap[phoneticsSnapshot.key.toString()] = it
                        if(phoneticsContentMap.size == phoneticsCount)
                            updateRecyclerView()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun updateRecyclerView() {

        val expList = listOf(
            R.layout.phonetics_two_1_dialog,
            R.layout.phonetics_two_2_dialog,
            R.layout.phonetics_two_3_dialog,
            R.layout.phonetics_two_4_dialog,
            R.layout.phonetics_two_5_dialog
        )

        val expandableModels = phoneticsContentMap.keys.map { phoneticsKey ->
            val contentItems = phoneticsContentMap[phoneticsKey]?.map { content ->
                val fullPath = "$phoneticsKey/$content"
                CollectionItem(fullPath, content.first, content.second)
            } ?: emptyList()

            ExpandableModel(contentItems, phoneticsKey, expList)
        }

        expandableAdapter.setData(expandableModels)
    }

    override fun onStop() {
        super.onStop()
        phoneticsContentMap.clear()
        phoneticsCount = 0
    }

}