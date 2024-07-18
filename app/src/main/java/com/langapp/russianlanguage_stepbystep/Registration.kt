package com.langapp.russianlanguage_stepbystep

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Registration : Fragment() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signUp: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentLayout = inflater.inflate(R.layout.fragment_registration, container, false)

        nameEditText = fragmentLayout.findViewById(R.id.name_edit_text)
        emailEditText = fragmentLayout.findViewById(R.id.email_edit_text)
        passwordEditText = fragmentLayout.findViewById(R.id.password_edit_text)
        signUp = fragmentLayout.findViewById(R.id.registration_button)

        return fragmentLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signUp.setOnClickListener {
            val name: String = nameEditText.text.toString()
            val email: String = emailEditText.text.toString()
            val password: String = passwordEditText.text.toString()

            if(TextUtils.isEmpty(name)) {
                nameEditText.error = "Введите ваше имя"
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(email)) {
                emailEditText.error = "Введите почту"
                return@setOnClickListener
            } else if(!isValidEmail(email)) {
                emailEditText.error = "Некорректный формат почты"
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(password)) {
                passwordEditText.error = "Введите пароль"
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {task ->
                    if(task.isSuccessful) {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid

                        val user = mapOf(
                            "name" to name,
                            "email" to email
                        )

                        userId?.let {
                            FirebaseDatabase.getInstance().reference.child("users").child(it).setValue(user).addOnCompleteListener { databaseTask ->
                                if(databaseTask.isSuccessful) {
                                    Toast.makeText(context, "Пользователь зарегистрирован", Toast.LENGTH_SHORT).show()
                                    view.findNavController().navigate(R.id.action_registration_to_lessons)
                                } else Toast.makeText(context, "Ошибка сохранения данных пользователя: ${databaseTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        } ?: kotlin.run { Toast.makeText(context, "Ошибка: UID пользователя не найден", Toast.LENGTH_SHORT).show() }
                    } else Toast.makeText(context, "Ошибка регистрации: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return if(TextUtils.isEmpty(email))
            false
        else
            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}