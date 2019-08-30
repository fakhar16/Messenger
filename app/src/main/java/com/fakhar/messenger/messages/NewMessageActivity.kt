package com.fakhar.messenger.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.fakhar.messenger.R
import com.fakhar.messenger.Model.User
import com.fakhar.messenger.Adapters.UserItem
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*

class NewMessageActivity : AppCompatActivity() {

    lateinit var database : FirebaseDatabase
    lateinit var myRef : DatabaseReference

    lateinit var rvNewMessage : RecyclerView

    companion object{
        val USER_KEY = "USER_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        init()

        fetchUser()
    }

    private fun fetchUser() {

        myRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach{

                    val user = it.getValue(User::class.java)
                    adapter.add(UserItem(user))
                }

                adapter.setOnItemClickListener { item, view ->

                    val userItem = item as UserItem

                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY , userItem.user)
                    startActivity(intent)

                    finish()

                }

                rvNewMessage.adapter = adapter
            }

        })
    }

    private fun init()
    {
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference("/users")

        supportActionBar?.title = "Select User"
        rvNewMessage =rv_newmessage


    }
}
