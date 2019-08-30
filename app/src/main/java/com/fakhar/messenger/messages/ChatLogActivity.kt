package com.fakhar.messenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.fakhar.messenger.Adapters.ChatFromItem
import com.fakhar.messenger.Adapters.ChattoItem
import com.fakhar.messenger.Model.ChatMessage
import com.fakhar.messenger.Model.User
import com.fakhar.messenger.R
import com.fakhar.messenger.activities.HomeActivity
import com.fakhar.messenger.messages.NewMessageActivity.Companion.USER_KEY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity() {

    lateinit var database : FirebaseDatabase
    lateinit var myRef : DatabaseReference

    private lateinit var mAuth: FirebaseAuth

    val adapter = GroupAdapter<ViewHolder>()

    var toUser : User? = null

    private fun init()
    {
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        myRef = database.getReference("/messages")

        toUser = intent.getParcelableExtra<User>(USER_KEY)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        init()



        supportActionBar?.title = toUser?.username

        rv_chatlog.adapter = adapter

        listenForMessages()

        btn_send.setOnClickListener(View.OnClickListener {
            performSendMessage()
        })


    }

    private fun listenForMessages() {

        val fromId = mAuth.uid
        val toId = toUser?.uid

        myRef = database.getReference("/user-messages/$fromId/$toId")


        myRef.addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if(mAuth.uid == chatMessage?.fromId){
                    adapter.add(ChatFromItem(chatMessage?.text!! , HomeActivity.currnetUser!!))
                }
                else{
                    adapter.add(ChattoItem(chatMessage?.text!! , toUser!!))
                }

                rv_chatlog.scrollToPosition(adapter.itemCount - 1)


            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })
    }

    private fun performSendMessage() {

        val text = et_entermessage.text.toString()
        val fromId = mAuth.uid
        val toId = toUser?.uid

        myRef = database.getReference("/user-messages/$fromId/$toId").push()
        val toRef = database.getReference("/user-messages/$toId/$fromId").push()



        val chatMessage = ChatMessage(myRef.key!!,text,fromId!!,toId!!, System.currentTimeMillis()/1000)

        myRef.setValue(chatMessage)
            .addOnSuccessListener {
                et_entermessage.text.clear()
                rv_chatlog.scrollToPosition(adapter.itemCount - 1)
            }

        toRef.setValue(chatMessage)

        val latestRef = database.getReference("/latest-messages/$fromId/$toId")
        latestRef.setValue(chatMessage)

        val latestToRef = database.getReference("/latest-messages/$toId/$fromId")
        latestToRef.setValue(chatMessage)

    }


}
