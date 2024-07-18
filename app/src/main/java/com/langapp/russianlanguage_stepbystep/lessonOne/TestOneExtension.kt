package com.langapp.russianlanguage_stepbystep.lessonOne

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.DragEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.langapp.russianlanguage_stepbystep.R
import com.squareup.picasso.Picasso

class TestOneExtension : Fragment(), View.OnDragListener, View.OnLongClickListener {
    private lateinit var mama: ImageView
    private lateinit var papa: ImageView
    private lateinit var dom: ImageView
    private lateinit var bukva: ImageView
    private lateinit var banan: ImageView

    private lateinit var dragTV1: TextView
    private lateinit var dragTV2: TextView
    private lateinit var dragTV3: TextView
    private lateinit var dragTV4: TextView
    private lateinit var dragTV5: TextView

    private lateinit var placeHolder1: LinearLayout
    private lateinit var placeHolder2: LinearLayout
    private lateinit var placeHolder3: LinearLayout
    private lateinit var placeHolder4: LinearLayout
    private lateinit var placeHolder5: LinearLayout
    private lateinit var wordHolder: LinearLayout
    private lateinit var parentLayout: ConstraintLayout

    private val imageTags = arrayOf("mama", "dom", "papa", "banan", "bukva")
    private val placedTexts = arrayOfNulls<String>(imageTags.size)

    private val auth = FirebaseAuth.getInstance()
    private lateinit var dbRef: DatabaseReference

    private lateinit var checkAnswerButton: Button

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_test_one_extension, container, false)

        dbRef = FirebaseDatabase.getInstance().getReference("users")

        dom = view.findViewById(R.id.dom)
        mama = view.findViewById(R.id.mama)
        papa = view.findViewById(R.id.papa)
        bukva = view.findViewById(R.id.bukva)
        banan = view.findViewById(R.id.banan)

        val papaUrl = "https://firebasestorage.googleapis.com/v0/b/languageguide-f3759.appspot.com/o/vocabularyLevelOneImages%2F%D0%BF%D0%B0%D0%BF%D0%B0.png?alt=media&token=4188c59b-b35d-4893-a717-01e9b3493c35"
        val mamaUrl = "https://firebasestorage.googleapis.com/v0/b/languageguide-f3759.appspot.com/o/vocabularyLevelOneImages%2F%D0%BC%D0%B0%D0%BC%D0%B0.png?alt=media&token=27014add-ecda-4cd6-8604-de8f474595eb"
        val domUrl = "https://firebasestorage.googleapis.com/v0/b/languageguide-f3759.appspot.com/o/vocabularyLevelOneImages%2F%D0%B4%D0%BE%D0%BC.png?alt=media&token=aa2cef36-9c29-47a8-ab28-1dfe0e5408da"
        val bananUrl = "https://firebasestorage.googleapis.com/v0/b/languageguide-f3759.appspot.com/o/vocabularyLevelOneImages%2F%D0%B1%D0%B0%D0%BD%D0%B0%D0%BD.png?alt=media&token=4f87b4b0-6892-4426-9f23-da5f0adbe5fd"
        val bukvaUrl = "https://firebasestorage.googleapis.com/v0/b/languageguide-f3759.appspot.com/o/vocabularyLevelOneImages%2F%D0%B1%D1%83%D0%BA%D0%B2%D0%B0.png?alt=media&token=be1c5417-9002-4e0e-a92c-8f481e2c6f8c"

        Picasso.get().load(papaUrl).into(papa)
        Picasso.get().load(mamaUrl).into(mama)
        Picasso.get().load(domUrl).into(dom)
        Picasso.get().load(bananUrl).into(banan)
        Picasso.get().load(bukvaUrl).into(bukva)

        dragTV1 = view.findViewById(R.id.dragText1)
        dragTV2 = view.findViewById(R.id.dragText2)
        dragTV3 = view.findViewById(R.id.dragText3)
        dragTV4 = view.findViewById(R.id.dragText4)
        dragTV5 = view.findViewById(R.id.dragText5)

        placeHolder1 = view.findViewById(R.id.placeHolder1)
        placeHolder2 = view.findViewById(R.id.placeHolder2)
        placeHolder3 = view.findViewById(R.id.placeHolder3)
        placeHolder4 = view.findViewById(R.id.placeHolder4)
        placeHolder5 = view.findViewById(R.id.placeHolder5)
        wordHolder = view.findViewById(R.id.wordHolderLayout)
        parentLayout = view.findViewById(R.id.linear)

        dragTV1.tag = "banan"
        dragTV2.tag = "dom"
        dragTV3.tag = "mama"
        dragTV4.tag = "papa"
        dragTV5.tag = "bukva"

        dragTV1.setOnLongClickListener(this)
        dragTV2.setOnLongClickListener(this)
        dragTV3.setOnLongClickListener(this)
        dragTV4.setOnLongClickListener(this)
        dragTV5.setOnLongClickListener(this)

        placeHolder1.setOnDragListener(this)
        placeHolder2.setOnDragListener(this)
        placeHolder3.setOnDragListener(this)
        placeHolder4.setOnDragListener(this)
        placeHolder5.setOnDragListener(this)
        wordHolder.setOnDragListener(this)
        parentLayout.setOnDragListener(this)

        checkAnswerButton = view.findViewById(R.id.checkAnswer)
        checkAnswerButton.setOnClickListener {
            if (!checkAnswers()) Toast.makeText(requireContext(), "Заполните все", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun checkAnswers(): Boolean {
        if (placedTexts.any { it == null } ) {
            return false
        }

        var correctMatches = 0
        for (i in imageTags.indices) {
            if (placedTexts[i] == imageTags[i])
                correctMatches++
        }

        updateDragScore(correctMatches)
        showExplanationDialog(correctMatches)

        return correctMatches == imageTags.size
    }

    private fun updateDragScore(correctMatches: Int) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            val testId = "testOne"
            val resultRef = FirebaseDatabase.getInstance().reference.child("users").child(userId).child(testId)

            resultRef.setValue(correctMatches)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) Toast.makeText(context, "Результаты теста сохранены", Toast.LENGTH_SHORT).show()
                    else Toast.makeText(context, "Ошибка при сохранении результатов: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
        } else Toast.makeText(context, "Пользователь не авторизован", Toast.LENGTH_SHORT).show()
    }

    private fun showExplanationDialog(correctMatches: Int) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())

        alertDialogBuilder.setTitle("Тест пройден")
        alertDialogBuilder.setMessage("Вы сделали верно $correctMatches из 5")

        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            view?.findNavController()?.popBackStack(R.id.lessons, true)
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onDrag(p0: View?, p1: DragEvent?): Boolean {
        when(p1!!.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                if(p1.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
                    return true
                return false
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                p0?.background?.setColorFilter(Color.argb(127, 90, 150, 250), PorterDuff.Mode.SRC_IN)
                p0?.invalidate()
                return true
            }
            DragEvent.ACTION_DRAG_LOCATION -> return true
            DragEvent.ACTION_DRAG_EXITED -> {
                p0?.background?.clearColorFilter()
                p0?.invalidate()
                return true
            }
            DragEvent.ACTION_DROP -> {
                val item = p1.clipData.getItemAt(0)
                val dragData = item.text.toString()

                p0?.background?.clearColorFilter()
                p0?.invalidate()

                val v = p1.localState as View
                val owner = v.parent as ViewGroup
                owner.removeView(v)

                val index = when (p0) {
                    placeHolder1 -> 0
                    placeHolder2 -> 1
                    placeHolder3 -> 2
                    placeHolder4 -> 3
                    placeHolder5 -> 4
                    wordHolder -> 5
                    else -> -1
                }

                if(index in 0..4) {
                    val container = p0 as LinearLayout
                    if (container.childCount == 0) {
                        container.addView(v)
                        v.visibility = View.VISIBLE
                        placedTexts[index] = dragData
                    } else {
                        wordHolder.addView(v)
                        v.visibility = View.VISIBLE
                    }
                } else {
                    wordHolder.addView(v)
                    v.visibility = View.VISIBLE
                }

                return true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                p0?.background?.clearColorFilter()
                p0?.invalidate()
                return true
            }
        }
        return false
    }

    override fun onLongClick(p0: View?): Boolean {
        val item = ClipData.Item(p0!!.tag as CharSequence)
        val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
        val data = ClipData(p0.tag.toString(), mimeTypes, item)
        val shadowBuilder = View.DragShadowBuilder(p0)

        p0.startDrag(
            data,
            shadowBuilder,
            p0,
            0
        )

        p0.visibility = View.INVISIBLE
        return true
    }
}