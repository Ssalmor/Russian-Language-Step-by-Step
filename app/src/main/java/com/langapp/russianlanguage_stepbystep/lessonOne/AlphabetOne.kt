package com.langapp.russianlanguage_stepbystep.lessonOne

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.langapp.russianlanguage_stepbystep.R
import com.langapp.russianlanguage_stepbystep.adapters.RVGridAdapter
import com.langapp.russianlanguage_stepbystep.models.ImageAndTextModel
import java.text.Collator
import java.util.Locale

class AlphabetOne : Fragment() {

    private lateinit var alphabetRecycler: RecyclerView
    private lateinit var dbRef: DatabaseReference
    private lateinit var gridAdapter: RVGridAdapter
    private var data: ArrayList<ImageAndTextModel> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        postponeEnterTransition()

        val view = inflater.inflate(R.layout.fragment_alphabet_one, container, false)

        alphabetRecycler = view.findViewById(R.id.alphabetRecycler)
        alphabetRecycler.layoutManager = GridLayoutManager(requireContext(), 3, LinearLayoutManager.VERTICAL, false)
        alphabetRecycler.setHasFixedSize(true)

        val dbPath = arguments?.getString("databasePath") ?: return null
        dbRef = FirebaseDatabase.getInstance().reference.child(dbPath)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gridAdapter = RVGridAdapter(data)
        alphabetRecycler.adapter = gridAdapter

        readData {
            startPostponedEnterTransition()
        }
    }

    private fun readData(onDataLoaded: () -> Unit) {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                data.clear()
                val collator = Collator.getInstance(Locale("ru","RU"))
                collator.strength = Collator.PRIMARY // Игнорировать регистр и акценты
                snapshot.children.map { child ->
                    val icon = child.child("Image").value.toString().toUri()
                    val text = child.child("Word").value.toString()
                    val sound = child.child("Sound").value.toString()
                    ImageAndTextModel(icon, text, sound)
                }.sortedWith(Comparator { o1, o2 ->
                    collator.compare(o1.text, o2.text)
                }).also { sortedList ->
                    data.addAll(sortedList)
                }

                gridAdapter.notifyDataSetChanged()
                onDataLoaded()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Ошибка чтения данных.\n Повторте попытку.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        data.clear()
        gridAdapter.releaseMediaPlayer()
    }
}