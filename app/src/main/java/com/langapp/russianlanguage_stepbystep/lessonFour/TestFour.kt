package com.langapp.russianlanguage_stepbystep.lessonFour

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.langapp.russianlanguage_stepbystep.R
import com.langapp.russianlanguage_stepbystep.models.TestModel

class TestFour : Fragment() {
    private lateinit var dbRef: DatabaseReference
    private val textsContentMap = mutableMapOf<String, TestModel>()
    private var currentIndex = 0
    private val userAnswers = mutableMapOf<String, String?>()

    private lateinit var questionNumHolder: TextView
    private lateinit var questionHolder: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var back: ImageButton
    private lateinit var forward: ImageButton
    private lateinit var end: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_test_two, container, false)

        val dbPath = arguments?.getString("databasePath") ?: return null
        dbRef = FirebaseDatabase.getInstance().reference.child(dbPath)

        questionNumHolder = view.findViewById(R.id.questionNumHolder)
        questionHolder = view.findViewById(R.id.questionHolder)
        radioGroup = view.findViewById(R.id.radioGroup)
        back = view.findViewById(R.id.btnPrev)
        forward = view.findViewById(R.id.btnNext)
        end = view.findViewById(R.id.endBtn)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back.setOnClickListener { navigateQuestions(-1) }
        forward.setOnClickListener { navigateQuestions(1) }
        end.setOnClickListener {
            if(textsContentMap.keys.size != userAnswers.size)
                showCustomConfirmationDialog()
            else
                writeData()
        }

        readTestData()
    }

    private fun writeData() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            val correctAnswersCount = userAnswers.count { (question, userAnswer) ->
                Log.d("writeData", "$question ${userAnswers[question]}")
                textsContentMap.values.elementAtOrNull(question.toInt())?.answer_r == userAnswer
            }

            val testId = "testFour"
            val resultRef = FirebaseDatabase.getInstance().reference.child("users").child(userId).child(testId)

            resultRef.setValue(correctAnswersCount)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Результаты теста сохранены", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Ошибка при сохранении результатов: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        // Логируем ошибку
                        Log.e("writeData", "Ошибка при сохранении результатов", task.exception)
                    }
                }
        } else {
            Toast.makeText(context, "Пользователь не авторизован", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCustomConfirmationDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.transfer_confirm_dialog, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.yesBtn)?.setOnClickListener {
            writeData()
            view?.findNavController()?.popBackStack()
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.noBtn)?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun readTestData() {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { textSnapshot ->
                    val testModel = TestModel(
                        question = textSnapshot.key,
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