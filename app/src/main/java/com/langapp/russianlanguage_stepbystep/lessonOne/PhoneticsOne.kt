package com.langapp.russianlanguage_stepbystep.lessonOne

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

class PhoneticsOne : Fragment() {

    private val translationsMap = mapOf(
        "vowels" to "Гласные",
        "consonants" to "Согласные"
    )

    private lateinit var recyclerView: RecyclerView
    private lateinit var expandableAdapter: ExpandableAdapter
    private lateinit var dbRef: DatabaseReference
    private var phoneticsCount = 0
    private var phoneticsContentMap = mutableMapOf<String, List<String>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_phonetics_one, container, false)

        recyclerView = view.findViewById(R.id.phoneticsRecycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        var dbPath = arguments?.getString("databasePath") ?: return null
        dbRef = FirebaseDatabase.getInstance().reference.child(dbPath)

        expandableAdapter = ExpandableAdapter(
            requireContext(),
            arrayListOf(),
            itemClickListener = {collectionItem ->

                val actionId = resources.getIdentifier(
                    "action_phoneticsOne_to_phoneticsOneExtension",
                    "id",
                    requireContext().packageName
                )

                dbPath = "/$dbPath/${collectionItem.getOriginalKey()}"

                if(actionId != 0) {
                    val bundle = bundleOf("databasePath" to dbPath)
                    view.findNavController().navigate(actionId, bundle)
                }
                else view.findNavController().navigate(R.id.action_lessons_to_defaultFragment)
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
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { phoneticsSnapshot ->
                    phoneticsCount++
                    readPhoneticsContent(phoneticsSnapshot.key!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun readPhoneticsContent(phonetics: String) {
        val phoneticsRef = dbRef.child(phonetics)
        phoneticsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val content = snapshot.children.map { it.key ?: "" }
                phoneticsContentMap[phonetics] = content
                if(phoneticsContentMap.size == phoneticsCount)
                    updateRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun updateRecyclerView() {

        val priorityMap = mapOf(
            "vowels" to 1,
            "consonants" to 2
        )

        fun getPriority(item: String): Int {
            return priorityMap[item] ?: 3
        }

        val sortedPhonetics = phoneticsContentMap.keys.sortedBy { getPriority(it) }
        val expandableModels = sortedPhonetics.map { phoneticsKey ->
            val contentItems = phoneticsContentMap[phoneticsKey]?.map { content ->
                val fullPath = "$phoneticsKey/$content"
                CollectionItem(fullPath, content)
            } ?: emptyList()

            ExpandableModel(contentItems, translationsMap[phoneticsKey] ?: phoneticsKey)
        }

        expandableAdapter.setData(expandableModels)
    }

    override fun onStop() {
        super.onStop()
        phoneticsContentMap.clear()
        phoneticsCount = 0
    }

}