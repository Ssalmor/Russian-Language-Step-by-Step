package com.langapp.russianlanguage_stepbystep

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class Login : Fragment() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var registration: TextView
    private lateinit var login: Button

    private var auth = Firebase.auth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentLayout = inflater.inflate(R.layout.fragment_login, container, false)

        editTextEmail = fragmentLayout.findViewById(R.id.email_edit_text)
        editTextPassword = fragmentLayout.findViewById(R.id.password_edit_text)
        registration = fragmentLayout.findViewById(R.id.registration_button)
        login = fragmentLayout.findViewById(R.id.login_button)

        return fragmentLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(auth.currentUser == null) {
            login.setOnClickListener {
                val email: String = editTextEmail.text.toString()
                val password: String = editTextPassword.text.toString()

                if(TextUtils.isEmpty(email)) {
                    editTextEmail.error = "Введите почту"
                    return@setOnClickListener
                } else if(!isValidEmail(email)) {
                    editTextEmail.error = "Некорректный формат почты"
                    return@setOnClickListener
                }

                if(TextUtils.isEmpty(password)) {
                    editTextPassword.error = "Введите пароль"
                    return@setOnClickListener
                }

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Успешный вход", Toast.LENGTH_SHORT).show()
                            view.findNavController().navigate(R.id.action_login_to_lessons)
                        } else {
                            Toast.makeText(requireContext(), "Ошибка входа", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        } else {
            view.findNavController().navigate(R.id.action_login_to_lessons)
        }

        registration.setOnClickListener {
            view.findNavController().navigate(R.id.action_login_to_registration)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return if(TextUtils.isEmpty(email))
            false
        else
            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}