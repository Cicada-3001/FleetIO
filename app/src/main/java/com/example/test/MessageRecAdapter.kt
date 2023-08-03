package com.example.test


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.example.test.models.Message


class MessageRecAdapter(private var messageArrayList: ArrayList<Message>, private var context: Context) :
    Adapter<ViewHolder>() {


    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View? = null
        when (viewType) {
            0 -> {
                // below line we are inflating user message layout.
                view = LayoutInflater.from(parent.context).inflate(R.layout.layout_user_messages, parent, false)
                return UserViewHolder(view)
            }
            1 -> {
                // below line we are inflating bot message layout.
                view = LayoutInflater.from(parent.context).inflate(R.layout.layout_help_team_messages, parent, false)
                return BotViewHolder(view)
            }
        }
        return UserViewHolder(view!!)
    }



    override fun getItemCount(): Int {
        // return the size of array list
        return messageArrayList.size
    }

    override fun getItemViewType(position: Int): Int {
        // below line of code is to set position.
        return when (messageArrayList[position].sender) {
            "user" -> 0
            "bot" -> 1
            else -> -1
        }
    }

    class UserViewHolder(@NonNull itemView: View) : ViewHolder(itemView) {
        // creating a variable
        // for our text view.
        var userTV: TextView

        init {
            // initializing with id.
            userTV = itemView.findViewById(R.id.idTVUser)
        }
    }

    class BotViewHolder(@NonNull itemView: View) : ViewHolder(itemView) {
        // creating a variable
        // for our text view.
        var botTV: TextView

        init {
            // initializing with id.
            botTV = itemView.findViewById(R.id.idTVBot)
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // this method is use to set data to our layout file.
        val message: Message = messageArrayList[position]
        when (message.sender) {
            "user" ->                 // below line is to set the text to our text view of user layout
                (holder as UserViewHolder?)!!.userTV.setText(message.message)
            "bot" ->                 // below line is to set the text to our text view of bot layout
                (holder as BotViewHolder?)!!.botTV.setText(message.message)
        }

    }
}