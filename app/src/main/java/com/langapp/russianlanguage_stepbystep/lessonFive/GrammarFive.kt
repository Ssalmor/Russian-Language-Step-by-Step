package com.langapp.russianlanguage_stepbystep.lessonFive

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
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

class GrammarFive : Fragment() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var expandableAdapter: ExpandableAdapter
    private lateinit var dbRef: DatabaseReference
    private var grammarCount = 0
    private var grammarContentMap = mutableMapOf<String, List<Triple<String, String, String>>>()

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
                if(collectionItem.sound.contains(".png")) return@ExpandableAdapter
                else {
                    mediaPlayer?.release()
                    mediaPlayer = null

                    mediaPlayer =
                        MediaPlayer.create(requireContext(), Uri.parse(collectionItem.sound)).apply {
                            setOnCompletionListener { it.release() }
                            start()
                        }
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
                snapshot.children.forEach { grammarSnapshot ->
                    grammarCount++
                    if(grammarSnapshot.value.toString().contains("image"))
                        contentReader.readGrammarImagesContent(grammarSnapshot.key!!) {
                            grammarContentMap[grammarSnapshot.key.toString()] = it
                            if(grammarContentMap.size == grammarCount) updateRecyclerView()
                        }
                    else if(grammarSnapshot.value.toString().contains("text"))
                        contentReader.readGrammarContainTextContent(grammarSnapshot.key!!) {
                            grammarContentMap[grammarSnapshot.key.toString()] = it
                            if(grammarContentMap.size == grammarCount) updateRecyclerView()
                        }
                    else
                        contentReader.readGrammarRegularContent(grammarSnapshot.key!!) {
                            grammarContentMap[grammarSnapshot.key.toString()] = it
                            if(grammarContentMap.size == grammarCount) updateRecyclerView()
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun updateRecyclerView() {

//        val expList = listOf(
//            0,
//            0,
//            0,
//            R.layout.phonetics_two_4_dialog,
//            R.layout.phonetics_two_5_dialog
//        )

        val expandableModels = grammarContentMap.keys.map { phoneticsKey ->
            val contentItems = grammarContentMap[phoneticsKey]?.map { content ->
                val fullPath = "$phoneticsKey/${content.first}"
                CollectionItem(
                    originalKey = fullPath,
                    displayName = content.first,
                    sound = content.second,
                    answer = content.third
                )
            } ?: emptyList()

            ExpandableModel(contentItems, phoneticsKey)
        }

        expandableAdapter.setData(expandableModels)
    }

    override fun onStop() {
        super.onStop()
        grammarContentMap.clear()
        grammarCount = 0
    }

}