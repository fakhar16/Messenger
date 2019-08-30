package com.fakhar.messenger.Adapters

import com.fakhar.messenger.Model.User
import com.fakhar.messenger.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class ChattoItem(val text: String, val user: User) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.tv_chat_message.text = text

        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.iv_chat_profilepicture)
    }
}