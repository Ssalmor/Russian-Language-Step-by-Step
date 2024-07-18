package com.langapp.russianlanguage_stepbystep.lessonFour

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

class PhoneticsFour : Fragment() {

    private val translationsMap = mapOf(
        "dialog0" to "Диалог №1",
        "dialog1" to "Диалог №2",
        "dialog2" to "Диалог №3"
    )

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

        var dbPath = arguments?.getString("databasePath") ?: return null
        dbRef = FirebaseDatabase.getInstance().reference.child(dbPath)

        expandableAdapter = ExpandableAdapter(
            requireContext(),
            arrayListOf(),
            itemClickListener = { collectionItem ->
                if(dbRef.child(collectionItem.getOriginalKey()).key.toString().contains("dialog")) {
                    val actionId = resources.getIdentifier(
                        "action_phoneticsFour_to_phoneticsFourExtension",
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
        readPhonetics()
    }

    private fun readPhonetics() {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { phoneticsSnapshot ->
                    phoneticsCount++
                    readPhoneticsContent(phoneticsSnapshot.key!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Ошибка чтения данных.\n Повторте попытку.", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun readPhoneticsContent(phonetics: String) {
        val phoneticsRef = dbRef.child(phonetics)
        phoneticsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val content = snapshot.children.map { Pair(it.key.toString(), it.value.toString()) }
                phoneticsContentMap[phonetics] = content
                if(phoneticsContentMap.size == phoneticsCount)
                    updateRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Ошибка чтения данных.\n Повторте попытку.", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun updateRecyclerView() {

        val expList = listOf(
            R.layout.phonetics_two_2_dialog,
            R.layout.phonetics_four_2_dialog,
            R.layout.phonetics_four_3_dialog
        )

        val expandableModels = phoneticsContentMap.keys.map { phoneticsKey ->
            val contentItems = phoneticsContentMap[phoneticsKey]?.map { content ->
                val fullPath = "$phoneticsKey/${content.first}"
                CollectionItem(
                    originalKey = fullPath,
                    displayName = translationsMap[content.first] ?: content.first,
                    sound = content.second
                )
            } ?: emptyList()

            ExpandableModel(
                contentItems,
                phoneticsKey,
                explanationList = expList
            )
        }

        expandableAdapter.setData(expandableModels)
    }

    override fun onStop() {
        super.onStop()
        phoneticsContentMap.clear()
        phoneticsCount = 0
    }
}