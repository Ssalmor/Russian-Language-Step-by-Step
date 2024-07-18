package com.langapp.russianlanguage_stepbystep

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.langapp.russianlanguage_stepbystep.adapters.ExpandableAdapter
import com.langapp.russianlanguage_stepbystep.models.CollectionItem
import com.langapp.russianlanguage_stepbystep.models.ExpandableModel
import com.langapp.russianlanguage_stepbystep.utils.NumberWord

class Lessons : Fragment() {

    private var isDataLoaded = false
    private val openedSections = mutableSetOf<Int>()

    private val translationsMap = mapOf(
        "GrammarTheory" to "Грамматика-теория",
        "GrammarPractice" to "Грамматика-практика",
        "Grammar" to "Грамматика",
        "Alphabet" to "Алфавит",
        "Phonetics" to "Фонетика",
        "Lexics" to "Лексика",
        "Test" to "Тест",
        "Topics" to "Темы",
        "Texts" to "Тексты"
    )

    private lateinit var progressBar: ProgressBar

    private lateinit var recyclerView: RecyclerView
    private lateinit var expandableAdapter: ExpandableAdapter
    private val lessonsContentMap = mutableMapOf<String, List<String>>()
    private val databaseRef = FirebaseDatabase.getInstance().reference
    private var lessonsCount = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_lessons, container, false)
        recyclerView = view.findViewById(R.id.level_choose)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        expandableAdapter = ExpandableAdapter(
            requireContext(),
            arrayListOf(),
            itemClickListener = { collectionItem ->
                val parts = collectionItem.getOriginalKey().split("/")
                if (parts.size >= 2) {
                    val lessonPart = parts[0].replace("Lesson", "")
                    val contentPart = parts[1].lowercase()

                    val actionId = resources.getIdentifier(
                        "action_lessons_to_${contentPart}${lessonPart}",
                        "id",
                        requireContext().packageName
                    )

                    if (actionId != 0) {
                        val bundle = bundleOf("databasePath" to collectionItem.getOriginalKey())
                        view.findNavController().navigate(actionId, bundle)
                    } else view.findNavController().navigate(R.id.action_lessons_to_defaultFragment)
                }
            },
            sectionToggleListener = { sectionIndex, isOpened ->
                toggleSectionState(sectionIndex, isOpened)
            }
        )
        recyclerView.adapter = expandableAdapter
        progressBar = view.findViewById(R.id.progressBar)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar.visibility = View.VISIBLE
        if(!isDataLoaded) {
            readLessons()
            isDataLoaded = true
        }
    }

    private fun readLessons() {

        if(isDataLoaded) return

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { lessonSnapshot ->
                    if(lessonSnapshot.key!!.contains("Lesson")) {
                        lessonsCount++
                        readLessonContent(lessonSnapshot.key!!)
                    }
                }

                isDataLoaded = true
            }

            override fun onCancelled(databaseError: DatabaseError) {
                progressBar.visibility = View.GONE
            }
        })
    }

    private fun readLessonContent(lesson: String) {
        val lessonRef = databaseRef.child(lesson)
        lessonRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val content = snapshot.children.map { it.key ?: "" }
                lessonsContentMap[lesson] = content
                if (lessonsContentMap.size == lessonsCount) {
                    updateRecyclerView()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Обработка ошибки
            }
        })
    }

    private fun updateRecyclerView() {

        progressBar.visibility = View.GONE

        val priorityMap = mapOf(
            "Alphabet" to 1,
            "Lexics" to 2,
            "Phonetics" to 3,
            "Grammar" to 4,
            "GrammarTheory" to 5,
            "GrammarPractice" to 6,
            "Texts" to 7,
            "Topics" to 8,
            "Test" to 9
        )

        fun getPriority(item: String): Int {
            return priorityMap[item] ?: 10
        }

        val sortedLessons = lessonsContentMap.keys.sortedBy { NumberWord.fromName(it.replace("Lesson", "")) }
        val expandableModels = sortedLessons.map { lesson ->
            val contentItems = lessonsContentMap[lesson]?.sortedWith(compareBy { getPriority(it) })?.map { section ->
                val fullPath = "$lesson/$section"
                CollectionItem(fullPath, translationsMap[section] ?: section)
            } ?: emptyList()

            ExpandableModel(contentItems, "Урок №${NumberWord.fromName(lesson.replace("Lesson", ""))}")
        }

        expandableAdapter.setData(expandableModels)
    }

    private fun toggleSectionState(sectionIndex: Int, isOpened: Boolean) {
        if (isOpened) {
            openedSections.add(sectionIndex)
        } else {
            openedSections.remove(sectionIndex)
        }
    }

    override fun onStop() {
        super.onStop()
        isDataLoaded = false
        lessonsContentMap.clear()
        lessonsCount = 0
    }

    override fun onPause() {
        super.onPause()
        saveOpenedSectionsState()
    }

    override fun onResume() {
        super.onResume()
        restoreOpenedSectionsState()
    }

    private fun saveOpenedSectionsState() {
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putStringSet("OPENED_SECTIONS", openedSections.map { it.toString() }.toSet())
            apply()
        }

        Log.d("lessons", "${sharedPreferences.all}")
    }

    private fun restoreOpenedSectionsState() {
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val savedSections = sharedPreferences.getStringSet("OPENED_SECTIONS", emptySet())

        openedSections.clear()
        savedSections?.mapNotNullTo(openedSections) { it.toIntOrNull() }

        // Обновляем состояние моделей в адаптере
        expandableAdapter.updateOpenedSections(openedSections)
    }
}