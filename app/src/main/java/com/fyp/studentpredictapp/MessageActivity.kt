package com.fyp.studentpredictapp

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class MessageActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var messagesAdapter: MessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        messagesAdapter = MessagesAdapter()
        recyclerView.adapter = messagesAdapter

        fetchMessages()
    }

    private fun fetchMessages() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val messageCollection = firestore.collection("messages" )
            messageCollection.whereEqualTo("userId", userId).get()
                .addOnSuccessListener { result: QuerySnapshot ->
                    // Map the result to a list of Message objects
                    val messages = result.map { document ->
                        document.toObject(Message::class.java)
                    }
                    // Pass the messages list to the callback function
                    messagesAdapter.submitList(messages)
                }
                .addOnFailureListener { exception ->
                    // Handle any errors that occur during the query
                    exception.printStackTrace()
                }
        }
    }
}

data class Message(
    val courseCode: String = "",
    val message: String = ""
) {
    constructor() : this("", "")
}

class MessagesAdapter : RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {
    private val messages = mutableListOf<Message>()

    fun submitList(newMessages: List<Message>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount() = messages.size

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val courseCodeTextView: TextView = itemView.findViewById(R.id.courseCodeTextView)
        private val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)

        fun bind(message: Message) {
            courseCodeTextView.text = message.courseCode
            messageTextView.text = message.message
            if (message.message.contains("Need Attention", true)) {
                messageTextView.setTextColor(Color.RED)
            }
        }
    }
}
