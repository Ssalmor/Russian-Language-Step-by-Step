package com.langapp.russianlanguage_stepbystep.lessonFive

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.langapp.russianlanguage_stepbystep.R
import com.langapp.russianlanguage_stepbystep.models.TextModel

class TextsFive : Fragment() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var dbRef: DatabaseReference
    private val textsContentMap = mutableMapOf<String, TextModel>()
    private var currentIndex = 0
    private val userAnswers = mutableMapOf<String, String?>()

    private lateinit var questionNumHolder: TextView
    private lateinit var questionHolder: TextView
    private lateinit var textHolder: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var back: ImageButton
    private lateinit var forward: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_texts_four, container, false)

        val dbPath = arguments?.getString("databasePath") ?: return null
        dbRef = FirebaseDatabase.getInstance().reference.child(dbPath)

        questionNumHolder = view.findViewById(R.id.questionNumHolder)
        questionHolder = view.findViewById(R.id.questionHolder)
        textHolder = view.findViewById(R.id.textHolder)
        radioGroup = view.findViewById(R.id.radioGroup)
        back = view.findViewById(R.id.btnPrev)
        forward = view.findViewById(R.id.btnNext)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back.setOnClickListener { navigateQuestions(-1) }
        forward.setOnClickListener { navigateQuestions(1) }

        readTextsData()
    }

    private fun readTextsData() {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { textSnapshot ->
                    val testModel = TextModel(
                        question = textSnapshot.key.toString(),
                        text = textSnapshot.child("text").getValue(String::class.java),
                        answer1 = textSnapshot.child("answer1").getValue(String::class.java),
                        answer2 = textSnapshot.child("answer2").getValue(String::class.java),
                        answer3 = textSnapshot.child("answer3").getValue(String::class.java),
                        answer4 = textSnapshot.child("answer4").getValue(String::class.java),
                        answer_r = textSnapshot.child("answer_r").getValue(String::class.java)
                    )
                    textsContentMap[textSnapshot.key!!] = testModel
                }
                updateViews()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Ошибка чтения данных.\n Повторте попытку.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateQuestions(direction: Int) {
        val totalQuestions = textsContentMap.keys.size
        currentIndex = (currentIndex + direction + totalQuestions) % totalQuestions
        updateViews()
    }

    private fun updateViews() {
        val testData = textsContentMap.values.elementAtOrNull(currentIndex) ?: return

        questionNumHolder.text = "Вопрос №${currentIndex + 1}"
        questionHolder.text = testData.question
        textHolder.text = testData.text

        radioGroup.setOnCheckedChangeListener(null)
        radioGroup.removeAllViews()

        val answers = listOf(testData.answer1, testData.answer2, testData.answer3, testData.answer4)
        val userAnswer = userAnswers[currentIndex.toString()]
        var userAnswerChecked = false

        answers.forEachIndexed { index, answer ->
            if (answer != null) {
                val radioButton = RadioButton(context).apply {
                    id = index
                    text = answer
                    isEnabled = userAnswer == null // Блокируем, если ответ уже дан
                    textSize = 26f
                    if (answer == userAnswer) {
                        isChecked = true
                        userAnswerChecked = true
                        setBackgroundColor(if (answer == testData.answer_r) {
                            ContextCompat.getColor(requireContext(), R.color.green)
                        } else {
                            ContextCompat.getColor(requireContext(), R.color.red)
                        })
                    }
                }
                radioGroup.addView(radioButton)
            }
        }

        // Назначаем слушателя только если ответ еще не был дан
        if (!userAnswerChecked) {
            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                val correctAnswer = testData.answer_r
                val chosenAnswer = group.findViewById<RadioButton>(checkedId)?.text.toString()
                userAnswers[currentIndex.toString()] = chosenAnswer

                for (i in 0 until group.childCount) {
                    val btn = group.getChildAt(i) as RadioButton
                    btn.isEnabled = false // Отключаем возможность выбора после первого выбора

                    if (btn.text == correctAnswer) {
                        btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                    } else if (btn.id == checkedId) {
                        btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                    }
                }
            }
        }
    }
}