package com.langapp.russianlanguage_stepbystep.lessonTwo

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.core.net.toUri
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

class GrammarTheoryTwoExtensionTwo : Fragment() {

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

        if(dbPath.contains("Неодушевлённые"))
            showDialog(R.layout.grammar_theory_two_1_dialog)
        else showDialog(R.layout.grammar_theory_two_2_dialog)

        setData()

        vocabularyAdapter = VocabularyAdapter(data)
        vocabularyRecycler.adapter = vocabularyAdapter

        return view
    }

    private fun showDialog(layout: Int) {

        val dialogInflater = LayoutInflater.from(context).inflate(layout, null)

        val dialog = Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(dialogInflater)
            setCancelable(true)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }

        dialogInflater.findViewById<Button>(R.id.ok_button).setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun setData() {
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
                TODO("Not yet implemented")
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        data.clear()
        vocabularyAdapter.releaseMediaPlayer()
    }
}