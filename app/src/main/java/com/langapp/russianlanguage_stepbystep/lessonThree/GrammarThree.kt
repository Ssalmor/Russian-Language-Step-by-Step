package com.langapp.russianlanguage_stepbystep.lessonThree

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
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

class GrammarThree : Fragment() {

    private val translationsMap = mapOf(
        "dialog0" to "Диалог №1",
        "dialog1" to "Диалог №2",
        "dialog2" to "Диалог №3"
    )

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
        val view = inflater.inflate(R.layout.fragment_grammar_two, container, false)

        recyclerView = view.findViewById(R.id.grammarRecycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        var dbPath = arguments?.getString("databasePath") ?: return null
        dbRef = FirebaseDatabase.getInstance().reference.child(dbPath)

        expandableAdapter = ExpandableAdapter(
            requireContext(),
            arrayListOf(),
            itemClickListener = { collectionItem ->
                if(dbRef.child(collectionItem.getOriginalKey()).key.toString().contains("Диалог №")) {
                    val actionId = resources.getIdentifier(
                        "action_grammarThree_to_grammarThreeExtension",
                        "id",
                        requireContext().packageName
                    )

                    dbPath = "/$dbPath/${collectionItem.getOriginalKey()}"

                    if(actionId != 0) {
                        val bundle = bundleOf("databasePath" to dbPath)
                        view.findNavController().navigate(actionId, bundle)
                    }
                    else view.findNavController().navigate(R.id.action_lessons_to_defaultFragment)
                } else if(dbRef.child(collectionItem.getOriginalKey()).key.toString().contains("Текст №")) {
                    val actionId = resources.getIdentifier(
                        "action_grammarThree_to_grammarThreeExtensionTwo",
                        "id",
                        requireContext().packageName
                    )

                    dbPath = "/$dbPath/${collectionItem.getOriginalKey()}"

                    if(actionId != 0) {
                        val bundle = bundleOf("databasePath" to dbPath)
                        view.findNavController().navigate(actionId, bundle)
                    }
                    else view.findNavController().navigate(R.id.action_lessons_to_defaultFragment)
                } else if(collectionItem.sound.contains(".png")) return@ExpandableAdapter
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
        readGrammar()
    }

    private fun readGrammar() {
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

        val expList = listOf(
            R.layout.grammar_three_1_dialog,
            R.layout.grammar_three_2_dialog,
            R.layout.grammar_three_3_dialog,
            R.layout.grammar_three_4_dialog,
            R.layout.grammar_three_5_dialog
        )

        val expandableModels = grammarContentMap.keys.map { grammarKey ->
            val contentItems = grammarContentMap[grammarKey]?.map { content ->
                val fullPath = "$grammarKey/${content.first}"
                CollectionItem(
                    originalKey = fullPath,
                    displayName = translationsMap[content.first] ?: content.first,
                    sound = content.second,
                    answer = content.third
                )
            } ?: emptyList()

            ExpandableModel(contentItems, grammarKey, explanationList = expList)
        }

        expandableAdapter.setData(expandableModels)
    }

    override fun onStop() {
        super.onStop()
        grammarContentMap.clear()
        grammarCount = 0
    }

}