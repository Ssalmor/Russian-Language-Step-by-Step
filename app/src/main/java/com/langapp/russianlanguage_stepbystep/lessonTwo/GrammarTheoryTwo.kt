package com.langapp.russianlanguage_stepbystep.lessonTwo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.langapp.russianlanguage_stepbystep.adapters.ChooseCategoryAdapter
import com.langapp.russianlanguage_stepbystep.adapters.ExpandableAdapter
import com.langapp.russianlanguage_stepbystep.models.CollectionItem
import com.langapp.russianlanguage_stepbystep.models.ExpandableModel

class GrammarTheoryTwo : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryAdapter: ChooseCategoryAdapter
    private lateinit var dbRef: DatabaseReference
    private var grammarCount = 0
    private var data: ArrayList<String> = arrayListOf()

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

        categoryAdapter = ChooseCategoryAdapter(arrayListOf()) { category ->
            dbPath = "/$dbPath/$category"

            if(category.contains("Род")) {
                val bundle = bundleOf("databasePath" to dbPath)
                view.findNavController().navigate(R.id.action_grammarTheoryTwo_to_grammarTheoryTwoExtension, bundle)
            } else {
                val bundle = bundleOf("databasePath" to dbPath)
                view.findNavController().navigate(R.id.action_grammarTheoryTwo_to_grammarTheoryTwoExtensionTwo, bundle)
            }
        }

        recyclerView.adapter = categoryAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        readGrammar()
    }

    private fun readGrammar() {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { data.add(it.key.toString()) }
                categoryAdapter.setData(data)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onStop() {
        super.onStop()
        data.clear()
        grammarCount = 0
    }

}