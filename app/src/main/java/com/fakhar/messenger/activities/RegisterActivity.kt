package com.fakhar.messenger.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_register.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import android.widget.Toast
import com.fakhar.messenger.Model.User
import com.fakhar.messenger.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class RegisterActivity : AppCompatActivity() {

    lateinit var etUserName : TextInputEditText
    lateinit var etEmail : TextInputEditText
    lateinit var etPassword : TextInputEditText
    lateinit var btnRegister : Button
    lateinit var tvLoginLink : TextView
    lateinit var btnSelectPhoto : Button
    lateinit var ivSelectPhoto : CircleImageView

    private val IMG_PICK_REQUEST_CODE = 0

    var selectedPhotoUri : Uri? = null

    //FireBase
     private lateinit var mAuth: FirebaseAuth
    lateinit var mStorageRef: StorageReference

    // Write a message to the database
     lateinit var database : FirebaseDatabase
    lateinit var myRef : DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        init()
        initFireBase()

        btnSelectPhoto.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent , IMG_PICK_REQUEST_CODE)
        })



        btnRegister.setOnClickListener(View.OnClickListener {
            performRegister()
        })

        tvLoginLink.setOnClickListener(View.OnClickListener {
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == IMG_PICK_REQUEST_CODE  && resultCode == Activity.RESULT_OK && data != null)
        {
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)

            ivSelectPhoto.setImageBitmap(bitmap)
            btnSelectPhoto.alpha = 0f

        }
    }



    private fun performRegister() {

        var email = etEmail.text.toString()
        var password = etPassword.text.toString()

        if(email.isEmpty() || password.isEmpty())
            return

        mAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{

                if(!it.isSuccessful)
                    return@addOnCompleteListener


                uploadImageToFireBase()
            }
    }

    private fun uploadImageToFireBase() {

        val filename = UUID.randomUUID().toString()
        val ref = mStorageRef.child("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {

                ref.downloadUrl.addOnSuccessListener {
                    saveUserToFireBase(it.toString())
                }
            }
            .addOnFailureListener{

            }

    }

    private fun saveUserToFireBase(profileImageUrl :String ) {

        val uid = mAuth.uid
        myRef = database.getReference("/users/$uid")

        var user =
            User(uid!!, etUserName.text.toString(), profileImageUrl)

        myRef.setValue(user)
            .addOnSuccessListener {
                Toast.makeText(baseContext,"User Created Successfully",Toast.LENGTH_SHORT).show()

                var intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }


    }

    private fun initFireBase()
    {
        mAuth = FirebaseAuth.getInstance()
        mStorageRef = FirebaseStorage.getInstance().getReference()

        database = FirebaseDatabase.getInstance()

    }

    private fun init()
    {
        etUserName = et_username
        etEmail = et_email
        etPassword = et_password
        btnRegister = btn_register
        tvLoginLink =tv_loginlink
        btnSelectPhoto = btn_selectphoto
        ivSelectPhoto = iv_selectphoto

    }
}
