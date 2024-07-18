package com.langapp.russianlanguage_stepbystep.lessonThree

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.langapp.russianlanguage_stepbystep.R

class GrammarThreeExtensionTwo : Fragment() {

    private val spinnerOptions = arrayOf("мой", "моя", "моё", "меня", "его", "её", "вас")
    private var parts: List<String> = listOf()
    private lateinit var dbRef: DatabaseReference
    private lateinit var layout: ConstraintLayout
    private var prevView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_grammar_three_extension_two, container, false)

        val dbPath = arguments?.getString("databasePath") ?: return null
        dbRef = FirebaseDatabase.getInstance().reference.child(dbPath)

        setData()

        return view
    }

    private fun setData() {
//        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                layout = view?.findViewById(R.id.constraint) ?: return
//
//                // Очистить layout перед добавлением новых view.
//                layout.removeAllViews()
//
//                val textWithBlanks = snapshot.value.toString()
//                val parts = textWithBlanks.split("___")
//
//                parts.forEachIndexed { index, part ->
//                    // Создаем TextView для текста.
//                    val textView = TextView(requireContext()).apply {
//                        id = View.generateViewId()
//                        text = part
//                    }
//                    layout.addView(textView)
//
//                    // Настраиваем ограничения для TextView
//                    val textParams = textView.layoutParams as ConstraintLayout.LayoutParams
//                    textParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
//                    textParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
//                    textParams.startToEnd = prevView?.id ?: ConstraintLayout.LayoutParams.PARENT_ID
//                    textView.layoutParams = textParams
//
//                    prevView = textView // Обновляем предыдущий вид для следующего элемента.
//
//                    // Создаем Spinner для пропусков, если это не последний элемент.
//                    if (index < parts.size - 1) {
//                        val spinner = Spinner(requireContext()).apply {
//                            id = View.generateViewId()
//                            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerOptions).also { adapter ->
//                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                            }
//                        }
//                        layout.addView(spinner)
//
//                        // Настраиваем ограничения для Spinner
//                        val spinnerParams = spinner.layoutParams as ConstraintLayout.LayoutParams
//                        spinnerParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
//                        spinnerParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
//                        spinnerParams.startToEnd = textView.id
//                        if (index == parts.size - 2) {
//                            spinnerParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
//                        }
//                        spinner.layoutParams = spinnerParams
//
//                        prevView = spinner // Обновляем предыдущий вид для следующего элемента.
//                    }
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })
    }

//    private fun insertSpinnersInText(textWithBlanks: String) {
//        val constraintLayout = view?.findViewById<ConstraintLayout>(R.id.constraint) ?: return
//        constraintLayout.removeAllViews()
//
//        val parts = textWithBlanks.split("___")
//        var prevViewId = ConstraintLayout.LayoutParams.PARENT_ID // Начинаем с родительского ID
//
//        parts.forEachIndexed { index, part ->
//            val horizontalLayout = LinearLayout(requireContext()).apply {
//                id = View.generateViewId()
//                orientation = LinearLayout.HORIZONTAL
//            }
//            constraintLayout.addView(horizontalLayout)
//
//            // Настраиваем ограничения для horizontalLayout
//            val horizontalConstraints = ConstraintLayout.LayoutParams(
//                ConstraintLayout.LayoutParams.MATCH_PARENT,
//                ConstraintLayout.LayoutParams.WRAP_CONTENT
//            )
//            horizontalConstraints.topToBottom = prevViewId
//            horizontalLayout.layoutParams = horizontalConstraints
//
//            // Создаем и добавляем TextView
//            val textView = TextView(requireContext()).apply {
//                text = part
//            }
//            horizontalLayout.addView(textView)
//
//            // Если это не последний элемент, создаем и добавляем Spinner
//            if (index < parts.size - 1) {
//                val spinner = Spinner(requireContext()).apply {
//                    adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerOptions).also { adapter ->
//                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                    }
//                }
//                horizontalLayout.addView(spinner)
//            }
//
//            // Обновляем prevViewId для следующего horizontalLayout
//            prevViewId = horizontalLayout.id
//        }
//    }
}