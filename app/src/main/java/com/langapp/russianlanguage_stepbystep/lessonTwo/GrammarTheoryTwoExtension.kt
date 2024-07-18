package com.langapp.russianlanguage_stepbystep.lessonTwo

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.core.os.bundleOf
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

class GrammarTheoryTwoExtension : Fragment() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var expandableAdapter: ExpandableAdapter
    private lateinit var dbRef: DatabaseReference
    private var grammarCount = 0
    private var grammarContentMap = mutableMapOf<String, List<Pair<String, String>>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_grammar_two, container, false)

        recyclerView = view.findViewById(R.id.grammarRecycler)
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

                mediaPlayer =
                    MediaPlayer.create(requireContext(), Uri.parse(collectionItem.sound)).apply {
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
        readGrammar()
    }

    private fun readGrammar() {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { grammarSnapshot ->
                    grammarCount++
                    readGrammarContent(grammarSnapshot.key!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun readGrammarContent(grammar: String) {
        val grammarRef = dbRef.child(grammar)
        grammarRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val content = snapshot.children.map { Pair(it.key.toString(), it.value.toString()) }
                grammarContentMap[grammar] = content
                if(grammarContentMap.size == grammarCount)
                    updateRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun updateRecyclerView() {
        val expandableModels = grammarContentMap.keys.map { grammarKey ->
            val contentItems = grammarContentMap[grammarKey]?.map { content ->
                val fullPath = "$grammarKey/${content.first}"
                CollectionItem(fullPath, content.first, content.second)
            } ?: emptyList()

            ExpandableModel(contentItems, grammarKey)
        }

        expandableAdapter.setData(expandableModels)
    }


    override fun onStop() {
        super.onStop()
        grammarContentMap.clear()
        grammarCount = 0
    }
}