package com.fakhar.messenger.Adapters

import android.util.Log
import com.fakhar.messenger.Model.ChatMessage
import com.fakhar.messenger.Model.User
import com.fakhar.messenger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.home_message_row.view.*
import kotlin.math.PI

class HomeMessageRow(val chatMessage: ChatMessage) : Item<ViewHolder>() {
    var chatPartnerUser : User? = null
    override fun getLayout(): Int {
        return R.layout.home_message_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

        var chatPartnerId  = "Fakhar"

        if(chatMessage.fromId == FirebaseAuth.getInstance().uid)
        {
            chatPartnerId = chatMessage.toId
        }
        else {
            chatPartnerId = chatMessage.fromId
        }


        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)

                var imageUrl : String = ""
                imageUrl = chatPartnerUser?.profileImageUrl!!

                viewHolder.itemView.tv_home_username.text = chatPartnerUser?.username!!

                Picasso.get().load(imageUrl).into(viewHolder.itemView.iv_home_profile_photo)
            }

        })


        viewHolder.itemView.tv_home_user_message.text = chatMessage.text

    }
}