package com.fakhar.messenger.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import com.fakhar.messenger.Adapters.HomeMessageRow
import com.fakhar.messenger.Model.ChatMessage
import com.fakhar.messenger.Model.User
import com.fakhar.messenger.R
import com.fakhar.messenger.messages.ChatLogActivity
import com.fakhar.messenger.messages.NewMessageActivity
import com.fakhar.messenger.messages.NewMessageActivity.Companion.USER_KEY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    lateinit var database : FirebaseDatabase
    lateinit var myRef : DatabaseReference

    companion object{
        var currnetUser : User? = null
    }

    val adapter = GroupAdapter<ViewHolder>()
    val latestMessageMap = HashMap<String,ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        rv_home.adapter = adapter
        rv_home.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this, ChatLogActivity::class.java)

            val row = item as HomeMessageRow
            intent.putExtra(USER_KEY , row.chatPartnerUser)
            startActivity(intent)
        }

        initFireBase()

        fetchCurrentUser()
        verifyIsUserLoggedIn()

        listenForLatestMessages()

    }

    private fun listenForLatestMessages() {
       val fromId = mAuth.uid

        val ref = database.getReference("latest-messages/$fromId")

        ref.addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                val chatMessage = p0.getValue(ChatMessage::class.java)
                latestMessageMap[p0.key!!] = chatMessage!!
                refreshRecyclerViewMessages()
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                latestMessageMap[p0.key!!] = chatMessage!!
                refreshRecyclerViewMessages()
            }

        })
    }

    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        latestMessageMap.values.forEach{
            adapter.add(HomeMessageRow(it))
        }
    }

    private fun fetchCurrentUser() {

        val uid = mAuth.uid
        myRef = database.getReference("/users/$uid")

        myRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                currnetUser = p0.getValue(User::class.java)
            }

        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId)
        {
            R.id.menu_newmessage ->
            {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }

            R.id.menu_signout ->
            {
                mAuth.signOut()

                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }
    private fun verifyIsUserLoggedIn() {
        var uid = mAuth.uid

        if(uid == null)
        {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }

    private fun initFireBase()
    {
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()


    }


}
