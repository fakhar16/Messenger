package com.fakhar.messenger.Adapters

import com.fakhar.messenger.Model.User
import com.fakhar.messenger.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class UserItem(val user: User?) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.tv_username.text = user?.username

        Picasso.get().load(user?.profileImageUrl).into(viewHolder.itemView.iv_profilepicture)
    }
}