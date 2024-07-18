package com.langapp.russianlanguage_stepbystep.utils

import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.langapp.russianlanguage_stepbystep.models.ImageAndTextModel
import com.langapp.russianlanguage_stepbystep.models.TestModel

class FirebaseContentReader(private val dbRef: DatabaseReference) {

    fun readGrammarContainTextContent(grammar: String, callback: (List<Triple<String, String, String>>) -> Unit) {
        val grammarRef = dbRef.child(grammar)
        grammarRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val content = snapshot.children.map {
                    if(it.hasChild("text"))
                        Triple(
                            it.child("text").value.toString().replace("\\n", "\n"),
                            it.child("sound").value.toString(),
                            it.child("answer_r").value.toString()
                        )
                    else
                        Triple(
                            it.key.toString(),
                            it.value.toString(),
                            it.value.toString()
                        )
                }
                callback(content)
            }

            override fun onCancelled(error: DatabaseError) {
                // Логика обработки ошибки
            }
        })
    }

//    fun readTextsContent(callback: (List<TestModel>) -> Unit) {
//        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val content = snapshot.children.map {
//                    if (it.key.toString().startsWith("option"))
//                        TestModel(
//                            text = null,
//                            sound = null,
//                            question = it.child("question").getValue(String::class.java),
//                            answer1 = it.child("answer1").getValue(String::class.java),
//                            answer2 = it.child("answer2").getValue(String::class.java),
//                            answer3 = it.child("answer3").getValue(String::class.java),
//                            answer4 = it.child("answer4").getValue(String::class.java),
//                            answerR = it.child("answer_r").getValue(String::class.java)
//                        )
//                    else
//                        TestModel(
//                            text = it.child("text").getValue(String::class.java),
//                            sound = it.child("sound").getValue(String::class.java),
//                            question = null,
//                            answer1 = null,
//                            answer2 = null,
//                            answer3 = null,
//                            answer4 = null,
//                            answerR = null
//                        )
//                }
//
//                callback(content)
//            }
//            override fun onCancelled(error: DatabaseError) {
//                // Реализация обработки ошибок, возможно, уведомление пользователя
//            }
//        })
//    }

    fun readGrammarTextEditContent(grammar: String, callback: (List<Triple<String, String, String>>) -> Unit) {
        val grammarRef = dbRef.child(grammar)
        grammarRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val content = snapshot.children.map {
                    Triple(
                        it.child("text").value.toString(),
                        "",
                        it.child("answer_r").value.toString()
                    )
                }
                callback(content)
            }

            override fun onCancelled(error: DatabaseError) {
                // Логика обработки ошибки
            }
        })
    }

    fun readGrammarDialogsContent(grammar: String, callback: (List<Triple<String, String, String>>) -> Unit) {
        val grammarRef = dbRef.child(grammar)
        grammarRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val content = snapshot.children.map {
                    Triple(
                        it.child("text").value.toString(),
                        it.child("sound").value.toString(),
                        it.child("text").value.toString()
                    )
                }
                callback(content)
            }

            override fun onCancelled(error: DatabaseError) {
                // Логика обработки ошибки
            }
        })
    }

    fun readGrammarImagesContent(grammar: String, callback: (List<Triple<String, String, String>>) -> Unit) {
        val grammarRef = dbRef.child(grammar)
        grammarRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val content = snapshot.children.map {
                    Triple(
                        it.key.toString(),
                        it.child("image").value.toString(),
                        it.child("answer_r").value.toString()
                    )
                }
                callback(content)
            }

            override fun onCancelled(error: DatabaseError) {
                // Логика обработки ошибки
            }
        })
    }

    fun readGrammarRegularContent(grammar: String, callback: (List<Triple<String, String, String>>) -> Unit) {
        val grammarRef = dbRef.child(grammar)
        grammarRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val content = snapshot.children.map {
                    Triple(
                        it.key.toString(),
                        it.value.toString(),
                        it.value.toString()
                    )
                }
                callback(content)
            }

            override fun onCancelled(error: DatabaseError) {
                // Логика обработки ошибки
            }
        })
    }

    fun readVocabularyContent(callback: (ArrayList<ImageAndTextModel>) -> Unit) {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = arrayListOf<ImageAndTextModel>()
                snapshot.children.forEach {
                    data.add(
                        ImageAndTextModel(
                            it.child("image").value.toString().toUri(),
                            it.key.toString(),
                            it.child("sound").value.toString()
                        )
                    )
                }

                callback(data) // Вызовем колбек с загруженными данными
            }

            override fun onCancelled(error: DatabaseError) {
                // Вызовем колбек с ошибкой
            }
        })
    }

    fun readPhoneticsContent(phonetics: String, callback: (List<Pair<String, String>>) -> Unit) {
        val phoneticsRef = dbRef.child(phonetics)
        phoneticsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val content = snapshot.children.map {
                    Pair(
                        it.key.toString(),
                        it.value.toString()
                    )
                }
                callback(content)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}