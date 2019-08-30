package com.fakhar.messenger.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

import com.fakhar.messenger.R
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.et_email
import kotlinx.android.synthetic.main.activity_register.et_password
import android.widget.Toast

import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    lateinit var etEmail : TextInputEditText
    lateinit var etPassword : TextInputEditText
    lateinit var btnLogin : Button
    lateinit var tvRegisterLink : TextView

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        init()

        tvRegisterLink.setOnClickListener(View.OnClickListener {
            var intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        })

        btnLogin.setOnClickListener(View.OnClickListener {

            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this,
                    OnCompleteListener<AuthResult> { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information

                            val user = mAuth.getCurrentUser()

                            var intent = Intent(baseContext , HomeActivity::class.java)
                            startActivity(intent)

                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(
                                this, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        // ...
                    })
        })
    }

    fun init()
    {

        mAuth = FirebaseAuth.getInstance()
        etEmail = et_email
        etPassword = et_password
        btnLogin = btn_login
        tvRegisterLink =tv_registerlink
    }
}
